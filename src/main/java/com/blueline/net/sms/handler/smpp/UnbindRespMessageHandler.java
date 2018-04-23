package com.blueline.net.sms.handler.smpp;

import com.blueline.net.sms.codec.smpp.msg.UnbindResp;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class UnbindRespMessageHandler extends SimpleChannelInboundHandler<UnbindResp> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, UnbindResp msg) throws Exception {
		ctx.channel().close();
		
	}}