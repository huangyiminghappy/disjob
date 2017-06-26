package com.huangyiming.disjob.rpc.client.proxy;

import java.lang.ref.SoftReference;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.commons.pool2.impl.PooledSoftReference;

import com.huangyiming.disjob.common.exception.RpcServiceException;
import com.huangyiming.disjob.common.exception.TransportException;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.monitor.rms.CommonRMSMonitor;
import com.huangyiming.disjob.monitor.rms.MonitorType;
import com.huangyiming.disjob.rpc.client.HURL;
import com.huangyiming.disjob.rpc.client.handler.RpcResponseHandler;
import com.huangyiming.disjob.rpc.codec.Response;
import com.huangyiming.disjob.rpc.codec.RpcDecoder;
import com.huangyiming.disjob.rpc.codec.RpcEncoder;
import com.huangyiming.disjob.rpc.codec.RpcRequest;
import com.huangyiming.disjob.rpc.codec.RpcResponse;
import com.huangyiming.disjob.rpc.utils.RpcConstants;
import com.huangyiming.disjob.rpc.utils.RpcSpringWorkFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 
 * @author Disjob
 *
 */
public abstract class AbstractRpcPoolClient implements RpcPoolClient{
	/**
	 * 1、对象池相关参数
	 */
	protected static long defaultMinEvictableIdleTimeMillis = (long) 1000 * 60 * 60;//默认链接空闲时间
    protected static long defaultSoftMinEvictableIdleTimeMillis = (long) 1000 * 60 * 10;//
    protected static long defaultTimeBetweenEvictionRunsMillis = (long) 1000 * 60 * 10;//默认回收周期
    public GenericObjectPool<Channel> pool;
    protected GenericObjectPoolConfig poolConfig;
    protected BasePooledObjectFactory<Channel> factory;
    
    /**
     * 
     * 2、连接服务端的netty 参数
     */
    protected EventLoopGroup group;
    protected Bootstrap bootstrap;
    
    /**
     * 3、服务端信息抽象
     * 
     */
    protected HURL hurl;
    
    public AbstractRpcPoolClient(HURL hurl) {
        this.hurl = hurl;
    }

	@Override
	public void initPool(boolean lazyInit) {
        poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMinIdle(2);  
        poolConfig.setMaxIdle(10);//假设每隔核有两个硬件线程  
        poolConfig.setTestOnReturn(false);  
        poolConfig.setTestOnBorrow(true);//每次borrow 的时候，都检测这个管道是否可用，直到拿到一根可用的NettyChannel
        poolConfig.setMaxTotal(10);  
        
        poolConfig.setMaxWaitMillis(3000);//3s
        poolConfig.setLifo(true);
        poolConfig.setMinEvictableIdleTimeMillis(defaultMinEvictableIdleTimeMillis); 
        poolConfig.setSoftMinEvictableIdleTimeMillis(defaultSoftMinEvictableIdleTimeMillis);
        poolConfig.setTimeBetweenEvictionRunsMillis(defaultTimeBetweenEvictionRunsMillis); 
        factory = createChannelFactory();

        pool = new GenericObjectPool<Channel>(factory, poolConfig);
        
        AbandonedConfig abandonedConfig = new AbandonedConfig();
        abandonedConfig.setRemoveAbandonedOnMaintenance(true); //在Maintenance的时候检查是否有泄漏
        abandonedConfig.setRemoveAbandonedOnBorrow(true); //borrow 的时候检查泄漏
        /**
         * 设置一个被遗弃的对象，多少秒后可从池中移除：
         * 单位：s. 这里设置的是 半个小时
         */
        abandonedConfig.setRemoveAbandonedTimeout(60 * 30);
        pool.setAbandonedConfig(abandonedConfig);
        if (lazyInit) {
        	return ;
        }
        
        for (int i = 0; i < poolConfig.getMinIdle(); i++) {
        	try {
        		pool.addObject();
        	} catch (Exception e) {
        		LoggerUtil.error("NettyClient init pool create connect Error: url=" + hurl.getUri(), e);
        	}
        }
	}

	@Override
	public void initBootstrap() {
		group = new NioEventLoopGroup();
		bootstrap = new Bootstrap();
		bootstrap.group(group);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel channel) throws Exception {
				ChannelPipeline pipeline = channel.pipeline();
				pipeline.addLast(new RpcDecoder(RpcConstants.MAX_FRAME_LENGTH, RpcConstants.LENGTH_FIELD_OFFSET, RpcConstants.LENGTH_FIELD_LENGTH));
				pipeline.addLast(new RpcEncoder(RpcRequest.class));
				pipeline.addLast(new IdleStateHandler(RpcConstants.READ_IDLE_TIME, RpcConstants.WRITE_IDLE_TIME, RpcConstants.ALL_IDLE_TIME, TimeUnit.SECONDS));  
				pipeline.addLast(new RpcResponseHandler());
				pipeline.addLast(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                    	super.channelInactive(ctx);
                    	//channel状态的处理交给对象池处理
                    	/*io.netty.channel.Channel channel = ctx.channel();
                    	String address = ServerLinkedService.getRemoterAddress(channel);
                    	LoggerUtil.debug(address+":in active;"+ctx.channel().toString());
                    	NettyChannel nettyChannel = ServerLinkedService.getNettyChannel(channel);
                    	nettyChannel.close();
                    	pool.getFactory().destroyObject(new PooledSoftReference<Channel>(new SoftReference<Channel>(nettyChannel)));*/
                    	
                    }
                });
			}
		});
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, RpcConstants.CONNECT_TIMEOUT);
	}

	@Override
	public io.netty.channel.Channel connect(int failReConCount) {
		failReConCount = failReConCount <=0 ? 1 : failReConCount;
		
		if(bootstrap == null){
			initBootstrap();
		}
		ChannelFuture future = null ;
		int tmpCount = failReConCount ;
		String message = getChannelKey()+" 连接次数超过3次。";
		do{
			try {
				if(tmpCount > failReConCount){
					TimeUnit.SECONDS.sleep(1);
				}
				
				future = bootstrap.connect(new InetSocketAddress(hurl.getHost(), hurl.getPort()));
				future.get();
				if(future.isSuccess()){
					ServerLinkedService.putChannel(getChannelKey(), future.channel());
				}
			} catch (Exception e) {
				LoggerUtil.error("[ "+RpcClient.class.getName()+" ]", e);
				e.printStackTrace();
				message = e.getMessage();
			}
			failReConCount--;
		}while(!future.channel().isActive()&&failReConCount>0);
		
		if(failReConCount<=0){
			LoggerUtil.warn(getChannelKey()+" 连接次数超过3次。");
			//这里检测连接不上，进行计数检测报警
			CommonRMSMonitor.sendNetWork(MonitorType.NetWork.SYSTEM_CONNECT_REFUSE, message);
			return null ;
		}
		
	    return future.channel();
	}

	protected String getChannelKey(){
		return hurl.getHost()+":"+hurl.getPort();
	}
	
	@Override
	public void close(int timeout) {
		try {
			bootstrap.group().shutdownGracefully(0,timeout,TimeUnit.SECONDS);
			pool.close();
			LoggerUtil.info("NettyClient close Success: url={}", hurl.getUri());
		} catch (Exception e) {
			LoggerUtil.error("NettyClient close Error: url=" + hurl.getUri(), e);
		}
	}

	@Override
	public Channel borrowObject(String requestId) throws Exception {
		Channel nettyChannel = null;
    	try {
    		nettyChannel = pool.borrowObject();
    	} catch (Exception e) {
    		CommonRMSMonitor.sendSystem(MonitorType.System.SERIOUS_BORROWCHANNEL_ERROR, "exception occur when borrowObject from pool :url=" + hurl.toString()+"; requestId is:"+requestId);				
			throw new RpcServiceException(e);
		}
    	
    	if(nettyChannel == null){
    		CommonRMSMonitor.sendSystem(MonitorType.System.SERIOUS_BORROWCHANNEL_ERROR, " borrow an nettychannel from pool is null.the url=" + hurl.toString()+"; requestId is:"+requestId);
    	}
    	LoggerUtil.debug("[channel key] "+getChannelKey()+"; "+getObjectPoolContainer().toString());
    	return nettyChannel ;
	}
	
	public ObjectPoolContainer getObjectPoolContainer(){
		if(pool == null){
			return new ObjectPoolContainer();
		}
		
		ObjectPoolContainer objectPoolContainer = new ObjectPoolContainer();
		objectPoolContainer.setBorrowedCount(pool.getBorrowedCount());
		objectPoolContainer.setCreatedCount(pool.getCreatedCount());
		objectPoolContainer.setDestroyedByBorrowValidationCount(pool.getDestroyedByBorrowValidationCount());
		objectPoolContainer.setDestroyedByEvictorCount(pool.getDestroyedByEvictorCount());
		objectPoolContainer.setDestroyedCount(pool.getDestroyedCount());
		objectPoolContainer.setNumActive(pool.getNumActive());
		objectPoolContainer.setNumIdle(pool.getNumIdle());
		objectPoolContainer.setNumWaiters(pool.getNumWaiters());
		objectPoolContainer.setReturnedCount(pool.getReturnedCount());
		return objectPoolContainer ;
	}
	
	@Override
	public void invalidateObject(Channel nettyChannel) {
		if (nettyChannel == null) {
			return;
		}
		try {
			pool.invalidateObject(nettyChannel);
		} catch (Exception ie) {
			LoggerUtil.error(this.getClass().getSimpleName()+ " invalidate client Error: url=" + hurl.getUri(), ie);
		}
	}

	@Override
	public void returnObject(Channel nettyChannel) {
		if (nettyChannel == null) {
			return;
		}
		try {
			pool.returnObject(nettyChannel);
		} catch (Exception ie) {
			LoggerUtil.error(this.getClass().getSimpleName()+ " return client Error: url=" + hurl.getUri(), ie);
		}
	}
	private ReentrantLock lock = new ReentrantLock();
	@Override
	public RpcResponse writeMessage(io.netty.channel.Channel channel,RpcRequest rpcRequest) {
		String requestId = rpcRequest.getData().getRequestId();
		RpcResponse response = new RpcResponse();
		try {
			if (channel!=null&&channel.isActive()) {
				ChannelFuture writeFuture = null;
				try{
					lock.lock();
					LoggerUtil.debug("before write: " + this.hurl.getHost() + "-"+ this.hurl.getPort() + " request "+ requestId +" rpcRequest="+ rpcRequest.toString());
					/*if(!StringUtils.isNotBlank(rpcRequest.getData().getClassName())){*/
				    if(checkNull(rpcRequest)){
						LoggerUtil.debug("write data is null : " + this.hurl.getHost() + "-"+ this.hurl.getPort() + " request "+ rpcRequest.getData().getRequestId()+" rpcRequest="+ rpcRequest.toString());
						rpcRequest = RpcSpringWorkFactory.getStoreRepThreadPoolService().getRpcRequest(requestId);
						if(checkNull(rpcRequest)){
							LoggerUtil.debug("[getStoreRepThreadPoolService] write data is null : " + this.hurl.getHost() + "-"+ this.hurl.getPort() + " request "+ rpcRequest.getData().getRequestId()+" rpcRequest="+ rpcRequest.toString());
							return response;
						}
						
						LoggerUtil.debug("[getStoreRepThreadPoolService] after write data is not null : " + this.hurl.getHost() + "-"+ this.hurl.getPort() + " request "+ rpcRequest.getData().getRequestId()+" rpcRequest="+ rpcRequest.toString());
					}
					writeFuture = channel.writeAndFlush(rpcRequest);
				}finally{
					RpcSpringWorkFactory.getStoreRepThreadPoolService().removeRpcRequest(requestId);
					lock.unlock();
				}
				LoggerUtil.debug("after write: " + this.hurl.getHost() + "-"+ this.hurl.getPort() + " request "+ rpcRequest.getData().getRequestId()+" rpcRequest="+ rpcRequest.toString());
				writeFuture.addListener(new WriterChannelFutureListener(channel,rpcRequest));
			} else {
				LoggerUtil.error(getChannelKey()+" 这个 channel 不可用");
				response.setException("the channel of the " +getChannelKey()+" is unactive.");
			}
		} catch (Exception e) {
			response.setException(rpcRequest.getData().getRequestId() + " request failed:"+ e.getMessage());
			e.printStackTrace();
		}
		return response;
	}

	private boolean checkNull(RpcRequest rpcRequest) {
		return rpcRequest.getData().getClassName() ==null || rpcRequest.getData().getClassName().trim().length() ==0 || ("null").equalsIgnoreCase(rpcRequest.getData().getClassName().trim());
	}
	
	protected abstract BasePooledObjectFactory<Channel> createChannelFactory();
    
    public abstract Response request(RpcRequest request) throws TransportException;
    
    public abstract boolean open();
    
}
