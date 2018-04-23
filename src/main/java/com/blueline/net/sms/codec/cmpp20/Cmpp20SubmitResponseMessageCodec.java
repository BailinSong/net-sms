/**
 * 
 */
package com.blueline.net.sms.codec.cmpp20;

import com.blueline.net.sms.codec.cmpp.msg.CmppSubmitResponseMessage;
import com.blueline.net.sms.codec.cmpp.msg.Message;
import com.blueline.net.sms.codec.cmpp.packet.PacketType;
import com.blueline.net.sms.codec.cmpp20.packet.Cmpp20PacketType;
import com.blueline.net.sms.codec.cmpp20.packet.Cmpp20SubmitResponse;
import com.blueline.net.sms.common.util.DefaultMsgIdUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

import static com.blueline.net.sms.common.util.NettyByteBufUtil.toArray;

/**
 * shifei(shifei@asiainfo.com)
 */
public class Cmpp20SubmitResponseMessageCodec extends MessageToMessageCodec<Message, CmppSubmitResponseMessage> {
	private PacketType packetType;

	/**
	 * 
	 */
	public Cmpp20SubmitResponseMessageCodec() {
		this(Cmpp20PacketType.CMPPSUBMITRESPONSE);
	}

	public Cmpp20SubmitResponseMessageCodec(PacketType packetType) {
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
		CmppSubmitResponseMessage responseMessage = new CmppSubmitResponseMessage(msg.getHeader());

		ByteBuf bodyBuffer =Unpooled.wrappedBuffer(msg.getBodyBuffer());

		responseMessage.setMsgId(DefaultMsgIdUtil.bytes2MsgId(toArray(bodyBuffer,Cmpp20SubmitResponse.MSGID.getLength())));
		responseMessage.setResult(bodyBuffer.readUnsignedByte());
		ReferenceCountUtil.release(bodyBuffer);
		out.add(responseMessage);
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, CmppSubmitResponseMessage msg, List<Object> out) throws Exception {
		ByteBuf bodyBuffer = Unpooled.buffer(Cmpp20SubmitResponse.RESULT.getBodyLength());

		bodyBuffer.writeBytes(DefaultMsgIdUtil.msgId2Bytes(msg.getMsgId()));
		bodyBuffer.writeByte((int) msg.getResult());
		msg.setBodyBuffer(toArray(bodyBuffer,bodyBuffer.readableBytes()));
		msg.getHeader().setBodyLength(msg.getBodyBuffer().length);
		ReferenceCountUtil.release(bodyBuffer);
		out.add(msg);
	}
	


}