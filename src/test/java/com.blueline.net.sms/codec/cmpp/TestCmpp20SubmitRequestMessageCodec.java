package com.blueline.net.sms.codec.cmpp;

import com.blueline.net.sms.codec.AbstractTestMessageCodec;
import com.blueline.net.sms.codec.cmpp.msg.CmppSubmitRequestMessage;
import com.blueline.net.sms.common.MsgId;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Assert;
import org.junit.Test;

public class TestCmpp20SubmitRequestMessageCodec extends AbstractTestMessageCodec<CmppSubmitRequestMessage> {
	@Override
	protected int getVersion(){
		return 0x20;
	}


	@Test
	public void testCodec()
	{
		CmppSubmitRequestMessage msg = new CmppSubmitRequestMessage();
		msg.setDestterminalId(new String[]{"13800138000"});
		msg.setLinkID("0000");
		msg.setMsgContent("abde");
		System.out.println(msg.getMsgContent().length());
		msg.setMsgid(new MsgId());
		msg.setServiceId("10086");
		msg.setSrcId("10086");
		
		
		ByteBuf buf = encode(msg);
		
		ByteBuf copybuf = buf.copy();
		
		int length = buf.readableBytes();
		
		Assert.assertEquals(length, buf.readUnsignedInt());
		Assert.assertEquals(msg.getPacketType().getCommandId(), buf.readUnsignedInt());
		Assert.assertEquals(msg.getHeader().getSequenceId(), buf.readUnsignedInt());
		
	
		
		CmppSubmitRequestMessage result = decode(copybuf);
		System.out.println(result);
		Assert.assertEquals(msg.getHeader().getSequenceId(), result.getHeader().getSequenceId());

		Assert.assertEquals(msg.getMsgContent(), result.getMsgContent());
		Assert.assertEquals(msg.getServiceId(), result.getServiceId());
	}

	@Test
	public void testchinesecode()
	{
		testlongCodec("1234567890123456789中01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" );
	}

	@Test
	public void testASCIIcode()
	{
		testlongCodec("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
	}
	
	
	public void testlongCodec(String content)
	{
		CmppSubmitRequestMessage msg = new CmppSubmitRequestMessage();
		msg.setDestterminalId(new String[]{"13800138000"});
		msg.setLinkID("0000");
		msg.setMsgContent(content);
		msg.setMsgid(new MsgId());
		msg.setServiceId("10086");
		msg.setSrcId("10086");
		msg.setSupportLongMsg(true);
		channel().writeOutbound(msg);
		ByteBuf buf =(ByteBuf)channel().readOutbound();
		ByteBuf copybuf = Unpooled.buffer();
	    while(buf!=null){
			
			
	    	copybuf.writeBytes(buf.copy());
			int length = buf.readableBytes();
			
			Assert.assertEquals(length, buf.readUnsignedInt());
			Assert.assertEquals(msg.getPacketType().getCommandId(), buf.readUnsignedInt());
			

			buf =(ByteBuf)channel().readOutbound();
	    }
	    
		CmppSubmitRequestMessage result = decode(copybuf);
		
		System.out.println(result.getMsgContent());
		Assert.assertEquals(msg.getServiceId(), result.getServiceId());
		Assert.assertEquals(msg.getMsgContent(), result.getMsgContent());
	}
}
