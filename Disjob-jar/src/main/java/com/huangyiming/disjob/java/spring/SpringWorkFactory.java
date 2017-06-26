package com.huangyiming.disjob.java.spring;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.huangyiming.disjob.java.job.DisJob;
import com.huangyiming.disjob.java.spring.bean.DisJobGroup;

/**
 * @author Liu.ms
 * @date 2016-7-4 上午11:58:59
 */
@Service
public final class SpringWorkFactory {
	private static final SpringWorkFactory intance = new SpringWorkFactory();
	
	protected static ApplicationContext staticContext;
	private final static ConcurrentHashMap<String, Object> workObjectContainer = new ConcurrentHashMap<String, Object>();	
	
	private SpringWorkFactory() {
    }
	
	public static SpringWorkFactory getInstance(){
		
		return intance;
	}
	private Object getWorkObject(String serviceName){
		if(workObjectContainer.get(serviceName) != null){
			return workObjectContainer.get(serviceName);
		}
		Object obj = staticContext.getBean(serviceName);  
		workObjectContainer.putIfAbsent(serviceName, obj);
		return obj;
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		
		staticContext = applicationContext;
	}
	
	public ApplicationContext getApplicationContext(){
		
		return staticContext;
	}
	
	public DisJobGroup getDisJobGroup(String id){
		
		return (DisJobGroup) getWorkObject(id);
	}
	
	public DisJob getDisJob(String key){
		
		return (com.huangyiming.disjob.java.job.DisJob)getWorkObject(key);
	}
	
	public void setDisJob(String key,DisJob disJob){
		workObjectContainer.putIfAbsent(key, disJob);	
	}
}
