package com.huangyiming.disjob.register.repository.watch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.springframework.stereotype.Service;

import com.huangyiming.disjob.common.exception.ZKNodeException;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.register.repository.watch.listener.AbstractNodeCacheListener;

/**
 * 实现WatchApi接口
 * 
 * @author Disjob
 *
 */
@Service("watchApi")
public class WatchApiCuratorImpl implements WatchApi {
	private static final ConcurrentHashMap<String, PathChildrenCache> pathChildrenCacheMap=new ConcurrentHashMap<String, PathChildrenCache>();

	public PathChildrenCache pathChildrenCache;
	
	//public final static ArrayBlockingQueue<PathChildrenCache> childrenCacheLst = new ArrayBlockingQueue<PathChildrenCache>(500);

	//static final ThreadFactory defaultThreadFactory = ThreadUtils.newThreadFactory("TreeCache");

	@SuppressWarnings("resource")
	public void nodeWatch(CuratorFramework client, String znode, NodeCacheListener listener) throws ZKNodeException {
		final NodeCache nodeCache = new NodeCache(client, znode);
		try
        {
            nodeCache.start(true);
        }
        catch (Exception e)
        {
            LoggerUtil.error("client start error",e);
        }
		nodeCache.getListenable().addListener(listener);
	}
	
	/**
	 * 监控节点变化的时候如果需要查看变化为具体什么值需要使用本方法
	 * @param client
	 * @param znode
	 * @param listener
	 * @throws ZKNodeException
	 */
	public void nodeWatch(CuratorFramework client, String znode, AbstractNodeCacheListener listener) throws ZKNodeException {
        final NodeCache nodeCache = new NodeCache(client, znode);
        try
        {
            nodeCache.start(true);
        }
        catch (Exception e)
        {
            LoggerUtil.error("client start error",e);
        }
       
        nodeCache.getListenable().addListener(listener);
        listener.setNodeCache(nodeCache);
    }

	public void nodeWatch(CuratorFramework client, String znode, boolean dataIsComressed, NodeCacheListener listener)
			throws ZKNodeException {
		@SuppressWarnings("resource")
		final NodeCache nodeCache = new NodeCache(client, znode, dataIsComressed);
		try
        {
            nodeCache.start(true);
        }
        catch (Exception e)
        {
            LoggerUtil.error("client start error",e);
        }
		nodeCache.getListenable().addListener(listener);
	}

	public List<String> pathChildrenWatch(CuratorFramework client, String znode, boolean cacheData,
			PathChildrenCacheListener listener) throws ZKNodeException {
		List<String> result = new ArrayList<String>();
		final PathChildrenCache childrenCache = new PathChildrenCache(client, znode, cacheData);
		//childrenCache.getListenable() .addListener(listener,Executors.newFixedThreadPool(50));
		childrenCache.getListenable() .addListener(listener);

		setPathChildrenCache(childrenCache);
		try {
			childrenCache.start();
			//childrenCacheLst.put(childrenCache);
		} catch (Exception e) {
			LoggerUtil.error("childrenCache start error", e);
		}
		
		List<ChildData> childDataList = pathChildrenCache.getCurrentData();
		for (ChildData data : childDataList) {
			result.add(data.getPath());
		}
		pathChildrenCacheMap.put(znode, childrenCache);

		return result;
	}

	@Override
	public TreeCache treeWatch(CuratorFramework client, String znode, TreeCacheListener listener) {
		TreeCache cache =  new TreeCache(client, znode);
		cache.getListenable().addListener(listener);
		try {
			cache.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cache;
	}
	
	public PathChildrenCache getPathChildrenCache() {
		return pathChildrenCache;
	}

	public void setPathChildrenCache(PathChildrenCache pathChildrenCache) {
		this.pathChildrenCache = pathChildrenCache;
	}
	public static void closePathChildrenCache(String key){
		PathChildrenCache pathChildrenCache=pathChildrenCacheMap.get(key);
		if(pathChildrenCache!=null){
			try {
			    LoggerUtil.info("begin close listener ["+key+"] 的 pathChildrenCache");
				pathChildrenCache.close();
				pathChildrenCacheMap.remove(key);
			} catch (IOException e) {
				// TODO Auto-generated catch block
			  LoggerUtil.error("关闭 ["+key+"] 失败");
			}
		}
	}
}
