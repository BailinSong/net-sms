/**
 * 
 */
package com.blueline.net.sms.codec.sgip12.codec;

import com.blueline.net.sms.codec.cmpp.msg.Message;
import com.blueline.net.sms.codec.cmpp.packet.PacketType;
import com.blueline.net.sms.codec.sgip12.msg.SgipBindRequestMessage;
import com.blueline.net.sms.codec.sgip12.packet.SgipBindRequest;
import com.blueline.net.sms.codec.sgip12.packet.SgipPacketType;
import com.blueline.net.sms.common.util.CMPPCommonUtil;
import com.blueline.net.sms.common.GlobalConstance;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

import static com.blueline.net.sms.common.util.NettyByteBufUtil.toArray;

/**
 * @author huzorro(huzorro@gmail.com)
 * @author Lihuanghe(18852780@qq.com)
 */
public class SgipBindRequestMessageCodec extends MessageToMessageCodec<Message, SgipBindRequestMessage> {
	private PacketType packetType;

	public SgipBindRequestMessageCodec() {
		this(SgipPacketType.BINDREQUEST);
	}

	public SgipBindRequestMessageCodec(PacketType packetType) {
		this.packetType = packetType;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
		long commandId = ((Long) msg.getHeader().getCommandId()).longValue();
		if (packetType.getCommandId() != commandId) {
			// 不解析，交给下一个codec
			out.add(msg);
			return;
		}

		SgipBindRequestMessage requestMessage = new SgipBindRequestMessage(msg.getHeader());
		requestMessage.setTimestamp(msg.getTimestamp());

		ByteBuf bodyBuffer = Unpooled.wrappedBuffer(msg.getBodyBuffer());

		requestMessage.setLoginType(bodyBuffer.readUnsignedByte());
		requestMessage.setLoginName(bodyBuffer.readCharSequence(SgipBindRequest.LOGINNAME.getLength(), GlobalConstance.defaultTransportCharset).toString().trim());
		requestMessage.setLoginPassowrd(bodyBuffer.readCharSequence(SgipBindRequest.LOGINPASSWD.getLength(), GlobalConstance.defaultTransportCharset).toString().trim());
		requestMessage.setReserve(bodyBuffer.readCharSequence(SgipBindRequest.RESERVE.getLength(), GlobalConstance.defaultTransportCharset).toString().trim());
		ReferenceCountUtil.release(bodyBuffer);
		out.add(requestMessage);
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, SgipBindRequestMessage requestMessage, List<Object> out) throws Exception {
		ByteBuf bodyBuffer = Unpooled.buffer(SgipBindRequest.LOGINNAME.getBodyLength());
		bodyBuffer.writeByte(requestMessage.getLoginType());
		bodyBuffer.writeBytes(CMPPCommonUtil.ensureLength(requestMessage.getLoginName().getBytes(GlobalConstance.defaultTransportCharset),
				SgipBindRequest.LOGINNAME.getLength(), 0));
		bodyBuffer.writeBytes(CMPPCommonUtil.ensureLength(requestMessage.getLoginPassowrd().getBytes(GlobalConstance.defaultTransportCharset),
				SgipBindRequest.LOGINPASSWD.getLength(), 0));
		bodyBuffer.writeBytes(CMPPCommonUtil.ensureLength(requestMessage.getReserve().getBytes(GlobalConstance.defaultTransportCharset),
				SgipBindRequest.RESERVE.getLength(), 0));

		requestMessage.setBodyBuffer(toArray(bodyBuffer, bodyBuffer.readableBytes()));
		requestMessage.getHeader().setBodyLength(requestMessage.getBodyBuffer().length);
		ReferenceCountUtil.release(bodyBuffer);
		out.add(requestMessage);
	}

}
