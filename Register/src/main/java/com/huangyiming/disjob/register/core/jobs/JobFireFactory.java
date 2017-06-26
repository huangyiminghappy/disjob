package com.huangyiming.disjob.register.core.jobs;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.huangyiming.disjob.register.domain.Job;

public class JobFireFactory extends AbstractJobFactory{

	public void now(Job job){
		processJob(job.getGroupName(), job.getJobName(), job.getParameters(),job);			
	}

	private void processJob(String groupName, String jobName, String parameters, Job job) {
		processJob(groupName, jobName, parameters, job, job.isIfBroadcast());
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		throw new UnsupportedOperationException();
	}
}
