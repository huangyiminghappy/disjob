package com.huangyiming.disjob.rpc.client.proxy;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;

import com.huangyiming.disjob.rpc.client.HURL;

/**
 * @author Disjob
 */
public class RpcClientCache {
	public static final ConcurrentMap<String, RpcClient> rpcClientPool = new ConcurrentHashMap<String, RpcClient>();
	private static final ReentrantLock lock = new ReentrantLock();
	public static RpcClient get(HURL hurl) {
		String serverKey = getRpcClientKey(hurl);
		try{
			lock.lock();
			RpcClient client = rpcClientPool.get(serverKey);
			if (client == null) {
				RpcClient newClient = new RpcClient(hurl);
				newClient.open();
				client = newClient;
				rpcClientPool.put(serverKey, newClient);
			}
			return client;
		}finally{
			lock.unlock();
		}
	}
	
	public static void removeRpcClient(String key){
		if(StringUtils.isNotBlank(key)){
			rpcClientPool.remove(key);
		}
	}
	
	public static String getRpcClientKey(HURL hurl){
		String host = hurl.getHost();
		int port = hurl.getPort();
		String serverKey = host + ":" + port;
		return serverKey;
	}
	
	
}
