package com.huangyiming.disjob.monitor.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.ReferenceCountUtil;

/**
 * udp服务端处理类
 */
 public class UDPMessageHandler   extends SimpleChannelInboundHandler<DatagramPacket> {
	 ThreadPoolHandler threadPoolHandler;
	 public UDPMessageHandler( ThreadPoolHandler threadPoolHandler){
		 this.threadPoolHandler = threadPoolHandler;
	 }
	 
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
 		super.channelActive(ctx);
	}
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
     	ReferenceCountUtil.retain(msg);
    	//ThreadPoolHandler threadPoolHandler = SysUtil.getBean("threadPoolHandler");
     	threadPoolHandler.callHandle(ctx, msg);
     }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        cause.printStackTrace();
    }

}