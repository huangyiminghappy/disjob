package com.huangyiming.disjob.java;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import com.huangyiming.disjob.java.listener.CuratorConnectStateListener;
import com.huangyiming.disjob.java.service.DisJobConfigService;
import com.huangyiming.disjob.java.utils.StringUtils;

public class CuratorClientBuilder {
	public static volatile boolean isCanUse = false ;

	private static CuratorFramework client = null;
	private final static CuratorClientBuilder zkClientBuilder = new CuratorClientBuilder();
	
	private CuratorClientBuilder(){}
	
	public static void initCurator(String zkHost,String zkRootNode){
		if(client == null){
			synchronized (zkClientBuilder) {
				if(client == null){
					client =CuratorFrameworkFactory.builder().connectString(zkHost).namespace(zkRootNode).sessionTimeoutMs(3000).retryPolicy(new ExponentialBackoffRetry(3000, 10)).build();
					client.getConnectionStateListenable().addListener(new CuratorConnectStateListener());

					client.start();
					isCanUse = true ;

				}
			}
		}
	}
	
	public static CuratorClientBuilder getInstance(){
		
		return zkClientBuilder ;
	}
	
	public CuratorFramework getCuratorFramework(){
		if(client == null){
			String zkhost = DisJobConfigService.getZkHost();
			if(StringUtils.isEmpty(zkhost)){
				throw new RuntimeException(CuratorClientBuilder.class.getName() + "; start disJob fail because the config of zkhost is null.");
			}
			String zkrootnode = DisJobConfigService.getZKRootNode();
			synchronized (zkClientBuilder) {
				if(client == null){
					initCurator(zkhost, zkrootnode);
				}
			}
		}
		return client ;
	}
	
	public CuratorFramework getCuratorFramework(String nameSpace){
		CuratorFramework client =CuratorFrameworkFactory.builder().connectString(DisJobConfigService.getZkHost())
				.namespace(nameSpace).sessionTimeoutMs(3000).retryPolicy(new ExponentialBackoffRetry(3000, 10)).build();
		client.start();
		return client;
	}
}
