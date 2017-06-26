package com.huangyiming.disjob.java.app.alarm;

import com.huangyiming.disjob.java.bean.SchedulerParam;
import com.huangyiming.disjob.java.core.annotation.JobDec;
import com.huangyiming.disjob.java.job.DisJob;
import com.huangyiming.disjob.quence.TaskExecuteException;

@JobDec(group="alarm",jobName="alarmJob1",quartz="0/10 * * * * ?",fireNow=true)
public class AlarmJobAction implements DisJob{

	@Override
	public void beforeExecute(SchedulerParam schedulerParam) {
		
	}

	@Override
	public void execute(SchedulerParam schedulerParam)throws TaskExecuteException {
		
		System.err.println("alarm----->");
	}

	@Override
	public void executeSuccess(SchedulerParam schedulerParam) {
		
	}

	@Override
	public void executeFail(SchedulerParam schedulerParam) {
		
	}

}
