package com.huangyiming.disjob.rpc.codec;

public class RpcRestartProcessRequestData {
	
	 private boolean is_only_task = true;

	public boolean isIs_only_task() {
		return is_only_task;
	}

	public void setIs_only_task(boolean is_only_task) {
		this.is_only_task = is_only_task;
	}

	@Override
	public String toString() {
		return "RpcRestartProcessRequestData [is_only_task=" + is_only_task
				+ "]";
	}
	 
	 
	
}
