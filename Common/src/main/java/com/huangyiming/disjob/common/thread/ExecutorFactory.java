package com.huangyiming.disjob.common.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import com.huangyiming.disjob.quence.Executor;

public final class ExecutorFactory {

	private static Executor executor = null;
	
	public static Executor executeStateExecutor = null;

	public static void offerExecutor(Executor executor) {

		ExecutorFactory.executor = executor;
	}

	/**
	 * 调度一个job 时 并发去掉。提高吞吐量
	 * @return
	 */
	public static Executor getExecutor() {

		return ExecutorFactory.executor;
	}
	
	/**
	 * 
	 * @return
	 */
	public static Executor getExecuteStateExecutor() {
		return executeStateExecutor;
	}
	
	/**
	 * 接收 服务端返回的消息，专门用一个线程池去处理
	 * @param executeStateExecutor
	 */
	public static void offerExecuteStateExecutor(Executor executeStateExecutor) {
		ExecutorFactory.executeStateExecutor = executeStateExecutor;
	}

	private static ScheduledExecutorService schedulerService = ThreadPoolBuilder.getInstance().builderSchedulerThreadPool();
	
	public static ScheduledExecutorService getScheduledExecutorService(){
		
		return schedulerService;
	}
	
	private static ExecutorService signalThreadPoolService = ThreadPoolBuilder.getInstance().builderSingleThreadExecutor();
	
	public static ExecutorService getSignalThreadPoolService(){
		
		return signalThreadPoolService;
	}
	
	/**
	 * submit rpc thread pool
	 */
	private static Executor submitRpcExecutor = null;
	
	public static void setSubmitRpcExecutor(Executor executor){
		if(submitRpcExecutor == null){
			synchronized (ExecutorFactory.class) {
				if(submitRpcExecutor == null){
					submitRpcExecutor = executor;
				}
			}
		}
	}
	
	public static Executor getSubmitRpcExecutor(){
		return submitRpcExecutor;
	}
}
