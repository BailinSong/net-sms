package com.blueline.net.sms.codec.cmpp;

import com.blueline.net.sms.codec.AbstractTestMessageCodec;
import com.blueline.net.sms.codec.cmpp.msg.CmppSubmitRequestMessage;
import com.blueline.net.sms.common.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class TestCmppSubmitRequestMessageCodec  extends AbstractTestMessageCodec<CmppSubmitRequestMessage>{


	@Test
	public void testCodec()
	{
		CmppSubmitRequestMessage msg = new CmppSubmitRequestMessage();
		msg.setDestterminalId(new String[]{"13800138000"});
		msg.setLinkID("0000");
		msg.setMsgContent("123");
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
		
		Assert.assertEquals(msg.getHeader().getSequenceId(), result.getHeader().getSequenceId());

		Assert.assertEquals(msg.getMsgContent(), result.getMsgContent());
		Assert.assertEquals(msg.getServiceId(), result.getServiceId());
	}

	@Test
	public void testchinesecode()
	{
		
		testlongCodec(createTestReq("尊敬的客户,您好！您于2016-03-23 14:51:36通过中国移动10085销售专线订购的【一加手机高清防刮保护膜】，请点击支付http://www.10085.cn/web85/page/zyzxpay/wap_order.html?orderId=76DEF9AE1808F506FD4E6CB782E3B8E7EE875E766D3D335C 完成下单。请在60分钟内完成支付，如有疑问，请致电10085咨询，谢谢！中国移动10085"));
	}

	@Test
	public void testASCIIcode()
	{
		testlongCodec(createTestReq("12345678901AssBC56789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"));
	}
	
	
	

	@Test
	public void testPortTextSMSH()
	{
		Random rnd_ = new Random();
		CmppSubmitRequestMessage msg = createTestReq("");
		SmsPortAddressedTextMessage textMsg =new SmsPortAddressedTextMessage(new SmsPort(rnd_.nextInt() &0xffff,"")  ,new SmsPort(rnd_.nextInt()&0xffff,""),"这是一条端口文本短信");
		msg.setMsgContent(textMsg);
		CmppSubmitRequestMessage result =testWapCodec(msg);
		SmsPortAddressedTextMessage smsmsg = (SmsPortAddressedTextMessage)result.getMsg();
		Assert.assertEquals(textMsg.getDestPort_(), smsmsg.getDestPort_());
		Assert.assertEquals(textMsg.getOrigPort_(), smsmsg.getOrigPort_());
		Assert.assertEquals(textMsg.getText(), smsmsg.getText());
	}

	@Test
	public void testseptedMsg(){

		String origin = "112aaaasssss2334455@£$¥èéùìòçØøÅåΔ_ΦΓΛΩΠΨΣΘΞ^{}\\[~]|€ÆæßÉ!\"#¤%&'()*+,-./0123456789:;<=>?¡ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÑÜ§¿abcdefghijklmnopqrstuvwxyzäöñüà";
		CmppSubmitRequestMessage msg = createTestReq(origin);
		msg.setMsgContent(new SmsTextMessage(origin));
		
		CmppSubmitRequestMessage ret =  testWapCodec(msg);
		Assert.assertEquals(msg.getMsgContent(), ret.getMsgContent());
	}
	
	private CmppSubmitRequestMessage createTestReq(String content) {

		// 取时间，用来查看编码解码时间
		CmppSubmitRequestMessage msg = new CmppSubmitRequestMessage();
		msg.setDestterminalId(new String[]{"13800138000"});
		msg.setLinkID("0000");
		msg.setMsgContent(content);
		msg.setMsgid(new MsgId());
		msg.setServiceId("10086");
		msg.setSrcId("10086");
		return msg;
	}
	public CmppSubmitRequestMessage  testWapCodec(CmppSubmitRequestMessage msg)
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
	    
		CmppSubmitRequestMessage result = decode(copybuf);
		System.out.println(result);
		Assert.assertEquals(msg.getServiceId(), result.getServiceId());
		Assert.assertTrue(result.getMsg() instanceof SmsMessage);
		return result;
	}
	
	public void testlongCodec(CmppSubmitRequestMessage msg)
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
	    
		CmppSubmitRequestMessage result = decode(copybuf);
		
		System.out.println(result.getMsgContent());
		Assert.assertEquals(msg.getServiceId(), result.getServiceId());
		Assert.assertEquals(msg.getMsgContent(), result.getMsgContent());
	}
}
