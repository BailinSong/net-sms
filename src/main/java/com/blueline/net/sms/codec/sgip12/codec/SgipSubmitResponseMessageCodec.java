/**
 * 
 */
package com.blueline.net.sms.codec.sgip12.codec;

import com.blueline.net.sms.codec.cmpp.msg.Message;
import com.blueline.net.sms.codec.cmpp.packet.PacketType;
import com.blueline.net.sms.codec.sgip12.msg.SgipSubmitResponseMessage;
import com.blueline.net.sms.codec.sgip12.packet.SgipPacketType;
import com.blueline.net.sms.codec.sgip12.packet.SgipSubmitResponse;
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
public class SgipSubmitResponseMessageCodec extends MessageToMessageCodec<Message, SgipSubmitResponseMessage> {
	private PacketType packetType;

	/**
	 * 
	 */
	public SgipSubmitResponseMessageCodec() {
		this(SgipPacketType.SUBMITRESPONSE);
	}

	public SgipSubmitResponseMessageCodec(PacketType packetType) {
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

		SgipSubmitResponseMessage responseMessage = new SgipSubmitResponseMessage(msg.getHeader());
		responseMessage.setTimestamp(msg.getTimestamp());
		ByteBuf bodyBuffer = Unpooled.wrappedBuffer(msg.getBodyBuffer());

		responseMessage.setResult(bodyBuffer.readUnsignedByte());
		responseMessage.setReserve(bodyBuffer.readCharSequence(SgipSubmitResponse.RESERVE.getLength(),GlobalConstance.defaultTransportCharset).toString().trim());
		ReferenceCountUtil.release(bodyBuffer);
		out.add(responseMessage);

	}

	@Override
	protected void encode(ChannelHandlerContext ctx, SgipSubmitResponseMessage msg, List<Object> out) throws Exception {
		ByteBuf bodyBuffer = Unpooled.buffer(SgipSubmitResponse.RESULT.getBodyLength());

		bodyBuffer.writeByte(msg.getResult());
		bodyBuffer.writeBytes(CMPPCommonUtil.ensureLength(msg.getReserve().getBytes(GlobalConstance.defaultTransportCharset),
				SgipSubmitResponse.RESERVE.getLength(), 0));

		msg.setBodyBuffer(toArray(bodyBuffer, bodyBuffer.readableBytes()));
		msg.getHeader().setBodyLength(msg.getBodyBuffer().length);
		ReferenceCountUtil.release(bodyBuffer);
		out.add(msg);
	}

}
