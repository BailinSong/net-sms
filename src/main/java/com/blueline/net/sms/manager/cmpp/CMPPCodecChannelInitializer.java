package com.blueline.net.sms.manager.cmpp;

import com.blueline.net.sms.codec.cmpp.CMPPMessageCodecAggregator;
import com.blueline.net.sms.codec.cmpp.CmppHeaderCodec;
import com.blueline.net.sms.codec.cmpp20.CMPP20MessageCodecAggregator;
import com.blueline.net.sms.codec.cmpp7F.CMPP7FMessageCodecAggregator;
import com.blueline.net.sms.common.GlobalConstance;
import com.blueline.net.sms.common.NotSupportedException;
import com.blueline.net.sms.handler.cmpp.CMPPDeliverLongMessageHandler;
import com.blueline.net.sms.handler.cmpp.CMPPSubmitLongMessageHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lihuanghe(18852780@qq.com)
 *         初始化pipeline，解码是在当前handler前插入，不是使用pipeLine.addLast方法。注意使用
 **/
public class CMPPCodecChannelInitializer extends ChannelInitializer<Channel> {
	private static final Logger logger = LoggerFactory.getLogger(CMPPCodecChannelInitializer.class);
	private int version;
	
	private final static int defaultVersion = 0x30;

	public CMPPCodecChannelInitializer() {
			this.version = defaultVersion;
	}
	
	public CMPPCodecChannelInitializer(int version) {
			this.version = version;
	}

	public static String pipeName() {
		return "cmppCodec";
	}

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		/*
		 * 消息总长度(含消息头及消息体) 最大消消息长度要从配置里取 处理粘包，断包
		 */
		pipeline.addBefore(pipeName(), "FrameDecoder", new LengthFieldBasedFrameDecoder(4 * 1024 , 0, 4, -4, 0, true));

		pipeline.addBefore(pipeName(), "CmppHeaderCodec", new CmppHeaderCodec());

		pipeline.addBefore(pipeName(), GlobalConstance.codecName, getCodecHandler(version));
		
		//处理长短信
		pipeline.addBefore(pipeName(), "CMPPDeliverLongMessageHandler", new CMPPDeliverLongMessageHandler());
		pipeline.addBefore(pipeName(),"CMPPSubmitLongMessageHandler",  new CMPPSubmitLongMessageHandler());
	}

	public static ChannelDuplexHandler getCodecHandler(int version) throws Exception {
		if (version == 0x30L) {
			return CMPPMessageCodecAggregator.getInstance();
		} else if (version == 0x20L) {
			return CMPP20MessageCodecAggregator.getInstance();
		} else if (version == 0x7FL) {
			return CMPP7FMessageCodecAggregator.getInstance();
		}
		logger.error("not supported protocol version: {}", version);
		throw new NotSupportedException("not supported protocol version.");
	}

}
