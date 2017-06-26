package com.huangyiming.disjob.register.repository.watch.listener;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.utils.ZKPaths;

import com.google.common.collect.Sets;
import com.huangyiming.disjob.common.Constants;
import com.huangyiming.disjob.common.util.LocalHost;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.monitor.rms.CommonRMSMonitor;
import com.huangyiming.disjob.monitor.rms.MonitorType;
import com.huangyiming.disjob.register.repository.ZnodeApiCuratorImpl;
import com.huangyiming.disjob.slaver.utils.SlaveUtils;

/**
 * <pre>
 * 
 *  File: SlaveIpNodeListener.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  master监控/disJob/scheduler/slave/ip节点,发现slave挂掉后
 *  1自动分配该slave节点job到其他job上
 *  2.清空job节点的信息
 * 
 *  Revision History
 *  Date,					Who,					What;
 *  2016年6月14日				Disjob				Initial.
 *
 * </pre>
 */
public class SlaveIpNodeListener implements PathChildrenCacheListener {
	ZnodeApiCuratorImpl nodeApi = new ZnodeApiCuratorImpl();

	@Override
	public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
		// 监听到status节点移除,则进行该节点job的重新分配
		if (event.getType() == Type.CHILD_REMOVED) {
			// String masterIp = nodeApi.getData(client,
			// Constants.DISJOB_SERVER_NODE_MASTER_IP);
			String masterIp = SlaveUtils.getLeaderLatch().getLeader().getId();

			// 只有masterIp才能监听其他slave节点down的情况来进行job分配,有可能会存在某master暂时断线然后slave便为新master此时原来的master又网络好了,
			// 两个监听slave变化导致job分配两次,故增加masterIp判断
			if (StringUtils.isNotEmpty(masterIp) && !masterIp.equals(new LocalHost().getIp())) {
				return;
			}

			String path = event.getData().getPath();
			LoggerUtil.info("slave node : " + path + " is down ,so begin to distribute job to other slaves");
			LoggerUtil.trace("slave node : " + path + " is down ,so begin to distribute job to other slaves");
			String[] array = path.split(Constants.PATH_SEPARATOR);
			CuratorTransaction transaction = nodeApi.startTransaction(client);

			if (array.length > 4) {
				String tmp = array[5];
				String ip = array[4];
				// 如果全部disJob均不可用,则报警
				if (StringUtils.isNoneEmpty(tmp) && tmp.equalsIgnoreCase("status")) {
					// slave得重新分配
					// 等待几秒钟如果重新连接上来则不分配job
					Thread.sleep(Constants.WAIT_RECONNECT_TIME);
					// 如果5s后又重新连接上来(status又有值则不进行slave上job转移等处理)

					if (nodeApi.checkExists(client, path)) {
						LoggerUtil.info("slave node : " + path + " is down ,but after " + Constants.WAIT_RECONNECT_TIME
								+ " ms connect to zk ,so not distribute");
						LoggerUtil.trace("slave node : " + path + " is down ,but after " + Constants.WAIT_RECONNECT_TIME
								+ " ms connect to zk ,so not distribute");
						return;
					}

					try {
						SlaveUtils.distributeSlave(ip, client, transaction);
						// 清空该节点上job
						SlaveUtils.clearSlaveJobByTransaction(ip, client, transaction);
						/*
						 * CuratorTransactionFinal curatorTransactionFinal =
						 * transaction.check().forPath("/").and();
						 * curatorTransactionFinal.commit();
						 */
					} catch (Exception e) {
						LoggerUtil.error("SlaveIpNodeListener.childEvent distributeSlave and clearSlavDISJOBByTransaction got an exception ", e);
					}
				}
				String slavePath = ZKPaths.makePath(Constants.ROOT, Constants.DISJOB_SERVER_NODE_ROOT, Constants.DISJOB_SERVER_NODE_SLAVE);
				List<String> slaveIPs = nodeApi.getChildren(client, slavePath);
				int available = 0;
				Set<String> availableIps = Sets.newConcurrentHashSet();
				Set<String> unAvailableIps = Sets.newConcurrentHashSet();
				if (slaveIPs != null && !slaveIPs.isEmpty()) {
					for (String slaveIP : slaveIPs) {
						String slaveIPStatusPath = ZKPaths.makePath(slavePath, slaveIP, Constants.DISJOB_SERVER_NODE_SLAVE_STATUS);
						if (nodeApi.checkExists(client, slaveIPStatusPath) && Constants.DISJOB_SLAVE_STATUS.equals(nodeApi.getData(client, slaveIPStatusPath))) {
							available++;
							availableIps.add(slaveIP);
						}else{
							unAvailableIps.add(slaveIP);
						}
					}
				}
				if (available == 1) {
					notifySingleDisJobAvailable(availableIps, unAvailableIps);
				} else {
					notifyPartDisJobUnAvailable(availableIps, unAvailableIps);
				}
			}
		}
	}

	private void notifyPartDisJobUnAvailable(Set<String> availableIps, Set<String> unAvailableIps) {
		CommonRMSMonitor.sendSystem(MonitorType.System.DISJOB_EXCEPTION, "disJob part unavaiable! availableIps[" + availableIps + "] unAvailableIps[" + unAvailableIps + "]");
	}

	private void notifySingleDisJobAvailable(Set<String> availableIps, Set<String> unAvailableIps) {
		CommonRMSMonitor.sendSystem(MonitorType.System.SERIOUS_DISJOB_SIMGLE_AVAILABLE, "disJob only one avaiable! availableIps[" + availableIps + "] unAvailableIps[" + unAvailableIps + "]");
	}
}
