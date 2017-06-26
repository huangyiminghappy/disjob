package com.huangyiming.disjob.console.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huangyiming.disjob.console.service.JobInfoService;
import com.huangyiming.disjob.monitor.db.mappers.DBJobBasicInfoMapper;
import com.huangyiming.disjob.register.job.DisJobServerService;
import com.huangyiming.disjob.register.rpc.SubscribeService;
import com.huangyiming.disjob.rpc.client.HURL;
import com.huangyiming.disjob.rpc.client.proxy.ChannelBootStrap;
import com.huangyiming.disjob.rpc.codec.DisJobKillTaskResponse;
import com.huangyiming.disjob.rpc.codec.DisJobResponse;
import com.huangyiming.disjob.rpc.codec.DisJobRestartTaskResponse;
import com.huangyiming.disjob.rpc.codec.Header;
import com.huangyiming.disjob.rpc.codec.RpcRequest;
import com.huangyiming.disjob.rpc.codec.RpcRequestData;
import com.huangyiming.disjob.rpc.utils.PhpTaskCmd;

/**
 * <pre>
 * 
 *  File: ScheduleJobServiceImpl.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  任务信息处理服务实现
 * 
 *  Revision History
 *
 *  Date：		2016年5月19日
 *  Author：		Disjob
 *
 * </pre>
 */
@Service
public class DisJobInfoServiceImpl implements JobInfoService {
	@Autowired
	private DBJobBasicInfoMapper mapper;
	@Autowired
	public   DisJobServerService initServerExecuteJobService;
	
	@Autowired
	public   SubscribeService subscribeService;
	/**
	 * 1为成功,0任务不存在,2为任务状态不是exec,-1代表超时

	 */
	@Override
	public List<String> KillTaskByRequestId(String requestId) {
		PhpTaskCmd cmd = PhpTaskCmd.KILLTASK;
		byte type = 15;
		RpcRequest request = new RpcRequest();
		Header header = new Header();
		header.setType((byte)type);
		header.setVersion(1);
		
		RpcRequestData data = new RpcRequestData();
		
		data.setRequestId(requestId);
		request.setHeader(header);
		request.setData(data);
		List<String> result = process(requestId, cmd, request); 		
		return result;
  	}
	private List<String> process(String requestId, PhpTaskCmd cmd,RpcRequest data ) {
		List<String> result = new ArrayList<String>();
		List<HURL> urlList = null;
		try {
			urlList = subscribeService.getProvidesByRequestId(requestId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
		if(urlList !=null && urlList.size() >0){
			for(HURL url : urlList){
				String host = url.getHost();
				int port = url.getPort();
				 int code =  requestCmd(requestId, host, port, cmd,data,new DisJobKillTaskResponse());
				 result.add(host+"_"+port+":"+code);
 			}
 		}
		return result;
	}
	
	private List<String> process(String groupName,String jobName, PhpTaskCmd cmd,RpcRequest data) {
		List<String> result = new ArrayList<String>();
	
		List<HURL> urlList = SubscribeService.getHURListProvidesByService(groupName, jobName);
 
		if(urlList !=null && urlList.size() >0){
			for(HURL url : urlList){
				String host = url.getHost();
				int port = url.getPort();
				 int code =  requestCmd(null, host, port,cmd,data,new DisJobRestartTaskResponse());
				 result.add(host+"_"+port+":"+code);
 			}
 		}else{
 			
 		}
		return result;
	}
	 
	private int requestCmd(String requestId, String host, int port,final PhpTaskCmd cmd,RpcRequest data,DisJobResponse response) {
		//DisJobKillTaskResponse response = new DisJobKillTaskResponse();
		CountDownLatch downLatch = new CountDownLatch(1);
		ChannelBootStrap b = new ChannelBootStrap(cmd,response,downLatch,host,port);
		b.request(data);
		try {
			downLatch.await(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
 			b.bootstrap.group().shutdownGracefully();
		}
		if(response !=null){
			//状态统一转化为code处理
			if(response instanceof DisJobRestartTaskResponse){
				return ((DisJobRestartTaskResponse)response ).isStatus() ? 1 :0;
 			}else if(response instanceof DisJobKillTaskResponse){
				return ((DisJobKillTaskResponse)response ).getCode();
 			}else{
 				return -1;
 			}
		}else{
			return -1;
		}
	}
	@Override
	public List<String> restartJob(String groupName,String jobName) {
		PhpTaskCmd cmd = PhpTaskCmd.RESTART;
		byte type = 14;
	 
		RpcRequest request = new RpcRequest();
		Header header = new Header();
		header.setType((byte)type);
		header.setVersion(1);
		
		RpcRequestData data = new RpcRequestData();
		data.setOnlytask(true);
 		request.setHeader(header);
		request.setData(data);
		List<String> result = process(groupName,jobName, cmd, request); 		
		return result;
		/*DisJobRestartTaskResponse response = new DisJobRestartTaskResponse();
		CountDownLatch downLatch = new CountDownLatch(1);
		
		
		RpcRequest request = new RpcRequest();
    	Header header = new Header();
    	header.setType((byte)14);
    	header.setVersion(1);
    	
    	RpcRequestData data = new RpcRequestData();
    	//data.setRequestId("ff808081581945c00158437017fe7fbc");
    	data.setIs_only_task(true);
    	request.setHeader(header);
    	request.setData(data);
    	 
    	ChannelBootStrap b = new ChannelBootStrap(PhpTaskCmd.RESTART,response,downLatch);
    	b.request(request);
    	try {
			downLatch.await(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     	if(response !=null){
    		return response.getStatus();
    	}else{
    		return "false";
    	}*/
  
 	}

}
