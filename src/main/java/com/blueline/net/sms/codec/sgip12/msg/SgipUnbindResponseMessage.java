/**
 * 
 */
package com.blueline.net.sms.codec.sgip12.msg;

import com.blueline.net.sms.codec.cmpp.msg.DefaultMessage;
import com.blueline.net.sms.codec.cmpp.msg.Header;
import com.blueline.net.sms.codec.sgip12.packet.SgipPacketType;

/**
 * @author huzorro(huzorro@gmail.com)
 *
 */
public class SgipUnbindResponseMessage extends DefaultMessage {
	private static final long serialVersionUID = 4638514500085975L;
	public SgipUnbindResponseMessage() {
		super(SgipPacketType.UNBINDRESPONSE);
	}
	public SgipUnbindResponseMessage(Header header) {
		super(SgipPacketType.UNBINDRESPONSE,header);
	}

}
