package com.blueline.net.sms.codec.smpp;

import com.blueline.net.sms.codec.cmpp.msg.*;
import com.blueline.net.sms.codec.smpp.msg.*;
import com.blueline.net.sms.common.SmsDcs;
import com.blueline.net.sms.handler.api.AbstractBusinessHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

@Sharable
public class SMPP2CMPPBusinessHandler extends AbstractBusinessHandler {

	private static SMPP2CMPPCodec codec = new SMPP2CMPPCodec();
	@Override
	public String name() {
		return "SMPP2CMPPBusinessHandler";
	}
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	codec.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    	codec.write(ctx, msg, promise);
    }
    
    private static class SMPP2CMPPCodec extends MessageToMessageCodec<Pdu, DefaultMessage> {

    	@Override
    	protected void encode(ChannelHandlerContext ctx, DefaultMessage msg, List<Object> out) throws Exception {
    		if(msg instanceof CmppDeliverRequestMessage){
    			BaseSm pdu = null;
    			CmppDeliverRequestMessage deliver = (CmppDeliverRequestMessage)msg;
    			if(deliver.isReport()){
    				DeliverSmReceipt pdur = new DeliverSmReceipt();
    				CmppReportRequestMessage report = deliver.getReportRequestMessage();
    				pdur.setId(report.getMsgId().toString());
    				pdur.setSub("000");
    				pdur.setDlvrd("000");
    				pdur.setSubmit_date(report.getSubmitTime());
    				pdur.setDone_date(report.getDoneTime());
    				pdur.setStat(report.getStat());
    				pdur.setErr("000");
    				pdur.setText("ZYZX");
    				pdur.setEsmClass((byte)0x04);
    				pdu = pdur;
    			}else{
    				pdu = new DeliverSm();
    			}
    				pdu.setDataCoding(deliver.getMsgfmt().getValue());
    		        pdu.setCommandStatus(0);
    		        pdu.setSequenceNumber((int)deliver.getHeader().getSequenceId());
    		        pdu.setServiceType("");
    		        pdu.setSourceAddress(new Address((byte)0,(byte)0,deliver.getSrcterminalId()));
    		        pdu.setDestAddress(new Address((byte)0,(byte)0,deliver.getDestId()));
    		        pdu.setSmsMsg(deliver.getMsg());
    		        out.add(pdu);
    			
    		}else if(msg instanceof CmppDeliverResponseMessage){
    			
    			DeliverSmResp resp = new DeliverSmResp();
    		    resp.setSequenceNumber((int)msg.getHeader().getSequenceId());
    		    resp.setCommandStatus((int)((CmppDeliverResponseMessage)msg).getResult());
    		    out.add(resp);
    			
    		}else if(msg instanceof CmppSubmitRequestMessage){
    			CmppSubmitRequestMessage submit = (CmppSubmitRequestMessage)msg;
    			SubmitSm pdu = new SubmitSm();
    	        pdu.setCommandStatus(0);
    	        pdu.setSequenceNumber((int)submit.getHeader().getSequenceId());
    	        pdu.setServiceType("");
    	        pdu.setSourceAddress(new Address((byte)0,(byte)0,submit.getSrcId()));
    	        pdu.setDestAddress(new Address((byte)0,(byte)0,submit.getDestterminalId()[0]));
    	        pdu.setSmsMsg(submit.getMsg());
    	        out.add(pdu);
    			
    			
    		}else if(msg instanceof CmppSubmitResponseMessage){
    			SubmitSmResp resp = new SubmitSmResp();
    		    resp.setSequenceNumber((int)msg.getHeader().getSequenceId());
    		    resp.setCommandStatus((int)((CmppSubmitResponseMessage)msg).getResult());
    		    out.add(resp);
    		}
    	}
    	

    	@Override
    	protected void decode(ChannelHandlerContext ctx, Pdu msg, List<Object> out) throws Exception {
    		if(msg instanceof DeliverSm){
    			DeliverSm deli = (DeliverSm)msg;
    			CmppDeliverRequestMessage deliver = new CmppDeliverRequestMessage();
    			deliver.getHeader().setSequenceId(deli.getSequenceNumber());
    			deliver.setMsgContent(deli.getSmsMsg());
    			deliver.setSrcterminalId(deli.getSourceAddress().getAddress());
    			deliver.setDestId(deli.getDestAddress().getAddress());
    			deliver.setMsgfmt(new SmsDcs(deli.getDataCoding()));
    			deliver.setTppid(deli.getProtocolId());
    			deliver.setTpudhi(deli.getEsmClass());
    			
    			if(msg instanceof DeliverSmReceipt){
    				DeliverSmReceipt rece = (DeliverSmReceipt)msg;
    				CmppReportRequestMessage report = new CmppReportRequestMessage();
    				report.setDoneTime(rece.getDone_date());
    				report.setSubmitTime(rece.getSubmit_date());
    				report.setStat(rece.getStat());
    				deliver.setReportRequestMessage(report);
    			}
    			out.add(deliver);
    		}else if(msg instanceof DeliverSmResp){
    			DeliverSmResp deliresp = (DeliverSmResp)msg;
    			CmppDeliverResponseMessage delir = new CmppDeliverResponseMessage(msg.getSequenceNumber());
    			delir.setResult(deliresp.getCommandStatus());
    			out.add(delir);
    		}else if(msg instanceof SubmitSm){
    			SubmitSm sm = (SubmitSm)msg;
    			CmppSubmitRequestMessage submit = new CmppSubmitRequestMessage();
    			submit.getHeader().setSequenceId(sm.getSequenceNumber());
    			submit.setDestterminalId(sm.getDestAddress().getAddress());
    			submit.setSrcId(sm.getSourceAddress().getAddress());
    			submit.setMsgContent(sm.getSmsMsg());
    			submit.setMsgfmt(new SmsDcs(sm.getDataCoding()));
    			submit.setTppid(sm.getProtocolId());
    			submit.setTpudhi((short)((sm.getEsmClass()>>6)&0x01));
    			out.add(submit);
    			
    		}else if(msg instanceof SubmitSmResp){
    			SubmitSmResp submitresp = (SubmitSmResp)msg;
    			CmppSubmitResponseMessage mtresp = new CmppSubmitResponseMessage(msg.getSequenceNumber());
    			mtresp.setResult(submitresp.getCommandStatus());
    			out.add(mtresp);
    		}else{
    			out.add(msg);
    		}
    	}

    }
}
