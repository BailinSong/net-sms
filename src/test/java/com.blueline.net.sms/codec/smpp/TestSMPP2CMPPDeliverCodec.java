package com.blueline.net.sms.codec.smpp;

import com.blueline.net.sms.codec.AbstractSMPPTestMessageCodec;
import com.blueline.net.sms.codec.cmpp.msg.CmppDeliverRequestMessage;
import com.blueline.net.sms.codec.cmpp.msg.CmppReportRequestMessage;
import com.blueline.net.sms.codec.cmpp.msg.DefaultHeader;
import com.blueline.net.sms.codec.cmpp.msg.Header;
import com.blueline.net.sms.common.MsgId;
import com.blueline.net.sms.common.SmsMessage;
import com.blueline.net.sms.manager.smpp.SMPPCodecChannelInitializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;
import org.junit.Assert;
import org.junit.Test;

public class TestSMPP2CMPPDeliverCodec extends AbstractSMPPTestMessageCodec<CmppDeliverRequestMessage> {
	protected void doinitChannel(Channel ch){
		ResourceLeakDetector.setLevel(Level.ADVANCED);
		ChannelPipeline pipeline = ch.pipeline();
		SMPPCodecChannelInitializer codec = new SMPPCodecChannelInitializer();
		pipeline.addLast("serverLog", new LoggingHandler(LogLevel.INFO));
		pipeline.addLast(codec.pipeName(), codec);
		pipeline.addLast("SMPP2CMPPCodec", new SMPP2CMPPBusinessHandler());
	}
	

	@Test
	public void testCodec() {

		CmppDeliverRequestMessage msg = createTestReq("ad3 中");

		test0(msg);
	}

	@Test
	public void testReportCodec() {
		CmppDeliverRequestMessage msg = createTestReq("k k k ");
		msg.setMsgContent((SmsMessage)null);
		CmppReportRequestMessage reportRequestMessage = new CmppReportRequestMessage();
		reportRequestMessage.setSmscSequence(0x1234L);
		reportRequestMessage.setMsgId(new MsgId());
		reportRequestMessage.setDestterminalId("13800138000");
		reportRequestMessage.setStat("9876");
		msg.setReportRequestMessage(reportRequestMessage);
		msg.setRegisteredDelivery((short) 1);
		test0(msg);
	}

	private void test0(CmppDeliverRequestMessage msg) {

		
		ByteBuf buf = encode(msg);
		ByteBuf newbuf = buf.copy();

		int length = buf.readableBytes();
		

		buf.release();
		CmppDeliverRequestMessage result = decode(newbuf);

		Assert.assertEquals(msg.getHeader().getSequenceId(), result.getHeader().getSequenceId());
		if (msg.isReport()) {
			Assert.assertEquals(msg.getReportRequestMessage().getStat(), result.getReportRequestMessage().getStat());
		} else {
			Assert.assertEquals(msg.getMsgContent(), result.getMsgContent());
		}
		Assert.assertEquals(msg.getSrcterminalId(), result.getSrcterminalId());

	}

	private CmppDeliverRequestMessage createTestReq(String content) {

		Header header = new DefaultHeader();
		// 取时间，用来查看编码解码时间

		CmppDeliverRequestMessage msg = new CmppDeliverRequestMessage(header);
		msg.setDestId("13800138000");
		msg.setLinkid("0000");
		// 70个汉字
		msg.setMsgContent(content);
		msg.setMsgId(new MsgId());
		msg.setRegisteredDelivery((short) 0);
		msg.setServiceid("10086");
		msg.setSrcterminalId("13800138000");
		msg.setSrcterminalType((short) 1);
		header.setSequenceId(System.nanoTime() & 0x7fffffff);
		return msg;
	}
	
	@Test
	public void testchinesecode()
	{
		CmppDeliverRequestMessage msg = createTestReq("1234567890123456789中01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" );
		testlongCodec(msg);
	}

	@Test
	public void testASCIIcode()
	{
		CmppDeliverRequestMessage msg = createTestReq("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
		testlongCodec(msg);
	}

	public CmppDeliverRequestMessage testWapCodec(CmppDeliverRequestMessage msg)
	{
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
	    
	    CmppDeliverRequestMessage result = decode(copybuf);
		System.out.println(result);
		Assert.assertTrue(result.getMsg() instanceof SmsMessage);
		return result;
	}
	
	public void testlongCodec(CmppDeliverRequestMessage msg)
	{
		
		
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
	    
	    CmppDeliverRequestMessage result = decode(copybuf);
		
		System.out.println(result.getMsgContent());
		Assert.assertEquals(msg.getMsgContent(), result.getMsgContent());
	}
}
