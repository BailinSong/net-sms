/**
 * 
 */
package com.blueline.net.sms.handler.cmpp;

import com.blueline.net.sms.codec.cmpp.msg.CmppDeliverResponseMessage;
import com.blueline.net.sms.codec.cmpp.packet.CmppPacketType;
import com.blueline.net.sms.codec.cmpp.packet.PacketType;
import com.blueline.net.sms.manager.cmpp.CMPPEndpointEntity;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author huzorro(huzorro@gmail.com)
 *
 */
@Sharable
public class CmppDeliverResponseMessageHandler extends SimpleChannelInboundHandler<CmppDeliverResponseMessage> {
	private PacketType packetType;
	private static final Logger logger = LoggerFactory.getLogger(CmppDeliverResponseMessageHandler.class);

	public CmppDeliverResponseMessageHandler(CMPPEndpointEntity entity) {
		this(CmppPacketType.CMPPDELIVERRESPONSE);

	}

	public CmppDeliverResponseMessageHandler(PacketType packetType) {
		this.packetType = packetType;

	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, CmppDeliverResponseMessage e) throws Exception {

	}

}
