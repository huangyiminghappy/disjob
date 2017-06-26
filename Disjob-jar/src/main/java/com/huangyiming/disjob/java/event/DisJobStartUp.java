package com.huangyiming.disjob.java.event;

import java.util.concurrent.locks.ReentrantLock;

import com.huangyiming.disjob.event.AbstractEventObject;
import com.huangyiming.disjob.event.ObjectEvent;
import com.huangyiming.disjob.java.DisJobConstants;
import com.huangyiming.disjob.java.ProviderClassName;
import com.huangyiming.disjob.java.bean.StartUpConfig;
import com.huangyiming.disjob.java.listener.DisJobInitConfigListener;
import com.huangyiming.disjob.java.listener.DisJobInitServerListener;
import com.huangyiming.disjob.java.listener.DisJobPublishJobListener;
import com.huangyiming.disjob.java.listener.DisJobSchedulerServerCheckListener;
import com.huangyiming.disjob.java.listener.DisJobStartFinishListener;
import com.huangyiming.disjob.java.listener.DisJobStartUpListener;
import com.huangyiming.disjob.java.listener.DisJobStopListener;
import com.huangyiming.disjob.java.service.DisJobConfigService;
import com.huangyiming.disjob.java.utils.Log;

public class DisJobStartUp extends AbstractEventObject<StartUpConfig> implements ProviderClassName{
	
	public static enum StartUpState{
		UNSTARTUP("未开启"),
		STARTING("正在开启"),
		STARTUP_FINISH("开启完成") ;
		private String desc ;
		StartUpState(String desc){
			this.desc = desc;
		}
		public String getDesc() {
			return desc;
		}
		public void setDesc(String desc) {
			this.desc = desc;
		}
	};//未开启，正在开启，开启完成
	
	private StartUpState currentState = StartUpState.UNSTARTUP; //set the default status
	private ReentrantLock lock = new ReentrantLock();
	private long start = 0;
	public DisJobStartUp() {
		start = System.currentTimeMillis();
	}
	
	/**
	 * whether startup by spring or context listener ,this method can only be called once
	 * @param startUpConfig
	 */
	public void notifyStartUp(StartUpConfig startUpConfig){
		boolean isCanStartUp = true ;
		lock.lock();
		try{
			if(this.currentState == StartUpState.UNSTARTUP){
				this.currentState = StartUpState.STARTING;
			}else{
				isCanStartUp = false ;
			}
		}finally{
			lock.unlock();
		}
		if(isCanStartUp){
			DisJobConfigService.configProperties.setProperty(DisJobConstants.StartUpType.START_UP_TYPE, String.valueOf(startUpConfig.getType()));
			ObjectEvent<StartUpConfig> startEvent = new ObjectEvent<StartUpConfig>(startUpConfig,EventType.START_UP);
			this.notifyListeners(startEvent);
		}
	}
	
	/**
	 * 
	 */
	public void notifyStaUpFinish(){
		ObjectEvent<StartUpConfig> finishEvent = new ObjectEvent<StartUpConfig>(null, EventType.START_FINISH);
		this.notifyListeners(finishEvent);
	}
	
	/**
	 * 
	 */
	public void notifyStop() {
		ObjectEvent<StartUpConfig> stopEvent = new ObjectEvent<StartUpConfig>(null, EventType.DISJOB_STOP);
		this.notifyListeners(stopEvent);
	}
	
	public String getClassName(){
		
		return this.getClass().getName();
	}

	public StartUpState getCurrentState() {
		return currentState;
	}
	
	public void setCurrentState(StartUpState currentState) {
		lock.lock();
		try{
			this.currentState = currentState;
			if(this.currentState == StartUpState.STARTUP_FINISH){
				Log.debug(getClassName() + " disJob start success has take time:"+(System.currentTimeMillis()-start)/1000 + " s");
			}
		}finally{
			lock.unlock();
		}
	}

	/**
	 * if you have some listeners then attach these in here 
	 */
	@Override
	public void attachListener() {
		this.addListener(new DisJobInitConfigListener(), EventType.START_UP);
		this.addListener(new DisJobSchedulerServerCheckListener(), EventType.START_UP);
		this.addListener(new DisJobInitServerListener(), EventType.START_UP);
		this.addListener(new DisJobPublishJobListener(), EventType.START_UP);
		this.addListener(new DisJobStartUpListener(), EventType.START_UP);
		
		this.addListener(new DisJobStopListener(this), EventType.DISJOB_STOP);
		this.addListener(new DisJobStartFinishListener(this), EventType.START_FINISH);
	}
}
