package com.huangyiming.disjob.register.rpc;

import java.util.List;

import com.huangyiming.disjob.rpc.client.HURL;

/**
 * <pre>
 * 
 *  File: IServiceDiscovery.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  服务发现接口
 * 
 *  Revision History
 *  Date,					Who,					What;
 *  2016年5月16日				Disjob				Initial.
 *
 * </pre>
 */
public interface IServiceDiscovery
{
    /**
     * 
     * 根据服务名得到所有服务的地址.
     *
     * @param serviceName
     * @return
     * @throws Exception
     */
    public List<String> getProvidesByService(String group,String serviceName) throws Exception;
    
    
    public List<String> getconsumersByService(String group,String serviceName) throws Exception;
    
    //public List<HURL> getHURListProvidesByService(String group,String serviceName) throws Exception;

    
    /**
     * 
     * 根据URL得到所有服务的地址
     *
     * @param url
     * @return
     */
    public List<HURL> doDiscover(HURL url) ;
    
    
    /**
     * 
     * 根据请求id获取该请求对应的服务列表
     *
     * @param serviceName
     * @return
     * @throws Exception
     */
    public List<HURL> getProvidesByRequestId(String requestId) throws Exception;


}

