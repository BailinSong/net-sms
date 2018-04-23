package com.blueline.net.sms.handler.cmpp;

import com.blueline.net.sms.common.GlobalConstance;
import com.blueline.net.sms.manager.EndpointEntity;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author huzorro(huzorro@gmail.com)
 *
 */

public class CMPPMessageLogHandler extends ChannelDuplexHandler {
	private final Logger logger;
	private EndpointEntity entity;

	public CMPPMessageLogHandler(EndpointEntity entity) {
		this.entity = entity;
		logger = LoggerFactory.getLogger(String.format(GlobalConstance.loggerNamePrefix, entity.getId()));
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			logger.debug("Receive:{}", msg);
		ctx.fireChannelRead(msg);
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
			logger.debug("Send:{}", msg);
		ctx.write(msg, promise);
	}

}
