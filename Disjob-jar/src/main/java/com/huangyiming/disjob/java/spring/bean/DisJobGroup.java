package com.huangyiming.disjob.java.spring.bean;

import java.util.List;

import com.huangyiming.disjob.java.bean.JobInfo;

public class DisJobGroup {
	
	private String id;
	
	private String name;
	
	private List<JobInfo> jobList;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<JobInfo> getJobList() {
		return jobList;
	}

	public void setJobList(List<JobInfo> jobList) {
		this.jobList = jobList;
	}
	
}
