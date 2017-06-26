package com.huangyiming.disjob.java.listener;

import java.util.concurrent.CountDownLatch;
import com.huangyiming.disjob.event.ObjectEvent;
import com.huangyiming.disjob.event.ObjectListener;
import com.huangyiming.disjob.java.bean.StartUpConfig;
import com.huangyiming.disjob.java.service.StartUpService;
import com.huangyiming.disjob.java.utils.Log;

public class DisJobInitServerListener implements ObjectListener<StartUpConfig>{

	public void onEvent(ObjectEvent<StartUpConfig> event) {
		if(StartUpService.isInitSuccess == false){
			return ;
		}
		CountDownLatch countDownLatch = StartUpService.initExecutorServer();
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
			Log.error(e);
			StartUpService.isInitSuccess = false;
			return ;
		}
		StartUpService.initCurator();
	}
}
