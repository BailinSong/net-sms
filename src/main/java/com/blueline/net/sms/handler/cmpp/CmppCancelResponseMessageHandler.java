/**
 * 
 */
package com.blueline.net.sms.handler.cmpp;

import com.blueline.net.sms.codec.cmpp.msg.CmppCancelResponseMessage;
import com.blueline.net.sms.codec.cmpp.packet.CmppPacketType;
import com.blueline.net.sms.codec.cmpp.packet.PacketType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author huzorro(huzorro@gmail.com)
 *
 */
public class CmppCancelResponseMessageHandler extends
        SimpleChannelInboundHandler<CmppCancelResponseMessage> {
	private PacketType packetType;
	
	public CmppCancelResponseMessageHandler() {
		this(CmppPacketType.CMPPCANCELRESPONSE);
	}

	public CmppCancelResponseMessageHandler(PacketType packetType) {
		this.packetType = packetType;
	}


	@Override
	public void channelRead0(ChannelHandlerContext ctx, CmppCancelResponseMessage e)
			throws Exception {

        
        CmppCancelResponseMessage responseMessage = (CmppCancelResponseMessage) e;
       
	}
	
	
}
