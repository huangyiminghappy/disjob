package com.huangyiming.disjob.monitor.rms.pojo;

import java.io.Serializable;

public class SendMonitorMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String monitorIndex ;
	private RMSMonitorInfo rmsMonitorInfo ;
	private String message ;
	public SendMonitorMessage(String monitorIndex,RMSMonitorInfo rmsMonitorInfo, String message) {
		super();
		this.monitorIndex = monitorIndex;
		this.rmsMonitorInfo = rmsMonitorInfo;
		this.message = message;
	}
	public RMSMonitorInfo getRmsMonitorInfo() {
		return rmsMonitorInfo;
	}
	public void setRmsMonitorInfo(RMSMonitorInfo rmsMonitorInfo) {
		this.rmsMonitorInfo = rmsMonitorInfo;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMonitorIndex() {
		return monitorIndex;
	}
	public void setMonitorIndex(String monitorIndex) {
		this.monitorIndex = monitorIndex;
	}
	
	
}
