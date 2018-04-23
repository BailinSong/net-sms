package com.blueline.net.sms.manager.smpp;

import com.blueline.net.sms.common.GlobalConstance;
import com.blueline.net.sms.handler.cmpp.CMPPMessageLogHandler;
import com.blueline.net.sms.handler.smpp.EnquireLinkMessageHandler;
import com.blueline.net.sms.handler.smpp.EnquireLinkRespMessageHandler;
import com.blueline.net.sms.handler.smpp.UnbindMessageHandler;
import com.blueline.net.sms.handler.smpp.UnbindRespMessageHandler;
import com.blueline.net.sms.manager.AbstractClientEndpointConnector;
import com.blueline.net.sms.manager.EndpointEntity;
import com.blueline.net.sms.session.AbstractSessionStateManager;
import com.blueline.net.sms.session.smpp.SMPPSessionLoginManager;
import com.blueline.net.sms.session.smpp.SMPPSessionStateManager;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SMPPClientEndpointConnector extends AbstractClientEndpointConnector {


	private static final Logger logger = LoggerFactory.getLogger(SMPPClientEndpointConnector.class);
	
	public SMPPClientEndpointConnector(EndpointEntity endpoint) {
		super(endpoint);
	}
	@Override
	protected AbstractSessionStateManager createSessionManager(EndpointEntity entity, Map storeMap, boolean preSend) {
		return new SMPPSessionStateManager(entity, storeMap, preSend);
	}

	@Override
	protected void doBindHandler(ChannelPipeline pipe, EndpointEntity entity) {
		pipe.addFirst("socketLog", new LoggingHandler(String.format(GlobalConstance.loggerNamePrefix, entity.getId()), LogLevel.TRACE));
		pipe.addLast("msgLog", new CMPPMessageLogHandler(entity));
		pipe.addLast("EnquireLinkMessageHandler",new EnquireLinkMessageHandler());
		pipe.addLast("EnquireLinkRespMessageHandler",new EnquireLinkRespMessageHandler());
		pipe.addLast("UnbindRespMessageHandler", new UnbindRespMessageHandler());
		pipe.addLast("UnbindMessageHandler", new UnbindMessageHandler());
	}

	@Override
	protected void doinitPipeLine(ChannelPipeline pipeline) {
		EndpointEntity entity = getEndpointEntity();
		pipeline.addLast(GlobalConstance.IdleCheckerHandlerName, new IdleStateHandler(0, 0, entity.getIdleTimeSec(), TimeUnit.SECONDS));
		pipeline.addLast("SmppServerIdleStateHandler", GlobalConstance.smppidleHandler);
		pipeline.addLast(SMPPCodecChannelInitializer.pipeName(), new SMPPCodecChannelInitializer());
		pipeline.addLast("sessionLoginManager", new SMPPSessionLoginManager(getEndpointEntity()));
	}

}
