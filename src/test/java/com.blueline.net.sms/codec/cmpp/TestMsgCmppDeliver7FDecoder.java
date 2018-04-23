package com.blueline.net.sms.codec.cmpp;

import com.blueline.net.sms.codec.AbstractTestMessageCodec;
import com.blueline.net.sms.codec.cmpp.msg.CmppDeliverRequestMessage;
import io.netty.buffer.ByteBuf;
import org.junit.Assert;
import org.junit.Test;

public class TestMsgCmppDeliver7FDecoder extends AbstractTestMessageCodec<CmppDeliverRequestMessage> {
	@Override
	protected int getVersion() {
		return 0x7F;
	}

	
	
	@Test
	public void testCodec()
	{
		CmppDeliverRequestMessage msg = new CmppDeliverRequestMessage();
	
		msg.setAttachment("adfad");
		msg.setMsgContent("12341");
		ByteBuf buf = encode(msg);
		ByteBuf copybuf = buf.copy();
		// packageLength
		buf.readUnsignedInt();
		
		Assert.assertEquals(msg.getPacketType().getCommandId(), buf.readUnsignedInt());
		Assert.assertEquals(msg.getHeader().getSequenceId(), buf.readUnsignedInt());
	
		
		CmppDeliverRequestMessage result = decode(copybuf);
		

		Assert.assertEquals(msg.getAttachment(), result.getAttachment());
		Assert.assertEquals(msg.getMsgContent(), result.getMsgContent());
	}
	@Test
	public void testCodecNullAttach()
	{
		CmppDeliverRequestMessage msg = new CmppDeliverRequestMessage();
	
	
		msg.setMsgContent("12341");
		ByteBuf buf = encode(msg);
		ByteBuf copybuf = buf.copy();
		// packageLength
		buf.readUnsignedInt();
		
		Assert.assertEquals(msg.getPacketType().getCommandId(), buf.readUnsignedInt());
		Assert.assertEquals(msg.getHeader().getSequenceId(), buf.readUnsignedInt());
	
		
		CmppDeliverRequestMessage result = decode(copybuf);
		

		Assert.assertEquals(msg.getAttachment(), result.getAttachment());
		Assert.assertEquals(msg.getMsgContent(), result.getMsgContent());
	}
	
}
