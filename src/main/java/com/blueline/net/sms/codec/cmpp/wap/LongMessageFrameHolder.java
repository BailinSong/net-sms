package com.blueline.net.sms.codec.cmpp.wap;

import com.blueline.net.sms.codec.cmpp.msg.LongMessageFrame;
import com.blueline.net.sms.common.*;
import com.blueline.net.sms.common.util.CMPPCommonUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.netty.buffer.ByteBufUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

//短信片断持久化需要集中保存，因为同一短信的不同分片会从不同的连接发送。可能不在同一台主机。
//可以使用 Redis.Memcached等。
public enum LongMessageFrameHolder {
	INS;
	private final Logger logger = LoggerFactory.getLogger(LongMessageFrameHolder.class);

	/**
	 * 以服务号码+帧唯一码为key
	 *  注意：这里使用的jvm缓存保证长短信的分片。如果是集群部署，从网关过来的长短信会随机发送到不同的主机，需要使用集群缓存，如redis,memcached来保存长短信分片。
	 *  由于可能有短信分片丢失，造成一直不能组装完成，为防止内存泄漏，这里要使用支持过期失效的缓存。
	 */
	private static Cache<String, FrameHolder> cache =CacheBuilder.newBuilder().expireAfterAccess(7200, TimeUnit.SECONDS).build();
	private static ConcurrentMap<String, FrameHolder> map = cache.asMap();

	private SmsMessage generatorSmsMessage(FrameHolder fh, LongMessageFrame frame) throws NotSupportedException {
		byte[] contents = fh.mergeAllcontent();
		InformationElement udheader = fh.getAppUDHinfo();
		// udh为空表示文本短信
		if (udheader == null) {
			return CMPPCommonUtil.buildTextMessage(contents, frame.getMsgfmt());
		} else {
			if (SmsUdhIei.APP_PORT_16BIT.equals(udheader.udhIei)) {
				// 2948 wap_push  0x0B84
				int destport = (((udheader.infoEleData[0] & 0xff) << 8) | (udheader.infoEleData[1] & 0xff)) & 0x0ffff;
				// 9200 wap-wsp   0x23f0
				int srcport = (((udheader.infoEleData[2] & 0xff) << 8) | (udheader.infoEleData[3] & 0xff)) & 0x0ffff;
				if (destport == SmsPort.WAP_PUSH.getPort() && srcport == SmsPort.WAP_WSP.getPort()) {
					throw new NotSupportedException("WAP_PUSH or WAP_WSP");
				} else if (destport == SmsPort.NOKIA_MULTIPART_MESSAGE.getPort() && srcport == SmsPort.ZERO.getPort()) {
					// Nokia手机支持的OTA图片格式
					throw new NotSupportedException("NOKIA_MULTIPART_MESSAGE");
				} else if(destport == SmsPort.OTA_SETTINGS_BROWSER.getPort()){
					// Nokia手机支持的OTA浏览器书签
					throw new NotSupportedException("OTA_SETTINGS_BROWSER");
				}

				logger.warn("UnsupportedportMessage UDH:{} udhdata:{},pdu:[{}]", udheader.udhIei, ByteBufUtil.hexDump(udheader.infoEleData), ByteBufUtil.hexDump(contents));

				SmsTextMessage text = CMPPCommonUtil.buildTextMessage(contents, frame.getMsgfmt());
				return new SmsPortAddressedTextMessage(new SmsPort(destport),new SmsPort(srcport),text);
			} else {
				// 其它都当成文本短信
				logger.warn("Unsupported UDH:{} udhdata:{},pdu:[{}]", udheader.udhIei, ByteBufUtil.hexDump(udheader.infoEleData), ByteBufUtil.hexDump(contents));
				return CMPPCommonUtil.buildTextMessage(contents, frame.getMsgfmt());
			}
		}
	}
	
	/**
	 * 获取长短信切分后的短信片断内容
	 * 
	 **/
	public String getPartTextMsg(LongMessageFrame frame){
		if (frame.getTpudhi() == 0) {
			return CMPPCommonUtil.buildTextMessage(frame.getPayloadbytes(0), frame.getMsgfmt()).getText();
		} else if ((frame.getTpudhi() & 0x01) == 1 || (frame.getTpudhi()&0x40)==0x40) {
			UserDataHeader header = parseUserDataHeader(frame.getMsgContentBytes());
			byte[] payload = frame.getPayloadbytes(header.headerlength);
			return CMPPCommonUtil.buildTextMessage(payload, frame.getMsgfmt()).getText();
		}
		return null;
		
	}

	/**
	 * 获取一条完整的长短信，如果长短信组装未完成，返回null
	 **/
	public SmsMessage putAndget(String serviceNum, LongMessageFrame frame) throws NotSupportedException {

		//assert (frame.getTppid() == 0);

		// 短信内容不带协议头，直接获取短信内容
		// udhi只取第一个bit
		if (frame.getTpudhi() == 0) {
			return CMPPCommonUtil.buildTextMessage(frame.getPayloadbytes(0), frame.getMsgfmt());

		} else if ((frame.getTpudhi() & 0x01) == 1 || (frame.getTpudhi()&0x40)==0x40) {

			FrameHolder fh = createFrameHolder(frame);
			
			// 判断是否只有一帧
			if (fh.isComplete()) {

				return generatorSmsMessage(fh, frame);
			}

			// 超过一帧的，进行长短信合并
			String mapKey = new StringBuilder().append(serviceNum).append(".").append(fh.frameKey).toString();

			FrameHolder oldframeHolder = map.putIfAbsent(mapKey, fh);

			if (oldframeHolder != null) {

				mergeFrameHolder(oldframeHolder, frame);
			} else {
				oldframeHolder = fh;
			}

			if (oldframeHolder.isComplete()) {

				map.remove(mapKey);

				return generatorSmsMessage(oldframeHolder, frame);
			}

		} else {
			throw new NotSupportedException("Not Support LongMsg.Tpudhi");
		}

		return null;

	}

	public List<LongMessageFrame> splitmsgcontent(SmsMessage content) throws SmsException {

		List<LongMessageFrame> result = new ArrayList<LongMessageFrame>();
		SmsPdu[] pdus = content.getPdus();
		int i = 1;
		for (SmsPdu aMsgPdu : pdus) {
			byte[] udh = aMsgPdu.getUserDataHeaders();
			LongMessageFrame frame = new LongMessageFrame();
			frame.setPktotal((short) pdus.length);
			frame.setPknumber((short) i++);
			frame.setMsgfmt(aMsgPdu.getDcs());
			frame.setTppid((short) 0);
			frame.setTpudhi(udh != null ? (short) 1 : (short) 0);

			ByteArrayOutputStream btos = new ByteArrayOutputStream(200);
			frame.setMsgLength((short) encodePdu(aMsgPdu, btos));
			frame.setMsgContentBytes(btos.toByteArray());
			result.add(frame);
		}

		return result;
	}

	private FrameHolder mergeFrameHolder(FrameHolder fh, LongMessageFrame frame) throws NotSupportedException {
		byte[] msgcontent = frame.getMsgContentBytes();
		UserDataHeader header = parseUserDataHeader(msgcontent);

		if (header.infoElement.size() > 0) {

			for (InformationElement udhi : header.infoElement) {
				if (SmsUdhIei.CONCATENATED_8BIT.equals(udhi.udhIei)) {

					fh.merge(frame.getPayloadbytes(header.headerlength), udhi.infoEleData[2] - 1);
				} else if (SmsUdhIei.CONCATENATED_16BIT.equals(udhi.udhIei)) {

					fh.merge(frame.getPayloadbytes(header.headerlength), udhi.infoEleData[3] - 1);
				}
			}

			return fh;
		}

		throw new NotSupportedException("Not Support LongMsg");
	}
	
	private int byteToint(byte b){
		return (int)(b&0x0ff);
	}

	private FrameHolder createFrameHolder(LongMessageFrame frame) throws NotSupportedException {

		byte[] msgcontent = frame.getMsgContentBytes();

		UserDataHeader header = parseUserDataHeader(msgcontent);

		if (header.infoElement.size() > 0) {
			FrameHolder frameholder = null;
			InformationElement appudhinfo = null;
			int i = 0;
			int frameKey = 0;
			for (InformationElement udhi : header.infoElement) {
				if (SmsUdhIei.CONCATENATED_8BIT.equals(udhi.udhIei)) {
					frameKey = udhi.infoEleData[i];
					i++;
					frameholder = new FrameHolder(frameKey, byteToint(udhi.infoEleData[i]), frame.getPayloadbytes(header.headerlength), byteToint(udhi.infoEleData[i + 1]) - 1);

				} else if (SmsUdhIei.CONCATENATED_16BIT.equals(udhi.udhIei)) {
					frameKey = (((udhi.infoEleData[i] & 0xff) << 8) | (udhi.infoEleData[i + 1] & 0xff) & 0x0ffff);
					i += 2;
					frameholder = new FrameHolder(frameKey,byteToint( udhi.infoEleData[i]), frame.getPayloadbytes(header.headerlength), byteToint(udhi.infoEleData[i + 1]) - 1);
				} else {
					appudhinfo = udhi;
				}
			}
			// 不是续列短信
			if (frameholder == null) {
				frameholder = new FrameHolder(0x0, 1, frame.getPayloadbytes(header.headerlength), 0);
			}
			// 如果没有app的udh，默认为文本短信

			frameholder.setAppUDHinfo(appudhinfo);
			return frameholder;
		}

		throw new NotSupportedException("Not Support LongMsg");
	}

	private UserDataHeader parseUserDataHeader(byte[] pdu) {
		UserDataHeader udh = new UserDataHeader();
		udh.headerlength = pdu[0]; // 05
		udh.infoElement = new ArrayList<InformationElement>();

		int i = 1;
		while (i < udh.headerlength) {
			InformationElement t = new InformationElement();
			t.udhIei = SmsUdhIei.valueOf(pdu[i++]); // 00
			t.infoEleLength = pdu[i++]; // 03
			t.infoEleData = new byte[t.infoEleLength];
			System.arraycopy(pdu, i, t.infoEleData, 0, t.infoEleLength);
			i += t.infoEleLength;
			udh.infoElement.add(t);
		}
		return udh;
	}

	private class UserDataHeader {
		int headerlength;
		List<InformationElement> infoElement;
	}

	private class InformationElement {

		SmsUdhIei udhIei;
		int infoEleLength;
		byte[] infoEleData;
	}

	// 用来保存一条短信的各个片断
	/**
	 * TP_udhi ：0代表内容体里不含有协议头信息
	 * 1代表内容含有协议头信息（长短信，push短信等都是在内容体上含有头内容的）当设置内容体包含协议头
	 * ，需要根据协议写入相应的信息，长短信协议头有两种：<br/>
	 * 6位协议头格式：05 00 03 XX MM NN<br/>
	 * byte 1 : 05, 表示剩余协议头的长度<br/>
	 * byte 2 : 00, 这个值在GSM 03.40规范9.2.3.24.1中规定，表示随后的这批超长短信的标识位长度为1（格式中的XX值）。<br/>
	 * byte 3 : 03, 这个值表示剩下短信标识的长度<br/>
	 * byte 4 : XX，这批短信的唯一标志，事实上，SME(手机或者SP)把消息合并完之后，就重新记录，所以这个标志是否唯 一并不是很 重要。<br/>
	 * byte 5 : MM, 这批短信的数量。如果一个超长短信总共5条，这里的值就是5。<br/>
	 * byte 6 : NN, 这批短信的数量。如果当前短信是这批短信中的第一条的值是1，第二条的值是2。<br/>
	 * 例如：05 00 03 39 02 01 <br/>
	 * 
	 * 7 位的协议头格式：06 08 04 XX XX MM NN<br/>
	 * byte 1 : 06, 表示剩余协议头的长度<br/>
	 * byte 2 : 08, 这个值在GSM 03.40规范9.2.3.24.1中规定，表示随后的这批超长短信的标识位长度为2（格式中的XX值）。<br/>
	 * byte 3 : 04, 这个值表示剩下短信标识的长度<br/>
	 * byte 4-5 : XX
	 * XX，这批短信的唯一标志，事实上，SME(手机或者SP)把消息合并完之后，就重新记录，所以这个标志是否唯一并不是很重要。<br/>
	 * byte 6 : MM, 这批短信的数量。如果一个超长短信总共5条，这里的值就是5。<br/>
	 * byte 7 : NN, 这批短信的数量。如果当前短信是这批短信中的第一条的值是1，第二条的值是2。<br/>
	 * 例如：06 08 04 00 39 02 01 <br/>
	 **/
	private class FrameHolder {
		/**
		 * 长短信的总分片数量
		 * */
		private int totalLength = 0;
		private int frameKey;
		// 保存帧的Map,每帧都有一个唯一码。以这个唯一码做key
		private byte[][] content;

		private int totalbyteLength = 0;

		private BitSet idxBitset;

		private InformationElement appUDHinfo;

		// 用来保存应用类型，如文本短信或者wap短信
		public void setAppUDHinfo(InformationElement appUDHinfo) {
			this.appUDHinfo = appUDHinfo;
		}

		public InformationElement getAppUDHinfo() {
			return this.appUDHinfo;
		}

		public FrameHolder(int frameKey, int totalLength, byte[] content, int frameIndex) {
			this.frameKey = frameKey;
			this.totalLength = totalLength;

			this.content = new byte[totalLength][];
			this.idxBitset = new BitSet(totalLength);
			merge(content, frameIndex);
		}

		public synchronized void merge(byte[] content, int idx) {

			if (idxBitset.get(idx)) {
				logger.warn("have received the same index of Message. do not merge this content. ");
				return;
			}
			if (this.content.length <= idx) {
				logger.warn("have received error index:{} of Message content length:{}. do not merge this content. ", idx, this.content.length);
				return;
			}
			// 设置该短信序号已填冲
			idxBitset.set(idx);

			this.content[idx] = content;

			this.totalbyteLength += this.content[idx].length;
		}

		public synchronized boolean isComplete() {
			return totalLength == idxBitset.cardinality();
		}

		public synchronized byte[] mergeAllcontent() {
			if (totalLength == 1) {
				return content[0];
			}
			byte[] ret = new byte[totalbyteLength];
			int idx = 0;
			for (int i = 0; i < totalLength; i++) {
				System.arraycopy(content[i], 0, ret, idx, content[i].length);
				idx += content[i].length;
			}

			return ret;
		}
	}

	/**
	 * Convert a stream of septets read as octets into a byte array containing
	 * the 7-bit values from the octet stream.
	 * 
	 * @param octets
	 *            FIXME pass the septet length in here, so if there is a spare
	 *            septet at the end of the octet, we can ignore that
	 * @return
	 */

	public static byte[] octetStream2septetStream(byte[] octets) {
		int septetCount = (8 * octets.length) / 7;
		byte[] septets = new byte[septetCount];
		for (int newIndex = septets.length - 1; newIndex >= 0; --newIndex) {
			for (int bit = 6; bit >= 0; --bit) {
				int oldBitIndex = ((newIndex * 7) + bit);
				if ((octets[oldBitIndex >>> 3] & (1 << (oldBitIndex & 7))) != 0)
					septets[newIndex] |= (1 << bit);
			}
		}

		return septets;
	}

	public static int octetLengthfromseptetsLength(int septetLength) {
		return (int) Math.ceil((septetLength * 7) / 8.0);
	}


	private int encodePdu(SmsPdu pdu, OutputStream baos) throws SmsException {
		switch (pdu.getDcs().getAlphabet()) {
		case GSM:
			return encodeSeptetPdu(pdu, baos);
		default:
			return encodeOctetPdu(pdu, baos);
		}
	}
	
	// 如果是7bit编码，需要计算真实的数据长度
	public static int getPayloadLength(SmsAlphabet alpha,int udl) {
		switch (alpha) {
		case GSM:
			return LongMessageFrameHolder.octetLengthfromseptetsLength(udl);
		default:
			return udl;
		}
	}

	/**
	 * Encodes an septet encoded pdu.
	 * 
	 * @param pdu
	 * @param baos
	 * @return
	 * @throws SmsException
	 */
	private static int encodeSeptetPdu(SmsPdu pdu, OutputStream baos) throws SmsException {
		SmsUserData userData = pdu.getUserData();
		byte[] ud = userData.getData();
		byte[] udh = pdu.getUserDataHeaders();

		int nUdSeptets = ud.length * 8 / 7;
		int nUdBits = 0;

		int nUdhBytes = (udh == null) ? 0 : udh.length;

		// UDH + UDHL
		int nUdhBits = 0;

		// UD + UDH + UDHL
		int nTotalBytes = 0;
		int nTotalBits = 0;
		int nTotalSeptets = 0;

		int nFillBits = 0;
		int length = 0;

		try {
			nUdhBits = nUdhBytes * 8;
			nUdBits = nUdSeptets * 7;

			nTotalBits = nUdBits + nFillBits + nUdhBits;
			nTotalSeptets = nTotalBits / 7;

			nTotalBytes = nTotalBits / 8;
			if (nTotalBits % 8 > 0) {
				nTotalBytes += 1;
			}

			// UDH?
			if ((udh == null) || (udh.length == 0)) {
				// TP-UDL
				length = nUdSeptets;

				// TP-UD
				baos.write(ud);
			} else {
				// The whole UD PDU
				byte[] fullUd = new byte[nTotalBytes];

				// TP-UDL
				// UDL includes the length of the UDHL

				length = nTotalSeptets;
				// TP-UDH (including user data header length)
				System.arraycopy(udh, 0, fullUd, 0, nUdhBytes);

				// TP-UD
				SmsPduUtil.arrayCopy(ud, 0, fullUd, nUdhBytes, nFillBits, nUdBits);

				baos.write(fullUd);
			}
			baos.close();
		} catch (IOException ex) {
			throw new SmsException(ex);
		}

		return length;
	}

	/**
	 * Encodes an octet encoded sms pdu.
	 * 
	 * @param pdu
	 * @param baos
	 * @return
	 * @throws SmsException
	 */
	private static int encodeOctetPdu(SmsPdu pdu, OutputStream baos) throws SmsException {
		SmsUserData userData = pdu.getUserData();
		byte[] ud = userData.getData();
		byte[] udh = pdu.getUserDataHeaders();
		int length = 0;
		try {
			int nUdBytes = userData.getLength();
			int nUdhBytes = (udh == null) ? 0 : udh.length;

			// 1 octet/ 7 octets
			// TP-VP - Optional

			// UDH?
			if (nUdhBytes == 0) {
				// 1 Integer
				// TP-UDL
				// UDL includes the length of UDH
				length = nUdBytes;

				// n octets
				// TP-UD
				baos.write(ud);
			} else {

				// TP-UDL includes the length of UDH
				// +1 is for the size header...
				length = nUdBytes + nUdhBytes;
				// TP-UDH (including user data header length)
				baos.write(udh);
				// TP-UD
				baos.write(ud);

			}
			baos.close();
		} catch (IOException ex) {
			throw new SmsException(ex);
		}

		return length;
	}

}
