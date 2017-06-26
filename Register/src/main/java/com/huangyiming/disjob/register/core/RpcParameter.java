package com.huangyiming.disjob.register.core;

import java.io.Serializable;

import com.huangyiming.disjob.register.domain.Job;
import com.huangyiming.disjob.rpc.client.HURL;

public class RpcParameter implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Job job;
	private HURL hurl;
	private String parameters;
	private String sharingRequestId;
	public RpcParameter() {
	}
	
	public RpcParameter(Job job, HURL hurl, String parameters,
			String sharingRequestId) {
		super();
		this.job = job;
		this.hurl = hurl;
		this.parameters = parameters;
		this.sharingRequestId = sharingRequestId;
	}

	public Job getJob() {
		return job;
	}
	public void setJob(Job job) {
		this.job = job;
	}
	public HURL getHurl() {
		return hurl;
	}
	public void setHurl(HURL hurl) {
		this.hurl = hurl;
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
	
}
