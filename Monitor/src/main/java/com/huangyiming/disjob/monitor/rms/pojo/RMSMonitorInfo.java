package com.huangyiming.disjob.monitor.rms.pojo;

public class RMSMonitorInfo {
	private String group;
	private String jobName ;
	private String projectCode;
	private String pointCode;
	private String errorCode;
	private String token;
	private boolean isTest;
	private String description;
	private boolean available;
	private int sendCondition ;
	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	public String getPointCode() {
		return pointCode;
	}

	public void setPointCode(String pointCode) {
		this.pointCode = pointCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public boolean isTest() {
		return isTest;
	}

	public void setTest(boolean isTest) {
		this.isTest = isTest;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public int getSendCondition() {
		return sendCondition;
	}

	public void setSendCondition(int sendCondition) {
		this.sendCondition = sendCondition;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	@Override
	public String toString() {
		return "RMSMonitorInfo [projectCode=" + projectCode + ", pointCode=" + pointCode + ", errorCode=" + errorCode
				+ ", token=" + token + ", isTest=" + isTest + ", description=" + description + ", available="
				+ available + "]";
	}
}
