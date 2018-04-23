package com.blueline.net.sms.manager.sgip;

import com.blueline.net.sms.manager.EndpointEntity;
import com.blueline.net.sms.manager.ServerEndpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SgipServerEndpointEntity extends EndpointEntity implements ServerEndpoint {

	
	private Map<String,SgipServerChildEndpointEntity> childrenEndpoint = new ConcurrentHashMap<String,SgipServerChildEndpointEntity>() ;
	
	
	public void addchild(EndpointEntity entity)
	{
		
		childrenEndpoint.put(((SgipServerChildEndpointEntity)entity).getLoginName().trim(), (SgipServerChildEndpointEntity)entity);
	}
	
	public void removechild(EndpointEntity entity){
		childrenEndpoint.remove(((SgipServerChildEndpointEntity)entity).getLoginName().trim());
	}
	
	public EndpointEntity getChild(String userName)
	{
		return childrenEndpoint.get(userName);
	}
	
	public List<EndpointEntity> getAllChild()
	{
		List<EndpointEntity> list = new ArrayList<EndpointEntity>();
		for(Map.Entry<String,SgipServerChildEndpointEntity> entry : childrenEndpoint.entrySet()){
			list.add(entry.getValue());
		}
		return list;
	}
	@Override
	public SgipServerEndpointConnector buildConnector() {
		return new SgipServerEndpointConnector(this);
	}

}
