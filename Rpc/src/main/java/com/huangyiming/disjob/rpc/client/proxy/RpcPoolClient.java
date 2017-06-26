package com.huangyiming.disjob.rpc.client.proxy;

import com.huangyiming.disjob.rpc.codec.RpcRequest;
import com.huangyiming.disjob.rpc.codec.RpcResponse;

public interface RpcPoolClient {

	public void initPool(boolean lazyInit);
	
	public void initBootstrap();
	
	public io.netty.channel.Channel connect(int failReConCount);
	
	public void close(int timeout);
	
	public Channel borrowObject(String requestId) throws Exception;
	
	public void invalidateObject(Channel nettyChannel);
	
	public void returnObject(Channel nettyChannel);
	
	public RpcResponse writeMessage(io.netty.channel.Channel channel,RpcRequest rpcRequest);
	
}
