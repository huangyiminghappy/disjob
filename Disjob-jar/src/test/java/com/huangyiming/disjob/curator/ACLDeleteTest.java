package com.huangyiming.disjob.curator;

import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;

public class ACLDeleteTest {

	public static void main(String[] args) throws Exception {
//		CuratorFramework zkBook = CuratorClientBuilder.getInstance().getCuratorFramework("zk-book");
//		System.out.println(zkBook);
		String path = "/auth_test";
		String childPath = "/auth_test/child" ;
		String address = "127.0.0.1:2181";
		ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181",60000, null);
		zooKeeper.addAuthInfo("digest", "foo:true".getBytes());
		if(zooKeeper.exists(path, false) !=null){
			zooKeeper.delete(path, -1);
		}
		zooKeeper.create(path, "auth info".getBytes(), Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);
		/**
		 * 如果在子节点下创建临时节点，那么他的父节点一定是 PERSISTENT 节点
		 */
		zooKeeper.create(childPath, "inif child".getBytes(), Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL);
		
		//使用无权限来进行节点的删除
//		ZooKeeper zooKeeper2 = new ZooKeeper(address, 60000, null);
//		zooKeeper2.delete(childPath,-1);
		
		//使用有权限进行节点删除
		ZooKeeper zooKeeper3 = new ZooKeeper(address, 60000, null);
		zooKeeper3.addAuthInfo("digest", "foo:true".getBytes());
		zooKeeper3.delete(childPath, -1);
		
	}
}
