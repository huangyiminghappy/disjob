package com.huangyiming.disjob.monitor.alarm.pojo;

import java.io.Serializable;

public class AlarmInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String jobGroup ;
	private String location ;
	private String type ;
	private String reason ;
	
	public AlarmInfo() {
	}

	public AlarmInfo(String jobGroup, String location, String type,String reason) {
		super();
		this.jobGroup = jobGroup;
		this.location = location;
		this.type = type;
		this.reason = reason;
	}


	public String getJobGroup() {
		return jobGroup;
	}
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}

	@Override
	public String toString() {
		return "AlarmInfo [jobGroup=" + jobGroup + ", location=" + location + ", type=" + type + ", reason=" + reason + "]";
	}
	
}
