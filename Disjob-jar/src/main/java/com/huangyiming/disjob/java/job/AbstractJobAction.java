package com.huangyiming.disjob.java.job;

import io.netty.channel.ChannelHandlerContext;

import java.util.Date;

import com.huangyiming.disjob.java.action.SendReceiveTimeAction;
import com.huangyiming.disjob.java.bean.RpcContainer;
import com.huangyiming.disjob.java.core.rpc.RpcRequest;
import com.huangyiming.disjob.java.service.JobService;
import com.huangyiming.disjob.quence.Action;

public abstract class AbstractJobAction extends Action implements JobProvider{
	private RpcContainer rpcContainer ;
	protected ChannelHandlerContext ctx ;
	protected RpcRequest request ;
	protected Date recvTime;
	protected boolean isSendSuccess = true ;
	public AbstractJobAction(RpcContainer rpcContainer) {
		super();
		this.rpcContainer = rpcContainer;
		this.ctx = rpcContainer.getCtx() ;
		this.request = rpcContainer.getMsg() ;
		this.recvTime = new Date();
		JobService.enqueue(ctx.channel(),new SendReceiveTimeAction(rpcContainer, recvTime));
	}
	
	@Override
	public void execute(){
		String className = request.getData().getClassName();
		String methodName = request.getData().getMethodName();
		DisJob action = getDisJobAction(className, methodName);
		JobService.handlerExecuter(rpcContainer, action, ctx.channel());
	}

	public abstract DisJob getDisJobAction(String className, String methodName) ;
	
}
