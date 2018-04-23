package com.blueline.net.sms.manager.cmpp;

import com.blueline.net.sms.manager.ClientEndpoint;

/**
 *@author Lihuanghe(18852780@qq.com)
 */
public class CMPPClientEndpointEntity extends CMPPEndpointEntity implements ClientEndpoint {

	@Override
	public CMPPClientEndpointConnector buildConnector() {
		return new CMPPClientEndpointConnector(this);
	}
}
