package com.huangyiming.disjob.register.repository.watch.listener;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;

import com.huangyiming.disjob.common.Constants;
import com.huangyiming.disjob.common.util.LocalHost;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.register.cache.ZKJobCache;
import com.huangyiming.disjob.register.repository.watch.WatchApiCuratorImpl;
import com.huangyiming.disjob.slaver.utils.SlaveUtils;

/**
 * <pre>
 * 
 *  File: JobGroupListener.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  监听/disJob/rpc下面子节点变化
 *  功能：有变化后自动在对应disJob/job上创建组信息
 * 
 *  Revision History
 *  Date,					Who,					What;
 *  2016年5月24日				Disjob				Initial.
 *
 * </pre>
 */
public class JobGroupListener extends AbstractJobBuild{
    @Override
    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception{
    	boolean isMaster = false;
        String [] array = event.getData().getPath().split(Constants.PATH_SEPARATOR);
        if (org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.CHILD_ADDED == event.getType()){
            LoggerUtil.debug("add group is ," + event.getData().getPath());
            String groupNode = event.getData().getPath();
            String masterIp = SlaveUtils.getLeaderLatch().getLeader().getId();

			if(StringUtils.isNotEmpty(masterIp) && masterIp.equals(new LocalHost().getIp())){
				isMaster = true;   
			}
            if (isMaster){   
            	LoggerUtil.debug(new LocalHost().getIp() + " is master,begin build job");
            	try{
            		buildJobByRPC(client, groupNode);
            	}catch (Exception e) {
					LoggerUtil.error("build job by rpc error",e);
				}
            }
           /* if (!ZKJobCache.groupList.contains(groupNode)){
                ZKJobCache.groupList.add(groupNode);
                
                if(array.length > 3){
                    Set<Job> list =  ZKJobCache.groupJobMap.get(array[3]);
                    if(CollectionUtils.isEmpty(list)){
                        list = new HashSet<Job>();
                        ZKJobCache.groupJobMap.put(array[3], list);//维护groupname和list<job>缓存
                    }
                  }
            }*/
            WatchApiCuratorImpl watch = new WatchApiCuratorImpl();
            watch.pathChildrenWatch(client, groupNode, false, new JobNameListener(groupNode));
        }
        
        if (org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.CHILD_REMOVED == event.getType()){
            LoggerUtil.info("remove group is ," + event.getData().getPath());
            String groupNode = event.getData().getPath();
            /*if (ZKJobCache.groupList.contains(groupNode)){
                ZKJobCache.groupList.remove(groupNode);
            }*/
           /* if (ZKJobCache.serverMap.containsKey(groupNode)){
                ZKJobCache.serverMap.remove(groupNode);
            }*/
          // ZKJobCache.groupJobMap.remove(array[3]); //维护缓存
           WatchApiCuratorImpl.closePathChildrenCache(groupNode);

        }
        
        if (org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.CHILD_UPDATED == event.getType()){
            LoggerUtil.info("update," + event.getData().getPath());
        }
     }
 }
