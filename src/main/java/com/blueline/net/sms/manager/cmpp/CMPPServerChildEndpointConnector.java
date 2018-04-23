package com.blueline.net.sms.manager.cmpp;

import com.blueline.net.sms.common.GlobalConstance;
import com.blueline.net.sms.handler.cmpp.CMPPMessageLogHandler;
import com.blueline.net.sms.manager.AbstractEndpointConnector;
import com.blueline.net.sms.manager.EndpointEntity;
import com.blueline.net.sms.session.AbstractSessionStateManager;
import com.blueline.net.sms.session.cmpp.SessionStateManager;
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

public class CMPPServerChildEndpointConnector extends AbstractEndpointConnector {
	private static final Logger logger = LoggerFactory.getLogger(CMPPServerChildEndpointConnector.class);
	
	public CMPPServerChildEndpointConnector(CMPPEndpointEntity endpoint) {
		super(endpoint);
	}

	@Override
	public ChannelFuture open() throws Exception {
		//TODO 子端口打开，说明有客户连接上来
		return null;
	}


	@Override
	protected AbstractSessionStateManager createSessionManager(EndpointEntity entity, Map storeMap,
															   boolean preSend) {
		return new SessionStateManager(entity, storeMap, preSend);
	}

	@Override
	protected void doBindHandler(ChannelPipeline pipe, EndpointEntity cmppentity) {
		CMPPEndpointEntity entity = (CMPPEndpointEntity)cmppentity;
		// 修改连接空闲时间,使用server.xml里配置的连接空闲时间生效
		if (entity instanceof CMPPServerChildEndpointEntity) {
			ChannelHandler handler = pipe.get(GlobalConstance.IdleCheckerHandlerName);
			if (handler != null) {
				pipe.replace(handler, GlobalConstance.IdleCheckerHandlerName, new IdleStateHandler(0, 0, entity.getIdleTimeSec(), TimeUnit.SECONDS));
			}
		}

		pipe.addFirst("socketLog", new LoggingHandler(String.format(GlobalConstance.loggerNamePrefix, entity.getId()), LogLevel.TRACE));
		pipe.addLast("msgLog", new CMPPMessageLogHandler(entity));

		pipe.addLast("CmppActiveTestRequestMessageHandler", GlobalConstance.activeTestHandler);
		pipe.addLast("CmppActiveTestResponseMessageHandler", GlobalConstance.activeTestRespHandler);
		pipe.addLast("CmppTerminateRequestMessageHandler", GlobalConstance.terminateHandler);
		pipe.addLast("CmppTerminateResponseMessageHandler", GlobalConstance.terminateRespHandler);
	}

	@Override
	protected SslContext createSslCtx() {
		return null;
	}

	@Override
	protected void initSslCtx(Channel ch, EndpointEntity entity) {
	}

	@Override
	protected void doinitPipeLine(ChannelPipeline pipeline) {
		
	}
}
