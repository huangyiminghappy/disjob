package com.huangyiming.disjob.spring;

import java.io.Serializable;

public class ExecutorInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name ;
	private int delay ;
	private int interval ;
	private String address ;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getDelay() {
		return delay;
	}
	public void setDelay(int delay) {
		this.delay = delay;
	}
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	@Override
	public String toString() {
		return "ExecutorInfo [name=" + name + ", delay=" + delay+ ", interval=" + interval + ", address=" + address + "]";
	}
}
