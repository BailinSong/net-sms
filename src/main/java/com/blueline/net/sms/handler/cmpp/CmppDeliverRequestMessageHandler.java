/**
 * 
 */
package com.blueline.net.sms.handler.cmpp;

import com.blueline.net.sms.codec.cmpp.msg.CmppDeliverRequestMessage;
import com.blueline.net.sms.codec.cmpp.msg.CmppDeliverResponseMessage;
import com.blueline.net.sms.codec.cmpp.packet.CmppPacketType;
import com.blueline.net.sms.codec.cmpp.packet.PacketType;
import com.blueline.net.sms.manager.cmpp.CMPPEndpointEntity;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lihuanghe(18852780@qq.com)
 *
 */
public class CmppDeliverRequestMessageHandler extends SimpleChannelInboundHandler<CmppDeliverRequestMessage> {
	private PacketType packetType;
	private static final Logger logger = LoggerFactory.getLogger(CmppDeliverRequestMessageHandler.class);


	public CmppDeliverRequestMessageHandler(CMPPEndpointEntity entity) {
		this(CmppPacketType.CMPPDELIVERREQUEST);
	
	}

	public CmppDeliverRequestMessageHandler(PacketType packetType) {
		this.packetType = packetType;
	}


	@Override
	public void channelRead0(ChannelHandlerContext ctx, CmppDeliverRequestMessage e) throws Exception {

		CmppDeliverResponseMessage responseMessage = new CmppDeliverResponseMessage(e.getHeader().getSequenceId());
		responseMessage.setMsgId(e.getMsgId());
		responseMessage.setResult(0);
		ctx.writeAndFlush(responseMessage);
	}

}
