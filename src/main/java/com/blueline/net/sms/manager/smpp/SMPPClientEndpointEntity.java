package com.blueline.net.sms.manager.smpp;

import com.blueline.net.sms.manager.ClientEndpoint;


public class SMPPClientEndpointEntity extends SMPPEndpointEntity implements ClientEndpoint {

	@Override
	public SMPPClientEndpointConnector buildConnector() {
		
		return new SMPPClientEndpointConnector(this);
	}

}
