package com.huangyiming.disjob.register.repository.watch.listener;

import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;

/**
 * <pre>
 * 
 *  File: AbstractNodeCacheListener.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  功能：子节点数据变化监听器包装类
 * 
 *  Revision History
 *  Date,					Who,					What;
 *  2016年5月31日				Disjob				Initial.
 *
 * </pre>
 */
public abstract class AbstractNodeCacheListener implements NodeCacheListener
{
     protected  NodeCache nodeCache;

    public void setNodeCache(NodeCache nodeCache)
    {
        this.nodeCache = nodeCache;
    }

    public NodeCache getNodeCache()
    {
        return nodeCache;
    }
    
}

