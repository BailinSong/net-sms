package com.blueline.net.sms.manager;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Lihuanghe(18852780@qq.com) 系统连接的统一管理器，负责连接服务端，或者开启监听端口，等客户端连接 。
 */
public enum EndpointManager implements EndpointManagerInterface {
	INS;
	private static final Logger logger = LoggerFactory.getLogger(EndpointManager.class);

	private Set<EndpointEntity> endpoints = Collections.synchronizedSet(new HashSet<EndpointEntity>());

	private ConcurrentHashMap<String, EndpointEntity> idMap = new ConcurrentHashMap<String, EndpointEntity>();

	private ConcurrentHashMap<String, EndpointConnector<?>> map = new ConcurrentHashMap<String, EndpointConnector<?>>();

	private Timer timer=new Timer("EndpointManager reconnect",true);

	private EndpointManager(){
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				reOpen();
			}
		},30000,30000 );
	}

	public synchronized void openEndpoint(EndpointEntity entity) {
		if (!entity.isValid())
			return;

		EndpointEntity old = idMap.get(entity.getId());
		if (old == null) {
			addEndpointEntity(entity);
		}

		EndpointConnector<?> conn = map.get(entity.getId());
		if (conn == null){
			conn = entity.buildConnector();
			map.put(entity.getId(), conn);
		}

		try {
			conn.open();
		} catch (Exception e) {
			logger.error("Open Endpoint Error. {}", entity, e);
		}
	}

	public synchronized void close(EndpointEntity entity) {
		EndpointConnector<?> conn = map.get(entity.getId());
		if (conn == null)
			return;
		try {
			conn.close();
			// 关闭所有连接，并把Connector删掉
			map.remove(entity.getId());

		} catch (Exception e) {
			logger.error("close Error", e);
		}
	}

	public EndpointConnector<?> getEndpointConnector(EndpointEntity entity) {
		return map.get(entity.getId());
	}

	public EndpointConnector<?> getEndpointConnector(String entityId) {
		return map.get(entityId);
	}

	public EndpointEntity getEndpointEntity(String id) {
		return idMap.get(id);
	}

	public void openAll() throws Exception {
		for (EndpointEntity e : endpoints)
			openEndpoint(e);
	}

	private void reOpen(){
		for (EndpointEntity e : endpoints){
			if(e instanceof ClientEndpoint){
				if(map.get(e.getId()).getConnectionNum()==0) {
					try {
						map.get(e.getId()).open();
					} catch (Exception e1) {
						logger.error("re open Error", e);
					}
				}
			}
		}
	}

	public synchronized void addEndpointEntity(EndpointEntity entity) {
		endpoints.add(entity);
		idMap.put(entity.getId(), entity);
		if (entity instanceof ServerEndpoint) {
			ServerEndpoint serverentity = (ServerEndpoint) entity;
			if(serverentity.getAllChild()!=null)
				for (EndpointEntity child : serverentity.getAllChild()) {
				endpoints.add(child);
				idMap.put(child.getId(), child);
			}
		}
	}

	public void addAllEndpointEntity(List<EndpointEntity> entities) {
		if (entities == null || entities.size() == 0)
			return;
		for (EndpointEntity entity : entities) {
			if (entity.isValid())
				addEndpointEntity(entity);
		}
	}

	public Set<EndpointEntity> allEndPointEntity() {
		return endpoints;
	}

	@Override
	public synchronized void remove(String id) {
		EndpointEntity entity = idMap.remove(id);
		if (entity != null) {
			endpoints.remove(entity);
			close(entity);
		}
	}

	public void close() {
		for (EndpointEntity en : endpoints) {
			close(en);
		}
	}

}
