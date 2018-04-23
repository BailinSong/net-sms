package com.blueline.net.sms.session.smpp;

import com.blueline.net.sms.bean.VersionObject;
import com.blueline.net.sms.codec.smpp.msg.Pdu;
import com.blueline.net.sms.manager.EndpointEntity;
import com.blueline.net.sms.session.AbstractSessionStateManager;

import java.util.Map;

public class SMPPSessionStateManager extends AbstractSessionStateManager<Integer, Pdu> {

	public SMPPSessionStateManager(EndpointEntity entity, Map<Integer, VersionObject<Pdu>> storeMap, boolean preSend) {
		super(entity, storeMap, preSend);
	}

	@Override
	protected Integer getSequenceId(Pdu msg) {
		
		return msg.getSequenceNumber();
	}

	@Override
	protected boolean needSendAgainByResponse(Pdu req, Pdu res) {
		return false;
	}

}
