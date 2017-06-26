package com.huangyiming.disjob.monitor.pojo;

import java.io.Serializable;

/**
 * 基本信息
 * @author haungyiming
 *
 */
public class BaseInfoMessage implements Serializable {

	//{"requestId":"8aa8867455e7c3d90155e831b6c101bd","jobBegingTime":"2016-07-14 15:07:22","jobCompleteTime":"2016-07-14 15:12:23","processTime":5000,"timeout":0,"code":0,"exception":""}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String requestId;
	
	private String jobBegingTime;
	
	private String jobCompleteTime;
	
	private long processTime;
	
	private int timeout;
	
	private int code;
	
	private String exception;
	
	private String jobRecvTime;
	
    private String sharingRequestId;

	public String getJobRecvTime() {
		return jobRecvTime;
	}

	public void setJobRecvTime(String jobRecvTime) {
		this.jobRecvTime = jobRecvTime;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getJobBegingTime() {
		return jobBegingTime;
	}

	public void setJobBegingTime(String jobBegingTime) {
		this.jobBegingTime = jobBegingTime;
	}

	public String getJobCompleteTime() {
		return jobCompleteTime;
	}

	public void setJobCompleteTime(String jobCompleteTime) {
		this.jobCompleteTime = jobCompleteTime;
	}

	public long getProcessTime() {
		return processTime;
	}

	public void setProcessTime(long processTime) {
		this.processTime = processTime;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	public String getSharingRequestId() {
		return sharingRequestId;
	}

	public void setSharingRequestId(String sharingRequestId) {
		this.sharingRequestId = sharingRequestId;
	}
	
	
	
	
	
	//com.huangyiming.disjob.monitor.db.service.DBJobBasicInfoService.update(DBJobBasicInfo)

}
