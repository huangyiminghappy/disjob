package com.huangyiming.job.pack.pck2;

import com.huangyiming.disjob.java.bean.SchedulerParam;
import com.huangyiming.disjob.java.core.annotation.JobDec;
import com.huangyiming.disjob.java.job.DisJob;
import com.huangyiming.disjob.java.utils.TimeUtils;
import com.huangyiming.disjob.quence.TaskExecuteException;

@JobDec(group="packagesOne",jobName="packageTwo_1Job",quartz="0 0/1 * * * ?")
public class PackageTwo_1Job implements DisJob{

	@Override
	public void beforeExecute(SchedulerParam schedulerParam) {
		
	}

	@Override
	public void execute(SchedulerParam schedulerParam)throws TaskExecuteException {
		System.err.println(this.getClass().getName()+"; execute at time:"+TimeUtils.getFormatNow());
	}

	@Override
	public void executeSuccess(SchedulerParam schedulerParam) {
		
	}

	@Override
	public void executeFail(SchedulerParam schedulerParam) {
		
	}

}
