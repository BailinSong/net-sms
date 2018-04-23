package com.blueline.net.sms.manager.sgip;

import com.blueline.net.sms.manager.ClientEndpoint;


public class SgipClientEndpointEntity extends SgipEndpointEntity implements ClientEndpoint {

	@Override
	public SgipClientEndpointConnector buildConnector() {
		
		return new SgipClientEndpointConnector(this);
	}

}
