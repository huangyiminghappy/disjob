package com.huangyiming.disjob.java.listener;

import com.huangyiming.disjob.event.ObjectEvent;
import com.huangyiming.disjob.event.ObjectListener;
import com.huangyiming.disjob.java.ExecutorBuilder;
import com.huangyiming.disjob.java.bean.StartUpConfig;
import com.huangyiming.disjob.java.event.DisJobStartUp;
import com.huangyiming.disjob.java.service.DisJobConfigService;

public class DisJobStopListener implements ObjectListener<StartUpConfig>{
	
	private DisJobStartUp disJobStartUp ;
	public DisJobStopListener(DisJobStartUp disJobStartUp) {
		this.disJobStartUp = disJobStartUp ;
	}
	public void onEvent(ObjectEvent<StartUpConfig> event) {
		DisJobConfigService.destory();
		ExecutorBuilder.getJobExecutor().stop();
		this.disJobStartUp.clearListener();
	}
}
