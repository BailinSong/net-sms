package com.blueline.net.sms.manager.smpp;

import com.blueline.net.sms.common.GlobalConstance;
import com.blueline.net.sms.handler.cmpp.CMPPMessageLogHandler;
import com.blueline.net.sms.handler.smpp.EnquireLinkMessageHandler;
import com.blueline.net.sms.handler.smpp.EnquireLinkRespMessageHandler;
import com.blueline.net.sms.handler.smpp.UnbindMessageHandler;
import com.blueline.net.sms.handler.smpp.UnbindRespMessageHandler;
import com.blueline.net.sms.manager.AbstractEndpointConnector;
import com.blueline.net.sms.manager.EndpointEntity;
import com.blueline.net.sms.session.AbstractSessionStateManager;
import com.blueline.net.sms.session.smpp.SMPPSessionStateManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SMPPServerChildEndpointConnector extends AbstractEndpointConnector {
	private static final Logger logger = LoggerFactory.getLogger(SMPPServerChildEndpointConnector.class);
	public SMPPServerChildEndpointConnector(EndpointEntity endpoint) {
		super(endpoint);
	}

	@Override
	public ChannelFuture open() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected SslContext createSslCtx() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void doBindHandler(ChannelPipeline pipe, EndpointEntity entity) {
		
		// 修改连接空闲时间,使用server.xml里配置的连接空闲时间生效
		if (entity instanceof SMPPServerChildEndpointEntity) {
			ChannelHandler handler = pipe.get(GlobalConstance.IdleCheckerHandlerName);
			if (handler != null) {
				pipe.replace(handler, GlobalConstance.IdleCheckerHandlerName, new IdleStateHandler(0, 0, entity.getIdleTimeSec(), TimeUnit.SECONDS));
			}
		}
		pipe.addFirst("socketLog", new LoggingHandler(String.format(GlobalConstance.loggerNamePrefix, entity.getId()), LogLevel.TRACE));
		pipe.addLast("msgLog", new CMPPMessageLogHandler(entity));
		pipe.addLast("EnquireLinkMessageHandler",new EnquireLinkMessageHandler());
		pipe.addLast("EnquireLinkRespMessageHandler",new EnquireLinkRespMessageHandler());
		pipe.addLast("UnbindRespMessageHandler", new UnbindRespMessageHandler());
		pipe.addLast("UnbindMessageHandler", new UnbindMessageHandler());
		
	}

	@Override
	protected void doinitPipeLine(ChannelPipeline pipeline) {
		
		
	}

	@Override
	protected void initSslCtx(Channel ch, EndpointEntity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected AbstractSessionStateManager createSessionManager(EndpointEntity entity, Map storeMap, boolean preSend) {
		return new SMPPSessionStateManager(entity, storeMap, preSend);
	}

}
