package com.huangyiming.disjob.register.rpc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.springframework.stereotype.Service;

import com.huangyiming.disjob.register.repository.ZnodeApiCuratorImpl;
import com.huangyiming.disjob.register.repository.watch.WatchApiCuratorImpl;
import com.huangyiming.disjob.register.repository.watch.listener.ServerNodeListener;
import com.huangyiming.disjob.register.utils.RegisterUtils;
import com.huangyiming.disjob.register.utils.ZooKeeperRegistryUtils;
import com.huangyiming.disjob.rpc.client.HURL;

/**
 * <pre>
 *  File: ZookeeperRegistry.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  zoopkeeper的订阅和发现处理
 * 
 *  Revision History
 *  Date,					Who,					What;
 *  2016年5月12日				Disjob				Initial.
 * </pre>
 */
@Service("zookeeperRegistry")
public class ZookeeperRegistry extends FailbackRegistry {
	private CuratorFramework zkClient;
	private ZnodeApiCuratorImpl znode;
	private WatchApiCuratorImpl watchApiCuratorImpl;
	private ConcurrentHashMap<HURL, ConcurrentHashMap<NotifyListener, PathChildrenCacheListener>> urlListeners = new ConcurrentHashMap<HURL, ConcurrentHashMap<NotifyListener, PathChildrenCacheListener>>();
	 
	//因为zkClient统一管理,所以这里的zkclient是设置进来,后期改为直接从client池里获取
	public void setZkClient(CuratorFramework zkClient)
    {
        this.zkClient = zkClient;
    }
	@PostConstruct
	public void init(){
	  
	}
	 public ZookeeperRegistry(){
	      super(new HURL());
 	      znode = new ZnodeApiCuratorImpl();
	      watchApiCuratorImpl = new WatchApiCuratorImpl();
	 }
	  
	 

	public ConcurrentHashMap<HURL, ConcurrentHashMap<NotifyListener, PathChildrenCacheListener>> getUrlListeners() {
		return urlListeners;
	}

	/**
	 * 订阅服务和注册监听
	 */
	@Override
	public void doSubscribe(final HURL url, final NotifyListener notifyListener) {
 		ConcurrentHashMap<NotifyListener, PathChildrenCacheListener> childChangeListeners = urlListeners.get(url);
		if (childChangeListeners == null) {
			urlListeners.putIfAbsent(url, new ConcurrentHashMap<NotifyListener, PathChildrenCacheListener>());
			childChangeListeners = urlListeners.get(url);
		}
		PathChildrenCacheListener zkChildListener = childChangeListeners.get(notifyListener);
		if (zkChildListener == null) {
			childChangeListeners.putIfAbsent(notifyListener, new ServerNodeListener(url, notifyListener));
			zkChildListener = childChangeListeners.get(notifyListener);
		}

		// 防止旧节点未正常注销
		removeNode(url, ZkNodeType.CONSUMER);
		createNode(url, ZkNodeType.CONSUMER);

		// 订阅server节点，并获取当前可用server
		String serverTypePath = RegisterUtils.toNodeTypePath(url, ZkNodeType.PROVIDER);
		List<String > currentChilds = watchApiCuratorImpl.pathChildrenWatch(zkClient, serverTypePath, false, zkChildListener);
		ZooKeeperRegistryUtils.notify(url, notifyListener, RegisterUtils.nodeChildsToUrls(zkClient, serverTypePath, currentChilds));
	}

	@Override
	public void doUnsubscribe(HURL url, NotifyListener notifyListener) {

	}

	/**
	 * client直接从服务端取URL对应的所有服务地址
	 */
	@Override
	public List<HURL> doDiscover(HURL url) {
		try {
			String parentPath = RegisterUtils.toNodeTypePath(url, ZkNodeType.PROVIDER);
			List<String> currentChilds = new ArrayList<String>();
			if (znode.checkExists(zkClient, parentPath)) {
				currentChilds = zkClient.getChildren().forPath(parentPath);
			}
			return RegisterUtils.nodeChildsToUrls(zkClient, parentPath, currentChilds);
		} catch (Throwable e) {
			throw new RuntimeException(String.format("Failed to discover %s from zookeeper(), cause: %s", url, e.getMessage()));
		}
	}

	@Override
	protected void doAvailable(HURL url) {

	}

	private void createNode(HURL url, ZkNodeType nodeType) {
		String nodeTypePath = RegisterUtils.toNodeTypePath(url, nodeType);
		if (!znode.checkExists(zkClient, nodeTypePath)) {
			znode.createPersistent(zkClient, nodeTypePath, true);
		}
		znode.createEphemeral(zkClient, RegisterUtils.toNodePath(url, nodeType), url.toFullStr());
	}

	private void removeNode(HURL url, ZkNodeType nodeType) {
		String nodePath = RegisterUtils.toNodePath(url, nodeType);
		if (znode.checkExists(zkClient, nodePath)) {
			znode.deleteByZnode(zkClient, nodePath);
		}
	}

	@Override
	protected void doUnavailable(HURL url) {
		// TODO Auto-generated method stub
  	}

	public void doRegister(HURL url) {
		// TODO Auto-generated method stub

	}

}
