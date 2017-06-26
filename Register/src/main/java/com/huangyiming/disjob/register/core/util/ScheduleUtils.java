package com.huangyiming.disjob.register.core.util;

import java.util.Date;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.calendar.AnnualCalendar;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;

import com.huangyiming.disjob.common.model.JobInfo;
import com.huangyiming.disjob.common.util.DateUtil;
import com.huangyiming.disjob.common.util.LoggerUtil;

/**
 * <pre>
 * 
 *  File: ScheduleUtils.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  调度器服务辅助工具实现类
 * 
 *  Revision History
 *
 *  Date：		2016年5月25日
 *  Author：		Disjob
 *
 * </pre>
 */
public class ScheduleUtils {
	/**
	 * 获取触发器key
	 * 
	 * @param jobName
	 * @param jobGroup
	 * @return
	 */
	public static TriggerKey getTriggerKey(String jobName, String jobGroup) {
		return TriggerKey.triggerKey(jobName, jobGroup);
	}
	/**
	 * 获取jobKey
	 * @param jobName 任务名称
	 * @param jobGroup 任务组
	 * @return 根据任务名和任务组生成一个Jobkey
	 */
	public static JobKey getJobKey(String jobName, String jobGroup) {
		return JobKey.jobKey(jobName, jobGroup);
	}
	/**检查job是否存在
	 * @param scheduler	调度器
	 * @param jobName	任务名称
	 * @param jobGroup	任务组
	 * @return	存在返回true，否则返回false
	 */
	public static boolean isExistJob(Scheduler scheduler, String jobName, String jobGroup)  throws SchedulerException{
		if(jobName == null || jobGroup == null)
			return false;
		try {
			return scheduler.checkExists(JobKey.jobKey(jobName, jobGroup));
		} catch (SchedulerException e) {
			return false;
		}
	}
	/**
	 * 获取表达式触发器
	 *
	 * @param scheduler 调度器
	 * @param jobName	任务名称
	 * @param jobGroup	任务组
	 * @return cron 返回job对应的CronTrigger或null
	 */
	public static CronTrigger getCronTrigger(Scheduler scheduler, String jobName, String jobGroup) {
		try {
			TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
			return (CronTrigger) scheduler.getTrigger(triggerKey);
		} catch (SchedulerException e) {
			return null;
		}
	}

	/**
	 * 根据ScheduleJob的相关属性创建job，并放入到调度器中
	 * @param scheduler 调度器
	 * @param jobInfo	job相关参数
	 * @throws SchedulerException 处理异常
	 */
	@SuppressWarnings("unchecked")
	public static void createJob(Scheduler scheduler, final JobInfo jobInfo) throws SchedulerException{
		checkJobInfo(jobInfo);
		LoggerUtil.info("scheduleJob is "+jobInfo);
		JobDetail jobDetail = JobBuilder.newJob(jobInfo.getJobClass()).withIdentity(jobInfo.getJobName(), jobInfo.getGroupName())
				.setJobData(packToJobDataMap(jobInfo)).build();

		CronTriggerImpl trigger = null;
 		if(jobInfo.isMisfire()){//错过执行处理策略：从上一次执行的时间开始调度
			trigger = (CronTriggerImpl)TriggerBuilder.newTrigger().withIdentity(jobInfo.getJobName(), jobInfo.getGroupName())
			.withSchedule(CronScheduleBuilder.cronSchedule(jobInfo.getCronExpression()).withMisfireHandlingInstructionIgnoreMisfires()).build();
			
			if(StringUtils.isNoneBlank(jobInfo.getLastFireTime())){//如果存在lastfiretime则代表需要从lastfiretime开始执行错过执行的job
				try{
			    	String lastFireTime = jobInfo.getLastFireTime();
			    	trigger.setPreviousFireTime(DateUtil.parse(lastFireTime));
			    	trigger.setStartTime(DateUtil.parse(lastFireTime));
			    	
			    	AnnualCalendar holidays = new AnnualCalendar();
			    	java.util.Calendar cal = java.util.Calendar.getInstance();
			    	cal.setTime(DateUtil.parse(lastFireTime));
			    	holidays.setDayExcluded(cal, false);
		 	    	trigger.updateWithNewCalendar(holidays, 10);//设置为10ms全局周期,则与trigger策略无关,则会自动的把错过执行的时间段执行
				}catch(Exception e){
					LoggerUtil.error("job misfire occur error ,lastFireTime is "+jobInfo.getLastFireTime(), e);
				}
			}
		}else{
			trigger = (CronTriggerImpl)TriggerBuilder.newTrigger().withIdentity(jobInfo.getJobName(), jobInfo.getGroupName())
					.withSchedule(CronScheduleBuilder.cronSchedule(jobInfo.getCronExpression()).withMisfireHandlingInstructionDoNothing()).build();
		}
       
 		if(StringUtils.isNoneBlank(jobInfo.getEndTime())){
 			long endTime = DateUtil.parse(jobInfo.getEndTime()).getTime();
 			if(endTime > trigger.getStartTime().getTime()){
 				trigger.setEndTime(new Date(endTime));
 			}
 		}
 		
		try {
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			throw new SchedulerException("创建任务失败，"+e.getMessage());
		}
		if(jobInfo.isFireNow()){//是否马上触发
			try {
				scheduler.triggerJob(jobDetail.getKey());
			} catch (SchedulerException e) {
				throw new SchedulerException("任务马上调度执行失败，"+e.getMessage());
			}
		}
	}
	private static void checkJobInfo(final JobInfo scheduleJob)
			throws SchedulerException {
		if(scheduleJob == null)
			throw new SchedulerException("请输入参数");
		if(scheduleJob.getJobName() == null || scheduleJob.getJobName().length() < 1)//无效
			throw new SchedulerException("任务名称无效");
		if(scheduleJob.getCronExpression() == null || !CronExpression.isValidExpression(scheduleJob.getCronExpression()))//无效
			throw new SchedulerException("groupName:" + scheduleJob.getGroupName() + " jobName:" + scheduleJob.getJobName() + "无效的cron时间表达式: "+scheduleJob.getCronExpression());
		if(scheduleJob.getJobClass() == null || !Job.class.isAssignableFrom(scheduleJob.getJobClass()))//为null或不是Job的子类
			throw new SchedulerException("无效的JobClass");
	}

	/**
	 * 运行一次任务
	 * 
	 * @param scheduler	调度器
	 * @param jobName	任务名称
	 * @param jobGroup	任务组
	 * @throws SchedulerException 处理异常
	 */
	public static void runOnceJob(Scheduler scheduler, String jobName, String jobGroup) throws SchedulerException{
		if(jobName == null || jobName.length() < 1)
			throw new SchedulerException("任务名称无效");
		if(jobGroup == null || jobGroup.length() < 1)
			throw new SchedulerException("任务组名无效");
		JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
		try {
			scheduler.triggerJob(jobKey);
		} catch (SchedulerException e) {
			throw new SchedulerException("任务运行一次失败，"+e.getMessage());
		}
	}

	/**
	 * 暂停任务
	 * @param scheduler	调度器
	 * @param jobName	任务名称
	 * @param jobGroup	任务组
	 * @throws SchedulerException 处理异常
	 */
	public static void pauseJob(Scheduler scheduler, String jobName, String jobGroup) throws SchedulerException{
		if(jobName == null || jobName.length() < 1)
			throw new SchedulerException("任务名称无效");
		if(jobGroup == null || jobGroup.length() < 1)
			throw new SchedulerException("任务组名无效");
		JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
		try {
			scheduler.pauseJob(jobKey);
		} catch (SchedulerException e) {
			throw new SchedulerException("暂停任务失败，"+e.getMessage());
		}
	}

	/**
	 * 恢复任务
	 * @param scheduler	调度器
	 * @param jobName	任务名称
	 * @param jobGroup	任务组
	 * @throws SchedulerException 处理异常
	 */
	public static void resumeJob(Scheduler scheduler, String jobName, String jobGroup) throws SchedulerException{
		if(jobName == null || jobName.length() < 1)
			throw new SchedulerException("任务名称无效");
		if(jobGroup == null || jobGroup.length() < 1)
			throw new SchedulerException("任务组名无效");
		JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
		try {
			scheduler.resumeJob(jobKey);
		} catch (SchedulerException e) {
			throw new SchedulerException("恢复任务失败，"+e.getMessage());
		}
	}

	/**
	 * 删除定时任务
	 * @param scheduler   调度器
	 * @param jobName	任务名称
	 * @param jobGroup	任务组
	 * @throws SchedulerException 处理异常
	 */
	public static void deleteJob(Scheduler scheduler, String jobName, String jobGroup) throws SchedulerException{
		if(jobName == null || jobName.length() < 1)
			throw new SchedulerException("任务名称无效");
		if(jobGroup == null || jobGroup.length() < 1)
			throw new SchedulerException("任务组名无效");
		try {
			LoggerUtil.info("delete group:"+jobGroup + ",jobName:"+jobName);
			scheduler.deleteJob(getJobKey(jobName, jobGroup));
		} catch (SchedulerException e) {
			throw new SchedulerException("删除任务失败，"+e.getMessage());
		}
	}
	/**
	 * 删除任务组的任务
	 * @param scheduler   调度器
	 * @param jobGroup	任务组
	 * @throws SchedulerException 处理异常
	 */
	public static void deleteJobGroup(Scheduler scheduler, String jobGroup) throws SchedulerException{
		if(jobGroup == null || jobGroup.length() < 1)
			return;
		GroupMatcher<JobKey> matcher = GroupMatcher.jobGroupEquals(jobGroup);//匹配
		Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);//查找所有job
		//scheduler.deleteJobs(jobKeys);批量删除
		for (JobKey jobKey : jobKeys) {//循环删除
			try {
				LoggerUtil.info("delete jobkey is "+jobKey.toString());
				scheduler.deleteJob(jobKey);
			} catch (SchedulerException e) {
				throw new SchedulerException("删除任务失败，"+e.getMessage());
			}
		}
	}
	/**
	 * 封包参数，把scheduleJob中的一些参数封装到JobDataMap中返回
	 * @param scheduleJob 从该对象中区参数
	 * @return 返回封装了相关参数的JobDataMap
	 */
	public static JobDataMap packToJobDataMap(JobInfo scheduleJob){
		JobDataMap map = new JobDataMap();
		if(scheduleJob != null){
			map.put(CoreConstants.JOB_PARAMS, scheduleJob.getParameters());
			map.put(CoreConstants.GROUP_NAME, scheduleJob.getGroupName());
			map.put(CoreConstants.JOB_NAME, scheduleJob.getJobName());
			map.put(CoreConstants.JOB_PATH, scheduleJob.getJobPath());
			map.put(CoreConstants.IS_BROADCAST, scheduleJob.isIfBroadcast());
		}
		return map;
	}
	/**
	 * 解包JobDataMap，封装相关的参数到ScheduleJob中
	 * @param scheduleJob job相关参数管理bean
	 * @param from	参数来自JobDataMap对象的相关参数
	 */
	public static void unPackFromJobDataMap(JobInfo scheduleJob,JobDataMap from){
		if(scheduleJob != null && from != null){
			scheduleJob.setParameters(from.getString(CoreConstants.JOB_PARAMS));
		}
	}
	
	/**
	 * 根据job信息获取job详情
	 *
	 * @param scheduler
	 * @param job
	 * @return
	 * @throws SchedulerException
	 */
	public static JobDetail getJobDetail(Scheduler scheduler, JobInfo job)
			throws SchedulerException {
		return scheduler.getJobDetail(JobKey.jobKey(job.getJobName(),
				job.getGroupName()));
	}
	
	
	/**
	 * 根据组名获取已经装在到RAM中的jobKey集合,与实际该组在slave对应借点job对比避免重复装载或者少装载的情况
	 * TODO.
	 *
	 * @param scheduler
	 * @param groupName
	 * @return
	 * @throws SchedulerException
	 */
	public static Set<JobKey> getJobKeysByGroupName(Scheduler scheduler,
			String groupName) throws SchedulerException {

		return scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName));
	}
	public static String  getKey(JobInfo job){ 
		return getKey(job.getGroupName(),job.getJobName());
	}
	public static String  getKey(String groupName,String jobName){ 
		return groupName+":"+jobName;
	}
 }
