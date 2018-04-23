package com.blueline.net.sms.connect.manager.cmpp;

import com.blueline.net.sms.handler.api.BusinessHandlerInterface;
import com.blueline.net.sms.handler.api.smsbiz.MessageReceiveHandler;
import com.blueline.net.sms.manager.EndpointManager;
import com.blueline.net.sms.manager.cmpp.CMPPClientEndpointEntity;
import com.blueline.net.sms.mbean.ConnState;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
/**
 *经测试，35个连接，每个连接每200/s条消息
 *lenovoX250能承担7000/s消息编码解析无压力。
 *10000/s的消息服务不稳定，开个网页，或者打开其它程序导致系统抖动，会有大量消息延迟 (超过500ms)
 *
 *低负载时消息编码解码可控制在10ms以内。
 *
 */


public class ClientTestCMPPEndPoint {
	private static final Logger logger = LoggerFactory.getLogger(ClientTestCMPPEndPoint.class);

	@Test
	public void testCMPPEndpoint() throws Exception {
	
		final EndpointManager manager = EndpointManager.INS;
	
	
		CMPPClientEndpointEntity client = new CMPPClientEndpointEntity();
		client.setId("client");
		client.setHost("127.0.0.1");
		client.setPort(7891);
		client.setChartset(Charset.forName("utf-8"));
		client.setGroupName("test");
		client.setUserName("901782");
		client.setPassword("ICP");


		client.setMaxChannels((short)12);
		client.setWindows((short)16);
		client.setVersion((short)0x20);
		client.setRetryWaitTimeSec((short)10);
		client.setUseSSL(false);
		client.setReSendFailMsg(false);

		List<BusinessHandlerInterface> clienthandlers = new ArrayList<BusinessHandlerInterface>();
		clienthandlers.add( new MessageReceiveHandler());
		client.setBusinessHandlerSet(clienthandlers);
		manager.addEndpointEntity(client);
		
		manager.openAll();
		//LockSupport.park();
		 MBeanServer mserver = ManagementFactory.getPlatformMBeanServer();  

        ObjectName stat = new ObjectName("com.zx.sms:name=ConnState");
        mserver.registerMBean(new ConnState(), stat);
        System.out.println("start.....");
        
		Thread.sleep(300000);
		EndpointManager.INS.close();
	}
}
