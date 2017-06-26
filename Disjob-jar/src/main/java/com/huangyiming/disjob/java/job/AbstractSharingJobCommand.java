package com.huangyiming.disjob.java.job;

import com.huangyiming.disjob.java.bean.RpcContainer;
import com.huangyiming.disjob.java.core.rpc.RpcRequest;
import com.huangyiming.disjob.java.service.JobService;
import com.huangyiming.disjob.quence.Command;
import com.huangyiming.disjob.quence.TaskExecuteException;

public abstract class AbstractSharingJobCommand extends Command implements JobProvider{

	private RpcContainer rpcContainer ;
	
	public AbstractSharingJobCommand(RpcContainer rpcContainer) {
		super();
		this.rpcContainer = rpcContainer;
	}

	@Override
	public void executeException(String execeptionMsg) {
		
	}

	@Override
	public void execute() throws TaskExecuteException {
		RpcRequest request = rpcContainer.getMsg();
		String className = request.getData().getClassName();
		String methodName = request.getData().getMethodName();
		DisJob action = getDisJobAction(className, methodName);
		JobService.handlerExecuter(rpcContainer, action, rpcContainer.getCtx().channel());
	}

	@Override
	public void executeSuccess() {
		
	}

	public abstract DisJob getDisJobAction(String className, String methodName) ;
}
