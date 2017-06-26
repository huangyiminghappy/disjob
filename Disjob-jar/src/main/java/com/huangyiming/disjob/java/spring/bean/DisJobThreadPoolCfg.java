package com.huangyiming.disjob.java.spring.bean;

/**
 * disJob配置类
 * @author Disjob
 *
 */
public class DisJobThreadPoolCfg {

	/**
	 * 核心线程数
	 */
	private int corePoolSize;
	
	/**
	 * 最大线程数
	 */
	private int maxPoolSize;
	
	
	private int keepAliveTime;
	/**
	 * 线程名字前缀
	 */
	private String prefix;

	
	private String packages;
	
	
	public String getPackages() {
		return packages;
	}

	public void setPackages(String packages) {
		this.packages = packages;
	}

	public int getCorePoolSize() {
		return corePoolSize;
	}

	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public int getKeepAliveTime() {
		return keepAliveTime;
	}

	public void setKeepAliveTime(int keepAliveTime) {
		this.keepAliveTime = keepAliveTime;
	}
	
}
