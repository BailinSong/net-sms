/**
 * 
 */
package com.blueline.net.sms.handler.cmpp;

import com.blueline.net.sms.codec.cmpp.msg.CmppSubmitRequestMessage;
import com.blueline.net.sms.codec.cmpp.msg.CmppSubmitResponseMessage;
import com.blueline.net.sms.codec.cmpp.packet.CmppPacketType;
import com.blueline.net.sms.codec.cmpp.packet.PacketType;
import com.blueline.net.sms.common.MsgId;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author huzorro(huzorro@gmail.com)
 *
 */
public class CmppSubmitRequestMessageHandler extends SimpleChannelInboundHandler<CmppSubmitRequestMessage> {
	private PacketType packetType;

	/**
	 * 
	 */
	public CmppSubmitRequestMessageHandler() {
		this(CmppPacketType.CMPPSUBMITREQUEST);
	}

	public CmppSubmitRequestMessageHandler(PacketType packetType) {
		this.packetType = packetType;
	}


	@Override
	public void channelRead0(ChannelHandlerContext ctx, CmppSubmitRequestMessage e) throws Exception {
	
		CmppSubmitResponseMessage responseMessage = new CmppSubmitResponseMessage(e.getHeader().getSequenceId());

		
		responseMessage.setMsgId(new MsgId());
		responseMessage.setResult(0L);

		ctx.channel().writeAndFlush(responseMessage);

	}

}
