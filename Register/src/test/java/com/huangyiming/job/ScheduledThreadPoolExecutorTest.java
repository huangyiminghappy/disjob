package com.huangyiming.job;

import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.huangyiming.disjob.monitor.rms.SelfTestRMSMonitor;

public class ScheduledThreadPoolExecutorTest {

	public static void main(String[] args){
		ScheduledExecutorService scheduledExecutorService = SelfTestRMSMonitor.createExecutorService(1);
		scheduledExecutorService.scheduleAtFixedRate(new MyTask(), 0, 1L, TimeUnit.MINUTES);
		
		System.err.println("");
	}
	
	static class MyTask implements Runnable{

		@Override
		public void run() {
			System.err.println("exe data " + new Date());
		}
		
	}
}
