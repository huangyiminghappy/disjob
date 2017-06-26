package com.huangyiming.disjob.register.repository.watch;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;

import com.huangyiming.disjob.common.exception.ZKNodeException;

/**
 * 封装Curator监听方法
 * 注意，Curator无法对二级子节点进行监听
 * 比如用pathChildrenWatch对/test节点进行监听
 * 那么/test/test1/test2节点的增删改是无法监听到的
 * @author Disjob
 *
 */
public interface WatchApi {
	/**
	 * 用于监听指定ZooKeeper节点本身的变化
	 * 需要实现NodeCacheListener接口并将其传入
	 * @param client
	 * @param znode
	 * @param listhener
	 * @throws ZKNodeException
	 */
	void nodeWatch(CuratorFramework client, String znode, NodeCacheListener listener)  throws ZKNodeException;
    
	/**
	 * 用于监听指定ZooKeeper节点本身的变化
	 * 可指定是否需要对数据进行压缩
	 * 需要实现NodeCacheListener接口并将其传入
	 * @param client
	 * @param znode
	 * @param dataIsComressed
	 * @param listhener
	 * @throws ZKNodeException
	 */
	void nodeWatch(CuratorFramework client, String znode, boolean dataIsComressed, NodeCacheListener listener)  throws ZKNodeException;
     
	/**
	 * 用于监听指定节点的子节点变化情况
	 * cacheData，如果为true，那么当对子节点调用setData时，
	 * PathChildrenCache会收到CHILD_UPDATED事件
	 * 需要实现PathChildrenCacheListener接口并将其传入
	 * @param client
	 * @param znode
	 * @param cacheData
	 * @param listener
	 * @throws ZKNodeException
	 */
    List<String> pathChildrenWatch(CuratorFramework client, String znode, boolean cacheData, PathChildrenCacheListener listener) throws ZKNodeException;
    
    /**
     * 
     * @param client
     * @param znode
     * @param listener
     * @return
     * @throws ZKNodeException
     */
    TreeCache treeWatch(CuratorFramework client, String znode, TreeCacheListener listener) throws ZKNodeException;
}
