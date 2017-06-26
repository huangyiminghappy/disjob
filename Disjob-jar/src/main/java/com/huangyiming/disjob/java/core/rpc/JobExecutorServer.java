package com.huangyiming.disjob.java.core.rpc;

import java.util.concurrent.CountDownLatch;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import com.huangyiming.disjob.java.ProviderClassName;
import com.huangyiming.disjob.java.service.ClientLinkedService;
import com.huangyiming.disjob.java.service.DisJobConfigService;
import com.huangyiming.disjob.java.utils.Log;

/**
 * 作业服务器
 * 
 * @author Disjob
 *
 */
public class JobExecutorServer implements ProviderClassName,Runnable{
	private CountDownLatch countDownLatch ;
	public JobExecutorServer(CountDownLatch countDownLatch) {
		this.countDownLatch = countDownLatch;
	}
	public void run() {
		ServerBootstrap executorServer = new ServerBootstrap();

		EventLoopGroup boss = new NioEventLoopGroup();
		EventLoopGroup worker = new NioEventLoopGroup();
		try {
			executorServer.group(boss, worker).channel(NioServerSocketChannel.class);
			executorServer.option(ChannelOption.SO_BACKLOG, 1024).option(ChannelOption.TCP_NODELAY, true);

			executorServer.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch)throws Exception {
							ChannelPipeline pipeline = ch.pipeline();
							// 对输入的数据进行解码
							pipeline.addLast(new RpcDecoder(RpcConstants.MAX_FRAME_LENGTH, RpcConstants.LENGTH_FIELD_OFFSET, RpcConstants.LENGTH_FIELD_LENGTH));
							// 对输出的数据进行编码
							pipeline.addLast(new RpcEncoder(RpcResponse.class));
							pipeline.addLast(new RpcRequestHandler());
							pipeline.addLast(new ChannelInboundHandlerAdapter() {
								@Override
								public void channelInactive(ChannelHandlerContext ctx)throws Exception {
									Log.warn("ip: "+ClientLinkedService.getRemoterAddress(ctx.channel())+" 连接断开.");
									ClientLinkedService.removeChannels(ctx.channel());
								}
							});
							Log.warn(ch.toString()+" 重连.");
						}
					});
			Log.info(getClassName()+" start to listener on the port:"+DisJobConfigService.getServerPort());
			// 3、绑定在某个端口上
			ChannelFuture channelFuture = executorServer.bind(DisJobConfigService.getServerPort());
			channelFuture.addListener(new ChannelFutureListener() {
				
				public void operationComplete(ChannelFuture future) throws Exception {
					if(future.isSuccess()){
						countDownLatch.countDown();//这里会触发：countDownLatch.await() 这里的代码执行
					}else{
						throw new RuntimeException("初始化 监听端口失败.");
					}
				}
			});
			channelFuture.sync().channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			worker.shutdownGracefully();
			worker.shutdownGracefully();
		}
	}

	public String getClassName() {
		
		return this.getClass().getName();
	}
	
}
