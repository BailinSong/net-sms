package com.blueline.net.sms.handler.sgip;

import com.blueline.net.sms.codec.cmpp.wap.AbstractLongMessageHandler;
import com.blueline.net.sms.codec.sgip12.msg.SgipSubmitRequestMessage;
import com.blueline.net.sms.codec.sgip12.msg.SgipSubmitResponseMessage;
import com.blueline.net.sms.common.SmsMessage;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang.StringUtils;

public class SgipSubmitLongMessageHandler extends AbstractLongMessageHandler<SgipSubmitRequestMessage> {

	@Override
	protected void response(ChannelHandlerContext ctx, SgipSubmitRequestMessage msg) {
		//短信片断未接收完全，直接给网关回复resp，等待其它片断
		SgipSubmitResponseMessage responseMessage = new SgipSubmitResponseMessage(msg.getHeader());
		ctx.writeAndFlush(responseMessage);
	}

	@Override
	protected boolean needHandleLongMessage(SgipSubmitRequestMessage msg) {
		return true;
	}

	@Override
	protected String generateFrameKey(SgipSubmitRequestMessage msg) {
		return StringUtils.join(msg.getUsernumber(), "|");
	}

	@Override
	protected SmsMessage getSmsMessage(SgipSubmitRequestMessage t) {
		
		return t.getMsg();
	}

	@Override
	protected void resetMessageContent(SgipSubmitRequestMessage t, SmsMessage content) {
		t.setMsgContent(content);
		
	}

}
