package com.huangyiming.disjob.graph;

import com.huangyiming.disjob.java.bean.SchedulerParam;
import com.huangyiming.disjob.java.job.DependDisJob;
import com.huangyiming.disjob.quence.TaskExecuteException;

public class JobAction implements Runnable{

	private Node<DependDisJob> dependEJob ;
	private Scheduler scheduler ;
	public JobAction(Node<DependDisJob> dependEJob,Scheduler scheduler) {
		this.dependEJob = dependEJob ;
		this.scheduler = scheduler ;
	}
	public void run() {
		try {
			dependEJob.getVal().execute(new SchedulerParam());
			this.scheduler.notify(dependEJob);
		} catch (TaskExecuteException e) {
			e.printStackTrace();
		}
		
	}
}
