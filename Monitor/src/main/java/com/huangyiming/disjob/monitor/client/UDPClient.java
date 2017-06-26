package com.huangyiming.disjob.monitor.client;
 
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

import com.huangyiming.disjob.monitor.pojo.UdpMessage;
import com.huangyiming.disjob.quence.Log;
import com.google.gson.Gson;
import com.huangyiming.disjob.monitor.client.handler.UDPClientHandler;

 //我们跟php端通信的时候,php处理是tcp连接不可用的时候使用udp通信,将执行进度传回给disjob
public class UDPClient {
    private EventLoopGroup group;

    public void run(int port) throws Exception {
        group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioDatagramChannel.class).option(ChannelOption.SO_BROADCAST, true)
                 //   .handler(new UDPClientHandler());
                                .handler(new ChannelInitializer<Channel>() {
 									@Override
									protected void initChannel(Channel ch)
											throws Exception {
										ch.pipeline().addLast(new UDPClientHandler());
									}
								});

            Channel ch = b.bind(0).sync().channel();
            UdpMessage message = new UdpMessage();
    		message.setRequestId("123456789");
    		message.setContent("1900");
    		message.setTime("2016-06-09 12:30:23 am");
    		message.setType("1");
    		/*for(int i=0;i<40;i++){
    			message.setContent("123-"+i);*/
    			String str = new Gson().toJson(message);
    			ch.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(str, CharsetUtil.UTF_8),
    					new InetSocketAddress("10.40.6.100", port))).sync();
    		//}
    			ch.close();
            if (!ch.closeFuture().await(150000)) {
            	Log.warn(this.getClass().getName()+"; 查询超时！");
            }
        } finally {
           group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
    	//for(int i=0;i<100;i++){
    		
    	
        // 初始化端口参数
        int port = 9001;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
      //  for(int i=0;i<4;i++){
        new UDPClient().run(port);
        //}
        // 服务端启动
        
    /* Executor   nPool = new ThreadPoolExecutor(
				5, 5, 2, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(10),new ThreadPoolExecutor.DiscardOldestPolicy());
     nPool.execute(command);*/
      }
    //}
}