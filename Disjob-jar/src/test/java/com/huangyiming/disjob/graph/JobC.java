package com.huangyiming.disjob.graph;

import com.huangyiming.disjob.java.bean.SchedulerParam;
import com.huangyiming.disjob.java.job.DependDisJob;
import com.huangyiming.disjob.quence.TaskExecuteException;

public class JobC extends DependDisJob{

	private String name;
	public JobC(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	

	public String getKey() {
		return "depend_"+this.name;
	}

	public void beforeExecute(SchedulerParam schedulerParam) {
		
	}

	public void execute(SchedulerParam schedulerParam)
			throws TaskExecuteException {
		
	}

	public void executeSuccess(SchedulerParam schedulerParam) {
		
	}

	public void executeFail(SchedulerParam schedulerParam) {
		
	}

}
