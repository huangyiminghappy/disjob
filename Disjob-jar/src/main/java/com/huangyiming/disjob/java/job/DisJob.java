package com.huangyiming.disjob.java.job;

import com.huangyiming.disjob.java.bean.SchedulerParam;
import com.huangyiming.disjob.quence.TaskExecuteException;


public interface DisJob {
	public void beforeExecute(SchedulerParam schedulerParam) ;
	
	public void execute(SchedulerParam schedulerParam) throws TaskExecuteException;
	
	public void executeSuccess(SchedulerParam schedulerParam);
	
	public void executeFail(SchedulerParam schedulerParam);
}
