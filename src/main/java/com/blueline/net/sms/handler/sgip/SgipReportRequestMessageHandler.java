package com.blueline.net.sms.handler.sgip;

import com.blueline.net.sms.codec.sgip12.msg.SgipReportRequestMessage;
import com.blueline.net.sms.codec.sgip12.msg.SgipReportResponseMessage;
import com.blueline.net.sms.handler.api.AbstractBusinessHandler;
import io.netty.channel.ChannelHandlerContext;

public class SgipReportRequestMessageHandler extends AbstractBusinessHandler{
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	if(msg instanceof SgipReportRequestMessage){
    		SgipReportResponseMessage resp = new SgipReportResponseMessage(((SgipReportRequestMessage)msg).getHeader());
    		resp.setResult((short)0);
    		ctx.channel().writeAndFlush(resp);
    	}else{
    		ctx.fireChannelRead(msg);
    	}
    	
    }

	@Override
	public String name() {
		return "SgipReportRequestMessageHandler";
	}
}
