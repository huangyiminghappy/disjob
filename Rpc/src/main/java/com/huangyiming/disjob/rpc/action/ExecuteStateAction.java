package com.huangyiming.disjob.rpc.action;

import org.apache.commons.lang3.StringUtils;

import com.huangyiming.disjob.common.util.DateUtil;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.monitor.alarm.pojo.AlarmInfo;
import com.huangyiming.disjob.monitor.db.domain.DBJobBasicInfo;
import com.huangyiming.disjob.monitor.event.JobTracker;
import com.huangyiming.disjob.monitor.rms.CommonRMSMonitor;
import com.huangyiming.disjob.monitor.rms.MonitorType;
import com.huangyiming.disjob.monitor.util.MonitorSpringWorkFactory;
import com.huangyiming.disjob.quence.Action;
import com.huangyiming.disjob.rpc.codec.RpcResponse;

public class ExecuteStateAction extends Action {
	private RpcResponse rep ;
	public ExecuteStateAction(RpcResponse rep) {
		this.rep = rep ;
	}
	@Override
	public void execute() {
		JobTracker jobTracker = MonitorSpringWorkFactory.getJobService().getJobTracker(rep.getRequestId());
		/**
		 * 统一在这里get,remove 在一个特定的地方。那就是接收到 recvTime,begintime,completetime 都接收到时 去remove 掉
		 */
		DBJobBasicInfo info = new DBJobBasicInfo();
		info.setUuid(rep.getRequestId());
		//1、
		if (StringUtils.isNoneEmpty(rep.getJobRecvTime())) {
			info.setJobRecvTime(DateUtil.utc2Local(rep.getJobRecvTime(),DateUtil.patten));
			MonitorSpringWorkFactory.getJobService().setJobReceiveTime(rep.getRequestId(), info.getJobRecvTime());
			
		}
		//2、
		if (StringUtils.isNoneEmpty(rep.getJobBegingTime())) {
			info.setExecuteStart(DateUtil.utc2Local(rep.getJobBegingTime(),DateUtil.patten));
			MonitorSpringWorkFactory.getJobService().setJobBeginTime(rep.getRequestId(), info.getExecuteStart());
		}
		//3、
		if (StringUtils.isNoneEmpty(rep.getJobCompleteTime())) {
			info.setExecuteEnd(DateUtil.utc2Local(rep.getJobCompleteTime(),DateUtil.patten));
			MonitorSpringWorkFactory.getJobService().setJobCompleteTime(rep.getRequestId(), info.getExecuteEnd());
		}
		
		if (rep.getKillprocess() != 0) {
			info.setKillprocess(rep.getKillprocess());
		}
		//4、异常报警处理


		if ((!"0".equals(rep.getCode())) || StringUtils.isNotBlank(rep.getException())) {
			LoggerUtil.error("rpcserver occur error, group:" + jobTracker.getGroupName() + ",job:" + jobTracker.getJobName() + ",requestId:" + rep.getRequestId());
			AlarmInfo alarmInfo = new AlarmInfo(jobTracker.getGroupName(),jobTracker.getJobName(), rep.getCode(), "[ "+rep.getRequestId()+" ]" + rep.getException());
			MonitorSpringWorkFactory.getRtxMsgPushService().notify(jobTracker.getGroupName(),"group:" + jobTracker.getGroupName() + ",job:"+ jobTracker.getJobName(), rep.getCode(),rep.getRequestId());
			CommonRMSMonitor.sendBusiness(MonitorType.Business.RPC_RESPONSE_EXCEPTION,alarmInfo.toString(), rep.getRequestId());
			info.setCurrentStatus('0');
		}
		
		info.setErrorReason(rep.getException());
		info.setErrorType(rep.getCode());
		jobTracker.notifyUpdateDBBasicInfoEvent(info);
	}
	public static void main(String[] args) {
		String error = "is not exists";
		System.out.println(StringUtils.isNotBlank(error));
	}
}
