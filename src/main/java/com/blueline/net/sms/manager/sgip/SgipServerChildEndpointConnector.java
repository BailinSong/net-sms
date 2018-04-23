package com.blueline.net.sms.manager.sgip;

import com.blueline.net.sms.common.GlobalConstance;
import com.blueline.net.sms.handler.cmpp.CMPPMessageLogHandler;
import com.blueline.net.sms.handler.sgip.SgipUnbindRequestMessageHandler;
import com.blueline.net.sms.handler.sgip.SgipUnbindResponseMessageHandler;
import com.blueline.net.sms.manager.AbstractEndpointConnector;
import com.blueline.net.sms.manager.EndpointEntity;
import com.blueline.net.sms.session.AbstractSessionStateManager;
import com.blueline.net.sms.session.sgip.SgipSessionStateManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SgipServerChildEndpointConnector extends AbstractEndpointConnector {
	private static final Logger logger = LoggerFactory.getLogger(SgipServerChildEndpointConnector.class);
	public SgipServerChildEndpointConnector(EndpointEntity endpoint) {
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
		
		pipe.addFirst("socketLog", new LoggingHandler(String.format(GlobalConstance.loggerNamePrefix, entity.getId()), LogLevel.TRACE));
		pipe.addLast("msgLog", new CMPPMessageLogHandler(entity));
		pipe.addLast("SgipUnbindResponseMessageHandler", new SgipUnbindResponseMessageHandler());
		pipe.addLast("SgipUnbindRequestMessageHandler", new SgipUnbindRequestMessageHandler());

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
		return new SgipSessionStateManager(entity, storeMap, preSend);
	}

}
