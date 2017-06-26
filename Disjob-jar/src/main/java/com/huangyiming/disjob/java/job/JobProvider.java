package com.huangyiming.disjob.java.job;

public interface JobProvider {

	public DisJob getDisJobAction(String className, String methodName) ;
}
