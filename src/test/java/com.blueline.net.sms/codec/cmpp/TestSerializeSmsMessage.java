package com.blueline.net.sms.codec.cmpp;

import com.blueline.net.sms.common.SmsMessage;
import com.blueline.net.sms.common.SmsPort;
import com.blueline.net.sms.common.SmsPortAddressedTextMessage;
import com.blueline.net.sms.common.SmsTextMessage;
import com.blueline.net.sms.common.util.CMPPCommonUtil;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TestSerializeSmsMessage {

	@Test
	public void test() throws IOException, ClassNotFoundException{
		List<SmsMessage> list = new ArrayList<SmsMessage>();
		list.add(CMPPCommonUtil.buildTextMessage("test"));
		list.add(new SmsPortAddressedTextMessage(SmsPort.NOKIA_CLI_LOGO,SmsPort.NOKIA_IAC,"testporttext"));

		
		  ByteArrayOutputStream bos = new ByteArrayOutputStream();     
	        ObjectOutputStream out = new ObjectOutputStream(bos);     
	        out.writeObject(list);
	        byte[] b = bos.toByteArray();
	        System.out.println(b.length);
	        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(b));     
	        List<SmsMessage> result = (List<SmsMessage>)in.readObject();

	        Assert.assertEquals(((SmsTextMessage)list.get(0)).getText(), ((SmsTextMessage)result.get(0)).getText());
	        
	        Assert.assertEquals(((SmsPortAddressedTextMessage)list.get(1)).getText(), ((SmsPortAddressedTextMessage)result.get(1)).getText());
	        Assert.assertEquals(((SmsPortAddressedTextMessage)list.get(1)).getDcs().getValue(), ((SmsPortAddressedTextMessage)result.get(1)).getDcs().getValue());
	        Assert.assertEquals(((SmsPortAddressedTextMessage)list.get(1)).getDestPort_(), ((SmsPortAddressedTextMessage)result.get(1)).getDestPort_());
	        Assert.assertEquals(((SmsPortAddressedTextMessage)list.get(1)).getOrigPort_(), ((SmsPortAddressedTextMessage)result.get(1)).getOrigPort_());
	        
	}
}
