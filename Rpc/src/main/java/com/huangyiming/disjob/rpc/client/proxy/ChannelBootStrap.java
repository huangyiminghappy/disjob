package com.huangyiming.disjob.rpc.client.proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.huangyiming.disjob.common.util.UUIDHexGenerator;
import com.huangyiming.disjob.rpc.client.handler.DisJobKillResponseHandler;
import com.huangyiming.disjob.rpc.client.handler.DisJobRestartResponseHandler;
import com.huangyiming.disjob.rpc.codec.DisJobKillTaskResponse;
import com.huangyiming.disjob.rpc.codec.DisJobResponse;
import com.huangyiming.disjob.rpc.codec.DisJobRestartTaskResponse;
import com.huangyiming.disjob.rpc.codec.DisJobTaskDecoder;
import com.huangyiming.disjob.rpc.codec.Header;
import com.huangyiming.disjob.rpc.codec.RpcEncoder;
import com.huangyiming.disjob.rpc.codec.RpcRequest;
import com.huangyiming.disjob.rpc.codec.RpcRequestData;
import com.huangyiming.disjob.rpc.utils.PhpTaskCmd;
import com.huangyiming.disjob.rpc.utils.RpcConstants;

public class ChannelBootStrap {

	NioEventLoopGroup group = null;
	public Bootstrap bootstrap = null;
	String host;
	int port;

	public ChannelBootStrap(final PhpTaskCmd cmd, final DisJobResponse response,
			final CountDownLatch downLatch, String host, int port) {
		this.port = port;
		this.host = host;
		group = new NioEventLoopGroup();
		bootstrap = new Bootstrap();
		bootstrap.group(group);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel channel) throws Exception {
				ChannelPipeline pipeline = channel.pipeline();
				pipeline.addLast(new DisJobTaskDecoder(cmd,
						RpcConstants.MAX_FRAME_LENGTH,
						RpcConstants.LENGTH_FIELD_OFFSET,
						RpcConstants.LENGTH_FIELD_LENGTH));
				pipeline.addLast(new RpcEncoder(RpcRequest.class));
				DisJobResponse tmp = response;
				if (cmd == cmd.RESTART) {
					pipeline.addLast(new DisJobRestartResponseHandler(cmd,(DisJobRestartTaskResponse) tmp, downLatch));
				} else if (cmd == cmd.KILLTASK) {
					pipeline.addLast(new DisJobKillResponseHandler(cmd,(DisJobKillTaskResponse) tmp, downLatch));
				}
				pipeline.addLast(new ChannelInboundHandlerAdapter() {
					@Override
					public void channelInactive(ChannelHandlerContext ctx)throws Exception {
						super.channelInactive(ctx);
					}
				});
			}
		});
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,RpcConstants.CONNECT_TIMEOUT);
	}

	public void request(RpcRequest request) {
		try {
			ChannelFuture channelFuture = bootstrap.connect(host, port);
			final CountDownLatch downLatch = new CountDownLatch(1);
 			channelFuture.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future)throws Exception {
					downLatch.countDown();
				}
			});
			try {
				downLatch.await(3, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Channel channel = null;
			boolean success = channelFuture.isSuccess();
			if (success) {
				channel = channelFuture.channel();
				channel.writeAndFlush(request);
			}

		} finally {
		}
	}

	public static void main(String[] args) {
		// DisJobRestartTaskResponse response = new DisJobRestartTaskResponse();
		DisJobKillTaskResponse response = new DisJobKillTaskResponse();
		CountDownLatch downLatch = new CountDownLatch(1);

		RpcRequest request = new RpcRequest();
		Header header = new Header();
		header.setType((byte) 15);
		header.setVersion(1);

		RpcRequestData data = new RpcRequestData();
		data.setRequestId("ff808081581945c00158437017fe7fbc");
		data.setRequestId(UUIDHexGenerator.generate());

		request.setHeader(header);
		request.setData(data);
		String host = "10.40.6.100";
		int port = 9501;
		ChannelBootStrap b = new ChannelBootStrap(PhpTaskCmd.KILLTASK,
				response, downLatch, host, port);
		b.request(request);
		try {
			downLatch.await(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println();
		if (response != null) {
			System.out.println("yes:" + response);
		}
		/*
		 * RpcRequest request = new RpcRequest(); Header header = new Header();
		 * header.setType((byte)14); header.setVersion(1);
		 * 
		 * RpcRequestData data = new RpcRequestData();
		 * data.setRequestId("ff808081581945c00158437017fe7fbc");
		 * data.setIs_only_task(true); request.setHeader(header);
		 * request.setData(data); ChannelBootStrap b = new
		 * ChannelBootStrap(PhpTaskCmd.RESTART); b.request(request);
		 */
	}
}
