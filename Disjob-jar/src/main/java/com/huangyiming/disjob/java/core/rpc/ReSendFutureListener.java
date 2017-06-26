package com.huangyiming.disjob.java.core.rpc;

import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

import com.huangyiming.disjob.java.service.ClientLinkedService;
import com.huangyiming.disjob.java.service.JobService;

public class ReSendFutureListener implements FutureListener<Object>{
	private RpcResponse rpcResponse;
	private Channel channel ;
	public ReSendFutureListener(RpcResponse rpcResponse,Channel channel) {
		this.rpcResponse = rpcResponse; 
		this.channel = channel;
	}
	public void operationComplete(Future<Object> future) throws Exception {
		if(future.cause()!=null|| future.isCancelled()||!future.isSuccess()){
			JobService.putFailRpcResponse(channel,rpcResponse);
		}else{
			//借用这根管道检测需要重发同一台 ip 失败的那些消息
			ClientLinkedService.notifyReSend(channel);
		}
	}
}
