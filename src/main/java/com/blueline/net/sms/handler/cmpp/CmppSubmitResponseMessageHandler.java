/**
 * 
 */
package com.blueline.net.sms.handler.cmpp;

import com.blueline.net.sms.codec.cmpp.msg.CmppSubmitResponseMessage;
import com.blueline.net.sms.codec.cmpp.packet.CmppPacketType;
import com.blueline.net.sms.codec.cmpp.packet.PacketType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author huzorro(huzorro@gmail.com)
 *
 */
public class CmppSubmitResponseMessageHandler extends SimpleChannelInboundHandler<CmppSubmitResponseMessage> {
	private PacketType packetType;

	/**
	 * 
	 */
	public CmppSubmitResponseMessageHandler() {
		this(CmppPacketType.CMPPSUBMITRESPONSE);
	}

	public CmppSubmitResponseMessageHandler(PacketType packetType) {
		this.packetType = packetType;
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, CmppSubmitResponseMessage e) throws Exception {

	}

}
