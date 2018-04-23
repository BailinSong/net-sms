package com.blueline.net.sms.manager;

import java.util.List;

/**
 *@author Lihuanghe(18852780@qq.com)
 */
public interface ServerEndpoint {
	public void addchild(EndpointEntity entity);
	public void removechild(EndpointEntity entity);
	public EndpointEntity getChild(String userName);
	public List<EndpointEntity> getAllChild();
}
