package com.blueline.net.sms.session.sgip;

import com.blueline.net.sms.bean.VersionObject;
import com.blueline.net.sms.codec.cmpp.msg.Message;
import com.blueline.net.sms.manager.EndpointEntity;
import com.blueline.net.sms.session.AbstractSessionStateManager;

import java.util.Map;

public class SgipSessionStateManager extends AbstractSessionStateManager<Long, Message> {

	public SgipSessionStateManager(EndpointEntity entity, Map<Long, VersionObject<Message>> storeMap, boolean preSend) {
		super(entity, storeMap, preSend);
	}

	@Override
	protected Long getSequenceId(Message msg) {
		
		return msg.getHeader().getSequenceId();
	}

	@Override
	protected boolean needSendAgainByResponse(Message req, Message res) {
		return false;
	}

}
