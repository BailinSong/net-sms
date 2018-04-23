package com.blueline.net.sms.manager.cmpp;

import com.blueline.net.sms.common.GlobalConstance;
import com.blueline.net.sms.handler.cmpp.CMPPMessageLogHandler;
import com.blueline.net.sms.handler.cmpp.ReWriteSubmitMsgSrcHandler;
import com.blueline.net.sms.manager.AbstractClientEndpointConnector;
import com.blueline.net.sms.manager.ClientEndpoint;
import com.blueline.net.sms.manager.EndpointEntity;
import com.blueline.net.sms.session.AbstractSessionStateManager;
import com.blueline.net.sms.session.cmpp.SessionLoginManager;
import com.blueline.net.sms.session.cmpp.SessionStateManager;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *@author Lihuanghe(18852780@qq.com)
 */

public class CMPPClientEndpointConnector extends AbstractClientEndpointConnector {
	private static final Logger logger = LoggerFactory.getLogger(CMPPClientEndpointConnector.class);
	

	
	public CMPPClientEndpointConnector(CMPPClientEndpointEntity e)
	{
		super(e);
		
	}

	@Override
	protected void doBindHandler(ChannelPipeline pipe, EndpointEntity cmppentity) {
		CMPPEndpointEntity entity = (CMPPEndpointEntity)cmppentity;

		pipe.addFirst("socketLog", new LoggingHandler(String.format(GlobalConstance.loggerNamePrefix, entity.getId()), LogLevel.TRACE));
		pipe.addLast("msgLog", new CMPPMessageLogHandler(entity));

		if (entity instanceof ClientEndpoint) {
			pipe.addLast("reWriteSubmitMsgSrcHandler", new ReWriteSubmitMsgSrcHandler(entity));
		}

		pipe.addLast("CmppActiveTestRequestMessageHandler", GlobalConstance.activeTestHandler);
		pipe.addLast("CmppActiveTestResponseMessageHandler", GlobalConstance.activeTestRespHandler);
		pipe.addLast("CmppTerminateRequestMessageHandler", GlobalConstance.terminateHandler);
		pipe.addLast("CmppTerminateResponseMessageHandler", GlobalConstance.terminateRespHandler);
		
	}

	@Override
	protected void doinitPipeLine(ChannelPipeline pipeline) {
		CMPPCodecChannelInitializer codec = null;
		if (getEndpointEntity() instanceof CMPPEndpointEntity) {
			pipeline.addLast(GlobalConstance.IdleCheckerHandlerName,
					new IdleStateHandler(0, 0, ((CMPPEndpointEntity) getEndpointEntity()).getIdleTimeSec(), TimeUnit.SECONDS));
			
			codec = new CMPPCodecChannelInitializer(((CMPPEndpointEntity) getEndpointEntity()).getVersion());

		} else {
			pipeline.addLast(GlobalConstance.IdleCheckerHandlerName, new IdleStateHandler(0, 0, 30, TimeUnit.SECONDS));
			
			codec = new CMPPCodecChannelInitializer();
		}
		pipeline.addLast("CmppServerIdleStateHandler", GlobalConstance.idleHandler);
		pipeline.addLast(codec.pipeName(), codec);
		pipeline.addLast("sessionLoginManager", new SessionLoginManager(getEndpointEntity()));
	}



	@Override
	protected AbstractSessionStateManager createSessionManager(EndpointEntity entity, Map storeMap,
															   boolean preSend) {
		return new SessionStateManager(entity, storeMap, preSend);
	}

}
