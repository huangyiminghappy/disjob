package com.huangyiming.disjob.rpc.client.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.rpc.client.proxy.ServerLinkedService;
import com.huangyiming.disjob.rpc.codec.Header;
import com.huangyiming.disjob.rpc.codec.RpcRequest;
import com.huangyiming.disjob.rpc.codec.RpcResponse;
import com.huangyiming.disjob.rpc.utils.RpcSpringWorkFactory;

/**
 * 
 * @author Disjob
 *
 */
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcResponse> {

	private final boolean autoRelease;
	
	public RpcResponseHandler() {
		autoRelease = true;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		boolean release = true;
		try {
			if (acceptInboundMessage(msg)) {
				RpcResponse response = (RpcResponse) msg;
				channelRead0(ctx, response);
				 
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
	public void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
		LoggerUtil.debug("NettyClient has response from server:"+ response.getRequestId() +",response="+response.toString());
		// 将rpc返回结果投递到线程池进行处理
		RpcSpringWorkFactory.getStoreRepThreadPoolService().submit(response);
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
						String address = ServerLinkedService.getRemoterAddress(future.channel());
						if (future.isSuccess()) {
 							LoggerUtil.debug(address + " , "+ctx.channel().toString()+" heartbeat request success!");
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
