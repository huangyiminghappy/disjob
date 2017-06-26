package com.huangyiming.disjob.java.action;

import java.util.Date;

import com.huangyiming.disjob.java.bean.RpcContainer;
import com.huangyiming.disjob.java.core.rpc.RpcResponse;
import com.huangyiming.disjob.java.job.JobExecuteStatus;
import com.huangyiming.disjob.java.utils.TimeUtils;
import com.huangyiming.disjob.quence.TaskExecuteException;

/**
 * 向 disJob 调度中发送 job 的开始执行时间
 * @author Disjob
 *
 */
public class SendBeginExeTimeAction extends SendTimeAction{

	private Date beginExeTime =null;
	public SendBeginExeTimeAction(RpcContainer rpcContiner,Date beginExeTime) {
		super(rpcContiner);
		this.beginExeTime = beginExeTime ;
	}
	@Override
	public void execute() throws TaskExecuteException {
		RpcResponse rpcResponse = new RpcResponse() ;
		rpcResponse.setJobBegingTime(TimeUtils.local2Utc(beginExeTime));
		rpcResponse.setRequestId(requestId);
		rpcResponse.setCode(String.valueOf(JobExecuteStatus.SUCCESS));
		sendRpcResponse(rpcResponse);
	}
	
}
