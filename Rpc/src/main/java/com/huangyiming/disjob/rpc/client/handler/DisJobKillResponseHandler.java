package com.huangyiming.disjob.rpc.client.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.CountDownLatch;

import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.rpc.codec.DisJobKillTaskResponse;
import com.huangyiming.disjob.rpc.codec.DisJobResponse;
import com.huangyiming.disjob.rpc.codec.Header;
import com.huangyiming.disjob.rpc.codec.RpcRequest;
import com.huangyiming.disjob.rpc.utils.PhpTaskCmd;

 
public class DisJobKillResponseHandler extends SimpleChannelInboundHandler<DisJobResponse> {

	private final boolean autoRelease;
	public PhpTaskCmd cmd;
	
 	public DisJobKillTaskResponse response;
  
 	public CountDownLatch downLatch;
	public DisJobKillResponseHandler(PhpTaskCmd cmd,DisJobKillTaskResponse response,CountDownLatch downLatch ) {
 		autoRelease = true;
 		this.cmd = cmd;
 		this.response = response;
  		this.downLatch = downLatch;
  	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		boolean release = true;
		try {
			if (acceptInboundMessage(msg)) {
				DisJobKillTaskResponse msgResponse = (DisJobKillTaskResponse)msg;
				//response = DeepCopy.copy(msgResponse);
 				response.setCode(msgResponse.getCode());
				response.setMsg(msgResponse.getMsg());
				response.setRequestId(msgResponse.getRequestId());
				response.setStatus(msgResponse.isStatus());
				downLatch.countDown();
				/*DisJobResponse response = null;
		        //kill task  后面第二版返回值要改成15
		        switch (cmd.getType()) {
				case 15:
					response = (RpcKillTaskResponse) msg;
					break;
				case 14:
					response = (DisJobRestartTaskResponse) msg;
				default:
					break;
				}*/
				//channelRead0(ctx, response);
				 
			} else {
				release = false;
				ctx.fireChannelRead(msg);
			}
		} finally {
			if (autoRelease && release) {
				ReferenceCountUtil.release(msg);
			}
		}
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, DisJobResponse response) throws Exception {
		//LoggerUtil.debug("NettyClient has response from server:"+ response.getRequestId() +",response="+response.toString());
		// 将rpc返回结果投递到线程池进行处理

		//RpcSpringWorkFactory.getStoreRepThreadPoolService().submit(response);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void userEventTriggered(final ChannelHandlerContext ctx, Object evt) throws Exception {
		if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state() == IdleState.ALL_IDLE) {
				final RpcRequest request = new RpcRequest();
				request.setHeader(new Header((byte) 0, 1, 0));
				ChannelFuture writeFuture = ctx.channel().writeAndFlush(request);
				 
				writeFuture.addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						String channelId = "";
						 
						if (future.isSuccess()) {
 							LoggerUtil.debug(channelId + " , "+ctx.channel().toString()+" heartbeat request success!");
						} 
					}
				});
			}
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.fireChannelActive();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		ctx.fireChannelInactive();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		StackTraceElement[] test = cause.getStackTrace();
		for(StackTraceElement s : test){
			LoggerUtil.error("api caught exception "+ s.toString());
		}
		ctx.close();
	}

}
