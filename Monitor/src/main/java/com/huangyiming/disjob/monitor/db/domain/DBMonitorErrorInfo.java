package com.huangyiming.disjob.monitor.db.domain;

public class DBMonitorErrorInfo {

	private String pointCode;
	
	private String errorCode;
	
	private String description;
	
	private boolean available;
	
	private int sendCondition ;
	
	public String getPointCode() {
		return pointCode;
	}

	public void setPointCode(String pointCode) {
		this.pointCode = pointCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public int getSendCondition() {
		return sendCondition == 0 ? 1 : sendCondition;
	}

	public void setSendCondition(int sendCondition) {
		this.sendCondition = sendCondition;
	}

	@Override
	public String toString() {
		return "DBMonitorErrorInfo [pointCode=" + pointCode + ", errorCode=" + errorCode + ", description="
				+ description + ", available=" + available + "]";
	}
}
