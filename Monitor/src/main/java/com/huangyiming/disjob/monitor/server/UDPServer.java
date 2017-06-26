package com.huangyiming.disjob.monitor.server;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.monitor.server.handler.ThreadPoolHandler;
import com.huangyiming.disjob.monitor.server.handler.UDPMessageHandler;


/**
 * udp服务启动类
 * @author Disjob
 *
 */
@Service("udpServer")
public class UDPServer   {
 
	@Value("${monitor.port}")
 	private int port;
	@Resource
	public ThreadPoolHandler threadPoolHandler ;
	
   /**
    *随着spring容器启动UDP服务 
    */
 	@PostConstruct
	public void UdpServerStart(){
		  new Thread( new Server(port,threadPoolHandler)).start();
	}
	 
 }

class Server implements Runnable{
	 ThreadPoolHandler threadPoolHandler;
	 
	private int port;
	private volatile boolean closed = false;

	public Server(int port,ThreadPoolHandler threadPoolHandler) {
		this.port = port;
		this.threadPoolHandler = threadPoolHandler;
 	}
	private EventLoopGroup group;
	Bootstrap b = new Bootstrap();
	@Override
	public void run() {
 		// nio线程组
				group = new NioEventLoopGroup();
 				
				b.group(group).channel(NioDatagramChannel.class).option(ChannelOption.SO_BROADCAST, true)
		 		 .handler(new ChannelInitializer<Channel>() {
 											@Override
											protected void initChannel(Channel ch)
													throws Exception {
												ch.pipeline().addLast(new UDPMessageHandler(threadPoolHandler));
		 									}
										});
				doBind();
	}
	//绑定和关闭分开是为了更好的管理UDPServer
  	protected void doBind() {
 		if (closed) {
			return;
		}
  			b.bind(port).addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture f) throws Exception {
					if (f.isSuccess()) {
						LoggerUtil.info("Started Tcp Server :"+port);
					} else {
						LoggerUtil.info("Started Tcp Server Failed  "+port);
 						f.channel().eventLoop().schedule(new Runnable() {
							@Override
							public void run() {
								doBind();
 							}
						}, 10, TimeUnit.SECONDS);
					}
				}//绑定端口后设置channel关闭后给channel增加一个 管道关闭的监听器并同步阻塞,直到channel关闭,线程才会往下执行,结束进程
 			}); 
	}
  	
	//绑定和关闭分开是为了更好的管理UDPServer
  	public void close(){
  		LoggerUtil.info("close Tcp Server port :"+port);
		closed = true;
		group.shutdownGracefully();
 	}
 }