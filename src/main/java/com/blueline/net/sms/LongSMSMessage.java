package com.blueline.net.sms;

import com.blueline.net.sms.codec.cmpp.msg.LongMessageFrame;

public interface LongSMSMessage<T> {
	public LongMessageFrame generateFrame();
	public T generateMessage(LongMessageFrame frame) throws Exception;
}
