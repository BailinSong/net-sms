package com.blueline.net.sms.handler.cmpp;

import com.blueline.net.sms.codec.cmpp.msg.CmppSubmitRequestMessage;
import com.blueline.net.sms.manager.cmpp.CMPPEndpointEntity;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.apache.commons.lang.StringUtils;

/**
 *重新设置Msg_Src字段，有些网关系统会校验该字段 ，防止业务则填写错误
 */
public class ReWriteSubmitMsgSrcHandler extends ChannelDuplexHandler {
	private CMPPEndpointEntity entity;

	public ReWriteSubmitMsgSrcHandler(CMPPEndpointEntity entity) {
		this.entity = entity;
	}
	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		
		if(msg instanceof CmppSubmitRequestMessage){
			CmppSubmitRequestMessage submitMsg = (CmppSubmitRequestMessage)msg;
			if(StringUtils.isEmpty(submitMsg.getMsgsrc()))
				submitMsg.setMsgsrc(entity.getUserName());
		}
		ctx.write(msg, promise);
	}
}
