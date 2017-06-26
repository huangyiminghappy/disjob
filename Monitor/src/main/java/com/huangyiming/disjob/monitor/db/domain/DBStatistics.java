package com.huangyiming.disjob.monitor.db.domain;

/**
 * <pre>
 * 
 *  File: DBDateStatistics.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  统计保存
 * 
 *  Revision History
 *
 *  Date：		2016年6月28日
 *  Author：		Disjob
 *
 * </pre>
 */
public class DBStatistics {
	private String timeSeg;//查询的关键字段
	private int successNum;//成功数
	private int failNum;//失败数
	public String getTimeSeg() {
		return timeSeg;
	}
	public void setTimeSeg(String timeSeg) {
		this.timeSeg = timeSeg;
	}
	public int getSuccessNum() {
		return successNum;
	}
	public void setSuccessNum(int successNum) {
		this.successNum = successNum;
	}
	public int getFailNum() {
		return failNum;
	}
	public void setFailNum(int failNum) {
		this.failNum = failNum;
	}
	@Override
	public String toString() {
		return "DBStatistics [timeSeg=" + timeSeg + ", successNum=" + successNum + ", failNum=" + failNum + "]";
	}
}
