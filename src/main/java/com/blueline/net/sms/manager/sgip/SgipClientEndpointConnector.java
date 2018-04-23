package com.blueline.net.sms.manager.sgip;

import com.blueline.net.sms.common.GlobalConstance;
import com.blueline.net.sms.handler.cmpp.CMPPMessageLogHandler;
import com.blueline.net.sms.handler.sgip.SgipUnbindRequestMessageHandler;
import com.blueline.net.sms.handler.sgip.SgipUnbindResponseMessageHandler;
import com.blueline.net.sms.manager.AbstractClientEndpointConnector;
import com.blueline.net.sms.manager.EndpointEntity;
import com.blueline.net.sms.session.AbstractSessionStateManager;
import com.blueline.net.sms.session.sgip.SgipSessionLoginManager;
import com.blueline.net.sms.session.sgip.SgipSessionStateManager;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SgipClientEndpointConnector extends AbstractClientEndpointConnector {


	private static final Logger logger = LoggerFactory.getLogger(SgipClientEndpointConnector.class);
	
	public SgipClientEndpointConnector(EndpointEntity endpoint) {
		super(endpoint);
	}
	@Override
	protected AbstractSessionStateManager createSessionManager(EndpointEntity entity, Map storeMap, boolean preSend) {
		return new SgipSessionStateManager(entity, storeMap, preSend);
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
		EndpointEntity entity = getEndpointEntity();
		pipeline.addLast(GlobalConstance.IdleCheckerHandlerName, new IdleStateHandler(0, 0, entity.getIdleTimeSec(), TimeUnit.SECONDS));
		pipeline.addLast("SgipServerIdleStateHandler", GlobalConstance.sgipidleHandler);
		pipeline.addLast(SgipCodecChannelInitializer.pipeName(), new SgipCodecChannelInitializer());
		pipeline.addLast("sessionLoginManager", new SgipSessionLoginManager(getEndpointEntity()));
	}

}
