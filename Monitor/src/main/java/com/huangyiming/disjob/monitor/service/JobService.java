package com.huangyiming.disjob.monitor.service;

import java.lang.ref.SoftReference;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.huangyiming.disjob.common.util.DateUtil;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.monitor.event.JobTracker;
import com.huangyiming.disjob.monitor.pojo.JobProgressTimeInfo;
import com.huangyiming.disjob.monitor.util.MonitorSpringWorkFactory;
import com.huangyiming.disjob.monitor.alarm.AlarmCondition;
import com.huangyiming.disjob.monitor.db.domain.DBJobBasicInfo;

@Service("jobService")
public class JobService{

	private JobService(){
	}
	
	private ConcurrentHashMap<String, JobTracker> jobTrackerMap = new ConcurrentHashMap<String, JobTracker>();
	
	private ConcurrentHashMap<String, AlarmCondition> requestIdAlarmCondition = new ConcurrentHashMap<String, AlarmCondition>();
	
	private ConcurrentHashMap<String, SoftReference<String>> requestKeyMap = new ConcurrentHashMap<String, SoftReference<String>>();//一个周期后remove
	
	public final static ConcurrentHashMap<String, AtomicInteger> receiveCount = new ConcurrentHashMap<String, AtomicInteger>();
	
	public JobTracker newJobTracker(String requestId,String group,String jobName){
		
		JobTracker jobTracker = jobTrackerMap.get(requestId);
		if(jobTracker == null){
			jobTracker = new JobTracker(group,jobName);
			jobTrackerMap.put(requestId, jobTracker);
		}
		
		return jobTracker;
	}
	
	/**
	 * 注意：这里可能会有一个 潜在的问题：一个job 在 3 倍 的超时间之后再将 进度信息发送回来的时候，这个时候来 get 已经
	 * 被 remove 掉了。因此如果拿不到，就临时创建一个 jobTracker 给他，仅仅是为了后续的操作。不影响。
	 * 修复时间：2016-12-28 11:25
	 * @param requestId
	 * @return
	 */
	public JobTracker getJobTracker(String requestId){
		
		JobTracker jobTracker = jobTrackerMap.get(requestId);
		if(jobTracker == null){
			DBJobBasicInfo dbjobBasicInfo = MonitorSpringWorkFactory.getDBJobBasicInfoService().findByUuid(requestId);
			jobTracker = new JobTracker(dbjobBasicInfo.getGroupName(), dbjobBasicInfo.getJobName());
		}
		
		return jobTracker;
	}
	
	public JobTracker removeJobTracker(String requestId){
		
		return jobTrackerMap.remove(requestId);
	}
	
	public void setJobReceiveTime(String requestId,String receiveTime){
		AlarmCondition alarmCondition = requestIdAlarmCondition.get(requestId); 
		if(alarmCondition==null){
			LoggerUtil.warn("超过3倍 times out:"+requestId+"; receive:"+receiveTime+"; at time:"+new Date().toString());
			return ;
		}
		JobProgressTimeInfo jobProgressTimeInfo = alarmCondition.getObserviable();
		jobProgressTimeInfo.setJobRecvTime(DateUtil.parse(receiveTime).getTime());
		jobProgressTimeInfo.notifyProcess();//通知：处理是否执行超时
	}
	
	public void setJobBeginTime(String requestId,String jobBegingTime){
		AlarmCondition alarmCondition = requestIdAlarmCondition.get(requestId);
		if(alarmCondition == null){
			LoggerUtil.warn("超过3倍 times out:"+requestId+"; receive:"+jobBegingTime+"; at time:"+new Date().toString());
			return ;
		}
		JobProgressTimeInfo jobProgressTimeInfo = alarmCondition.getObserviable();
		jobProgressTimeInfo.setJobBegingTime(DateUtil.parse(jobBegingTime).getTime());
		jobProgressTimeInfo.notifyProcess();//通知：处理是否执行超时
	}
	
	public void setJobCompleteTime(String requestId,String jobCompleteTime){
		AlarmCondition alarmCondition = requestIdAlarmCondition.get(requestId);
		if(alarmCondition==null){
			LoggerUtil.warn("超过3倍 times out:"+requestId+"; receive:"+jobCompleteTime+"; at time:"+new Date().toString());
			return ;
		}
		JobProgressTimeInfo jobProgressTimeInfo = alarmCondition.getObserviable();
		jobProgressTimeInfo.setJobCompleteTime(DateUtil.parse(jobCompleteTime).getTime());
		jobProgressTimeInfo.notifyProcess();//通知：处理是否执行超时
	}
	
	public void setJobSchedulerTime(String requestId){
		JobProgressTimeInfo jobProgressTimeInfo = new JobProgressTimeInfo(requestId);
		jobProgressTimeInfo.setSchedulerStartTime(new Date().getTime());
		setAlarmCondition(requestId, jobProgressTimeInfo);
	}
	/**
	 * job 开始出发调度时就设置他的 报警condition
	 * @param requestId
	 */
	private void setAlarmCondition(String requestId,JobProgressTimeInfo jobProgressTimeInfo){
		this.requestIdAlarmCondition.put(requestId, new AlarmCondition(jobProgressTimeInfo,3));
	}
	public AlarmCondition getAlarmCondition(String requestId){
		return requestIdAlarmCondition.get(requestId);
	}
	public AlarmCondition removeAlarmCondition(String requestId){
		return requestIdAlarmCondition.remove(requestId);
	}
	
	public void setRequestIdKey(String requestId,String key){
		if(StringUtils.isNoneEmpty(requestId)&&StringUtils.isNotEmpty(key)){
			requestKeyMap.put(requestId, new SoftReference<String>(key));
		}
	}
	
	public String getRequestIdKey(String requestId){
		if(StringUtils.isNoneEmpty(requestId)){
			return requestKeyMap.get(requestId).get();
		}
		return "";
	}
	
}
