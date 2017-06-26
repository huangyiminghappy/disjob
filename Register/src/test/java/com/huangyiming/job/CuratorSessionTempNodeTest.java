package com.huangyiming.job;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class CuratorSessionTempNodeTest {

	static final String zkhost = "localhost:2181,localhost:2182";
//	static final String zkhost = "10.40.6.100:2181,10.40.6.101:2181,10.40.6.102:2181";
	public static void main(String[] args){
		Builder builder = CuratorFrameworkFactory.builder().connectString(zkhost)
				.retryPolicy(new ExponentialBackoffRetry(1500, 3));
		builder.sessionTimeoutMs(6000);
		builder.connectionTimeoutMs(6000);
		CuratorFramework client = builder.build();
		CuratorFramework client1 = builder.build();
		CuratorFramework client2 = builder.build();
		CuratorFramework client3 = builder.build();
		CuratorFramework client4 = builder.build();
		CuratorFramework client5 = builder.build();
		client.start();
		client1.start();
		client2.start();
		client3.start();
		client4.start();
		client5.start();
		String root = "disJob";
		try {
			if(client.checkExists().forPath("/" + root + "/session/89") == null){
				client.create().creatingParentsIfNeeded().forPath("/" + root + "/session/89");
			}
			client1.create().withMode(CreateMode.EPHEMERAL).forPath("/" + root + "/session/89/10.40.6.89:9501");
//			Thread.sleep(30 * 1000L);
//			client.close();
			
			
			System.in.read();
			ExecutorService exectorservice = new ThreadPoolExecutor(5,10,1000L,TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
				final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);
				@Override
				public Thread newThread(Runnable r) {
					Thread thread = new Thread(r, "test-path-listener-" + ATOMIC_INTEGER.incrementAndGet());
					return thread;
				}
			});
//			PathChildrenCache childrenCache = new PathChildrenCache(client, "/jobbang/session/omsss", true, true, exectorservice);
			
			for(int i = 0; i < 50; i++){
				PathChildrenCache childrenCache = new PathChildrenCache(client, "/jobbang/session/omsss" + i, true, true, exectorservice);
				childrenCache.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
