package com.blueline;

import com.blueline.net.sms.handler.api.BusinessHandlerInterface;
import com.blueline.net.sms.handler.api.gate.SessionConnectedHandler;
import com.blueline.net.sms.handler.api.smsbiz.MessageReceiveHandler;
import com.blueline.net.sms.manager.EndpointManager;
import com.blueline.net.sms.manager.cmpp.CMPPServerChildEndpointEntity;
import com.blueline.net.sms.manager.cmpp.CMPPServerEndpointEntity;
import com.blueline.net.sms.mbean.ConnState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

public class CmppServer {

    static final Logger logger=LoggerFactory.getLogger(CmppServer.class);

    public static void main(String[] args) throws Exception {
        final EndpointManager manager = EndpointManager.INS;

        CMPPServerEndpointEntity server = new CMPPServerEndpointEntity();
        server.setId("server");
        server.setHost("127.0.0.1");
        server.setPort(7891);
        server.setValid(true);
        //使用ssl加密数据流
        server.setUseSSL(false);

        CMPPServerChildEndpointEntity child = new CMPPServerChildEndpointEntity();
        child.setId("child");
        child.setChartset(Charset.forName("utf-8"));
        child.setGroupName("test");
        child.setUserName("901782");
        child.setPassword("ICP");

        child.setValid(true);
        child.setVersion((short)0x20);

//		child.setMaxChannels((short)20);
        child.setRetryWaitTimeSec((short)30);
//		child.setMaxRetryCnt((short)3);
        child.setReSendFailMsg(false);
//		child.setWriteLimit(200);
//		child.setReadLimit(200);
        List<BusinessHandlerInterface> serverhandlers = new ArrayList<BusinessHandlerInterface>();
//        serverhandlers.add(new SessionConnectedHandler(new AtomicInteger(300000)));
        serverhandlers.add(new MessageReceiveHandler());
        child.setBusinessHandlerSet(serverhandlers);
        server.addchild(child);

        manager.addEndpointEntity(server);

        manager.openAll();
        MBeanServer mserver = ManagementFactory.getPlatformMBeanServer();

        ObjectName stat = new ObjectName("com.blueline.net.sms:name=ConnState");
        mserver.registerMBean(new ConnState(), stat);
        logger.info("Cmpp server start...");
        LockSupport.park();
    }
}
