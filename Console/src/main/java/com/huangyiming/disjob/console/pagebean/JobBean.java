package com.huangyiming.disjob.console.pagebean;

import com.huangyiming.disjob.register.domain.Job;

public class JobBean extends Job{
	private static final long serialVersionUID = 1L;

	private String phpFilePath;
	
	private String className;
	
	private String methodName;
	
	private int version;

	public String getPhpFilePath() {
		return phpFilePath;
	}

	public void setPhpFilePath(String phpFilePath) {
		this.phpFilePath = phpFilePath;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "JobBean [phpFilePath=" + phpFilePath + ", className=" + className + ", methodName=" + methodName
				+ ", version=" + version + "]";
	}
}
