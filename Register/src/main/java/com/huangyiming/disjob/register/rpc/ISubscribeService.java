package com.huangyiming.disjob.register.rpc;

import org.apache.curator.framework.CuratorFramework;

import com.huangyiming.disjob.rpc.client.HURL;


/**
 * <pre>
 * 
 *  File: SubscribeService.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  服务订阅接口
 * 
 *  Revision History
 *  Date,                   Who,                    What;
 *  2016年5月14日              Disjob             Initial.
 *
 * </pre>
 */
public interface ISubscribeService
{
       public boolean DoSubscribe(HURL url);
       
       public boolean UnDoSubscribe(CuratorFramework client,HURL url);
    
}

