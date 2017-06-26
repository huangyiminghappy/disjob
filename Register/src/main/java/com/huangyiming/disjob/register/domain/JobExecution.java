package com.huangyiming.disjob.register.domain;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.huangyiming.disjob.common.util.CustomDateSerializer;

/**
 * <pre>
 * 
 *  File: JobExecution.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  记录job执行的时间、状态，方便前端了解任务运行的情况
 * 
 *  Revision History
 *
 *  Date：		2016年6月8日
 *  Author：		Disjob
 *
 * </pre>
 */
public class JobExecution {
	/** 正在运行标志 */
	private boolean running;
	/** 已经完成标志 */
	private boolean completed;
	/** 已故障转移标志 */
	private boolean failover;
	/** 最近一次任务运行开始时间 */
	@JsonSerialize(using = CustomDateSerializer.class)  
    private Date lastBeginTime;
	/** 最近一次任务运行结束时间 */ 
	@JsonSerialize(using = CustomDateSerializer.class)  
    private Date lastCompleteTime;
	/** 最近一次调度开始时间 */
	@JsonSerialize(using = CustomDateSerializer.class)  
    private Date lastScheduleBeginTime;
	/** 最近一次调度结束时间 */
		@JsonSerialize(using = CustomDateSerializer.class)  
    private Date lastScheduleCompleteTime;
	/** 下次任务运行开始时间 */ 
	@JsonSerialize(using = CustomDateSerializer.class)  
    private Date nextFireTime;
	
	public boolean isRunning() {
		return running;
	}
	public void setRunning(boolean running) {
		this.running = running;
	}
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	public boolean isFailover() {
		return failover;
	}
	public void setFailover(boolean failover) {
		this.failover = failover;
	}
	public Date getLastBeginTime() {
		return lastBeginTime;
	}
	public void setLastBeginTime(Date lastBeginTime) {
		this.lastBeginTime = lastBeginTime;
	}
	public Date getLastCompleteTime() {
		return lastCompleteTime;
	}
	public void setLastCompleteTime(Date lastCompleteTime) {
		this.lastCompleteTime = lastCompleteTime;
	}
	public Date getLastScheduleBeginTime() {
		return lastScheduleBeginTime;
	}
	public void setLastScheduleBeginTime(Date lastScheduleBeginTime) {
		this.lastScheduleBeginTime = lastScheduleBeginTime;
	}
	public Date getLastScheduleCompleteTime() {
		return lastScheduleCompleteTime;
	}
	public void setLastScheduleCompleteTime(Date lastScheduleCompleteTime) {
		this.lastScheduleCompleteTime = lastScheduleCompleteTime;
	}
	public Date getNextFireTime() {
		return nextFireTime;
	}
	public void setNextFireTime(Date nextFireTime) {
		this.nextFireTime = nextFireTime;
	}
	@Override
	public String toString() {
		return "JobExecution{running="+running+", completed="+completed
				+", failover="+failover+", lastBeginTime="+lastBeginTime
				+", lastCompleteTime="+lastCompleteTime+", lastScheduleBeginTime="+lastScheduleBeginTime
				+", lastScheduleCompleteTime="+lastScheduleCompleteTime+", nextFireTime="+nextFireTime
				+"}";
	}
}
