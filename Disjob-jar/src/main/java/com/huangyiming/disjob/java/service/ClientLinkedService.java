package com.huangyiming.disjob.java.service;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import com.huangyiming.disjob.java.event.ChannelReSendTracker;
import com.huangyiming.disjob.java.utils.StringUtils;
import com.huangyiming.disjob.quence.Log;

/**
 * 服务端需要维持有所可用的客户端。以防连接断开时发送失败的消息需要随着重连后心跳包进行返回
 * @author Disjob
 *
 */
public class ClientLinkedService {

	/**
	 * key:client ip,values: the multi linked of client
	 */
	private final static ConcurrentHashMap<String, Set<Channel>> clientLinkedMap = new ConcurrentHashMap<String, Set<Channel>>();
	
	private final static ConcurrentHashMap<String, ChannelReSendTracker> CHANNNEL_RESEND_TRACKER = new ConcurrentHashMap<String, ChannelReSendTracker>();
	private final static ReentrantLock lock = new ReentrantLock();
	/**
	 * 收到一个心跳消息时，看看这台有没有发送失败的消息，如果有的话，则随着心跳消息一起重发
	 * @param channel
	 */
	public static void putChannel(Channel channel){
		String clientIp = getRemoterAddress(channel);
		Set<Channel> channels = clientLinkedMap.putIfAbsent(clientIp, new HashSet<Channel>());
		try{
			lock.lock();
			if(channels == null){
				channels = clientLinkedMap.get(clientIp);
			}
			channels.add(channel);
		}finally{
			lock.unlock();
		}
		clientLinkedMap.put(clientIp, channels);
		notifyReSend(channel);
	}
	/**
	 * 记录同一个ip,检测发送失败，至少隔一分钟，如果有心跳
	 */
	private static final ConcurrentHashMap<String, Date> IP_NOTIFY_TIME = new ConcurrentHashMap<String, Date>();
	
	public static void notifyReSend(Channel channel){
		String clientIp = getRemoterAddress(channel);
		
		Date lastFire = IP_NOTIFY_TIME.get(clientIp);
		if(lastFire == null){
			lastFire = new Date();
			IP_NOTIFY_TIME.put(clientIp, lastFire);
		}
		long interVal = (System.currentTimeMillis()-lastFire.getTime()) / 1000;//单位：s
		if(interVal/60 > 1){
			Log.info("ip:"+clientIp+" 重发失败消息:");
			ChannelReSendTracker reSendTracker = CHANNNEL_RESEND_TRACKER.putIfAbsent(clientIp,new ChannelReSendTracker());
			if(reSendTracker == null){
				reSendTracker = CHANNNEL_RESEND_TRACKER.get(clientIp);
			}
			reSendTracker.notifyReSend(channel);
			lastFire = new Date();
			IP_NOTIFY_TIME.put(clientIp, lastFire);
		}
	}
	
	/**
	 * 始终保证这个管道连着是存活着且可发消息的。否则返回空。上层调用只许判断是否为空。如果不为null,则用这根管道发送。否则不做处理
	 * @param clientIp
	 * @return
	 */
	public static Channel getChannel(String clientIp){
		Set<Channel> channels = clientLinkedMap.get(clientIp);
		if(channels==null||channels.isEmpty()){
			return null ;
		}
		Set<Channel> tmpChannels = null;
		try{
			lock.lock();
			tmpChannels = new HashSet<Channel>(channels);
		}finally{
			lock.unlock();
		}
		Iterator<Channel> iterator = tmpChannels.iterator();
		for(;iterator.hasNext();){
			Channel channel =iterator.next();
			if(channel.isActive()&&channel.isWritable()){
				return channel;
			}
		}
		return null ;
	}
	
	/**
	 * 当与客户端连接断开的时候，和这一个ip 所有的连接 都不可用。因此需要从内存中移除
	 * @param clientIp
	 */
	public static void removeChannels(Channel channel){
		String clientIp = getRemoterAddress(channel);
		if(StringUtils.isEmpty(clientIp)){
			return ;
		}
		
		clientLinkedMap.remove(clientIp);
	}
	
	public static String getRemoterAddress(Channel channel){
		if(channel.remoteAddress() instanceof InetSocketAddress){
			InetSocketAddress insocket = (InetSocketAddress) channel.remoteAddress();
	        String clientIp = insocket.getAddress().getHostAddress();
	        return clientIp ;
		}
		return "" ;
	}
}

