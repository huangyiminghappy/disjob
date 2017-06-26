
package com.huangyiming.disjob.register.core.jobs;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import com.huangyiming.disjob.common.Constants;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.monitor.util.MonitorSpringWorkFactory;
import com.huangyiming.disjob.register.core.util.CoreConstants;
import com.huangyiming.disjob.register.domain.Job;
import com.huangyiming.disjob.register.repository.ZnodeApiCuratorImpl;
import com.google.gson.Gson;
import com.huangyiming.disjob.monitor.db.domain.DBJobBasicInfo;

/**
 * <pre>
 * 
 *  File: JobFactory.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  任务工厂类,负责执行非同步任务
 * 
 *  Revision History
 *
 *  Date：		2016年5月23日
 *  Author：		Disjob
 *
 * </pre>
 */
public class StatelessJobFactory  extends AbstractJobFactory {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		CuratorFramework client = null;
		try {
			  client = (CuratorFramework) context.getScheduler().getContext().get("client");
		} catch (SchedulerException e) {
 			LoggerUtil.error("get CuratorFramework error",e);
 			return;
		}
		
		//基础数据准备
		String groupName = (String)context.getJobDetail().getJobDataMap().get(CoreConstants.GROUP_NAME);
        String jobName = (String)context.getJobDetail().getJobDataMap().get(CoreConstants.JOB_NAME);
        	
        ZnodeApiCuratorImpl znodeApi = new ZnodeApiCuratorImpl();
        String jobPath = znodeApi.makePath(Constants.ROOT, Constants.APP_JOB_NODE_ROOT,Constants.PATH_SEPARATOR+groupName,Constants.PATH_SEPARATOR+jobName, Constants.APP_JOB_NODE_CONFIG);
        String data =  znodeApi.getData(client, jobPath);
        Job zkJob = new Job();
        if(StringUtils.isNotEmpty(data)){
           zkJob =  new Gson().fromJson(data, Job.class);
        } 
        String params = zkJob.getParameters();
        context.getJobDetail().getJobDataMap().put(CoreConstants.JOB_PARAMS, params);
        context.getJobDetail().getJobDataMap().put(CoreConstants.JOB_CONFIG, zkJob) ;
        executeJobInternal(context.getJobDetail().getJobDataMap());
	}

}
