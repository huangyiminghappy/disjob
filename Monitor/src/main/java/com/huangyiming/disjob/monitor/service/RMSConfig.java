package com.huangyiming.disjob.monitor.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("rmsConfig")
public class RMSConfig {

	@Value("${rms.monitor}")
	private String rmsMonitor;
	
	@Value("${rms.selftest.intervals}")
	private String intervals;
	
	@Value("${rms.disJobProjectCode}")
	private String disJobProjectCode;
	
	@Value("${rms.monitorurl}")
	private String monitorurl;
	
	@Value("${rms.selftest}")
	private String selfTest;

	public String getRmsMonitor() {
		return rmsMonitor;
	}

	public void setRmsMonitor(String rmsMonitor) {
		this.rmsMonitor = rmsMonitor;
	}

	public String getIntervals() {
		return intervals;
	}

	public void setIntervals(String intervals) {
		this.intervals = intervals;
	}

	public String getDisJobProjectCode() {
		return disJobProjectCode;
	}

	public void setDisJobProjectCode(String disJobProjectCode) {
		this.disJobProjectCode = disJobProjectCode;
	}

	public String getMonitorurl() {
		return monitorurl;
	}

	public void setMonitorurl(String monitorurl) {
		this.monitorurl = monitorurl;
	}

	public String getSelfTest() {
		return selfTest;
	}

	public void setSelfTest(String selfTest) {
		this.selfTest = selfTest;
	}
	
}
