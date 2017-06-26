package com.huangyiming.disjob.rpc.codec;

import java.io.Serializable;

public class DisJobKillTaskResponse implements DisJobResponse, Serializable {

	
	private static final long serialVersionUID = 1L;

	private boolean status;

	private String requestId;

	private int code;

	private String msg;

	public boolean isStatus() {
		return status;
	}

	public DisJobKillTaskResponse() {

	}

	public DisJobKillTaskResponse(boolean status, String requestId, int code,
			String msg) {
		super();
		this.status = status;
		this.requestId = requestId;
		this.code = code;
		this.msg = msg;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "RpcKillTaskResponse [status=" + status + ", requestId="
				+ requestId + ", code=" + code + ", msg=" + msg + "]";
	}

}
