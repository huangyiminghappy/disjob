package com.huangyiming.job;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import com.huangyiming.disjob.register.repository.ZnodeApi;
import com.huangyiming.disjob.register.repository.ZnodeApiCuratorImpl;
import com.google.common.collect.Lists;

public class CuratorClientTest {
	static CuratorFramework client;
	public void createServer() throws Exception{
		TestingServer server = new TestingServer();
		server.start();
		String serverConnectString = server.getConnectString();
		System.err.println(serverConnectString);
		Builder builder = CuratorFrameworkFactory.builder().connectString(serverConnectString)
				.retryPolicy(new ExponentialBackoffRetry(1500, 3));
		builder.sessionTimeoutMs(6000);
		builder.connectionTimeoutMs(6000);
		client = builder.build();
		client.start();
	}
	
	public static void main(String[] args){
		CuratorClientTest test = new CuratorClientTest();
		try {
			List<ACL> aclList = Lists.newArrayList(new ACL(ZooDefs.Perms.ALL,new Id("digest",DigestAuthenticationProvider.generateDigest("test:test"))));
			test.createServer();
			client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).withACL(aclList ).forPath("/a/b/c/e", "".getBytes());
			client.getData().forPath("/a/b/c/d/111");
			
			PathChildrenCache childrenCache = new PathChildrenCache(client, "/a/a/a" ,false);
			childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
				
				@Override
				public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
					System.err.println(" event data " + event.getData());
				}
			});
			childrenCache.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("");
	}
}
