package com.huangyiming.disjob.java.bean;

public class SchedulerParam {
	private String requestId;
	private String parameters;
 	private String sharingRequestId;
 	public SchedulerParam() {
	}
 	
 	public SchedulerParam(String requestId, String parameters,
			String sharingRequestId) {
		super();
		this.requestId = requestId;
		this.parameters = parameters;
		this.sharingRequestId = sharingRequestId;
	}

	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getParameters() {
		return parameters;
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	public String getSharingRequestId() {
		return sharingRequestId;
	}
	public void setSharingRequestId(String sharingRequestId) {
		this.sharingRequestId = sharingRequestId;
	}

	@Override
	public String toString() {
		return "SchedulerParam [requestId=" + requestId + ", parameters=" + parameters + ", sharingRequestId=" + sharingRequestId + "]";
	}
}
