package com.blueline.net.sms.codec.cmpp;


import com.blueline.net.sms.codec.AbstractTestMessageCodec;
import com.blueline.net.sms.codec.cmpp.msg.CmppCancelRequestMessage;
import com.blueline.net.sms.codec.cmpp.packet.CmppCancelRequest;
import com.blueline.net.sms.codec.cmpp.packet.CmppHead;
import com.blueline.net.sms.common.MsgId;
import io.netty.buffer.ByteBuf;
import org.junit.Assert;
import org.junit.Test;

public class TestCmppCancelRequestMessageCodec  extends AbstractTestMessageCodec<CmppCancelRequestMessage>{

	@Test
	public void testCode()
	{
		CmppCancelRequestMessage msg = new CmppCancelRequestMessage();
		msg.setMsgId(new MsgId());
		ByteBuf buf = encode(msg);
		ByteBuf copybuf = buf.copy();
		
		
		int length = buf.readableBytes();
		int expectLength = CmppCancelRequest.MSGID.getBodyLength() +  CmppHead.COMMANDID.getHeadLength();
		
		Assert.assertEquals(expectLength, length);
		Assert.assertEquals(expectLength, buf.readUnsignedInt());
		Assert.assertEquals(msg.getPacketType().getCommandId(), buf.readUnsignedInt());
		Assert.assertEquals(msg.getHeader().getSequenceId(), buf.readUnsignedInt());
		
		CmppCancelRequestMessage result = decode(copybuf);
		Assert.assertEquals(msg.getMsgId(), result.getMsgId());
	}
}
