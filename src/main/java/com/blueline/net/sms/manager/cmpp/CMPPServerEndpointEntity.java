package com.blueline.net.sms.manager.cmpp;

import com.blueline.net.sms.manager.EndpointEntity;
import com.blueline.net.sms.manager.ServerEndpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *@author Lihuanghe(18852780@qq.com)
 */
public class CMPPServerEndpointEntity extends EndpointEntity implements ServerEndpoint {

	private Map<String,CMPPServerChildEndpointEntity> childrenEndpoint = new ConcurrentHashMap<String,CMPPServerChildEndpointEntity>() ;
	
	
	public void addchild(EndpointEntity entity)
	{
		
		childrenEndpoint.put(((CMPPServerChildEndpointEntity)entity).getUserName().trim(), (CMPPServerChildEndpointEntity)entity);
	}
	
	public void removechild(EndpointEntity entity){
		childrenEndpoint.remove(((CMPPServerChildEndpointEntity)entity).getUserName());
	}
	
	public EndpointEntity getChild(String userName)
	{
		return childrenEndpoint.get(userName);
	}
	
	public List<EndpointEntity> getAllChild()
	{
		List<EndpointEntity> list = new ArrayList<EndpointEntity>();
		for(Map.Entry<String,CMPPServerChildEndpointEntity> entry : childrenEndpoint.entrySet()){
			list.add(entry.getValue());
		}
		return list;
	}
	@Override
	public CMPPServerEndpointConnector buildConnector() {
		return new CMPPServerEndpointConnector(this);
	}

}
