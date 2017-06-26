package com.huangyiming.disjob.register.repository.election;

import java.util.concurrent.ExecutorService;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;

public interface LeaderElectionApi {
	LeaderLatch useLeaderLatch(CuratorFramework client, String latchPath, String id);
	
	LeaderSelector useLeaderSelector(CuratorFramework client, String leaderPath, ExecutorService executorService, LeaderSelectorListener listener);

}
