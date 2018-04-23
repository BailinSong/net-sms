package com.blueline.net.sms.codec.sgip12.codec;

import com.blueline.net.sms.codec.cmpp.msg.Message;
import com.blueline.net.sms.codec.cmpp.packet.PacketType;
import com.blueline.net.sms.codec.sgip12.packet.SgipPacketType;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

@Sharable
public class SgipMessageCodecAggregator extends ChannelDuplexHandler {
	private static final Logger logger = LoggerFactory.getLogger(SgipMessageCodecAggregator.class);
	private static class SgipMessageCodecAggregatorHolder{
		private final static  SgipMessageCodecAggregator instance = new SgipMessageCodecAggregator();
	}
	
	private ConcurrentHashMap<Long, MessageToMessageCodec> codecMap = new ConcurrentHashMap<Long, MessageToMessageCodec>();

	private SgipMessageCodecAggregator() {
		for (PacketType packetType : SgipPacketType.values()) {
			codecMap.put(packetType.getCommandId(), packetType.getCodec());
		}
	}
	
	public static SgipMessageCodecAggregator getInstance(){
		return SgipMessageCodecAggregatorHolder.instance;
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
