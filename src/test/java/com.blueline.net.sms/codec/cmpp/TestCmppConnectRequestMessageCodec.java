package com.blueline.net.sms.codec.cmpp;

import com.blueline.net.sms.codec.AbstractTestMessageCodec;
import com.blueline.net.sms.codec.cmpp.msg.CmppConnectRequestMessage;
import com.blueline.net.sms.codec.cmpp.packet.CmppConnectRequest;
import com.blueline.net.sms.codec.cmpp.packet.CmppHead;
import io.netty.buffer.ByteBuf;
import org.junit.Assert;
import org.junit.Test;


public class TestCmppConnectRequestMessageCodec extends AbstractTestMessageCodec<CmppConnectRequestMessage>{
	@Test
	public void testCode()
	{
		CmppConnectRequestMessage msg = new CmppConnectRequestMessage();
		msg.setSourceAddr("106581");
		//长度为16
		msg.setAuthenticatorSource("passwordpassword".getBytes());
		
		ByteBuf buf = encode(msg);
		ByteBuf copybuf = buf.copy();
		
		int length = buf.readableBytes();
		int expectLength = CmppConnectRequest.AUTHENTICATORSOURCE.getBodyLength() +  CmppHead.COMMANDID.getHeadLength();
		
		Assert.assertEquals(expectLength, length);
		Assert.assertEquals(expectLength, buf.readUnsignedInt());
		Assert.assertEquals(msg.getPacketType().getCommandId(), buf.readUnsignedInt());
		Assert.assertEquals(msg.getHeader().getSequenceId(), buf.readUnsignedInt());
		
		CmppConnectRequestMessage result = decode(copybuf);
		
		Assert.assertEquals(msg.getHeader().getSequenceId(), result.getHeader().getSequenceId());
		Assert.assertEquals(msg.getSourceAddr(),result.getSourceAddr());
		Assert.assertArrayEquals(msg.getAuthenticatorSource(), result.getAuthenticatorSource());
	}
}
