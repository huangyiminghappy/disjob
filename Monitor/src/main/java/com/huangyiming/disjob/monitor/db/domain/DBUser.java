package com.huangyiming.disjob.monitor.db.domain;

/**
 * <pre>
 * 
 *  File: DBUser.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  数据库用户
 * 
 *  Revision History
 *
 *  Date：		2016年9月7日
 *  Author：		Disjob
 *
 * </pre>
 */
public class DBUser {
	private String username;/** 用户名    */
	private String password;/** 密码    */
	private String roleName;/** 角色名    */
	private String createdAt;/** 创建的时间 */
	private String updatedAt;/** 最后更新时间 */
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
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
	@Override
	public String toString() {
		return "DBUser [username=" + username + ", roleName=" + roleName + "]";
	}
}
