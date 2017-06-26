package com.huangyiming.disjob.rpc.codec;

public class RpcRequest {
	/**
	 * 消息头
	 */
	private Header header;
	
	/**
	 * 消息体
	 */
	private RpcRequestData data;
	
	
	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public RpcRequestData getData() {
		return data;
	}

	public void setData(RpcRequestData data) {
		this.data = data;
	}


	@Override
	public String toString() {
		return "RpcRequest [headerInfo = " + header.toString() + ", dataInfo = " + data.toString() + "]";
	}
	
}
