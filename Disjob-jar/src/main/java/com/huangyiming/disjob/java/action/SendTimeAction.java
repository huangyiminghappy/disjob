package com.huangyiming.disjob.java.action;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

import com.huangyiming.disjob.java.bean.RpcContainer;
import com.huangyiming.disjob.java.core.rpc.ReSendFutureListener;
import com.huangyiming.disjob.java.core.rpc.RpcResponse;
import com.huangyiming.disjob.java.service.JobService;
import com.huangyiming.disjob.quence.Action;
import com.huangyiming.disjob.quence.TaskExecuteException;

public abstract class SendTimeAction extends Action {
	protected RpcContainer rpcContiner;
	protected ChannelHandlerContext ctx ;
	protected String requestId ;
	public SendTimeAction(RpcContainer rpcContiner) {
		this.rpcContiner = rpcContiner ;
		this.ctx = this.rpcContiner.getCtx() ;
		this.requestId = this.rpcContiner.getMsg().getData().getRequestId();
	}
	@Override
	public abstract void execute() throws TaskExecuteException ;
	
	protected void sendRpcResponse(RpcResponse rpcResponse){
		if(this.ctx.channel().isActive()&&this.ctx.channel().isWritable()){
			ChannelFuture channelFuture = this.ctx.channel().writeAndFlush(rpcResponse);
			channelFuture.addListener(new ReSendFutureListener(rpcResponse,ctx.channel()));
		}else{
			JobService.putFailRpcResponse(this.ctx.channel(),rpcResponse);
		}
	}

}
