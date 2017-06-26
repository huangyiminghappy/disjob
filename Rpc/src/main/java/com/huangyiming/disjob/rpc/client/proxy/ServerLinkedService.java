package com.huangyiming.disjob.rpc.client.proxy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.huangyiming.disjob.common.Constants;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.rpc.codec.RpcRequest;

/**
 * 服务端需要维持有所可用的客户端。以防连接断开时发送失败的消息需要随着重连后心跳包进行返回
 * @author Disjob
 *
 */
public class ServerLinkedService {

	private final static ConcurrentHashMap<Integer, NettyChannel> poolClientLinkedMap = new ConcurrentHashMap<Integer, NettyChannel>();
	
	/**
	 * key:client ip:port,values: the multi linked of client
	 */
	private final static ConcurrentHashMap<String, Channel> clientLinkedMap = new ConcurrentHashMap<String, Channel>();
	
	/**
	 * hostPost=ip:port.for example 127.0.0.1:9501。给单根管道发送消息时用
	 */
	public final static ConcurrentHashMap<String, String> clientProvidersMap = new ConcurrentHashMap<String, String>();
	
	/**
	 * key:ip:port 表示向某个服务端发送失败的 rpc 请求列表
	 */
	public final static ConcurrentHashMap<String,List<RpcRequest>> sendFailRpcRequestMap = new ConcurrentHashMap<String,List<RpcRequest>>();
	public final static ConcurrentHashMap<String, ReentrantLock> segmentLockMap = new ConcurrentHashMap<String, ReentrantLock>();
	public final static ConcurrentHashMap<String, Date> ipHostLastReSendDateMap = new ConcurrentHashMap<String, Date>();
	
	/**
	 * 收到一个心跳消息时，看看这台有没有发送失败的消息，如果有的话，则随着心跳消息一起重发
	 * @param key ip:port.单根管道式一个服务端只对应一个 Channel
	 * @param channel
	 */
	public static void putChannel(String key,Channel channel){
		if(!channel.isActive()){
			return ;
		}
		
		String address = getRemoterAddress(channel);//key 和当前这根管道是否匹配：key=ip:port,方法返回值为：ip:port
		if(key.indexOf(address)<0){
			return ;
		}
		clientLinkedMap.put(key, channel);//(key, channels);
	}
	
	/**
	 * @param key compose of host:port
	 * @return
	 */
	public static Channel getChannel(String key,RpcClient rpcClient){
		//1、如果这个服务端在zk 上的会话已经失效了，则不进行重连
		if(!clientProvidersMap.containsKey(key)){
			return null;
		}
		//2、会话还在，则进行重连
		Channel channel = clientLinkedMap.get(key);
		if ((channel==null || !channel.isActive()) && Constants.RECON_COUNT_FAIL>0){
			rpcClient.connect(Constants.RECON_COUNT_FAIL);
			channel = clientLinkedMap.get(key);
		}
		
		return channel;
	}
	
	public static int putNettyChannel(NettyChannel nettyChannel){
		if(nettyChannel.getChannel()!=null&&nettyChannel.getChannel().isActive()){
			poolClientLinkedMap.putIfAbsent(nettyChannel.getChannel().hashCode(), nettyChannel);
		}
		
		return poolClientLinkedMap.size();
	}
	
	public static NettyChannel getNettyChannel(Channel channel){
		
		if(channel == null){
			return null ;
		}
		
		return poolClientLinkedMap.get(channel.hashCode());
	}
	
	/**
	 * 这个方法返回的是 ip:port
	 * @param channel
	 * @return
	 */
	public static String getRemoterAddress(Channel channel){
		InetSocketAddress insocket = (InetSocketAddress) channel.remoteAddress();
		if(insocket == null){
			return "";
		}
        return insocket.toString().substring(1);
	}
	
	/**
	 * 发送失败时，会将发送失败的这个消息存储到这一台服务器列表中。也就是说：每一台服务器都保存了发送 rpc 时发送失败的消息。一分钟后再重发。
	 */
	public static void putFailRpcRequest(String ipPort,RpcRequest rpcRequest){
		if(StringUtils.isEmpty(ipPort)){
			return ;
		}
		
		ReentrantLock lock = getReentrantLock(ipPort);
		
		List<RpcRequest> rpcRequestList = sendFailRpcRequestMap.putIfAbsent(ipPort, new ArrayList<RpcRequest>());
		if(rpcRequestList == null){
			rpcRequestList = sendFailRpcRequestMap.get(ipPort);
		}
	
		try{
			lock.lock();
			rpcRequestList.add(rpcRequest);
		}finally{
			lock.unlock();
		}
	}
	
	/**
	 * 发送失败时，会将发送失败的这个消息存储到这一台服务器列表中。也就是说：每一台服务器都保存了发送 rpc 时发送失败的消息。一分钟后再重发。
	 * bug 修复：负责调度的服务器发现job providers 有一个进程挂了，则将发送给这个进程失败的 rpc request merger 到另一个 provider 上去
	 */
	public static void putFailRpcRequest(String ipPort,List<RpcRequest> rpcRequests){
		if(StringUtils.isEmpty(ipPort) || CollectionUtils.isEmpty(rpcRequests)){
			return ;
		}
		
		ReentrantLock lock = getReentrantLock(ipPort);
		
		List<RpcRequest> rpcRequestList = sendFailRpcRequestMap.putIfAbsent(ipPort, new ArrayList<RpcRequest>());
		if(rpcRequestList == null){
			rpcRequestList = sendFailRpcRequestMap.get(ipPort);
		}
	
		try{
			lock.lock();
			rpcRequestList.addAll(rpcRequests);
		}finally{
			lock.unlock();
		}
	}
	
	private static ReentrantLock getReentrantLock(String ipHost){
		ReentrantLock lock = segmentLockMap.putIfAbsent(ipHost, new ReentrantLock());
		if(lock == null){
			lock = segmentLockMap.get(ipHost);
		}
		return lock;
	}
	
	/**
	 * 跟这根管道相同一组的其他管道消息发送失败都用这根发
	 * @param channel
	 */
	public static void checkReSendRpc(Channel channel){
		String ipHost = getRemoterAddress(channel);
		Date lastUpdate = ipHostLastReSendDateMap.get(ipHost);
		if(lastUpdate == null){
			ipHostLastReSendDateMap.put(ipHost, new Date());
			return ;
		}
		
		long interval = (System.currentTimeMillis()-lastUpdate.getTime()) / 1000;//单位：s
		
		if(interval/60 < 1){
			return ;
		}
		ipHostLastReSendDateMap.put(ipHost, new Date());
		//1 分钟后重发失败的消息
		List<RpcRequest> sendFailRpc = getFailRpcRequestList(ipHost);
		if(sendFailRpc==null||sendFailRpc.isEmpty()){
			return ;
		}

		LoggerUtil.warn(ipHost +" 重新发送失败的消息，个数："+sendFailRpc.size());
		for(RpcRequest rpcRequest : sendFailRpc){
			ChannelFuture channelFuture = channel.writeAndFlush(rpcRequest);
			channelFuture.addListener(new WriterChannelFutureListener(channel, rpcRequest));
			LoggerUtil.warn(ipHost +" 重新发送失败的消息："+rpcRequest.getData().toString());
		}
		
	}
	
	private static List<RpcRequest> getFailRpcRequestList(String ipHost){
		List<RpcRequest> rpcRequests = sendFailRpcRequestMap.get(ipHost);
		if(rpcRequests == null||rpcRequests.isEmpty()){
			return null;
		}
		ReentrantLock lock = segmentLockMap.get(ipHost);
		if(lock == null){
			lock = new ReentrantLock();
			segmentLockMap.put(ipHost, lock);
		}
		List<RpcRequest> tmpRpcRequests = new ArrayList<RpcRequest>();
		try{
			lock.lock();
			tmpRpcRequests.addAll(rpcRequests);
			rpcRequests.clear();
		}finally{
			lock.unlock();
		}
		return tmpRpcRequests;
	}
	
	/**
	 * 将检测到一个 provider 宕机了，则将这台发送失败的 rpc 消息 合并到同一组其他的一个在线 provider 中去
	 * @param shutdownProvider-> ip:port
	 * @param onlineProvider-> ip:port
	 */
	public static void mergerFailRpcRequest(String shutdownProviderIp,String onlineProviderIp){
		putFailRpcRequest(onlineProviderIp, getFailRpcRequestList(shutdownProviderIp));
	}
}

