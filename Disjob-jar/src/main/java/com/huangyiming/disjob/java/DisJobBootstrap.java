package com.huangyiming.disjob.java;

import com.huangyiming.disjob.java.bean.StartUpConfig;
import com.huangyiming.disjob.java.core.dispatcher.EventObjectDispatcher;

/**
 * 
 * @author Disjob
 *
 */
public class DisJobBootstrap {

	/**
	 * @param startUpConfig
	 */
	public void startUpDisJob(StartUpConfig startUpConfig){
		
		EventObjectDispatcher.dispatcherDisJobStartUp(startUpConfig);
	}
	
	/**
	 * 
	 * @param disJobConfigPath
	 * @param log4jProperties
	 */
	public void startUpDisJob(short type,String disJobConfigPath){
		
		this.startUpDisJob(new StartUpConfig(type,disJobConfigPath));
	}
	
	/**
	 * 如果不是一个spring 项目，则使用我们的disJob 配置文件来配置相关的参数，
	 * @param disJobConfigPath 配置文件的绝对路径
	 */
	public void webServletStartUpDisJob(String disJobConfigPath){
		
		EventObjectDispatcher.dispatcherDisJobStartUp(new StartUpConfig(DisJobConstants.StartUpType.WEB_SERVLET_START_UP,disJobConfigPath));
	}
	
	public static void main(String[] args) {
		String disJob = "isJob.properties";
		
		new DisJobBootstrap().startUpDisJob(new StartUpConfig(DisJobConstants.StartUpType.JAVA_APPLICATION,disJob));
	}
}
