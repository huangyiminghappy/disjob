package com.huangyiming.disjob.graph;

import com.huangyiming.disjob.java.bean.SchedulerParam;
import com.huangyiming.disjob.java.job.DependDisJob;
import com.huangyiming.disjob.quence.TaskExecuteException;

public class JobB extends DependDisJob{

	private String name;
	public JobB(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}

	public String getKey() {
		return "depend_"+this.name;
	}

	public void beforeExecute(SchedulerParam schedulerParam) {
		// TODO Auto-generated method stub
		
	}

	public void execute(SchedulerParam schedulerParam)
			throws TaskExecuteException {
		// TODO Auto-generated method stub
		
	}

	public void executeSuccess(SchedulerParam schedulerParam) {
		// TODO Auto-generated method stub
		
	}

	public void executeFail(SchedulerParam schedulerParam) {
		// TODO Auto-generated method stub
		
	}

}
