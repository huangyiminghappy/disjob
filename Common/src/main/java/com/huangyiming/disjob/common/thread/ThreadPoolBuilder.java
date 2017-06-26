package com.huangyiming.disjob.common.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class ThreadPoolBuilder {
	private int schedulerPooleSize = 64; 
	private int corePoolSize = schedulerPooleSize/2;
	private int maximumPoolSize = schedulerPooleSize; ;
	private int keepAliveTime = 60000;
	private final static ThreadPoolBuilder THREAD_POOL_BUILDER = new ThreadPoolBuilder();
	
	private ThreadPoolBuilder(){}
	
	public static ThreadPoolBuilder getInstance(){
		
		return THREAD_POOL_BUILDER ;
	}
	/**
	 * 
	 * @return
	 */
	public ThreadPoolExecutor builderExcutorThreadPool(){
		ThreadFactory threadFactory = new DefaultThreadFactory("scheduler-job"); 
		//可用线程不够情况下启动线程处理,没有对线程进行丢弃的处理方式
		RejectedExecutionHandler newThreadRunsPolicyhandler = new NewThreadRunsPolicy();
		BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>(1000);
		return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS,
	    			taskQueue, threadFactory,newThreadRunsPolicyhandler);
	}
	
	/**
	 * 
	 * 执行相关状态的线程池,线程池的reject策略采用新建线程方式处理
	 * @return
	 */
	public ThreadPoolExecutor builderRequestExecutorThreadPool(){
		ThreadFactory threadFactory = new DefaultThreadFactory("request-executor"); 
		//可用线程不够情况下启动线程处理,没有对线程进行丢弃的处理方式
		RejectedExecutionHandler newThreadRunsPolicyhandler = new NewThreadRunsPolicy();
		BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>(1000);
		return new ThreadPoolExecutor(1, maximumPoolSize,keepAliveTime, TimeUnit.MILLISECONDS,taskQueue, threadFactory,newThreadRunsPolicyhandler);
	}
	
	/**
	 * 执行相关状态的线程池,线程池的reject策略采用新建线程方式处理
	 * @return
	 */
	public ThreadPoolExecutor builderExeStateExecutorThreadPool(){
		//线程不够情况下使用抛弃旧线程方式
		ThreadFactory threadFactory = new DefaultThreadFactory("rpc-response-executor"); 
		BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>();
		return new ThreadPoolExecutor(corePoolSize, maximumPoolSize,keepAliveTime, TimeUnit.MILLISECONDS,taskQueue, threadFactory);
	}
	
	public ScheduledExecutorService builderSchedulerThreadPool(){
		
		return Executors.newSingleThreadScheduledExecutor(new DefaultThreadFactory("retrive-message-piple-thread")) ;
	}
	
	public ExecutorService builderSingleThreadExecutor(){
	
		return Executors.newSingleThreadExecutor(new DefaultThreadFactory("RTX-alarm"));
	}
	
	/**
	 * 
	 * @return
	 */
	public ThreadPoolExecutor builderSubmitRpcThreadPool(){
		ThreadFactory threadFactory = new DefaultThreadFactory("exe-rpc-executor");
		RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
		BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>(1000);
		ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS,taskQueue, threadFactory, handler);
		return executor;
	}
	
	static class DefaultThreadFactory implements ThreadFactory {
	    private static final AtomicInteger poolNumber = new AtomicInteger(1);
	    private final ThreadGroup group;
	    private final AtomicInteger threadNumber = new AtomicInteger(1);
	    private final String namePrefix;

	    public DefaultThreadFactory(String poolName) {
	        SecurityManager s = System.getSecurityManager();
	        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
	        namePrefix = poolName +"-" + poolNumber.getAndIncrement() + "-thread-";
	    }

	    public Thread newThread(Runnable r) {
	        Thread t = new Thread(group, r,namePrefix + threadNumber.getAndIncrement(),0);
	        if (t.isDaemon()){
	            t.setDaemon(false);
	        }
	        if (t.getPriority() != Thread.NORM_PRIORITY){
	            t.setPriority(Thread.NORM_PRIORITY);
	        }
	        return t;
	    }
	}
}
