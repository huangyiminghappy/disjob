package com.huangyiming.disjob.monitor.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.huangyiming.disjob.monitor.db.service.DBJobBasicInfoService;
import com.huangyiming.disjob.monitor.db.service.DBJobExeProgressService;
 
/**
 * 线程池处理类,处理业务
 * @author Disjob
 *
 */
@Service("threadPoolHandler")
public class ThreadPoolHandler {

	private   ExecutorService nPool;
	
	/**
	 * 线程池中所保存的核心线程数
	 */
	@Value("${monitor.corePoolSize}")
	private  int corePoolSize;
	
	/**
	 * 线程池允许创建的最大线程数
	 */
	@Value("${monitor.maximumPoolSize}")
	private  int maximumPoolSize;

	/**
	 * 当前线程池线程总数大于核心线程数时，终止多余的空闲线程的时间
	 */
	@Value("${monitor.keepAliveTime}")
	private int keepAliveTime;

	 /**
	  * 工作队列长度
	  */
	@Value("${monitor.workQueueSize}")
	private int workQueueSize;
	
	@Autowired
	@Qualifier("jobExeProgressService")
	private DBJobExeProgressService dbJobExeProgressService ;
	
	@Autowired
	@Qualifier("jobBasicInfoService")
	private DBJobBasicInfoService jobBasicInfoService;

	 @PostConstruct
	 public void init(){
		 nPool = new ThreadPoolExecutor(
				 corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(workQueueSize),new MonitorThreadFactory (),new DiscardOldestPolicy());
	 }
 	
	public   void callHandle(ChannelHandlerContext ctx,DatagramPacket msg){
		//从spring容器中得到进度处理service
		synchronized (this) {
			/*if(dbJobExeProgressService == null){
				 dbJobExeProgressService = SysUtil.getBean("jobExeProgressService");
	 		}*/
		}
 		 nPool.execute(new UDPTaskHandler(ctx,msg,dbJobExeProgressService,jobBasicInfoService));
 		
	}
}



/**
 * monitor thread factory,define thread name
 */
  class MonitorThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    MonitorThreadFactory() {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                              Thread.currentThread().getThreadGroup();
        namePrefix = "monitor-udp" +
                      poolNumber.getAndIncrement() +
                     "-thread-";
    }

    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r,
                              namePrefix + threadNumber.getAndIncrement(),
                              0);
        if (t.isDaemon())
            t.setDaemon(false);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }
    
    
}
  

       
   
