package com.huangyiming.disjob.rpc.client.proxy;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import com.huangyiming.disjob.rpc.codec.RpcRequest;

public class WriterChannelFutureListener implements ChannelFutureListener{
	private io.netty.channel.Channel channel;
	private RpcRequest request ;
	public WriterChannelFutureListener(io.netty.channel.Channel channel,RpcRequest request) {
		this.channel = channel ;
		this.request = request;
	}
	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
		if(future.cause()!=null|| future.isCancelled()||!future.isSuccess()){
			ServerLinkedService.putFailRpcRequest(ServerLinkedService.getRemoterAddress(channel), request);
		}else{
			//借用这根管道检测需要重发同一台 ip 失败的那些消息
			ServerLinkedService.checkReSendRpc(channel);
		}
	}
}