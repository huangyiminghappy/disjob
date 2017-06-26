package com.huangyiming.fire.now;

import com.huangyiming.disjob.java.bean.SchedulerParam;
import com.huangyiming.disjob.java.core.annotation.JobDec;
import com.huangyiming.disjob.java.job.DisJob;
import com.huangyiming.disjob.java.utils.TimeUtils;
import com.huangyiming.disjob.quence.TaskExecuteException;

@JobDec(group="huangyiming",jobName="TestFireNow",quartz="0/2 * * * * ?",fireNow=true)
public class TestFireNow implements DisJob{

	@Override
	public void beforeExecute(SchedulerParam schedulerParam) {
		
	}

	@Override
	public void execute(SchedulerParam schedulerParam)
			throws TaskExecuteException {
		System.err.println(this.getClass().getName()+"; at time:"+TimeUtils.getFormatNow());
	}

	@Override
	public void executeSuccess(SchedulerParam schedulerParam) {
		
	}

	@Override
	public void executeFail(SchedulerParam schedulerParam) {
		// TODO Auto-generated method stub
		
	}

}
