package com.huangyiming.disjob.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class CuratorConTest {

	public static void main(String[] args) throws Exception {
		String path = "/huangyiming-test/child";
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2180")
				.sessionTimeoutMs(10000).retryPolicy(new ExponentialBackoffRetry(30000, 6)).build();
		client.start();
		System.out.println(client);
		client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path,"huangyiming".getBytes());
		String data = new String(client.getData().forPath(path));
		System.out.println(data);
		client.setData().forPath(path, "new data".getBytes());
		System.out.println("new data:"+new String(client.getData().forPath(path)));
	}
}
