package com.blueline.net.sms.handler.smpp;

import com.blueline.net.sms.codec.smpp.msg.EnquireLink;
import com.blueline.net.sms.common.GlobalConstance;
import com.blueline.net.sms.session.cmpp.SessionState;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

@Sharable
public class SmppServerIdleStateHandler extends ChannelDuplexHandler {
	@Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.ALL_IDLE) {
            	//如果是SMPP连接未建立，直接关闭
            	if(ctx.channel().attr(GlobalConstance.attributeKey).get() != SessionState.Connect){
            		ctx.close();
            	}else{
            		ctx.channel().writeAndFlush(new EnquireLink());
            	}
            } 
        }else{
        	ctx.fireUserEventTriggered(evt);
        }
    }
}