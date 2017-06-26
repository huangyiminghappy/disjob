package com.huangyiming.disjob.rpc.utils;

import java.util.Date;

import com.huangyiming.disjob.rpc.codec.Response;

/**
 * rpc客户端调度后保存的实体
 * @author Disjob
 *
 */
public class ScheduleResponse implements Response {
	
	private String requestId;
	private String groupName;
	private String jobName;
	private Date scheduleStartTime;
	private Date scheduleEndTime;
	private String exception;
	private String scheduleServerIp;
	private String executeServerIp;
	private char status;
	private long timeOut ;
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

	public Date getScheduleStartTime() {
		return scheduleStartTime;
	}

	public void setScheduleStartTime(Date scheduleStartTime) {
		this.scheduleStartTime = scheduleStartTime;
	}

	public Date getScheduleEndTime() {
		return scheduleEndTime;
	}

	public void setScheduleEndTime(Date scheduleEndTime) {
		this.scheduleEndTime = scheduleEndTime;
	}

	public String getScheduleServerIp() {
		return scheduleServerIp;
	}

	public void setScheduleServerIp(String scheduleServerIp) {
		this.scheduleServerIp = scheduleServerIp;
	}
	
	public String getExecuteServerIp() {
		return executeServerIp;
	}

	public void setExecuteServerIp(String executeServerIp) {
		this.executeServerIp = executeServerIp;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	@Override
	public Object getValue() {
		return null;
	}

	@Override
	public String getException() {
		return exception;
	}

	@Override
	public String getRequestId() {
		return requestId;
	}

	@Override
	public long getProcessTime() {
		return 0;
	}

	@Override
	public void setProcessTime(long time) {

	}

	@Override
	public long getTimeout() {
		return this.timeOut;
	}
	
	public void setTimeOut(long timeOut) {
		this.timeOut = timeOut;
	}

	public char getStatus() {
		return status;
	}

	public void setStatus(char status) {
		this.status = status;
	}
	
}
