package com.huangyiming.disjob.spring.firenow;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.huangyiming.disjob.java.bean.SchedulerParam;
import com.huangyiming.disjob.java.core.annotation.JobDec;
import com.huangyiming.disjob.java.job.DisJob;
import com.huangyiming.disjob.java.utils.DebugInfoPrintUtil;
import com.huangyiming.disjob.java.utils.TimeUtils;
import com.huangyiming.disjob.quence.TaskExecuteException;

@Service
@JobDec(group="springCronJob_1",jobName="fireNowJob_0",quartz="0/10 * * * * ?",fireNow=true)
public class SpringFireNowJob_0 implements DisJob{

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
