package com.huangyiming.disjob.register.core.service;

import java.util.List;
import java.util.Set;

import org.quartz.JobKey;
import org.quartz.SchedulerException;

import com.huangyiming.disjob.common.model.JobInfo;

/**
 * <pre>
 * 
 *  File: GeneralSchedulerService.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  普通调度器服务接口类
 * 
 *  Revision History
 *
 *  Date：		2016年5月23日
 *  Author：		Disjob
 *
 * </pre>
 */
public interface GeneralSchedulerService {
	/**创建任务
	 * @param job  JobInfo对象的任务信息
	 * @return 成功返回true,否则返回false
	 */
	boolean create(JobInfo job);
	/**删除任务
	 * @param jobName  任务名
	 * @param jobGroup  任务组
	 * @return 成功返回true,否则返回false
	 */
	boolean delete(String jobName,String jobGroup);
	/**删除组任务
	 * @param jobGroup 任务组名
	 * @return 成功返回true,否则返回false
	 */
	boolean deleteJobGroup(String jobGroup);
	/**
	 * 更新任务
	 * @param job	JobInfo对象的任务信息
	 * @return 成功返回true,否则返回false
	 */
	boolean update(JobInfo job);
	/**
	 * 暂停任务
	 * @param jobName 任务名
	 * @param jobGroup 任务组
	 * @return 成功返回true,否则返回false
	 */
	boolean pause(String jobName,String jobGroup);
	/**
	 * 恢复任务
	 * @param jobName 任务名
	 * @param jobGroup  任务组
	 * @return 成功返回true,否则返回false
	 */
	boolean resume(String jobName,String jobGroup);
	/**
	 * 获取所有任务信息
	 * @return   返回JobInfo类型列表的任务信息
	 */
	public List<JobInfo> findAll();
	/**
	 * 获取正在执行的所有任务信息
	 * @return   返回正在执行的JobInfo类型列表的任务信息
	 */
	List<JobInfo> findAllExecuting();
	/**
	 * 查找指定的任务
	 * @param jobName 任务名
	 * @param jobGroup 任务组
	 * @return 返回JobInfo类型列表的任务信息
	 */
	List<JobInfo> findById(String jobName,String jobGroup);

	boolean isExistScheduler(JobInfo job) throws SchedulerException;

	Set<JobKey> getJobKeysByGroupName(String groupName) throws SchedulerException;

}
