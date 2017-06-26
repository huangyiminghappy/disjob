package com.huangyiming.disjob.curator;

import java.io.IOException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class ACLTest {

	public static String auth_path = "/auth_test";
	public static String auth_child = "/auth_test/child";
	public static String authtentication_type = "digest" ;
	public static String correctAuthentication = "huangyiming:123456";
	public static String inCorrentAuthentication = "huangyiming:234567";
	
	public static void main(String[] args) throws Exception {
		Builder builder = CuratorFrameworkFactory.builder();
		builder.connectString("127.0.0.1:2181");
		builder.sessionTimeoutMs(3000);
		builder.retryPolicy(new ExponentialBackoffRetry(5000, 3));
		CuratorFramework zkClient = builder.build();
		
	}

	private static void TEST1() throws IOException, KeeperException,
			InterruptedException {
		//		CuratorFramework zkBook = CuratorClientBuilder.getInstance().getCuratorFramework("zk-book");
		//		System.out.println(zkBook);
				String path = "/auth_test";
				String address = "127.0.0.1:2181";
				ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181",60000, null);
				zooKeeper.addAuthInfo("digest", "foo:true".getBytes());
				zooKeeper.create(path, "auth info".getBytes(), Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL);
				//使用正确的权限来访问
		//		ZooKeeper wrightAuth = new ZooKeeper(address, 60000, null);
		//		wrightAuth.addAuthInfo("digest", "foo:true".getBytes());
		//		String wrightData = new String(wrightAuth.getData(path, false, null));
		//		System.err.println(wrightData);
				
				//使用无权限访问有权限的数据节点
		//		ZooKeeper noAuth = new ZooKeeper(address, 60000, null);//
		//		byte[] datas = noAuth.getData(path, false,null);
		//		String data = new String(datas);
		//		System.err.println(data);
				//使用权限错误来访问
				ZooKeeper failAuth = new ZooKeeper(address, 60000, null);
				failAuth.addAuthInfo("digest", "foo:true".getBytes());
				String failData = new String(failAuth.getData(path, false, null));
				System.err.println(failData);
	}
}
