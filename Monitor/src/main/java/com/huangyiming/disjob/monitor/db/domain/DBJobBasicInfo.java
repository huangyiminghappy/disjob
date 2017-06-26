package com.huangyiming.disjob.monitor.db.domain;

import java.io.Serializable;

import com.huangyiming.disjob.common.util.DateUtil;
import com.huangyiming.disjob.monitor.util.DBCommonUtil;

/**
 * <pre>
 * 
 *  File: DBJobExeFail.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  任务执行失败记录表，记录任务标志id、uuid（主键）、创建时间、更新时间、服务器ip、启动时间、耗时、异常位置、异常类型、异常原因
 * 
 *  Revision History
 *
 *  Date：		2016年6月23日
 *  Author：		Disjob
 *
 * </pre>
 */
public class DBJobBasicInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String uuid;//uuid，任务唯一标识
	private String groupName;//任务组名称
	private String jobName;//任务名称
	private String createdAt;//创建的时间
	private String updatedAt;//最后更新时间
	private String scheduleSip;//调度服务器IP地址
	private String businessSip;//业务服务器IP地址
	private String scheduleStart;//调度开始时间
	private String scheduleEnd;//调度结束时间
	private String executeStart;//执行开始时间
	private String executeEnd;//执行结束时间
	private long timeConsuming;//耗时
	private char currentStatus = DBCommonUtil.JobStatus.Success.get();//当前状态
	private String errorLocation;//异常位置
	private String errorType;//异常类型
	private String errorReason;//异常原因
	private long timeOut;//超时
	private int isTimeout;//是否已经超时
	
	private String jobRecvTime;//rpc服务端接收任务的时间
	private int killprocess;//终止job标记,0:否,1:终止
	private String sharingRequestId;//分片job的requestId
	
	public String getSharingRequestId() {
		return sharingRequestId;
	}
	public void setSharingRequestId(String sharingRequestId) {
		this.sharingRequestId = sharingRequestId;
	}
	//
	public DBJobBasicInfo(){
		
	}
	public DBJobBasicInfo(String uuid,String executeStart,String executeEnd,char currentStatus,String errorReason,String errorType,String jobRecvTime,String sharingRequestId){
		this.uuid = uuid;
		this.executeStart = executeStart;
		this.executeEnd = executeEnd;
 		this.errorReason =  errorReason;
 		if(currentStatus != 'v'){
  			this.currentStatus = currentStatus;
 		}
 		this.errorType =  errorType;
 		long processTime = DateUtil.getExcuteTime(executeStart, executeEnd);
 		this.timeConsuming = processTime;
 		this.jobRecvTime = jobRecvTime;
 		this.sharingRequestId = sharingRequestId;
 		
	}
	
	
	public int getKillprocess() {
		return killprocess;
	}
	public void setKillprocess(int killprocess) {
		this.killprocess = killprocess;
	}
	public String getJobRecvTime() {
		return jobRecvTime;
	}
	public void setJobRecvTime(String jobRecvTime) {
		this.jobRecvTime = jobRecvTime;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}
	public String getScheduleSip() {
		return scheduleSip;
	}
	public void setScheduleSip(String scheduleSip) {
		this.scheduleSip = scheduleSip;
	}
	public String getBusinessSip() {
		return businessSip;
	}
	public void setBusinessSip(String businessSip) {
		this.businessSip = businessSip;
	}
	public String getScheduleStart() {
		return scheduleStart;
	}
	public void setScheduleStart(String scheduleStart) {
		this.scheduleStart = scheduleStart;
	}
	public String getScheduleEnd() {
		return scheduleEnd;
	}
	public void setScheduleEnd(String scheduleEnd) {
		this.scheduleEnd = scheduleEnd;
	}
	public String getExecuteStart() {
		return executeStart;
	}
	public void setExecuteStart(String executeStart) {
		this.executeStart = executeStart;
	}
	public String getExecuteEnd() {
		return executeEnd;
	}
	public void setExecuteEnd(String executeEnd) {
		this.executeEnd = executeEnd;
	}
	public long getTimeConsuming() {
		return timeConsuming;
	}
	public void setTimeConsuming(long timeConsuming) {
		this.timeConsuming = timeConsuming;
	}
	public char getCurrentStatus() {
		return currentStatus;
	}
	public void setCurrentStatus(char currentStatus) {
		this.currentStatus = currentStatus;
	}
	public String getErrorLocation() {
		return errorLocation;
	}
	public void setErrorLocation(String errorLocation) {
		this.errorLocation = errorLocation;
	}
	public String getErrorType() {
		return errorType;
	}
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}
	public String getErrorReason() {
		return errorReason;
	}
	public void setErrorReason(String errorReason) {
		this.errorReason = errorReason;
	}
	public long getTimeOut() {
		return timeOut;
	}
	public void setTimeOut(long timeOut) {
		this.timeOut = timeOut;
	}
	public int getIsTimeout() {
		return isTimeout;
	}
	public void setIsTimeout(int isTimeout) {
		this.isTimeout = isTimeout;
	}
	@Override
	public String toString() {
		return "DBJobBasicInfo [uuid=" + uuid + ", groupName=" + groupName + ", jobName=" + jobName + ", createdAt="
				+ createdAt + ", updatedAt=" + updatedAt + ", scheduleSip=" + scheduleSip + ", businessSip="
				+ businessSip + ", scheduleStart=" + scheduleStart + ", scheduleEnd=" + scheduleEnd + ", executeStart="
				+ executeStart + ", executeEnd=" + executeEnd + ", timeConsuming=" + timeConsuming + ", currentStatus="
				+ currentStatus + ", errorLocation=" + errorLocation + ", errorType=" + errorType + ", errorReason="
				+ errorReason + ", timeOut=" + timeOut + ", isTimeout=" + isTimeout + ", jobRecvTime=" + jobRecvTime
				+ ", killprocess=" + killprocess + "]";
	}
	 
}