package com.huangyiming.disjob.register.repository.watch.listener;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;

import com.huangyiming.disjob.common.util.LocalHost;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.register.rpc.ZookeeperRegistry;
import com.huangyiming.disjob.register.utils.RegisterUtils;

/**
 * master选举的listener实现
 * @author Disjob
 */
public class LeaderLatchListenerImpl implements LeaderLatchListener {
	
	private final LocalHost localHost = new LocalHost();
	public CuratorFramework client;
	public ZookeeperRegistry zookeeperRegistry;
	
	public LeaderLatchListenerImpl(CuratorFramework client,ZookeeperRegistry zookeeperRegistry){
	    this.client = client;
	    this.zookeeperRegistry = zookeeperRegistry;
	    zookeeperRegistry.setZkClient(client);
	}
	
	/**
	 * goes from hasLeadership = false to hasLeadership = true
	 */
	@Override
	public void isLeader() {
		LoggerUtil.info("DisJob:HostName->" + localHost.getHostName() + " IP->" + localHost.getIp() +"成功成为leader");
        RegisterUtils.watchRpc2Job( client, zookeeperRegistry);

	}

	/**
	 * goes from hasLeadership = true to hasLeadership = false
	 */
	@Override
	public void notLeader() {
		LoggerUtil.info("DisJob:HostName->" + localHost.getHostName() + " IP->" + localHost.getIp() +"成为slave");
	}

}
