package com.huangyiming.disjob.register.core.action;

import org.apache.commons.lang3.StringUtils;

import com.huangyiming.disjob.quence.Action;
import com.huangyiming.disjob.quence.TaskExecuteException;
import com.huangyiming.disjob.register.core.util.RegisterSpringWorkFactory;
import com.huangyiming.disjob.register.domain.Job;
import com.huangyiming.disjob.slaver.utils.SlaveUtils;

public class UpdateLastFireTimeAction extends Action{
	private Job job ;
	public UpdateLastFireTimeAction(Job job) {
		this.job = job ;
	}
	@Override
	public void execute() throws TaskExecuteException {
		if(job!=null&&StringUtils.isNotEmpty(job.getGroupName())&&StringUtils.isNotEmpty(job.getJobName())){
			SlaveUtils.setLastFireTimeByGroupNameAndJobName(RegisterSpringWorkFactory.getJobExecutedThreadPoolService().getClient(), job.getGroupName(), job.getJobName());
		}
	}

}
