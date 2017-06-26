package com.huangyiming.disjob.java.event;

import com.huangyiming.disjob.event.AbstractEventObject;
import com.huangyiming.disjob.event.ObjectEvent;
import com.huangyiming.disjob.event.ObjectListener;
import com.huangyiming.disjob.java.job.DisJob;
import com.huangyiming.disjob.java.service.JobService;
import com.huangyiming.disjob.java.utils.Log;
import com.huangyiming.disjob.java.utils.StringUtils;

/**
 * 使用spring 启动的方式来注册job
 * @author Disjob
 *
 */
public class SpringRegisterJob extends AbstractEventObject<String>{

	@Override
	public void attachListener() {
		this.addListener(new ObjectListener<String>() {
			
			public void onEvent(ObjectEvent<String> event) {
				String className =event.getValue();
				if(StringUtils.isEmpty(className)){
					return ;
				}
				
				try {
					Class<?> clazz = Class.forName(className.trim());
					Object disJob = clazz.newInstance();
					if(!(disJob instanceof DisJob)){
						return ;
					}
					JobService.initJob(className);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					Log.error(e.getMessage(),e);
				} catch (InstantiationException e) {
					e.printStackTrace();
					Log.error(e.getMessage(),e);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					Log.error(e.getMessage(),e);
				}
			}
		}, EventType.REGISTER_JOB);
	}
	
	public void notify(String className){
		ObjectEvent<String> event = new ObjectEvent<String>(className, EventType.REGISTER_JOB);
		notifyListeners(event);
	}
}
