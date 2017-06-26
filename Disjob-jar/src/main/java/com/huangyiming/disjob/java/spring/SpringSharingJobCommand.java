package com.huangyiming.disjob.java.spring;

import com.huangyiming.disjob.java.bean.RpcContainer;
import com.huangyiming.disjob.java.job.AbstractSharingJobCommand;
import com.huangyiming.disjob.java.job.DisJob;
import com.huangyiming.disjob.quence.Log;

/**
 * 处理分片的任务
 * @author Disjob
 *
 */
public class SpringSharingJobCommand extends AbstractSharingJobCommand{

	public SpringSharingJobCommand(RpcContainer rpcContainer) {
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
