package com.huangyiming.disjob.rpc.codec;

import java.io.Serializable;

public class DisJobRestartTaskResponse implements DisJobResponse,Serializable {

	public boolean status;

 

	public boolean isStatus() {
		return status;
	}



	public void setStatus(boolean status) {
		this.status = status;
	}



	@Override
	public String toString() {
		return "DisJobRestartTaskResponse [status=" + status + "]";
	}
	
	
	
}
