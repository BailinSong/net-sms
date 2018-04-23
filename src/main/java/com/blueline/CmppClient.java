package com.blueline;

import com.blueline.net.sms.handler.api.AbstractBusinessHandler;
import com.blueline.net.sms.handler.api.BusinessHandlerInterface;
import com.blueline.net.sms.handler.api.gate.SessionConnectedHandler;
import com.blueline.net.sms.handler.api.smsbiz.MessageReceiveHandler;
import com.blueline.net.sms.manager.EndpointManager;
import com.blueline.net.sms.manager.cmpp.CMPPClientEndpointEntity;
import com.blueline.net.sms.mbean.ConnState;
import com.blueline.net.sms.session.cmpp.SessionState;
import io.netty.channel.ChannelHandlerContext;
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

public class CmppClient {

    static final Logger logger=LoggerFactory.getLogger(CmppClient.class);

    public static void main(String[] args) throws Exception {
        final EndpointManager manager = EndpointManager.INS;
        CMPPClientEndpointEntity client = new CMPPClientEndpointEntity();
        client.setId("client");
        client.setHost("127.0.0.1");
        client.setPort(7891);
        client.setChartset(Charset.forName("utf-8"));
        client.setGroupName("test");
        client.setUserName("901782");
        client.setPassword("ICP");


//		client.setMaxChannels((short)12);
        client.setVersion((short)0x20);
        client.setRetryWaitTimeSec((short)10);
        client.setUseSSL(false);
        client.setReSendFailMsg(false);

        List<BusinessHandlerInterface> clienthandlers = new ArrayList<BusinessHandlerInterface>();
//        clienthandlers.add( new MessageReceiveHandler());
        clienthandlers.add(new SessionConnectedHandler(new AtomicInteger(30000000)));
//        clienthandlers.add(new AbstractBusinessHandler() {
//            @Override
//            public String name() {
//                return "Auto reconnect-smsBiz";
//            }
//
//            @Override
//            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//                if (evt == SessionState.DisConnect){
//                    ctx.
//                }
//            }
//        });
        client.setBusinessHandlerSet(clienthandlers);

        manager.addEndpointEntity(client);

        manager.openAll();
        MBeanServer mserver = ManagementFactory.getPlatformMBeanServer();

        ObjectName stat = new ObjectName("com.blueline.net.sms:name=ConnState");
        mserver.registerMBean(new ConnState(), stat);
        logger.info("Cmpp client start...");
        LockSupport.park();
    }
}
