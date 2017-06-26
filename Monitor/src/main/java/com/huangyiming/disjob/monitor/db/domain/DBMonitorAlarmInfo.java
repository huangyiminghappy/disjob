package com.huangyiming.disjob.monitor.db.domain;

import java.io.Serializable;

public class DBMonitorAlarmInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String index;
	
	private String productCode;
	
	private String pointCode;
	
	private String errorCode;
	
	private String description;

	private String application;
	
	private boolean available;
	
	private boolean isTest;
	
	public DBMonitorAlarmInfo() {
	}
	public DBMonitorAlarmInfo(String index) {
		this.index = index;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

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

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public boolean isAvailable() {
		return available;
	}
	
	public void setAvailable(boolean available) {
		this.available = available;
	}
	
	public boolean isTest() {
		return isTest;
	}
	public void setTest(boolean isTest) {
		this.isTest = isTest;
	}
	@Override
	public String toString() {
		return "DBMonitorAlarmInfo [productCode=" + productCode + ", pointCode=" + pointCode + ", errorCode=" + errorCode + "]";
	}

}
