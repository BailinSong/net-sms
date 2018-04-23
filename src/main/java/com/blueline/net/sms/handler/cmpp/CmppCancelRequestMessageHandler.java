/**
 * 
 */
package com.blueline.net.sms.handler.cmpp;

import com.blueline.net.sms.codec.cmpp.msg.CmppCancelRequestMessage;
import com.blueline.net.sms.codec.cmpp.msg.CmppCancelResponseMessage;
import com.blueline.net.sms.codec.cmpp.packet.CmppPacketType;
import com.blueline.net.sms.codec.cmpp.packet.PacketType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author huzorro(huzorro@gmail.com)
 *
 */
public class CmppCancelRequestMessageHandler extends
        SimpleChannelInboundHandler<CmppCancelRequestMessage> {
	private PacketType packetType;
	
	public CmppCancelRequestMessageHandler() {
		this(CmppPacketType.CMPPCANCELREQUEST);
	}

	public CmppCancelRequestMessageHandler(PacketType packetType) {
		this.packetType = packetType;
	}


	@Override
	public void channelRead0(ChannelHandlerContext ctx, CmppCancelRequestMessage e)
			throws Exception {
		CmppCancelResponseMessage responseMessage = new CmppCancelResponseMessage(e.getHeader().getSequenceId());
		
		responseMessage.setSuccessId(0L);
		
		ctx.channel().writeAndFlush(responseMessage);

	}
	
}
