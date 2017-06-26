package com.huangyiming.disjob.java.listener;

import com.huangyiming.disjob.event.ObjectEvent;
import com.huangyiming.disjob.event.ObjectListener;
import com.huangyiming.disjob.java.ProviderClassName;
import com.huangyiming.disjob.java.bean.StartUpConfig;
import com.huangyiming.disjob.java.core.dispatcher.EventObjectDispatcher;

public class DisJobStartUpListener implements ProviderClassName,ObjectListener<StartUpConfig> {
	
	public void onEvent(ObjectEvent<StartUpConfig> event) {
		//这里启动有个先后顺序：初始化参数配置->启动netty 监听 --> 初始化 curator
		System.out.println("disJob initinalize finish.");
		EventObjectDispatcher.dispatcherDisJobStartUpFinish();
	}
	public String getClassName() {
		return this.getClassName();
	}
	
}
