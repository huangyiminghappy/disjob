package com.huangyiming.disjob.rpc.codec;

public class RpcRestartProcessRequest {
	/**
	 * 消息头
	 */
	private Header header;
	
	/**
	 * 消息体
	 */
	private RpcRestartProcessRequestData data;
	
	
	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

 


	public RpcRestartProcessRequestData getData() {
		return data;
	}

	public void setData(RpcRestartProcessRequestData data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "RpcRequest [headerInfo = " + header.toString() + ", dataInfo = " + data.toString() + "]";
	}
	
}
