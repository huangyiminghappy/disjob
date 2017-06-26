package com.huangyiming.disjob.quence;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.huangyiming.disjob.pojo.ThreadPoolContainer;

public class Executor {
	private BaseActionQueue defaultQueue;
	private ThreadPoolExecutor pool;

	/**
	 * 执行action队列的线程池
	 * 
	 * @param corePoolSize 最小线程数，包括空闲线程
	 * @param maxPoolSize 最大线程数
	 * @param keepAliveTime 当线程数大于核心时，终止多余的空闲线程等待新任务的最长时间
	 * @param cacheSize 执行队列大小
	 * @param prefix 线程池前缀名称
	 */
	public Executor(int corePoolSize, int maxPoolSize, int keepAliveTime, String prefix) {
		TimeUnit unit = TimeUnit.MINUTES;
		LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();//无界队列，不需要 处理策略
		if (prefix == null) {
			prefix = "";
		}
		ThreadFactory threadFactory = new Threads(prefix);
		pool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, unit, workQueue, threadFactory);
		defaultQueue = new BaseActionQueue(this);
	}
	/**
	 * 定制化配置
	 * @param pool
	 */
	public Executor(ThreadPoolExecutor pool){
		this.pool = pool ;
		defaultQueue = new BaseActionQueue(this);
	}
	
	public ThreadPoolContainer getThreadPollContainer(){
		
		ThreadPoolContainer threadPoolContainer = new ThreadPoolContainer();
		threadPoolContainer.setThreadSize(this.pool.getPoolSize());
		threadPoolContainer.setLargestPoolSize(this.pool.getLargestPoolSize()+this.pool.getPoolSize());
		threadPoolContainer.setActiveTaskCount(this.pool.getActiveCount());
		threadPoolContainer.setCompleteTaskCount((int)this.pool.getCompletedTaskCount());
		threadPoolContainer.setWaitTaskCount(this.pool.getQueue().size());
		threadPoolContainer.setTaskCount((int)this.pool.getTaskCount());
		return threadPoolContainer;
	}
	
	public BaseActionQueue getDefaultQueue() {
		return defaultQueue;
	}

	public void enDefaultQueue(Action action) {
		if(action.getActionQueue()==null){
			action.setActionQueue(defaultQueue);
		}
		defaultQueue.enqueue(action);
	}
	
	/**
	 * if you execute a temporary or disposable action  then call this method,or normal and executor more times 
	 * you should be call the enDefaultQueue method 
	 * @param action
	 */
	public void execute(Runnable command) {
		pool.execute(command);
	}
	
	public <T> T submit(Callable<T> command){
		
		try {
			return (T) pool.submit(command).get();
		} catch (InterruptedException e) {
			Log.error(this.getClass().getName()+"; submit a task excetion.",e);
			e.printStackTrace();
		} catch (ExecutionException e) {
			Log.error(this.getClass().getName()+"; submit a task excetion.",e);
			e.printStackTrace();
		}
		return null ;
	}
	
	public void stop() {
		if (!pool.isShutdown()) {
			pool.shutdown();
		}
	}

	static class Threads implements ThreadFactory {

		static final AtomicInteger poolNumber = new AtomicInteger(1);
		final ThreadGroup group;
		final AtomicInteger threadNumber = new AtomicInteger(1);
		final String namePrefix;

		public Thread newThread(Runnable runnable) {
			Thread thread = new Thread(group, runnable, (new StringBuilder()).append(namePrefix).append(threadNumber.getAndIncrement()).toString(), 0L);
			if (thread.isDaemon())
				thread.setDaemon(false);
			if (thread.getPriority() != 5)
				thread.setPriority(5);
			return thread;
		}

		Threads(String prefix) {
			SecurityManager securitymanager = System.getSecurityManager();
			group = securitymanager == null ? Thread.currentThread().getThreadGroup() : securitymanager.getThreadGroup();
			namePrefix = (new StringBuilder()).append("pool-").append(poolNumber.getAndIncrement()).append("-").append(prefix).append("-thread-").toString();
		}
	}
}
