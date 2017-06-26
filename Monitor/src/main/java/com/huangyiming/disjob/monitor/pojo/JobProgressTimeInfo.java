package com.huangyiming.disjob.monitor.pojo;

import java.io.Serializable;
import com.huangyiming.disjob.common.EventType;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.event.AbstractEventObject;
import com.huangyiming.disjob.event.ObjectEvent;
import com.huangyiming.disjob.event.ObjectListener;
import com.huangyiming.disjob.monitor.util.MonitorSpringWorkFactory;
import com.huangyiming.disjob.monitor.alarm.AlarmCondition;

public class JobProgressTimeInfo extends AbstractEventObject<JobProgressTimeInfo> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String group;
	private String jobName;
	private String requestId ;
	private long schedulerStartTime ;//开始调度时间
	private long jobRecvTime ;
	private long jobBegingTime ;
	private long jobCompleteTime ;
	/**
	 * 一定要给一个request id
	 * @param requestId
	 */
	public JobProgressTimeInfo(String requestId) {
		this.requestId = requestId ;
	}
	public JobProgressTimeInfo(String group,String jobName,String requestId, long jobRecvTime, long jobBegingTime,long jobCompleteTime) {
		this(requestId);
		this.group = group;
		this.jobName = jobName;
		this.jobRecvTime = jobRecvTime;
		this.jobBegingTime = jobBegingTime;
		this.jobCompleteTime = jobCompleteTime;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public long getJobRecvTime() {
		return jobRecvTime;
	}
	public void setJobRecvTime(long jobRecvTime) {
		this.jobRecvTime = jobRecvTime;
	}
	public long getJobBegingTime() {
		return jobBegingTime;
	}
	public void setJobBegingTime(long jobBegingTime) {
		this.jobBegingTime = jobBegingTime;
	}
	public long getJobCompleteTime() {
		return jobCompleteTime;
	}
	public void setJobCompleteTime(long jobCompleteTime) {
		this.jobCompleteTime = jobCompleteTime;
	}
	public long getSchedulerStartTime() {
		return schedulerStartTime;
	}
	public void setSchedulerStartTime(long schedulerStartTime) {
		this.schedulerStartTime = schedulerStartTime;
	}
	
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public void notifyProcess(){
		notifyListeners(new ObjectEvent<JobProgressTimeInfo>(this, EventType.RECEIVE_RPECESS_TIME));
	}
	@Override
	public void attachListener() {
		this.addListener(new ObjectListener<JobProgressTimeInfo>() {
			
			@Override
			public void onEvent(ObjectEvent<JobProgressTimeInfo> event) {
				AlarmCondition alarmCondition = MonitorSpringWorkFactory.getJobService().getAlarmCondition(event.getValue().getRequestId());
				if(alarmCondition!=null){
					alarmCondition.handler();
				}else{
					LoggerUtil.debug(event.getValue().getRequestId()+" 任务执行时间超过 time out 的 3 倍时间。");
				}
			}
		}, EventType.RECEIVE_RPECESS_TIME);
	}
	@Override
	public String toString() {
		return "JobProgressTimeInfo [requestId=" + requestId+ ", schedulerStartTime=" + schedulerStartTime+ ", jobRecvTime=" + jobRecvTime + ", jobBegingTime="+ jobBegingTime + ", jobCompleteTime=" + jobCompleteTime + "]";
	}
}
