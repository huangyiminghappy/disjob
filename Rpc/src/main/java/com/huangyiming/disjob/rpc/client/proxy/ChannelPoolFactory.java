package com.huangyiming.disjob.rpc.client.proxy;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import com.huangyiming.disjob.common.Constants;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.rpc.client.HURL;

/**
 * 
 * @author Disjob
 *
 */
public class ChannelPoolFactory extends BasePooledObjectFactory<com.huangyiming.disjob.rpc.client.proxy.Channel> {
	private String factoryName = "";
	private RpcClient rpcClient;
	private final static AtomicInteger channelCount = new AtomicInteger();
	public ChannelPoolFactory(RpcClient rpcClient) {
		super();
		this.rpcClient = rpcClient;
		this.factoryName = "ChannelFactory_" + rpcClient.getHurl().getHost() + "_"+ rpcClient.getHurl().getPort();
	}
	
	public String getFactoryName() {
		return factoryName;
	}

	@Override
	public String toString() {
		return factoryName;
	}
	
	/**
	 * 对象池的话，统一在这里和服务端进行连接
	 */
	@Override
	public com.huangyiming.disjob.rpc.client.proxy.Channel create() throws Exception {
		NettyChannel nettyChannel = new NettyChannel(rpcClient);
		nettyChannel.setChannelId(getChannelKey());//每根新创建的channel 都有自己独立的一个key
		io.netty.channel.Channel channel = nettyChannel.connect(Constants.RECON_COUNT_FAIL);
		if(channel != null && channel.isActive()){
			int size = ServerLinkedService.putNettyChannel(nettyChannel);
			LoggerUtil.debug("create channel ："+channel.toString()+"; current create netty channel size :"+size);
			LoggerUtil.debug("[channel key]"+rpcClient.getChannelKey()+" [channelPoolFactory.create]"+rpcClient.getObjectPoolContainer());
		}
		return channel!=null ? nettyChannel : null;
	}

	private String getChannelKey(){
		String host = rpcClient.getHurl().getHost();
		String port = String.valueOf(rpcClient.getHurl().getPort());
		return host+"_"+port+"_"+channelCount.incrementAndGet();
	}
	
	@Override
	public PooledObject<Channel> wrap(com.huangyiming.disjob.rpc.client.proxy.Channel obj) {
         return new DefaultPooledObject<Channel>(obj);
	}
	
	@Override
	public boolean validateObject(PooledObject<Channel> obj) {
		LoggerUtil.debug("validate object");
		if (obj.getObject() instanceof Channel) {
 			try {
 				NettyChannel channel = (NettyChannel)obj.getObject(); 
 				LoggerUtil.debug("yes validate object");
				return  channel != null ?channel.getChannel().isActive():false ;
			} catch (final Exception e) {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 当成功创建一个对象的时候或者从池中借一个对象时，会触发这个方法回调
	 */
	@Override
	public void activateObject(PooledObject<Channel> p) throws Exception {

	}

	/**
	 * 准备放入到队列时会触发这里的回调
	 */
	@Override
	public void passivateObject(PooledObject<Channel> p) throws Exception {
	
	}
	
	/**
	 * 当一天对象被校验无效的时候，会触发这个方法的回调
	 */
	@Override
	public void destroyObject(PooledObject<Channel> obj) throws Exception {
		LoggerUtil.debug("destory object");
		if (obj.getObject() instanceof Channel) {
			NettyChannel channel = (NettyChannel) obj.getObject();
			HURL hurl = rpcClient.getHurl();
 			try {
				channel.close();
				LoggerUtil.info(factoryName + " client disconnect Success: " + hurl.getUri());
			} catch (Exception e) {
				LoggerUtil.error(factoryName + " client disconnect Error: " + hurl.getUri(), e);
			}
		}
		
		//LoggerUtil.debug("[channel key]"+rpcClient.getChannelKey()+"[channelPoolFactory.destroyObject]"+rpcClient.getObjectPoolContainer());
		super.destroyObject(obj);
	}
}
