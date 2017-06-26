package com.huangyiming;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
import org.junit.Test;

import com.huangyiming.disjob.common.Constants;

public class DisJobServerZKRegistryTest extends BaseJunitTest{
	 @Test
	 public void leaderElectionTest() throws Exception{
		 Builder builder = CuratorFrameworkFactory.builder()
	               .connectString("10.40.6.100:2181,10.40.6.101:2181,10.40.6.102:2181")
	               .retryPolicy(new ExponentialBackoffRetry(1000, 3));
	               //.namespace(Constants.ROOT);
	       builder.sessionTimeoutMs(500);
	       builder.connectionTimeoutMs(300);
	       String latchPath = ZKPaths.makePath(Constants.ROOT, Constants.DISJOB_SERVER_NODE_ROOT, Constants.DISJOB_SERVER_NODE_MASTER);
	       
	       CuratorFramework client1 = builder.build();
	       CuratorFramework client2 = builder.build();
	       client1.start();
	       client2.start();
	       ZKPaths.mkdirs(client1.getZookeeperClient().getZooKeeper(), latchPath);
	       class LeaderLatchListenerImpl implements LeaderLatchListener{
				@Override
				public void isLeader() {
					System.out.println("Master");
				}

				@Override
				public void notLeader() {
					System.out.println("Slave");
				}
		    	   
		       }
	       
	       LeaderLatch latch1 = new LeaderLatch(client1, latchPath, "1");
	       LeaderLatch latch2 = new LeaderLatch(client2, latchPath, "2");
	       LeaderLatchListener listener = new LeaderLatchListenerImpl();
	       latch1.addListener(listener);
	       latch2.addListener(listener);
	       latch1.start();
	       latch2.start();
	       Thread.sleep(3 * 1000);// 等待Leader选举完成
//	       latch1.await();
//	       latch2.await();
	       
	       LeaderLatch currentLeader = null;
	       if (latch1.hasLeadership())
           {
               currentLeader = latch1;
           }
	       
	       if (latch2.hasLeadership())
           {
               currentLeader = latch2;
           }
	       
	       //System.out.println("当前leader：" + currentLeader.getId());
	       
//	       latch1.close();
//	       latch2.close();
//	       client1.close();
//	       client2.close();
	 }
}
