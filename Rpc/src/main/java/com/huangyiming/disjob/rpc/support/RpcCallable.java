package com.huangyiming.disjob.rpc.support;

import com.huangyiming.disjob.common.exception.TransportException;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.quence.CallableCommand;
import com.huangyiming.disjob.rpc.client.proxy.Channel;
import com.huangyiming.disjob.rpc.codec.Response;
import com.huangyiming.disjob.rpc.codec.RpcRequest;
import com.huangyiming.disjob.rpc.codec.RpcResponse;

public class RpcCallable extends CallableCommand<Response> {

	private  Channel nettyChannel;
	
	private RpcRequest request;
	
	public RpcCallable( Channel nettyChannel,RpcRequest request){
		this.nettyChannel = nettyChannel;
		this.request = request;
	}
	@Override
	public Response execute(){
		RpcResponse response = new RpcResponse();
		
		try {
			return nettyChannel.request(request);
		} catch (TransportException e) {
			response.setRequestId(request.getData().getRequestId());
			response.setException(e.getMessage());
			LoggerUtil.error(e.getMessage());
		}
		return response ;
	}

}
