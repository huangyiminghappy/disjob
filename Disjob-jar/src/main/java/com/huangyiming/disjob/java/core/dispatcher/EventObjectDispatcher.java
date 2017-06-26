package com.huangyiming.disjob.java.core.dispatcher;

import com.huangyiming.disjob.java.bean.StartUpConfig;
import com.huangyiming.disjob.java.event.DynamicJobStartUp;
import com.huangyiming.disjob.java.event.DisJobStartUp;
import com.huangyiming.disjob.java.event.SpringRegisterJob;

public final class EventObjectDispatcher {

	private static final DisJobStartUp DISJOBSTA_DISJOB_START_UP = new DisJobStartUp();
	
	public static DisJobStartUp getDisJobStartUp(){
		return DISJOBSTA_DISJOB_START_UP ;
	}
	
	public static void dispatcherDisJobStartUp(StartUpConfig startUpConfig) {

		DISJOBSTA_DISJOB_START_UP.notifyStartUp(startUpConfig);
	}

	public static void dispatcherDisJobStop() {

		DISJOBSTA_DISJOB_START_UP.notifyStop();
	}

	public static void dispatcherDisJobStartUpFinish(){
		
		DISJOBSTA_DISJOB_START_UP.notifyStaUpFinish();
	}
	
	private static final DynamicJobStartUp DYNAMIC_JOB = new DynamicJobStartUp();
	public static void dispatcherDynamicJob(String fileName){

		DYNAMIC_JOB.notifyDynamicJob(fileName);
	}
	
	private static final SpringRegisterJob SPRING_REGISTER_JOB =  new SpringRegisterJob();
	public static void dispatcherSpringRegisterJob(String className){
		SPRING_REGISTER_JOB.notify(className);
	}
}
