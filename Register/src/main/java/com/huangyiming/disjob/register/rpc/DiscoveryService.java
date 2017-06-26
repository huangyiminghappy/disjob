package com.huangyiming.disjob.register.rpc;

import java.util.List;

import com.huangyiming.disjob.rpc.client.HURL;

/**
 * 
 * <pre>
 * 
 *  File: DiscoveryService.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  服务发现订阅接口
 * 
 *  Revision History
 *
 *  Date：		2016年5月28日
 *  Author：		Disjob
 *
 * </pre>
 */
public interface DiscoveryService {

    void subscribe(HURL url, NotifyListener listener);

    void unsubscribe(HURL url, NotifyListener listener);
 
    List<HURL> discover(HURL url);
    
     
}