package com.blueline.net.sms.codec.cmpp.wap;

import com.blueline.net.sms.LongSMSMessage;
import com.blueline.net.sms.codec.cmpp.msg.LongMessageFrame;
import com.blueline.net.sms.common.SmsMessage;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class AbstractLongMessageHandler<T> extends MessageToMessageCodec<T, T> {
	private final Logger logger = LoggerFactory.getLogger(AbstractLongMessageHandler.class);

	@Override
	protected void decode(ChannelHandlerContext ctx, T msg, List<Object> out) throws Exception {
		if (msg instanceof LongSMSMessage && needHandleLongMessage(msg)) {
			
			LongMessageFrame frame = ((LongSMSMessage)msg).generateFrame();
			String key = generateFrameKey(msg);
			try {
				SmsMessage content = LongMessageFrameHolder.INS.putAndget(key, frame);

				if (content != null) {
					resetMessageContent(msg, content);
					out.add(msg);
				} else {
					// 短信片断未接收完全，直接给网关回复resp，等待其它片断
					response(ctx, msg);
				}
			} catch (Exception ex) {
				logger.error("", ex);
				// 长短信解析失败，直接给网关回复 resp . 并丢弃这个短信
				logger.error("Decode Message Error ,msg dump :{}", ByteBufUtil.hexDump(frame.getMsgContentBytes()));
				response(ctx, msg);
			}
		} else {
			out.add(msg);
		}
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, T requestMessage, List<Object> out) throws Exception {
		if (requestMessage instanceof LongSMSMessage  && needHandleLongMessage(requestMessage)) {
			SmsMessage msgcontent = getSmsMessage(requestMessage);
			List<LongMessageFrame> frameList = LongMessageFrameHolder.INS.splitmsgcontent(msgcontent);
			boolean first = true;
			LongSMSMessage lmsg = (LongSMSMessage)requestMessage;
			for (LongMessageFrame frame : frameList) {
				
				T t = (T)lmsg.generateMessage(frame);
				out.add(t);
			}

		} else {
			out.add(requestMessage);
		}

	}

	protected abstract void response(ChannelHandlerContext ctx, T msg);

	protected abstract boolean needHandleLongMessage(T msg);

//	protected abstract LongMessageFrame generateFrame(T msg);

	protected abstract String generateFrameKey(T msg) throws Exception;

//	protected abstract T generateMessage(T t, LongMessageFrame frame) throws Exception;

	protected abstract SmsMessage getSmsMessage(T t);

	protected abstract void resetMessageContent(T t, SmsMessage content);
}
