package com.huangyiming.disjob.rpc.client.proxy;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

import com.huangyiming.disjob.common.exception.TransportException;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.rpc.client.ChannelState;
import com.huangyiming.disjob.rpc.client.HURL;
import com.huangyiming.disjob.rpc.codec.Response;
import com.huangyiming.disjob.rpc.codec.RpcRequest;
import com.huangyiming.disjob.rpc.codec.RpcResponse;

/**
 * 
 * @author Disjob
 *
 */
public class NettyChannel implements Channel{
	private volatile ChannelState state = ChannelState.UNINIT;
	private RpcClient rpcClient;
	private io.netty.channel.Channel channel = null;
	private InetSocketAddress remoteAddress = null;
	private InetSocketAddress localAddress = null;
	private String channelId ;
	
	@Override
	public io.netty.channel.Channel getChannel() {
		return channel;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public NettyChannel(RpcClient rpcClient) {
		this.rpcClient = rpcClient;
		this.remoteAddress = new InetSocketAddress(rpcClient.getHurl().getHost(), rpcClient.getHurl().getPort());
	}
	
	/**
	 * 
	 */
	public Response request(final RpcRequest request) throws TransportException {
		final RpcResponse response = new RpcResponse();
		final String requestId = request.getData().getRequestId();
		response.setRequestId(requestId);
		LoggerUtil.debug("begin write: " + request.getData().getRequestId());
		//1、
		LoggerUtil.debug(request.getData().getRequestId()+ " , "+ (rpcClient.getHurl().getHost() + " channelId:" + this.channelId));
		ChannelFuture writeFuture = this.channel.writeAndFlush(request);
		LoggerUtil.debug("after" + rpcClient.getHurl().getHost() + "-"+ rpcClient.getHurl().getPort() + " request "+ request.getData().getRequestId());
		//2、
		writeFuture.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
				if (future.isSuccess()) {
					LoggerUtil.info("request success:" + requestId);
				} else {
					response.setException(requestId + " request failed:"+ future.cause().getMessage());
					LoggerUtil.error(" request failed, requestId:" + requestId+ " rerequest", future.cause());
					RpcClientCache.get(rpcClient.getHurl()).request(request);
				}
			}
		});
		try {
			writeFuture.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		//3、
		return response;
	}
	
	@Override
	public synchronized io.netty.channel.Channel connect(int failReConCount) throws InterruptedException {
		if (isAvailable()) {
			LoggerUtil.warn("the channel already open, local: " + localAddress + " remote: " + remoteAddress + " url: "+ rpcClient.getHurl().getUri());
			return channel;
		}

		//1、连接
		long start = System.currentTimeMillis();
		this.channel = rpcClient.connect(failReConCount);
		if(this.channel == null){
			return null;
		}
		LoggerUtil.info(this.getClass().getName()+"; connect to server [ "+ServerLinkedService.getRemoterAddress(channel)+" ] has take time :"+(System.currentTimeMillis()-start)/1000 +" s");
		//2、设置状态
		boolean success = this.channel.isActive();
		if(success){
			this.state = ChannelState.ALIVE ;
			if (channel.localAddress() != null && channel.localAddress() instanceof InetSocketAddress) {
				localAddress = (InetSocketAddress) channel.localAddress();
			}
		}
		return this.channel ;
	}
	
	@Override
	public void close() {
		close(0);
	}

	@Override
	public void close(int timeout) {
		try {
			state = ChannelState.CLOSE;
			channel.close().addListener(ChannelFutureListener.CLOSE);
		} catch (Exception e) {
			LoggerUtil.error("NettyChannel close Error: " + rpcClient.getHurl().getUri() + " local=" + localAddress, e);
		}
	}

	@Override
	public boolean isAvailable() {
		return state.isAliveState();
	}

	@Override
	public InetSocketAddress getLocalAddress() {
		return localAddress;
	}

	@Override
	public InetSocketAddress getRemoteAddress() {
		return remoteAddress;
	}

	@Override
	public boolean isClosed() {
		return state.isCloseState();
	}

	@Override
	public HURL getHurl() {
		return rpcClient.getHurl();
	}
}
