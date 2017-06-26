package com.huangyiming.disjob.monitor.alarm;

import java.util.Date;

import com.huangyiming.disjob.common.util.DateUtil;
import com.huangyiming.disjob.event.BaseCondition;
import com.huangyiming.disjob.monitor.pojo.JobProgressTimeInfo;
import com.huangyiming.disjob.monitor.service.JobService;
import com.huangyiming.disjob.monitor.util.MonitorSpringWorkFactory;
import com.huangyiming.disjob.monitor.alarm.service.MessagePipleService;
import com.huangyiming.disjob.monitor.db.domain.DBJobBasicInfo;

public abstract class AbstractAlarmCondition extends BaseCondition<JobProgressTimeInfo,Integer>{
	public AbstractAlarmCondition(JobProgressTimeInfo observiable, Integer v) {
		super(observiable, v);
	}

	@Override
	public abstract boolean isFinished() ;
	
	@Override
	public void handler() {  
		MessagePipleService messageService = MonitorSpringWorkFactory.getMessagePipleService();
		JobProgressTimeInfo jptime = getObserviable();
		String requestId = jptime.getRequestId() ;
		if(isFinished()){//表示：接收时间、开始执行时间、执行结束三个消息都已经接收到
			long consumerTime = (jptime.getJobCompleteTime() - jptime.getJobBegingTime())/1000;
			messageService.offerMessage(requestId,consumerTime);
			//1、更新耗时
			DBJobBasicInfo info = new DBJobBasicInfo();
			info.setUuid(requestId);
			info.setTimeConsuming(consumerTime);
			info.setJobRecvTime(DateUtil.getFormat(new Date(jptime.getJobRecvTime()), DateUtil.YYYY_MM_DD_HH_MM_SS));
			info.setExecuteStart(DateUtil.getFormat(new Date(jptime.getJobBegingTime()), DateUtil.YYYY_MM_DD_HH_MM_SS));
			info.setExecuteEnd(DateUtil.getFormat(new Date(jptime.getJobCompleteTime()), DateUtil.YYYY_MM_DD_HH_MM_SS));
			JobService jobService = MonitorSpringWorkFactory.getJobService();
			jobService.getJobTracker(requestId).notifyUpdateDBBasicInfoEvent(info);;
			 
			//2、一个周期处理，卸载相关数据
			jobService.removeJobTracker(requestId);
			jobService.removeAlarmCondition(requestId);
		}

	}

}
