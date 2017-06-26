package com.huangyiming.disjob.monitor.db.domain;

import java.io.Serializable;

public class DBJobGroupInfo implements Serializable{

	private static final long serialVersionUID = 1L;

	private String id;
	
	private String name;
	
	private String remark;
	
	private boolean binded = false;

	public DBJobGroupInfo(){
	}
	
	public DBJobGroupInfo(String groupName){
		this.name = groupName;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	@Override
	public String toString() {
		return "DBJobGroupInfo [id=" + id + ", name=" + name + ", remark=" + remark + ", binded=" + binded + "]";
	}
}
