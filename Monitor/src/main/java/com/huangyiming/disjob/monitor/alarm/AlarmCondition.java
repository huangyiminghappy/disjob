package com.huangyiming.disjob.monitor.alarm;

import com.huangyiming.disjob.monitor.pojo.JobProgressTimeInfo;

public class AlarmCondition extends AbstractAlarmCondition{
	public AlarmCondition(JobProgressTimeInfo observiable, Integer v) {
		super(observiable, v);
	}

	@Override
	public boolean isFinished() {
		int targetCount = getValue();
		return caculeate() >= targetCount;
	}
	private int caculeate(){
		JobProgressTimeInfo jptime = getObserviable();
		int count = 0 ;
		
		/**
		 * 如果完成时间已经收到了,但是接收时间和开始调度时间还没收到,我们就默认将他的时间设置为开始调度时间.
		 */
		if(jptime.getJobCompleteTime() > 0){
			if(jptime.getJobRecvTime() == 0){
				jptime.setJobRecvTime(jptime.getSchedulerStartTime());
			}
			if(jptime.getJobBegingTime() == 0){
				jptime.setJobBegingTime(jptime.getSchedulerStartTime());
			}
		}
		
		if(jptime.getJobRecvTime() >0){
			count++ ;
		}
		
		if(jptime.getJobBegingTime() >0){
			count++;
		}
		
		if(jptime.getJobCompleteTime() > 0){
			count++;
		}
		return count;
	}

}
