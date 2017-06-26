package com.huangyiming.disjob.java.bean;

import io.netty.channel.ChannelHandlerContext;
import java.io.Serializable;
import com.huangyiming.disjob.java.core.rpc.RpcRequest;

public class RpcContainer implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ChannelHandlerContext ctx;
	private RpcRequest msg ;
	public RpcContainer() {
	}
	public RpcContainer(ChannelHandlerContext ctx, RpcRequest msg) {
		super();
 		this.ctx = ctx;
		this.msg = msg;
	}
	public ChannelHandlerContext getCtx() {
		return ctx;
	}
	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}
	public RpcRequest getMsg() {
		return msg;
	}
	public void setMsg(RpcRequest msg) {
		this.msg = msg;
	}
}
