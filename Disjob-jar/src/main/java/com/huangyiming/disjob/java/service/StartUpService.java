package com.huangyiming.disjob.java.service;

import java.util.concurrent.CountDownLatch;
import org.apache.log4j.PropertyConfigurator;
import com.huangyiming.disjob.java.CuratorClientBuilder;
import com.huangyiming.disjob.java.ExecutorBuilder;
import com.huangyiming.disjob.java.bean.StartUpConfig;
import com.huangyiming.disjob.java.core.rpc.JobExecutorServer;
import com.huangyiming.disjob.java.dynamic.WatchServiceReactor;
import com.huangyiming.disjob.java.job.JobInitScanner;
import com.huangyiming.disjob.java.utils.Log;
import com.huangyiming.disjob.java.utils.StringUtils;

public class StartUpService {
	
	public volatile static boolean isInitSuccess = true ;
	
	public static void startup(StartUpConfig startUpConfig){
		initLog4j(startUpConfig.getLog4jProperties());
		initDisJobConfig(startUpConfig.getDisJobConfigPath());
		initCurator();
		initJobScanner();
		//initDynamicJobDirWatcher();
	}
	
	public static void initLog4j(String log4jProperties) {
		if(StringUtils.isEmpty(log4jProperties)){
			return ;
		}
		PropertyConfigurator.configure(log4jProperties);
	}
	
	public static void initDisJobConfig(String disJobConfigPath) {
		if(StringUtils.isEmpty(disJobConfigPath)){
			Log.error(getClassName()+"[ spring start up ] the disJob config path is empty,please config the 'disJobConfigPath' paramter");
			try {
				throw new IllegalArgumentException("[ spring start up ] the disJob config path is empty,please config the 'disJobConfigPath' paramter");
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			return ;
		}
		DisJobConfigService.init(disJobConfigPath);
	}
	
	public static void initCurator(){
		String zkhost = DisJobConfigService.getZkHost();
		if(StringUtils.isEmpty(zkhost)){
			throw new RuntimeException(getClassName() + "; start disJob fail because the config of zkhost is null.");
		}
		String zkrootnode = DisJobConfigService.getZKRootNode();
		CuratorClientBuilder.initCurator(zkhost, zkrootnode);
	}
	
	public static void initJobScanner(){
		
		ExecutorBuilder.getJobExecutor().execute(new JobInitScanner());
	}
	
	public static CountDownLatch initExecutorServer(){
		CountDownLatch countDownLatch = new CountDownLatch(1);
		Thread thread = new Thread(new JobExecutorServer(countDownLatch),"thread-netty-server");
		thread.setDaemon(true);
		thread.start();
		return countDownLatch;
	}
	
	public static void initDynamicJobDirWatcher(){
		Thread thread = new Thread(new WatchServiceReactor(DisJobConfigService.getDynamicDir()),"thread-dynamic-job");
		thread.setDaemon(true);
		thread.start();
	}
	
	public static String getClassName() {
		return StartUpService.class.getName();
	}
	
	public static boolean check(String disJobConfigPath, String log4jProperties){
		if(StringUtils.isEmpty(disJobConfigPath) & StringUtils.isEmpty(log4jProperties)){
			Log.error(getClassName()+ " disJob config parameters is empty.");
			return false;
		}
		
		if(disJobConfigPath.equals(log4jProperties)){
			Log.error(getClassName()+" disJob config is equal to log4j config.");
			return false;
		}
		
		return true ;
	}
}
