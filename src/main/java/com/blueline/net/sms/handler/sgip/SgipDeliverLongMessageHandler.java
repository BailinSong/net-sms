package com.blueline.net.sms.handler.sgip;

import com.blueline.net.sms.codec.cmpp.wap.AbstractLongMessageHandler;
import com.blueline.net.sms.codec.sgip12.msg.SgipDeliverRequestMessage;
import com.blueline.net.sms.codec.sgip12.msg.SgipDeliverResponseMessage;
import com.blueline.net.sms.common.SmsMessage;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SgipDeliverLongMessageHandler extends AbstractLongMessageHandler<SgipDeliverRequestMessage> {
	private final Logger logger = LoggerFactory.getLogger(SgipDeliverLongMessageHandler.class);
	@Override
	protected void response(ChannelHandlerContext ctx, SgipDeliverRequestMessage msg) {
		
		//短信片断未接收完全，直接给网关回复resp，等待其它片断
		SgipDeliverResponseMessage responseMessage = new SgipDeliverResponseMessage(msg.getHeader());
		responseMessage.setResult((short)0);
		ctx.writeAndFlush(responseMessage);
	}

	@Override
	protected boolean needHandleLongMessage(SgipDeliverRequestMessage msg) {
	
		return true;
	}

	@Override
	protected String generateFrameKey(SgipDeliverRequestMessage msg) {
		return msg.getUsernumber();
	}

	@Override
	protected SmsMessage getSmsMessage(SgipDeliverRequestMessage t) {
		return t.getMsg();
	}

	@Override
	protected void resetMessageContent(SgipDeliverRequestMessage t, SmsMessage content) {
		t.setMsgContent(content);
	}

}
