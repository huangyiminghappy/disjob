package com.huangyiming.disjob.java.action;

import com.huangyiming.disjob.java.bean.RpcContainer;
import com.huangyiming.disjob.java.core.rpc.RpcResponse;
import com.huangyiming.disjob.quence.TaskExecuteException;

public class SendCompleteTimeAction extends SendTimeAction {
	private RpcResponse rpcResponse;
	public SendCompleteTimeAction(RpcContainer rpcContiner, RpcResponse rpcResponse) {
		super(rpcContiner);
		this.rpcResponse = rpcResponse ;
	}

	@Override
	public void execute() throws TaskExecuteException {
		sendRpcResponse(rpcResponse);
	}

}
