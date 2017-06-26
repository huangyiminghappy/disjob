package com.huangyiming.disjob.java;

import com.huangyiming.disjob.java.job.DisJob;
import com.huangyiming.disjob.java.service.DisJobConfigService;
import com.huangyiming.disjob.quence.Executor;
public final class ExecutorBuilder {

	private ExecutorBuilder(){}
	
	private static Executor jobExecutor = null ;
	
	/**
	 * 
	 * @return
	 */
	public static Executor getJobExecutor(){
		synchronized (DisJob.class) {
			if(jobExecutor == null){
				int coreSize = DisJobConfigService.getCorePoolSize(7) ;
				int maxCoreSize = DisJobConfigService.getMaxPoolSize(7) ;
				int keepAliveTime = DisJobConfigService.getKeepAliveTime();
				jobExecutor = new Executor(coreSize, maxCoreSize, keepAliveTime, "job-executor");
			}
		}
		return jobExecutor;
	}
	public static void main(String[] args) {
		int coreSize = 7 * Runtime.getRuntime().availableProcessors() + 1 ;
		int maxCoreSize = 3 * coreSize + 1 ;
		System.out.println(coreSize+";"+maxCoreSize);
	}
	private static Executor executor = null ;
	
	/**
	 * 处理客户端请求线程池
	 * @return
	 */
	public static Executor getExecutor(){
		synchronized (Executor.class) {
			if(executor == null){
				int coreSize = DisJobConfigService.getCorePoolSize(7) ;
				int maxCoreSize = DisJobConfigService.getMaxPoolSize(7) ;
				int keepAliveTime = DisJobConfigService.getKeepAliveTime();
				executor = new Executor(coreSize,maxCoreSize,keepAliveTime,"executor");
			}
		}
		return executor;
	}
	
	//分片单独一个线程池处理
	private static Executor sharingExecutor = null ;
	
	/**
	 * 处理客户端请求线程池
	 * @return
	 */
	public static Executor getSharingExecutor(){
		synchronized (Executor.class) {
			if(sharingExecutor == null){
				int coreSize = DisJobConfigService.getCorePoolSize(7) ;
				int maxCoreSize = DisJobConfigService.getMaxPoolSize(7) ;
				int keepAliveTime = DisJobConfigService.getKeepAliveTime();
				sharingExecutor = new Executor(coreSize,maxCoreSize,keepAliveTime,"sharing-executor");
			}
		}
		return sharingExecutor;
	}
}
