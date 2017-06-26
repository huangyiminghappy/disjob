package com.huangyiming.disjob.java.event;

import com.huangyiming.disjob.java.ExecutorBuilder;
import com.huangyiming.disjob.java.job.RegisterDisJobAction;
import com.huangyiming.disjob.quence.ActionQueue;
import com.huangyiming.disjob.quence.BaseActionQueue;

public class GroupRegisterTracker {

	private ActionQueue groupRegisterQueue = new BaseActionQueue(ExecutorBuilder.getJobExecutor());
	
	public void enqueue(RegisterDisJobAction registerEJobAction){
		groupRegisterQueue.enqueue(registerEJobAction);
	}
}
