package com.huangyiming.disjob.java.core.rpc;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import com.huangyiming.disjob.java.DisJobConstants;
import com.huangyiming.disjob.java.ExecutorBuilder;
import com.huangyiming.disjob.java.bean.RpcContainer;
import com.huangyiming.disjob.java.event.JobTracker;
import com.huangyiming.disjob.java.service.ClientLinkedService;
import com.huangyiming.disjob.java.service.JobService;
import com.huangyiming.disjob.pojo.ThreadPoolContainer;
import com.huangyiming.disjob.quence.Log;

public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
	}
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
		if(msg==null){
			return ;
		}
		if(msg.getHeader().getType() > 0 && msg.getData()!=null){
			JobTracker jobTracker = JobService.getJobTracker(msg.getData().getClassName());
			if(jobTracker == null){
				return ;
			}
			jobTracker.notifyRpcHandler(new RpcContainer(ctx, msg));//
			if(DisJobConstants.isDebug){
				ThreadPoolContainer container = ExecutorBuilder.getJobExecutor().getThreadPollContainer();
				Log.info("thread pool container:"+container.toString());
			}
		}
		ClientLinkedService.putChannel(ctx.channel());
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)throws Exception {
		cause.printStackTrace();
		StackTraceElement[] staele = cause.getStackTrace();
		for (StackTraceElement se : staele) {
			se.toString();
		}
		ctx.close();
	}
}
