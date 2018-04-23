package com.blueline.net.sms.manager.cmpp;

import com.blueline.net.sms.common.GlobalConstance;
import com.blueline.net.sms.manager.AbstractServerEndpointConnector;
import com.blueline.net.sms.manager.EndpointEntity;
import com.blueline.net.sms.session.AbstractSessionStateManager;
import com.blueline.net.sms.session.cmpp.SessionLoginManager;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *@author Lihuanghe(18852780@qq.com)
 */
public class CMPPServerEndpointConnector extends AbstractServerEndpointConnector {
	private static final Logger logger = LoggerFactory.getLogger(CMPPServerEndpointConnector.class);
	public CMPPServerEndpointConnector(EndpointEntity e) {
		super(e);
	}


	@Override
	protected void doBindHandler(ChannelPipeline pipe, EndpointEntity entity) {

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
	protected AbstractSessionStateManager createSessionManager(EndpointEntity entity, Map storeMap, boolean preSend) {
		// TODO Auto-generated method stub
		return null;
	}

}
