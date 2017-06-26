package com.huangyiming.disjob.register.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.ZKPaths;

import com.huangyiming.disjob.common.Constants;
import com.huangyiming.disjob.common.exception.DisJobFrameWorkException;
import com.huangyiming.disjob.common.util.DeepCopy;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.register.repository.ZnodeApiCuratorImpl;
import com.huangyiming.disjob.register.repository.watch.WatchApiCuratorImpl;
import com.huangyiming.disjob.register.repository.watch.listener.JobGroupListener;
import com.huangyiming.disjob.register.repository.watch.listener.SlaveIpNodeListener;
import com.huangyiming.disjob.register.rpc.DisJobConstants;
import com.huangyiming.disjob.register.rpc.ZkNodeType;
import com.huangyiming.disjob.register.rpc.ZookeeperRegistry;
import com.huangyiming.disjob.rpc.client.HURL;

/**
 * <pre>
 * 
 *  File: Utils.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  订阅相关的工具类
 * 
 *  Revision History
 *  Date,					Who,					What;
 *  2016年5月19日				Disjob				Initial.
 *
 * </pre>
 */
public class RegisterUtils {
	private RegisterUtils(){}
	
	public final static ConcurrentHashMap<String, Boolean> CHECK_TRIGGER_MAP = new ConcurrentHashMap<String, Boolean>();
	
	public static String toServicePath(HURL url) {
		return toGroupPath(url) + DisJobConstants.PATH_SEPARATOR + url.getServerName();
	}

	public static String toNodeTypePath(HURL url, ZkNodeType nodeType) {
		String type = "";
		if (nodeType == ZkNodeType.PROVIDER) {
			type = Constants.DISJOB_PROVIDERS;
		} /*
			 * else if (nodeType == ZkNodeType.UNAVAILABLE_SERVER) { type =
			 * "unavailbleServer"; }
			 */ else if (nodeType == ZkNodeType.CONSUMER) {
			type = Constants.DISJOB_CONSUMERS;
		} else {
			throw new DisJobFrameWorkException(
					String.format("Failed to get nodeTypePath, url: %s type: %s", url, nodeType.toString()));
		}
		return toServicePath(url) + DisJobConstants.PATH_SEPARATOR + type;
	}

	public static String toNodePath(HURL url, ZkNodeType nodeType) {
		return toNodeTypePath(url, nodeType) + DisJobConstants.PATH_SEPARATOR + url.getServerPortStr();
	}

	public static String toGroupPath(HURL url) {
		return DisJobConstants.ZOOKEEPER_REGISTRY_NAMESPACE + DisJobConstants.PATH_SEPARATOR + url.getGroup();
	}

	/**
	 * 
	 * 根据父节点和对应子节点的值得到对应的serverUrl的值并封装成HURL对象.
	 *
	 * @param client
	 * @param parentPath
	 * @param currentChilds
	 * @return
	 */
	public static List<HURL> nodeChildsToUrls(CuratorFramework client, String parentPath, List<String> currentChilds) {
		if (client.getState() != org.apache.curator.framework.imps.CuratorFrameworkState.STARTED) {
			client.start();
		}
		List<HURL> urls = new ArrayList<HURL>();
		if (CollectionUtils.isEmpty(currentChilds)) {
			return urls;
		}
		for (String node : currentChilds) {
			String nodePath = parentPath + DisJobConstants.PATH_SEPARATOR + node;
			// 因为存储是用存在字符串的字节数组故获取数据的时候也先得到字节数组再转化为字符串
			byte[] bytes = null;
			try {
				bytes = client.getData().forPath(nodePath);
			} catch (Exception e1) {
				LoggerUtil.error(String.format("get node path %s error", nodePath), e1);
			}
			// 没有对应的RPCurl则跳过不处理
			if (bytes == null) {
				continue;
			}

			String data = "";
			try {
				data = new String(bytes, DisJobConstants.DEFAULT_CHARACTER);
			} catch (UnsupportedEncodingException e1) {
				LoggerUtil.error(String.format("bytes to string error,byte get from path is  %s error", nodePath), e1);
				continue;
			}
			try {
				HURL url = HURL.valueOf(data);
				// 客户端调用服务端的地址
				url.setClientUrl(data);
				urls.add(url);
			} catch (Exception e) {
				LoggerUtil.warn(String.format("Found malformed urls from zookeeperRegistry, path=%s", nodePath), e);
			}
		}
		List<HURL> resultList = new ArrayList<HURL>();

		try {
			resultList = (List<HURL>)DeepCopy.copy(urls);
		} catch (Exception e) {
			 LoggerUtil.error(" RegisterUtils.nodeChildsToUrls deep copy error",e);
		}
		return resultList;
	}

	/**
	 * 监听RPC节点group和server
	 * 
	 * @param isMaster
	 *            是否master节点,如果是master节点则创建job节点
	 * @param client
	 * @param zookeeperRegistry
	 */
	public static void watchRpc2Job( CuratorFramework client, ZookeeperRegistry zookeeperRegistry) {
		WatchApiCuratorImpl watch = new WatchApiCuratorImpl();
		String parentNode = ZKPaths.makePath(Constants.ROOT, Constants.DISJOB_RPC_NODE_ROOT);
		watch.pathChildrenWatch(client, parentNode, false, new JobGroupListener());

	}

	/**
	 * master监控slave集群中机器状态,发现机器挂了自动分配机器上job
	 * @param client
	 */
	public static void watchSlaveNodeStatus(CuratorFramework client) {
		WatchApiCuratorImpl watch = new WatchApiCuratorImpl();
		ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
		String slaveNode = znode.makePath(Constants.ROOT, Constants.DISJOB_SERVER_NODE_ROOT,
				Constants.DISJOB_SERVER_NODE_SLAVE);
		List<String> ipList = znode.getChildren(client, slaveNode);
		if (CollectionUtils.isNotEmpty(ipList)) {
			for (String ip : ipList) {
				String ipNode = znode.makePath(Constants.ROOT, Constants.DISJOB_SERVER_NODE_ROOT,
						Constants.DISJOB_SERVER_NODE_SLAVE, Constants.PATH_SEPARATOR + ip);
				watch.pathChildrenWatch(client, ipNode, false, new SlaveIpNodeListener());
			}
		}
	}

}
