package com.blueline.net.sms.manager.cmpp;

import com.blueline.net.sms.manager.EndpointEntity;
import com.blueline.net.sms.manager.ServerEndpoint;

import java.util.List;


public class CMPPServerChildEndpointEntity extends CMPPEndpointEntity implements ServerEndpoint {

	@Override
	public CMPPServerChildEndpointConnector buildConnector() {
		
		return new CMPPServerChildEndpointConnector(this);
	}

	@Override
	public void addchild(EndpointEntity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removechild(EndpointEntity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public EndpointEntity getChild(String userName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EndpointEntity> getAllChild() {
		// TODO Auto-generated method stub
		return null;
	}


}
