package com.blueline.net.sms.codec.cmpp.msg;

import com.blueline.net.sms.BaseMessage;
import com.blueline.net.sms.codec.cmpp.packet.PacketType;

import java.io.Serializable;

/**
 * 
 * @author huzorro(huzorro@gmail.com)
 *
 */
public interface Message extends BaseMessage  {
	public void setPacketType(PacketType packetType);
	public PacketType getPacketType();
	public void setTimestamp(long milliseconds);
	public long getTimestamp();
	public void setLifeTime(long lifeTime);
	public long getLifeTime();
    public void setHeader(Header head);
    public Header getHeader();  
    public void setBodyBuffer(byte[] buffer);
    public byte[] getBodyBuffer();
    public Serializable getAttachment();
    public void setAttachment(Serializable attachment);
}
