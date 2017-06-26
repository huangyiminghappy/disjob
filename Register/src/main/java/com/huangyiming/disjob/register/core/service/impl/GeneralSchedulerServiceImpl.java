package com.huangyiming.disjob.register.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.huangyiming.disjob.common.model.JobInfo;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.register.auth.node.GlobalAuthNode;
import com.huangyiming.disjob.register.center.RegistryExceptionHandler;
import com.huangyiming.disjob.register.core.service.GeneralSchedulerService;
import com.huangyiming.disjob.register.core.util.CoreConstants;
import com.huangyiming.disjob.register.core.util.ScheduleUtils;
import com.huangyiming.disjob.register.repository.watch.listener.ConnectionStateListenerImpl;

/**
 * <pre>
 * 
 *  File: QuartzJobService.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  对外提供操作quartz的普通调度器服务，实现job的添加、删除、更新、暂停、恢复、查询等操作
 *  并声明为服务
 * 
 *  Revision History
 *
 *  Date：		2016年5月23日
 *  Author：		Disjob
 *
 * </pre>
 */
@Service("generalSchedulerService")
public final class GeneralSchedulerServiceImpl implements GeneralSchedulerService{
	private static String ERR = "[DisJob-core][GeneralSchedulerServiceImpl] exception : %s";

	/** 调度器Bean */
	@Autowired
	private Scheduler scheduler;
	private CuratorFramework client;
	
	@Value("${zk.host}")
	private String ZKHost;

	public GeneralSchedulerServiceImpl() {
	}

	public CuratorFramework getClient() {
		return client;
	}

	@PostConstruct
	public void init() {
		LoggerUtil.debug("DisJob server client init, ZK server list is:"+ ZKHost);
		Builder builder = CuratorFrameworkFactory.builder().connectString(ZKHost).retryPolicy(new ExponentialBackoffRetry(1500, 3));
		builder.sessionTimeoutMs(6000);
		builder.connectionTimeoutMs(6000);
		builder.authorization(new GlobalAuthNode(ZKHost).getAuthInfos());
		client = builder.build();
		client.getConnectionStateListenable().addListener(new ConnectionStateListenerImpl());
		client.start();
		try {
			client.blockUntilConnected(1, TimeUnit.SECONDS);
			scheduler.getContext().put("client", client);
		} catch (final Exception ex) {
			RegistryExceptionHandler.handleException(ex);
		}
	}

	

	/**创建任务
	 * @param job  JobInfo对象的任务信息
	 * @return 成功返回true,否则返回false
	 */
	public boolean create(JobInfo job){
		try {
			ScheduleUtils.createJob(scheduler, job);
		} catch (SchedulerException e) {
			LoggerUtil.error(String.format(ERR, new Object[]{e.getMessage()}));
			return false;
		}
		return true;
	}
	/**删除任务
	 * @param jobName  任务名
	 * @param jobGroup  任务组
	 * @return 成功返回true,否则返回false
	 */
	public boolean delete(String jobName,String jobGroup){
		try {
			ScheduleUtils.deleteJob(scheduler, jobName, jobGroup);
		} catch (SchedulerException e) {
			LoggerUtil.error(String.format(ERR, new Object[]{e.getMessage()}));
			return false;
		}
		return true;
	}
	/**删除组任务
	 * @param jobGroup 任务组名
	 * @return 成功返回true,否则返回false
	 */
	public boolean deleteJobGroup(String jobGroup) {
		try {
			ScheduleUtils.deleteJobGroup(scheduler, jobGroup);
		} catch (SchedulerException e) {
			LoggerUtil.error(String.format(ERR, new Object[]{e.getMessage()}));
			return false;
		}
		return true;
	}
	/**
	 * 更新任务:如果存在则先删除，再添加
	 * @param job	JobInfo对象
	 * @return 成功返回true,否则返回false
	 */
	public boolean update(JobInfo job){
		try{
			if(ScheduleUtils.isExistJob(scheduler, job.getJobName(), job.getGroupName())){//存在，则先删除
				delete(job.getJobName(),job.getGroupName());
			}
			ScheduleUtils.createJob(scheduler, job);//在创建
		} catch (SchedulerException e) {
			LoggerUtil.error(String.format(ERR, new Object[]{e.getMessage()}));
			return false;
		}
		return true;
	}
	/**
	 * 暂停任务
	 * @param jobName 任务名
	 * @param jobGroup 任务组
	 * @return 成功返回true,否则返回false
	 */
	public boolean pause(String jobName,String jobGroup){
		try{
			ScheduleUtils.pauseJob(scheduler, jobName, jobGroup);
		} catch (SchedulerException e) {
			LoggerUtil.error(String.format(ERR, new Object[]{e.getMessage()}));
			return false;
		}
		return true;
	}
	/**
	 * 恢复任务
	 * @param jobName 任务名
	 * @param jobGroup  任务组
	 * @return 成功返回true,否则返回false
	 */
	public boolean resume(String jobName,String jobGroup){
		try{
			ScheduleUtils.resumeJob(scheduler, jobName, jobGroup);
		} catch (SchedulerException e) {
			LoggerUtil.error(String.format(ERR, new Object[]{e.getMessage()}));
			return false;
		}
		return true;
	}
	/**
	 * 获取所有任务信息：查询所有组的job，根据job查询相关的trigger，然后封装程ScheduleJob列表返回
	 * @return   返回ScheduleJob类型列表的任务信息
	 */
	public List<JobInfo> findAll(){
		List<JobInfo> jobList = new ArrayList<JobInfo>();
		GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();//匹配所有组
		try {
			Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);//查找所有job
			for (JobKey jobKey : jobKeys) {
				List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);//查找job关联的所有触发器
				for (Trigger trigger : triggers) {
					JobInfo job = new JobInfo();
					job.setJobName(jobKey.getName());
					job.setGroupName(jobKey.getGroup());
					job.setTriggerName(trigger.getKey().getName());
					job.setTriggerGroup(trigger.getKey().getGroup());
					if (trigger instanceof CronTrigger) {//更新时间表达式
						job.setCronExpression(((CronTrigger) trigger).getCronExpression());
					}
					ScheduleUtils.unPackFromJobDataMap(job, scheduler.getJobDetail(jobKey).getJobDataMap());//解封装参数

					jobList.add(job);
				}
			}
		} catch (SchedulerException e) {
		}
		return jobList;
	}
	/**
	 * 获取正在执行的所有任务信息：查询正在执行的job，根据job查询关联的trigger，然后封装程ScheduleJob列表返回
	 * @return   返回正在执行的ScheduleJob类型列表的任务信息
	 */
	public List<JobInfo> findAllExecuting(){
		List<JobInfo> jobList = new ArrayList<JobInfo>();
		try {
			List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
			for (JobExecutionContext executingJob : executingJobs) {
				JobDetail jobDetail = executingJob.getJobDetail();//读取jobdetail
				JobKey jobKey = jobDetail.getKey();
				Trigger trigger = executingJob.getTrigger();//读取触发器

				JobInfo job = new JobInfo();
				job.setJobName(jobKey.getName());
				job.setGroupName(jobKey.getGroup());
				job.setTriggerName(trigger.getKey().getName());
				job.setTriggerGroup(trigger.getKey().getGroup());
				if (trigger instanceof CronTrigger) {//更新时间表达式
					job.setCronExpression(((CronTrigger) trigger).getCronExpression());
				}

				ScheduleUtils.unPackFromJobDataMap(job, scheduler.getJobDetail(jobKey).getJobDataMap());//解封装参数

				jobList.add(job);
			}
		} catch (SchedulerException e) {
		}
		return jobList;
	}
	/**
	 * 查找指定的任务：查询指定job名和job组名的job，根据job查询关联的trigger，然后封装程ScheduleJob列表返回
	 * @param jobName 任务名
	 * @param jobGroup 任务组
	 * @return 返回ScheduleJob类型列表的任务信息
	 */
	public List<JobInfo> findById(String jobName,String jobGroup){
		List<JobInfo> jobList = new ArrayList<JobInfo>();
		JobKey key = ScheduleUtils.getJobKey(jobName, jobGroup);
		try {
			JobDetail jobDetail = scheduler.getJobDetail(key);//查找job
			List<? extends Trigger> triggers = scheduler.getTriggersOfJob(key);//查找job关联的所有触发器
			for (Trigger trigger : triggers) {
				JobInfo job = new JobInfo();
				job.setJobName(key.getName());
				job.setGroupName(key.getGroup());
				job.setTriggerName(trigger.getKey().getName());
				job.setTriggerGroup(trigger.getKey().getGroup());
				if (trigger instanceof CronTrigger) {//更新时间表达式
					job.setCronExpression(((CronTrigger) trigger).getCronExpression());
				}
				ScheduleUtils.unPackFromJobDataMap(job, jobDetail.getJobDataMap());//解封装参数

				jobList.add(job);
			}
		} catch (SchedulerException e) {
		}
		return jobList;
	}

	@Override
	public boolean isExistScheduler(JobInfo job) throws SchedulerException {
		// TODO Auto-generated method stub
		/**
		 * 1:判断job是否存在
		 */
		if(!ScheduleUtils.isExistJob(scheduler, job.getJobName(), job.getGroupName())){
			return false;
		}
		Set<JobKey>set= ScheduleUtils.getJobKeysByGroupName(scheduler,  job.getGroupName());
		/**
		 * 2:获取对应jobdetail
		 */
		JobDetail jobDetail=ScheduleUtils.getJobDetail(scheduler, job);
		if(jobDetail==null||jobDetail.getJobDataMap()==null){
			return false;
		}
		String cronExpression=jobDetail.getJobDataMap().getString(CoreConstants.CRON_EXPRESSION);
		if(StringUtils.isEmpty(cronExpression)||!cronExpression.equals(job.getCronExpression())){
			return false;	
		}
		/**
		 * 3判断是否错过
		 */
	   
		boolean misFire=jobDetail.getJobDataMap().getBoolean(CoreConstants.MIS_FIRE);
		if(misFire!=job.isMisfire()){
			return false;
		}
		return true;
	}
	
	@Override
	public Set<JobKey> getJobKeysByGroupName(String groupName) throws SchedulerException {
		// TODO Auto-generated method stub
		return ScheduleUtils.getJobKeysByGroupName(scheduler, groupName);
	}
}
