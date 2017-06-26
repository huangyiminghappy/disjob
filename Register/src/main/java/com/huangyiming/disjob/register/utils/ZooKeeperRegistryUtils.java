package com.huangyiming.disjob.register.utils;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import com.huangyiming.disjob.register.rpc.NotifyListener;
import com.huangyiming.disjob.register.rpc.ZookeeperRegistry;
import com.huangyiming.disjob.rpc.client.HURL;

/**
 * <pre>
 * 
 *  File: ZooKeeperRegistryUtils.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  zoopkeeperRegister是一个单例,多个地方都要用,做一个统一的入口获取
 * 
 *  Revision History
 *  Date,					Who,					What;
 *  2016年5月30日				Disjob				Initial.
 *
 * </pre>
 */
@Service("zooKeeperRegistryUtils")
public class ZooKeeperRegistryUtils{
     
    public static ZookeeperRegistry zookeeperRegistry;

    public  ZookeeperRegistry getZookeeperRegistry(){
        return zookeeperRegistry;
    }

    @Resource
    public   void setZookeeperRegistry(ZookeeperRegistry zookeeperRegistry){
        ZooKeeperRegistryUtils.zookeeperRegistry = zookeeperRegistry;
    }
    
    /**
     * 消息通知,当子节点有变化时候会触发通知.
     *
     * @param refUrl
     * @param listener
     * @param urls
     */
    public static void notify(HURL refUrl, NotifyListener listener, List<HURL> urls) {
         
        if (listener == null || urls == null) {
            return;
        }
        
        List<HURL> curls = ZookeeperRegistry.subscribedCategoryResponses.get(refUrl);
        synchronized (ZookeeperRegistry.subscribedCategoryResponses) {
  	        if (CollectionUtils.isEmpty(curls)) {
	        	curls = new ArrayList<HURL>();
	        }
        
  	        curls.clear();
  	        curls.addAll(urls);//这里直接添加不做是否存在判断,因为urls已经代表所有子节点的最新值
  	        ZookeeperRegistry.subscribedCategoryResponses.put(refUrl,curls);
        }
     }
}

