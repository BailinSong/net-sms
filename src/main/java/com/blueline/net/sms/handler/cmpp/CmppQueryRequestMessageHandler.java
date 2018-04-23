/**
 * 
 */
package com.blueline.net.sms.handler.cmpp;

import com.blueline.net.sms.codec.cmpp.msg.CmppQueryRequestMessage;
import com.blueline.net.sms.codec.cmpp.msg.CmppQueryResponseMessage;
import com.blueline.net.sms.codec.cmpp.packet.CmppPacketType;
import com.blueline.net.sms.codec.cmpp.packet.PacketType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author huzorro
 *
 */
public class CmppQueryRequestMessageHandler extends SimpleChannelInboundHandler<CmppQueryRequestMessage> {
	private PacketType packetType;

	public CmppQueryRequestMessageHandler() {
		this(CmppPacketType.CMPPQUERYREQUEST);
	}

	public CmppQueryRequestMessageHandler(PacketType packetType) {
		this.packetType = packetType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jboss.netty.channel.SimpleChannelUpstreamHandler#messageReceived(
	 * org.jboss.netty.channel.ChannelHandlerContext,
	 * org.jboss.netty.channel.MessageEvent)
	 */
	@Override
	public void channelRead0(ChannelHandlerContext ctx, CmppQueryRequestMessage e) throws Exception {

		CmppQueryResponseMessage responseMessage = new CmppQueryResponseMessage(e.getHeader().getSequenceId());

		// TODO

		ctx.channel().write(responseMessage);

	}

}
