package com.huangyiming.disjob.monitor.db.domain;

public class DBUserPermit {

	private String id;
	
	private String username;
	
	private String permitItem;
	
	public DBUserPermit(String username, String permititem) {
		this.username = username;
		this.permitItem = permititem;
	}

	public DBUserPermit() {
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPermitIterm() {
		return permitItem;
	}

	public void setPermitIterm(String permitIterm) {
		this.permitItem = permitIterm;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "DBUserPermit [id=" + id + ", username=" + username + ", permitItem=" + permitItem + "]";
	}
	
}
