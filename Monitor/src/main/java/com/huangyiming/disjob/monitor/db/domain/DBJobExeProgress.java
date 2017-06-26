package com.huangyiming.disjob.monitor.db.domain;

import java.io.Serializable;

/**
 * <pre>
 * 
 *  File: DBJobExeProgress.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  任务执行进度记录表，记录任务标志id、uuid（主键）、创建时间、更新时间、服务器ip、启动时间、耗时、进度
 * 
 *  Revision History
 *
 *  Date：		2016年6月23日
 *  Author：		Disjob
 *
 * </pre>
 */
public class DBJobExeProgress implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;//自增长id
	private String uuid;//uuid，任务的唯一标识
	private String createdAt;//创建时间
	private String businessSip;//业务服务器IP地址
	private String dataTime;//数据生成时间
	private Integer type=0;//类型
	private String content;//内容
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String getBusinessSip() {
		return businessSip;
	}
	public void setBusinessSip(String businessSip) {
		this.businessSip = businessSip;
	}
	public String getDataTime() {
		return dataTime;
	}
	public void setDataTime(String dataTime) {
		this.dataTime = dataTime;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Override
	public String toString() {
		return "DBJobExeProgress [id=" + id + ", uuid=" + uuid + ", createdAt=" + createdAt + ", businessSip="
				+ businessSip + ", dataTime=" + dataTime + ", type=" + type + ", content=" + content + "]";
	}
}
