package com.huangyiming.disjob.register.repository.watch.listener;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.register.repository.watch.WatchApiCuratorImpl;
import com.huangyiming.disjob.slaver.utils.SlaveUtils;

/**
 * 监控/disJob/session 下的节点
 * <pre>
 * 
 *  File: SessionNodeListener.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 * 监听/disJob/session 下子节点的添加
 * 
 *  Revision History
 *
 *  Date：		2016年11月16日
 *  Author：		Disjob
 *
 * </pre>
 */
public class SessionNodeListener implements PathChildrenCacheListener{

	@Override
	public void childEvent(CuratorFramework client, PathChildrenCacheEvent event)
			throws Exception {
	 
		 if(event.getType() == org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.CHILD_ADDED){
 	        String path = event.getData().getPath();//->path:disJob/session/会话名称
 	        SlaveUtils.buildSessionHostMap(client);
  	        WatchApiCuratorImpl watch = new WatchApiCuratorImpl();
 	        LoggerUtil.debug("watch child path=="+path);
  	        
            watch.pathChildrenWatch(client,path , false, new ServerSessionListener());
		 }
 
	}
	
	

}
