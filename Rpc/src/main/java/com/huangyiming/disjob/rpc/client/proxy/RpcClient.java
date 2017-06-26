package com.huangyiming.disjob.rpc.client.proxy;

import io.netty.bootstrap.Bootstrap;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.BasePooledObjectFactory;

import com.huangyiming.disjob.common.Constants;
import com.huangyiming.disjob.common.exception.DisJobAbstractUncheckException;
import com.huangyiming.disjob.common.exception.RpcServiceException;
import com.huangyiming.disjob.common.exception.TransportException;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.rpc.client.HURL;
import com.huangyiming.disjob.rpc.client.HURLParamType;
import com.huangyiming.disjob.rpc.codec.Response;
import com.huangyiming.disjob.rpc.codec.RpcRequest;
import com.huangyiming.disjob.rpc.codec.RpcResponse;

/**
 * 1、netty client初始化
 * 2、连接池初始化
 * 3、编解码初始化
 * 4、heartbeat初始化
 * 5、断连重连
 * @author Disjob
 *
 */
public class RpcClient extends AbstractRpcPoolClient{
 	private AtomicLong errorCount = new AtomicLong(0);
 	private int maxClientConnection = 10;//最大连接数
 	
 	private InetSocketAddress remoteAddress = null;
	private InetSocketAddress localAddress = null;
	
	public RpcClient(HURL hurl) {
		super(hurl);
	}

	@Override
	public Response request(RpcRequest request) throws TransportException {
		boolean async = hurl.getMethodParameter(request.getData().getMethodName(), "", HURLParamType.async.getName(), HURLParamType.async.getBooleanValue());
		return request(request, async);
	}
	
	private Response request(RpcRequest request, boolean async) throws TransportException {
		Channel channel = null;
		Response response = null;
		try {
			channel = borrowObject(request.getData().getRequestId());//这里borrow 到的一根channel 经过validate 之后是一定可用的。
			if(channel == null){
				response = new RpcResponse();
				response.setException("class name:"+request.getData().getClassName()+"method name :"+request.getData().getMethodName()+" borrow a channel is null."); 
			}else{
 				response = writeMessage(channel.getChannel(), request);
			}
		} catch (Exception e) {
			e.printStackTrace();
			handlerException(request, response, e);
			invalidateObject(channel);
		}finally{
			if(channel !=null){
 				returnObject(channel);
			}
		}
		return asyncResponse(response, async);
	}

	private void handlerException(RpcRequest request, Response response, Exception e) {
		LoggerUtil.error("NettyClient request Error: url=" + hurl.getUri() + " " + request.toString() + "requestid:"+request.getData().getRequestId()+e.getMessage(),e);
		if(response !=null && (!StringUtils.isNotEmpty(response.getException() ))){
			response.setException("channel.request occur error,"+e.getMessage());
		}
		
		if (e instanceof DisJobAbstractUncheckException) {
			throw (DisJobAbstractUncheckException) e;
		} else {
			e.printStackTrace();
			throw new RpcServiceException("NettyClient request RpcServiceException: url=" + hurl.getUri() + " "+ request.toString(), e);
		}
	}
	
	/**
	 * 如果async是false，那么同步获取response的数据
	 * @param response
	 * @param async
	 * @return
	 */
	private Response asyncResponse(Response response, boolean async) {
		return new RpcResponse(response);
	}

	@Override
	public boolean open() {
		synchronized (RpcClient.class) {
			String serverKey = RpcClientCache.getRpcClientKey(hurl);
			RpcClient client = RpcClientCache.rpcClientPool.get(serverKey);
			if (client != null && client.isAvailable()) {
				return false;
			}
			initBootstrap();
			if(Constants.isCanConnPool){
				initPool(false);
			}else{
				connect(Constants.RECON_COUNT_FAIL);
			}
			LoggerUtil.info("NettyClient Open finished: url={}", hurl);
		}
		return true;
	}
	
	/**
	 * 增加调用失败的次数：
	 * 如果连续失败的次数 >= maxClientConnection, 那么把client设置成不可用状态
	 */
	void incrErrorCount() {
		long count = errorCount.incrementAndGet();
		if(count < maxClientConnection){
			return ;
		}

		synchronized (this) {
			count = errorCount.longValue();
			if (count >= maxClientConnection) {
				LoggerUtil.error("NettyClient unavailable Error: url=" + hurl.getIdentity() + " "+ hurl.getServerPortStr());
			}
		}
	}
	
	void resetErrorCount() {
		errorCount.set(0);
	}
	
	public Bootstrap getBootstrap() {
		return bootstrap;
	}

	public HURL getHurl() {
		return hurl;
	}

	@Override
	protected BasePooledObjectFactory<Channel> createChannelFactory() {
		return new ChannelPoolFactory(this);
	}

	public InetSocketAddress getLocalAddress() {
		return localAddress;
	}

	public InetSocketAddress getRemoteAddress() {
		return remoteAddress;
	}

	public boolean isClosed() {
		return false;
	}

	public boolean isAvailable() {
		if(Constants.isCanConnPool){
			return true ;
		}
		
		io.netty.channel.Channel channel= ServerLinkedService.getChannel(getChannelKey(), this);
		if(channel == null){
			return false ;
		}
		
		return channel.isActive();
	}

	public RpcResponse writerAndFlush(RpcRequest request){
		if(request == null){
			return null ;
		}
		
		return writeMessage(ServerLinkedService.getChannel(getChannelKey(),this), request);
	}

}
