package com.blueline.net.sms.codec.cmpp;

import com.blueline.net.sms.codec.cmpp.msg.LongMessageFrame;
import com.blueline.net.sms.codec.cmpp.wap.LongMessageFrameHolder;
import com.blueline.net.sms.common.SmsException;
import com.blueline.net.sms.common.SmsTextMessage;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TestLongMessageFrameHolder {
	String s = "尊敬的客户,您好！您于2016-03-23 14:51:36通过中国移动10085销售专线订购的【一加手机高清防刮保护膜】，请点击支付http://www.10085.cn/web85/page/zyzxpay/wap_order.html?orderId=76DEF9AE1808F506FD4E6CB782E3B8E7EE875E766D3D335C 完成下单。请在60分钟内完成支付，如有疑问，请";
	
	@Test
	public void test() throws SmsException {
		
		List<LongMessageFrame> l = LongMessageFrameHolder.INS.splitmsgcontent(new SmsTextMessage(s));
		
		for(LongMessageFrame frame : l){
			String stmp = LongMessageFrameHolder.INS.getPartTextMsg(frame);
			Assert.assertEquals(67, stmp.length());
			System.out.println(stmp);
		}
	}
}
