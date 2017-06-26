package com.huangyiming.disjob.monitor.rms;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.monitor.rms.pojo.RMSMonitorInfo;
import com.huangyiming.disjob.monitor.rms.util.RMSPropertityConfigUtil;
import com.huangyiming.disjob.monitor.util.MonitorSpringWorkFactory;

/**
  报警自检，这是公司报警平台的需要，后期将该代码去掉
**/
public class SelfTestRMSMonitor extends BaseRMSMonitor{

	private static final String SELF_TEST_MSG = "disJob-rms-self-test-message";
	public static final ScheduledExecutorService createExecutorService(int corePollSize){
		return new ScheduledThreadPoolExecutor(corePollSize, new ThreadFactory() {
			private final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(1);
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setName("thread-rms-monitor-selftest-" + ATOMIC_INTEGER.incrementAndGet());
				thread.setDaemon(true);
				return thread;
			}
		}, rmsDiscardPolicy);
	}
	public static ScheduledExecutorService scheduledExecutorService;

	public SelfTestRMSMonitor(RMSMonitorInfo monitorInfo, String selfTestMsg) {
		super(monitorInfo, selfTestMsg);
	}

	public void schedSelfTest(){
		scheduledExecutorService.scheduleAtFixedRate(this, 0, RMSPropertityConfigUtil.getSelfTestIntervals(), TimeUnit.MINUTES);
	}
	
	public static void start() {
		
		try {
			if(RMSPropertityConfigUtil.ifUseRMSMonitor() && RMSPropertityConfigUtil.availableSelfTest()){
				List<RMSMonitorInfo> monitorInfos = MonitorSpringWorkFactory.getRMSMonitorService().getAllSelfTestPointInfo();
				if(SelfTestRMSMonitor.scheduledExecutorService == null || SelfTestRMSMonitor.scheduledExecutorService.isShutdown()){
					SelfTestRMSMonitor.scheduledExecutorService = SelfTestRMSMonitor.createExecutorService(monitorInfos.size());
				}
				//开启自检
				for(RMSMonitorInfo monitorInfo : monitorInfos){
					monitorInfo.setTest(true);
					new SelfTestRMSMonitor(monitorInfo, SELF_TEST_MSG).schedSelfTest();
				}
			}else{
				shutdown();
			}
		} catch (Exception e) {
			LoggerUtil.error("[RMS Monitor] when start selfTestRMSMonitor got an exception ", e);
		}
	}

	public static void shutdown() {
		try {
			if(SelfTestRMSMonitor.scheduledExecutorService != null && !SelfTestRMSMonitor.scheduledExecutorService.isShutdown()){
				SelfTestRMSMonitor.scheduledExecutorService.shutdown();			
			}
		} catch (Exception e) {
			LoggerUtil.error("when shutdown selfTestRMSMonitor got an exception ", e);
		}
	}

	public static void reset() {
		SelfTestRMSMonitor.shutdown();
		SelfTestRMSMonitor.start();
	}
}
