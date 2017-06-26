package com.huangyiming.disjob.register.core.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * 
 *  File: JobSyncFactory.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  同步的任务工厂类：负责执行同步任务
 * 
 *  Revision History
 *
 *  Date：		2016年5月23日
 *  Author：		Disjob
 *
 * </pre>
 */
public class JobSyncFactory implements Job {

    /* 日志对象 */
    private static final Logger LOG = LoggerFactory.getLogger(JobSyncFactory.class);

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

    	LOG.info("JobSyncFactory execute");
    	LOG.info("jobName:" + jobExecutionContext.getJobDetail().getJobDataMap());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
