package com.huangyiming.disjob.java.core.rpc;

public class Header {
	/**
	 * 消息类型0：心跳，1:请求
	 */
	private byte type;
	/**
	 * 协议版本，目前是1
	 */
	private int version;
	/**
	 * 消息体长度
	 */
	private int length;
	
	public Header() {
		super();
	}
	
	public Header(byte type, int version, int length) {
		super();
		this.type = type;
		this.version = version;
		this.length = length;
	}

	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public byte getType() {
		return type;
	}
	public void setType(byte type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "Header [type=" + type + ", version=" + version + ", length=" + length + "]";
	}

}
