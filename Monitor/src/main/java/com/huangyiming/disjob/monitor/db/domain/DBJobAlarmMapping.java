package com.huangyiming.disjob.monitor.db.domain;

import java.io.Serializable;

import com.huangyiming.disjob.common.util.DateUtil;

/**
 * <pre>
 * 
 *  File: DBJobAlarmMapping.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  任务报警映射
 * 
 *  Revision History
 *
 *  Date：		2016年9月1日
 *  Author：		Disjob
 *
 * </pre>
 */
public class DBJobAlarmMapping implements Serializable {
	private static final long serialVersionUID = 1L;
	private String groupName;//任务组名称
	private String createdAt;//创建的时间
	private String updatedAt;//最后更新时间
	private boolean onOff;//是否报警,true表示开启、false表示关闭
	private String alarmRtx;//报警列表，以分号分割
	
	public DBJobAlarmMapping(){
		createdAt = DateUtil.getUtc();
		updatedAt  = createdAt;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}
	public boolean isOnOff() {
		return onOff;
	}
	public void setOnOff(boolean onOff) {
		this.onOff = onOff;
	}
	public String getAlarmRtx() {
		return alarmRtx;
	}
	public void setAlarmRtx(String alarmRtx) {
		this.alarmRtx = alarmRtx;
	}
	@Override
	public String toString() {
		return "DBJobAlarmMapping [groupName=" + groupName + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt
				+ ", onOff=" + onOff + ", alarmRtx=" + alarmRtx + "]";
	}
}
