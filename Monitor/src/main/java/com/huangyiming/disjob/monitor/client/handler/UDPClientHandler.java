package com.huangyiming.disjob.monitor.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

/**
 * udp客户端处理类，代码注释掉
 */
public class UDPClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

	@Override
	 public void channelActive(ChannelHandlerContext ctx) throws Exception {
		/*UdpMessage message = new UdpMessage();
		message.setRequestId("123456789");
		message.setTaskNum("123");
		message.setTime("2016-06-09 12:30:23 am");
		message.setType("1");
		String str = new Gson().toJson(message);
		 ctx.writeAndFlush(new DatagramPacket(
                 Unpooled.copiedBuffer(str.getBytes())));*/
		//ctx.channel().writeAndFlush(str);
	     //   ctx.fireChannelActive();
	    }
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
		 String resp = msg.content().toString(CharsetUtil.UTF_8);
		//if (resp.startsWith(HandlerHelper.NOW_TIME)) {
  		//} 
		/*UdpMessage message = new UdpMessage();
		message.setRequestId("123456789");
		message.setTaskNum("123");
		message.setTime("2016-06-09 12:30:23 am");
		message.setType("1");
		String str = new Gson().toJson(message);
		ctx.channel().writeAndFlush(str);*/
		
 		ctx.close();
 	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}
