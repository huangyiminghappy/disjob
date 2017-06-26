package com.huangyiming.disjob.java.bean;

import java.io.Serializable;

/**
 * a job base information
 * @author Disjob
 *
 */
public class JobInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String className ;
	private String groupName;
	private String jobName ;
	private String quartz ;
	private boolean fireNow ;
	public JobInfo(String className, String groupName, String jobName,String quartz,boolean fireNow) {
		super();
		this.className = className;
		this.groupName = groupName;
		this.jobName = jobName;
		this.quartz = quartz ;
		this.fireNow = fireNow ;
	}
	public JobInfo(String className, String groupName, String jobName) {
		super();
		this.className = className;
		this.groupName = groupName;
		this.jobName = jobName;
	}
	public JobInfo() {
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
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
	
	public String getCron() {
		return quartz;
	}
	public void setCron(String cron) {
		this.quartz = cron;
	}
	
	public boolean isfireNow() {
		return fireNow;
	}
	public void setfireNow(boolean fireNow) {
		this.fireNow = fireNow;
	}
	@Override
	public String toString() {
		return "JobInfo [className=" + className + ", groupName=" + groupName + ", jobName=" + jobName + "]";
	}
	
}
