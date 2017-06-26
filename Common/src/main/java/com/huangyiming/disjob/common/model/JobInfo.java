package com.huangyiming.disjob.common.model;

/**
 * <pre>
 * 
 *  File: ScheduleJob.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  目标：存储job名称、组名、描述、表达式、参数、调度模式、任务状态等数据
 * 
 *  Revision History
 *
 *  Date：		2016年5月12日
 *  Author：		Disjob
 *
 * </pre>
 */
public class JobInfo {
	/** 任务组名称 */
	private String groupName;
	/** 任务名称 */
	private String jobName;
	/** 触发器名称 */
	private String triggerName;
	/** 触发器组 */
	private String triggerGroup;
	/** 任务运行时间表达式 */
	private String cronExpression;
	/** 任务调用路径 */
	private String jobPath;
	/** 任务分片数 */
	private int shardingCount;
	/** 分片参数 */
	private String shardingItemParameters;
	/** 是否开启故障转移 */
	private boolean failover;
	/** 错过执行是否马上调度 */
	private boolean misfire;
	/** 是否马上开始执行（比如设置时间是5分钟，那么5分钟后才执行），如果设置为true，则现在执行一次 */
	private boolean fireNow;
	/** 任务描述 */
	private String desc;
	/** 任务参数----预留 */
	private String parameters;
	 
	/** 任务状态0未激活 1可运行 2正在运行 3暂停   */
	private int jobStatus;
	// private Class<? extends Job> jobClass;不使用该方式的原因在于model不应该关联第三方的类、接口
	/** 实现了Job的任务业务类 */
	private Class jobClass;
	
	/**
	 * 任务上次执行时间
	 */
	private String lastFireTime;
	
	/**
	 * 任务结束时间
	 */
	private String endTime;

	/**
	 * 是否并行,false:否,true:是
	 */
    private boolean ifParallel;   
    
    /**
     * 是否广播模式
     */
    private boolean ifBroadcast = false;
	
	public boolean isIfParallel() {
		return ifParallel;
	}

	public void setIfParallel(boolean ifParallel) {
		this.ifParallel = ifParallel;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getLastFireTime() {
		return lastFireTime;
	}

	public void setLastFireTime(String lastFireTime) {
		this.lastFireTime = lastFireTime;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public String getJobPath() {
		return jobPath;
	}

	public void setJobPath(String jobPath) {
		this.jobPath = jobPath;
	}

	public int getShardingCount() {
		return shardingCount;
	}

	public void setShardingCount(int shardingCount) {
		this.shardingCount = shardingCount;
	}

	public String getShardingCtemCarameters() {
		return shardingItemParameters;
	}

	public void setShardingItemParameters(String shardingItemParameters) {
		this.shardingItemParameters = shardingItemParameters;
	}

	public boolean isFailover() {
		return failover;
	}

	public void setFailover(boolean failover) {
		this.failover = failover;
	}

	public boolean isMisfire() {
		return misfire;
	}

	public void setMisfire(boolean misfire) {
		this.misfire = misfire;
	}
	
	public boolean isFireNow() {
		return fireNow;
	}

	public void setFireNow(boolean fireNow) {
		this.fireNow = fireNow;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public String getShardingItemParameters() {
		return shardingItemParameters;
	}

 

	public int getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(int jobStatus) {
		this.jobStatus = jobStatus;
	}

	public String getTriggerName() {
		return triggerName;
	}

	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}

	public String getTriggerGroup() {
		return triggerGroup;
	}

	public void setTriggerGroup(String triggerGroup) {
		this.triggerGroup = triggerGroup;
	}

	public Class getJobClass() {
		return jobClass;
	}

	public void setJobClass(Class jobClass) {
		this.jobClass = jobClass;
	}

	public boolean isIfBroadcast() {
		return ifBroadcast;
	}

	public void setIfBroadcast(boolean ifBroadcast) {
		this.ifBroadcast = ifBroadcast;
	}

	@Override
	public String toString() {
		return "JobInfo [groupName=" + groupName + ", jobName=" + jobName + ", triggerName=" + triggerName
				+ ", triggerGroup=" + triggerGroup + ", cronExpression=" + cronExpression + ", jobPath=" + jobPath
				+ ", shardingCount=" + shardingCount + ", shardingItemParameters=" + shardingItemParameters
				+ ", failover=" + failover + ", misfire=" + misfire + ", fireNow=" + fireNow + ", desc=" + desc
				+ ", parameters=" + parameters + ", jobStatus=" + jobStatus + ", jobClass=" + jobClass
				+ ", lastFireTime=" + lastFireTime + ", endTime=" + endTime + ", ifParallel=" + ifParallel
				+ ", ifBroadcast=" + ifBroadcast + "]";
	}

}