package com.huangyiming.disjob.job;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.huangyiming.disjob.AlamerLogWriter;
import com.huangyiming.disjob.java.bean.SchedulerParam;
import com.huangyiming.disjob.java.job.DisJob;
import com.huangyiming.disjob.java.utils.TimeUtils;
import com.huangyiming.disjob.quence.TaskExecuteException;

public class HaveSupper implements DisJob {

	@Override
	public void beforeExecute(SchedulerParam schedulerParam) {
		int time = new Random().nextInt(80);
		try {
			TimeUnit.SECONDS.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		AlamerLogWriter.writer.println(schedulerParam.getRequestId()+" "+this.getClass().getName()+"; take time :"+ time + "s .time:"+TimeUtils.getFormat(new Date(), TimeUtils.YYYY_MM_DD_HH_MM_SS));
		AlamerLogWriter.writer.flush();	
	}

	@Override
	public void execute(SchedulerParam schedulerParam)
			throws TaskExecuteException {
		
	}

	@Override
	public void executeSuccess(SchedulerParam schedulerParam) {
		
	}

	@Override
	public void executeFail(SchedulerParam schedulerParam) {
		
	}

}
