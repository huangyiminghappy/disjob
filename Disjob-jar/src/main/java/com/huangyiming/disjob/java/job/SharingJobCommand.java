package com.huangyiming.disjob.java.job;

import com.huangyiming.disjob.java.bean.RpcContainer;
import com.huangyiming.disjob.java.service.JobService;

public class SharingJobCommand extends AbstractSharingJobCommand{

	public SharingJobCommand(RpcContainer rpcContainer) {
		super(rpcContainer);
	}

	@Override
	public DisJob getDisJobAction(String className, String methodName) {
		
		return JobService.getDisJobAction(className, methodName);
	}

}
