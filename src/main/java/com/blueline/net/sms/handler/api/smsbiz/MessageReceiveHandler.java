package com.blueline.net.sms.handler.api.smsbiz;

import com.blueline.net.sms.codec.cmpp.msg.CmppDeliverRequestMessage;
import com.blueline.net.sms.codec.cmpp.msg.CmppDeliverResponseMessage;
import com.blueline.net.sms.codec.cmpp.msg.CmppSubmitRequestMessage;
import com.blueline.net.sms.codec.cmpp.msg.CmppSubmitResponseMessage;
import com.blueline.net.sms.handler.api.AbstractBusinessHandler;
import com.blueline.net.sms.manager.EventLoopGroupFactory;
import com.blueline.net.sms.manager.ExitUnlimitCirclePolicy;
import com.blueline.net.sms.session.cmpp.SessionState;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

@Sharable
public class MessageReceiveHandler extends AbstractBusinessHandler {
	private static final Logger logger = LoggerFactory.getLogger(MessageReceiveHandler.class);
	private int rate = 1;

	private AtomicLong cnt = new AtomicLong();
	private long lastNum = 0;
	private boolean inited = false;
	@Override
	public String name() {
		return "MessageReceiveHandler-smsBiz";
	}

	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt == SessionState.Connect && !inited) {
			EventLoopGroupFactory.INS.submitUnlimitCircleTask(new Callable<Boolean>(){
				
				@Override
				public Boolean call() throws Exception {
				
					long nowcnt = cnt.get();
					logger.info("Totle Receive Msg Num:{},   speed : {}/s", nowcnt, (nowcnt - lastNum)/rate);
					lastNum = nowcnt;
					return true;
				}
			}, new ExitUnlimitCirclePolicy() {
				@Override
				public boolean notOver(Future future) {
					return true;
				}
			},rate*1000);
			inited = true;
		}
		ctx.fireUserEventTriggered(evt);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		if (msg instanceof CmppDeliverRequestMessage) {
			CmppDeliverRequestMessage e = (CmppDeliverRequestMessage) msg;
			CmppDeliverResponseMessage responseMessage = new CmppDeliverResponseMessage(e.getHeader().getSequenceId());
			responseMessage.setResult(0);
			ctx.channel().writeAndFlush(responseMessage);
			cnt.incrementAndGet();

		} else if (msg instanceof CmppDeliverResponseMessage) {
			CmppDeliverResponseMessage e = (CmppDeliverResponseMessage) msg;

		} else if (msg instanceof CmppSubmitRequestMessage) {
			CmppSubmitRequestMessage e = (CmppSubmitRequestMessage) msg;
			CmppSubmitResponseMessage resp = new CmppSubmitResponseMessage(e.getHeader().getSequenceId());
			resp.setResult(0);
			ctx.channel().writeAndFlush(resp);
			cnt.incrementAndGet();
		} else if (msg instanceof CmppSubmitResponseMessage) {
			CmppSubmitResponseMessage e = (CmppSubmitResponseMessage) msg;
		} else {
			ctx.fireChannelRead(msg);
		}
	}
	
	public MessageReceiveHandler clone() throws CloneNotSupportedException {
		MessageReceiveHandler ret = (MessageReceiveHandler) super.clone();
		ret.cnt = new AtomicLong();
		ret.lastNum = 0;
		return ret;
	}

}
