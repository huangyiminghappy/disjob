package com.huangyiming.one.hundred;

import java.util.Date;

import com.huangyiming.disjob.java.bean.SchedulerParam;
import com.huangyiming.disjob.java.core.annotation.JobDec;
import com.huangyiming.disjob.java.job.DisJob;
import com.huangyiming.disjob.java.utils.DebugInfoPrintUtil;
import com.huangyiming.disjob.java.utils.TimeUtils;
import com.huangyiming.disjob.quence.TaskExecuteException;

@JobDec(group="cronJob_1",jobName="3000_fireNow_1",quartz="0/38 * * * * ?",fireNow=true)
public class FireNowJob_1 implements DisJob{

	@Override
	public void beforeExecute(SchedulerParam schedulerParam) {
		
	}

	@Override
	public void execute(SchedulerParam schedulerParam)
			throws TaskExecuteException {
		DebugInfoPrintUtil.debug("D:/cron_firenow.log", this.getClass().getName()+"; at time:"+TimeUtils.getFormat(new Date(), TimeUtils.YYYY_MM_DD_HH_MM_SS));
	}

	@Override
	public void executeSuccess(SchedulerParam schedulerParam) {
		
	}

	@Override
	public void executeFail(SchedulerParam schedulerParam) {
		
	}

}
