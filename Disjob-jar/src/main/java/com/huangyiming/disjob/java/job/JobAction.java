package com.huangyiming.disjob.java.job;

import com.huangyiming.disjob.java.bean.RpcContainer;
import com.huangyiming.disjob.java.service.JobService;

public class JobAction extends AbstractJobAction{
	public JobAction(RpcContainer rpcContainer) {
		super(rpcContainer);
	}
	public DisJob getDisJobAction(String className, String methodName){
		
		return JobService.getDisJobAction(className, methodName);
	}
	
}
