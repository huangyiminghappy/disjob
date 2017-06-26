package com.huangyiming.disjob.common.util;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Liu.ms
 * @date 2016-7-4 上午11:58:59
 */
public class SpringWorkFactory implements ApplicationContextAware {
	protected static ApplicationContext staticContext;
	private final static ConcurrentHashMap<String, Object> workObjectContainer = new ConcurrentHashMap<String, Object>();	
	
	protected SpringWorkFactory() {
    }
	
	public final static Object getWorkObject(String serviceName){
		if(workObjectContainer.get(serviceName) != null){
			return workObjectContainer.get(serviceName);
		}
		//获取spring容器对象 
		ApplicationContext ctx = staticContext;
		Object obj = ctx.getBean(serviceName);  
		//注意，putIfAbsent方法如果在第一次调用时，MAP中还没有这个对象，会将对象put进去，但返回值是null
		//如果第二次调用，对象已经存在这时不放对象了而是直接放回改对象
		workObjectContainer.putIfAbsent(serviceName, obj);
		return workObjectContainer.get(serviceName);
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)throws BeansException {
		staticContext = applicationContext;
	}
}
