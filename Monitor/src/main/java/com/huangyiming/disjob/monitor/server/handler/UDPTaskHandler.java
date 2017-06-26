package com.huangyiming.disjob.monitor.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.huangyiming.disjob.common.util.DateUtil;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.common.util.SerializeUtil;
import com.huangyiming.disjob.monitor.db.service.DBJobBasicInfoService;
import com.huangyiming.disjob.monitor.db.service.DBJobExeProgressService;
import com.huangyiming.disjob.monitor.pojo.UdpMessage;
import com.huangyiming.disjob.monitor.db.domain.DBJobBasicInfo;
import com.huangyiming.disjob.monitor.db.domain.DBJobExeProgress;

/**
 * UDP业务处理类,线程池中线程处理
 * @author Disjob
 *
 */
 public class UDPTaskHandler implements Runnable {

	private DatagramPacket msg;
	private ChannelHandlerContext ctx;
	
	private DBJobExeProgressService service;
	
	private   DBJobBasicInfoService  jobBasicInfoService;
	
	
	public DatagramPacket getMsg() {
		return msg;
	}
	public void setMsg(DatagramPacket msg) {
		this.msg = msg;
	}
	public ChannelHandlerContext getCtx() {
		return ctx;
	}
	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}
	
	
	public UDPTaskHandler(ChannelHandlerContext ctx,DatagramPacket msg,DBJobExeProgressService service,DBJobBasicInfoService  jobBasicInfoService){
		this.msg = msg;
		this.ctx = ctx;
		this.service =  service;
		this.jobBasicInfoService = jobBasicInfoService;
	}
	 
	@Override
	public void run() {
 		 String req = msg.content().toString(CharsetUtil.UTF_8);
 		 LoggerUtil.debug("UDP recieve message is "+req);
 		 UdpMessage response = new UdpMessage();
		 DBJobExeProgress info = new DBJobExeProgress();
  		 try {
			response = (UdpMessage) SerializeUtil.deserialize(req.getBytes(), UdpMessage.class);
 			info.setUuid(StringUtils.isNoneEmpty(response.getRequestId()) ?response.getRequestId():"123456" );
			
 			info.setDataTime(new Date().toString());
			info.setType(StringUtils.isNoneEmpty(response.getType())? Integer.parseInt(response.getType()):-1); 
			info.setDataTime(response.getTime());
			info.setContent(response.getContent());
			if(info.getType() == 1){
  				service.create(info);
			}else if(info.getType() == 0){
				//LoggerUtil.debug("type=0,update"+response.getRep().getRequestId());
		  
				char currentStatus='1';
				if(response.getRep().getCode() !=0){
					currentStatus = '0';
 				}
				DBJobBasicInfo basicInfo = new  DBJobBasicInfo(response.getRep().getRequestId(), DateUtil.utc2Local(response.getRep().getJobBegingTime(),DateUtil.patten), DateUtil.utc2Local(response.getRep().getJobCompleteTime(),DateUtil.patten), currentStatus, response.getRep().getException(), ""+response.getRep().getCode(),response.getRep().getJobRecvTime(),response.getRep().getSharingRequestId());
				//DBJobBasicInfo basicInfo = new  DBJobBasicInfo(response.getRep().getRequestId(), response.getRep().getJobBegingTime(), response.getRep().getJobCompleteTime(), response.getRep().getProcessTime(), response.getRep().getException());
				jobBasicInfoService.update(basicInfo);
			}
 			
			//LoggerUtil.debug("success save DBJobExeProgress:"+response.toString());
		} catch (IOException e) {
			LoggerUtil.error("create DBJobExeProgress error,DBJobExeProgress is "+info + " , req is "+ req,e);
		}

	}
	
	

}
