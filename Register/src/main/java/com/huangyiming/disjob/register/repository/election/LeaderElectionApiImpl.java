package com.huangyiming.disjob.register.repository.election;

import java.util.concurrent.ExecutorService;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;

public class LeaderElectionApiImpl implements LeaderElectionApi {

	@Override
	public LeaderLatch useLeaderLatch(CuratorFramework client, String latchPath, String id) {
		return new LeaderLatch(client, latchPath, id);
	}

	@Override
	public LeaderSelector useLeaderSelector(CuratorFramework client, String leaderPath, ExecutorService executorService,
			LeaderSelectorListener listener) {
		return null;
	}

}
