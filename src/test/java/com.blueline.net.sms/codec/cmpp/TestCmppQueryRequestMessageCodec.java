package com.blueline.net.sms.codec.cmpp;

import com.blueline.net.sms.codec.AbstractTestMessageCodec;
import com.blueline.net.sms.codec.cmpp.msg.CmppQueryRequestMessage;
import com.blueline.net.sms.codec.cmpp.packet.CmppHead;
import com.blueline.net.sms.codec.cmpp.packet.CmppQueryRequest;
import io.netty.buffer.ByteBuf;
import org.junit.Assert;
import org.junit.Test;

public class TestCmppQueryRequestMessageCodec extends AbstractTestMessageCodec<CmppQueryRequestMessage>{

	@Test
	public void testCodec()
	{
		CmppQueryRequestMessage msg = new CmppQueryRequestMessage();
		msg.setQueryCode("sdf");

		ByteBuf buf = encode(msg);
		ByteBuf copybuf = buf.copy();
		
		int length = buf.readableBytes();
		int expectLength = CmppQueryRequest.QUERYCODE.getBodyLength() +  CmppHead.COMMANDID.getHeadLength();
		
		Assert.assertEquals(expectLength, length);
		Assert.assertEquals(expectLength, buf.readUnsignedInt());
		Assert.assertEquals(msg.getPacketType().getCommandId(), buf.readUnsignedInt());
		Assert.assertEquals(msg.getHeader().getSequenceId(), buf.readUnsignedInt());
		
	
		
		CmppQueryRequestMessage result = decode(copybuf);
		
		Assert.assertEquals(msg.getHeader().getSequenceId(), result.getHeader().getSequenceId());

		Assert.assertEquals(msg.getQueryCode(), result.getQueryCode());

	}
}
