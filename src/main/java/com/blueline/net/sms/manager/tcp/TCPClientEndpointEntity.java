package com.blueline.net.sms.manager.tcp;

import com.blueline.net.sms.manager.ClientEndpoint;
import com.blueline.net.sms.manager.EndpointEntity;

public class TCPClientEndpointEntity extends EndpointEntity implements ClientEndpoint {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6580678038168372304L;

	public TCPClientEndpointEntity(String host, int port) {
		setHost(host);
		setPort(port);
	}

	@Override
	public TCPClientEndpointConnector buildConnector() {

		return new TCPClientEndpointConnector(this);
	}

}
