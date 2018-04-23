package com.blueline.net.sms.codec.cmpp7F;

import com.blueline.net.sms.codec.cmpp.msg.Message;
import com.blueline.net.sms.codec.cmpp.packet.PacketType;
import com.blueline.net.sms.codec.cmpp7F.packet.Cmpp7FPacketType;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

@Sharable
public class CMPP7FMessageCodecAggregator extends ChannelDuplexHandler {
	private static final Logger logger = LoggerFactory.getLogger(CMPP7FMessageCodecAggregator.class);
	
	private static class CMPP7FMessageCodecAggregatorHolder{
		private final static  CMPP7FMessageCodecAggregator instance = new CMPP7FMessageCodecAggregator();
	}
	
	private ConcurrentHashMap<Long, MessageToMessageCodec> codecMap = new ConcurrentHashMap<Long, MessageToMessageCodec>();

	private CMPP7FMessageCodecAggregator() {
		for (PacketType packetType : Cmpp7FPacketType.values()) {
			codecMap.put(packetType.getCommandId(), packetType.getCodec());
		}
	}
	
	public static CMPP7FMessageCodecAggregator getInstance(){
		return CMPP7FMessageCodecAggregatorHolder.instance;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		long commandId = (Long) ((Message) msg).getHeader().getCommandId();
		MessageToMessageCodec codec = codecMap.get(commandId);
		codec.channelRead(ctx, msg);
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		long commandId = (Long) ((Message) msg).getHeader().getCommandId();
		MessageToMessageCodec codec = codecMap.get(commandId);
		codec.write(ctx, msg, promise);
	}
}
