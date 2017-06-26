package com.huangyiming.disjob.java.action;

import java.util.Date;
import com.huangyiming.disjob.java.bean.RpcContainer;
import com.huangyiming.disjob.java.core.rpc.RpcResponse;
import com.huangyiming.disjob.java.job.JobExecuteStatus;
import com.huangyiming.disjob.java.utils.TimeUtils;

/**
 * 向 disJob 调度中心发送 job 的接收时间
 * @author Disjob
 *
 */
public class SendReceiveTimeAction extends SendTimeAction{
	
	private Date receiveTime ;
	public SendReceiveTimeAction(RpcContainer rpcContiner,Date receiveTime) {
		super(rpcContiner);
		this.receiveTime = receiveTime ;
	}
	@Override
	public void execute(){
		RpcResponse rpcResponse = new RpcResponse() ;
		rpcResponse.setJobRecvTime(TimeUtils.local2Utc(receiveTime));
		rpcResponse.setRequestId(requestId);
		rpcResponse.setCode(String.valueOf(JobExecuteStatus.SUCCESS));
		sendRpcResponse(rpcResponse);
	}
	
}
