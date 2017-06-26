package com.huangyiming.disjob.java.spring;

import com.huangyiming.disjob.java.bean.RpcContainer;
import com.huangyiming.disjob.java.job.AbstractJobAction;
import com.huangyiming.disjob.java.job.DisJob;
import com.huangyiming.disjob.quence.Log;

public class SpringJobAction extends AbstractJobAction{

	public SpringJobAction(RpcContainer rpcContainer) {
		super(rpcContainer);
	}

	@Override
	public DisJob getDisJobAction(String className, String methodName) {
		if("execute".equals(methodName)){
			return SpringWorkFactory.getInstance().getDisJob(className);
		}else{
			Log.warn("[ java ] 注册job的 method 参数类型错误，不是 execute。the error method is："+methodName);
			return null ;
		}
	}
}
