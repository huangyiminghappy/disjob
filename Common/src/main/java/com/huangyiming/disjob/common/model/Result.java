package com.huangyiming.disjob.common.model;

/**
 * <pre>
 * 
 *  File: Result.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  控制器处理结果保存
 * 
 *  Revision History
 *
 *  Date：		2016年5月19日
 *  Author：		Disjob
 *
 * </pre>
 */
public class Result {
	private boolean successful = true;
	
	private String msg;
	
	private Object data;
	
	public Result() {
	}
	
	public Result(boolean successful) {
		this(successful, null);
	}

	public Result(String msg) {
		this(false, msg);
	}
	
	public Result(boolean successful, String msg) {
		this.successful = successful;
		this.msg = msg;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "Result [successful=" + successful + ", msg=" + msg + ", data=" + data + "]";
	}
	
}
