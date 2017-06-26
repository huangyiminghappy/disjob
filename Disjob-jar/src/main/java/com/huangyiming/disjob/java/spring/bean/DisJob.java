package com.huangyiming.disjob.java.spring.bean;

public class DisJob {

	private String name;
	
	private String group;
	
	private String classPath;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getClassPath() {
		return classPath;
	}

	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}

	public DisJob(String name, String group, String classPath) {
		super();
		this.name = name;
		this.group = group;
		this.classPath = classPath;
	}
}
