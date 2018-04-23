/**
 * 
 */
package com.blueline.net.sms.codec.cmpp;

import com.blueline.net.sms.codec.cmpp.msg.CmppCancelResponseMessage;
import com.blueline.net.sms.codec.cmpp.msg.Message;
import com.blueline.net.sms.codec.cmpp.packet.CmppCancelResponse;
import com.blueline.net.sms.codec.cmpp.packet.CmppPacketType;
import com.blueline.net.sms.codec.cmpp.packet.PacketType;
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
public class CmppCancelResponseMessageCodec extends MessageToMessageCodec<Message, CmppCancelResponseMessage> {
	private PacketType packetType;
	
	public CmppCancelResponseMessageCodec() {
		this(CmppPacketType.CMPPCANCELRESPONSE);
	}

	public CmppCancelResponseMessageCodec(PacketType packetType) {
		this.packetType = packetType;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
		long commandId = ((Long) msg.getHeader().getCommandId()).longValue();
		if (packetType.getCommandId() != commandId)
		{
			//不解析，交给下一个codec
			out.add(msg);
			return;
		}

		CmppCancelResponseMessage responseMessage = new CmppCancelResponseMessage(msg.getHeader());
		ByteBuf bodyBuffer = Unpooled.wrappedBuffer( msg.getBodyBuffer());
		responseMessage.setSuccessId(bodyBuffer.readUnsignedInt());
		ReferenceCountUtil.release(bodyBuffer);
		out.add(responseMessage);
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, CmppCancelResponseMessage msg, List<Object> out) throws Exception {

		ByteBuf bodyBuffer =  Unpooled.buffer(CmppCancelResponse.SUCCESSID.getBodyLength());
        bodyBuffer.writeInt((int) msg.getSuccessId());
		
		msg.setBodyBuffer(toArray(bodyBuffer,bodyBuffer.readableBytes()));
		msg.getHeader().setBodyLength(msg.getBodyBuffer().length);
		ReferenceCountUtil.release(bodyBuffer);
		out.add(msg);
	}

}
