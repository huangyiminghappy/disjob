package com.huangyiming.disjob.common.model;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * <pre>
 * 
 *  File: SchedulerCoreMonitor.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  core层调度器监控数据，用于及时了解调度器的内部运行情况。
 * 
 *  Revision History
 *
 *  Date：		2016年6月11日
 *  Author：		Disjob
 *
 * </pre>
 */
public class SchedulerCoreMonitor {
	/** 调度器名称 */
	private String schedulerName;
	/** 总线程数 */
	private int totalThread;
	/** 可用线程数 */
	private int availThread;
	/** 正在运行的线程池数 */
	private int busyThread;
	/** 总的任务数 */
	private int totalJob;
	/** 调度器所在物理机的IP地址 */
	private String localIP;
	
	public SchedulerCoreMonitor() {
		localIP = getIP();
	}
	public String getSchedulerName() {
		return schedulerName;
	}
	public void setSchedulerName(String schedulerName) {
		this.schedulerName = schedulerName;
	}
	public int getTotalThread() {
		return totalThread;
	}
	public void setTotalThread(int totalThread) {
		this.totalThread = totalThread;
	}
	public int getAvailThread() {
		return availThread;
	}
	public void setAvailThread(int availThread) {
		this.availThread = availThread;
	}
	public int getBusyThread() {
		return busyThread;
	}
	public void setBusyThread(int busyThread) {
		this.busyThread = busyThread;
	}
	public int getTotalJob() {
		return totalJob;
	}
	public void setTotalJob(int totalJob) {
		this.totalJob = totalJob;
	}
	public String getLocalIP() {
		return localIP;
	}
	public void setLocalIP(String localIP) {
		this.localIP = localIP;
	}
	/**获取本地IP
	 * @return 本地IP或null
	 */
	private static String getIP(){
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (final UnknownHostException ex) {
			return null;
		}
	}
	@Override
	public String toString() {
		return "SchedulerCoreMonitor{ schedulerName="+schedulerName+", totalThread="+totalThread+", availThread="+availThread+
				", busyThread="+busyThread+", totalJob="+totalJob+", localIP="+localIP+"}";
	}
}
