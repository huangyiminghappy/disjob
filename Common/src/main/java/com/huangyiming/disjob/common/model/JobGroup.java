package com.huangyiming.disjob.common.model;

import org.apache.commons.lang3.StringUtils;

/**
 * <pre>
 * 
 *  File: SysGroup.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  存储任务组信息
 * 
 *  Revision History
 *
 *  Date：		2016年5月24日
 *  Author：		Disjob
 *
 * </pre>
 */
public class JobGroup {
	/** 任务组名称 */
	private String groupName;
	/** 任务组备注 */
	private String remark;
	
	private boolean binded;
	
	private String bindSession;
	
	public JobGroup() {
	}
	public JobGroup(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public boolean isBinded() {
		return binded;
	}

	public void setBinded(boolean binded) {
		this.binded = binded;
	}

	public String getBindSession() {
		return bindSession;
	}
	
	public void setBindSession(String bindSession) {
		this.bindSession = bindSession;
	}
	
	public void addBindSession(String bindSession) {
		if(!StringUtils.isEmpty(this.bindSession)){
			this.bindSession += "," + bindSession;
		}else{
			this.bindSession = bindSession;			
		}
	}
	
	@Override
	public String toString() {
		return "JobGroup [groupName=" + groupName + ", remark=" + remark + ", binded=" + binded + ", bindSession="
				+ bindSession + "]";
	}
	
}
