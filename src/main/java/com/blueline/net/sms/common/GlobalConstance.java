package com.blueline.net.sms.common;

import com.blueline.lang.Property;
import io.netty.util.AttributeKey;

import java.nio.charset.Charset;

import com.blueline.net.sms.handler.cmpp.BlackHoleHandler;
import com.blueline.net.sms.handler.cmpp.CmppActiveTestRequestMessageHandler;
import com.blueline.net.sms.handler.cmpp.CmppActiveTestResponseMessageHandler;
import com.blueline.net.sms.handler.cmpp.CmppServerIdleStateHandler;
import com.blueline.net.sms.handler.cmpp.CmppTerminateRequestMessageHandler;
import com.blueline.net.sms.handler.cmpp.CmppTerminateResponseMessageHandler;
import com.blueline.net.sms.handler.sgip.SgipServerIdleStateHandler;
import com.blueline.net.sms.handler.smpp.SmppServerIdleStateHandler;
import com.blueline.net.sms.session.cmpp.SessionState;

public interface GlobalConstance {
	public final static int MaxMsgLength = Property.get("maxMsgLength",140);
	public final static String emptyString = "";
	public final static byte[] emptyBytes= new byte[0];
	public final static String[] emptyStringArray= new String[0];
  
    public static final Charset defaultTransportCharset = Charset.forName(Property.get("charset","GBK"));

    public final static  CmppActiveTestRequestMessageHandler activeTestHandler =  new CmppActiveTestRequestMessageHandler();
    public final static  CmppActiveTestResponseMessageHandler activeTestRespHandler =  new CmppActiveTestResponseMessageHandler();
    public final static  CmppTerminateRequestMessageHandler terminateHandler =  new CmppTerminateRequestMessageHandler();
    public final static  CmppTerminateResponseMessageHandler terminateRespHandler = new CmppTerminateResponseMessageHandler();
    public final static  CmppServerIdleStateHandler idleHandler = new CmppServerIdleStateHandler();
    public final static  SmppServerIdleStateHandler smppidleHandler = new SmppServerIdleStateHandler();
    public final static  SgipServerIdleStateHandler sgipidleHandler = new SgipServerIdleStateHandler();
    public final static AttributeKey<SessionState> attributeKey = AttributeKey.newInstance(SessionState.Connect.name());
    public final static BlackHoleHandler blackhole = new BlackHoleHandler();
    public final static String IdleCheckerHandlerName = "IdleStateHandler";
    public final static boolean isSupportLongMsg = Property.get("supportLongMsg",true);
    
    public final static String loggerNamePrefix = "entity.%s";
    public final static String codecName = "codecName";
}
