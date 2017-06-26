package com.huangyiming.disjob.register.rpc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.CollectionUtils;

import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.rpc.client.HURL;
import com.huangyiming.disjob.rpc.client.HURLParamType;

 
/**
 * <pre>
 * 
 *  File: AbstractRegistry.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  订阅发现抽象类,主要是发现和通知逻辑
 * 
 *  Revision History
 *  Date,					Who,					What;
 *  2016年5月12日				Disjob				Initial.
 *
 * </pre>
 */
public abstract class AbstractRegistry implements DiscoveryService  {

    public final static ConcurrentHashMap<HURL,  List<HURL>> subscribedCategoryResponses = new ConcurrentHashMap<HURL, List<HURL>>();

    protected String registryClassName = this.getClass().getSimpleName();

    public AbstractRegistry() {
        
    }
    
    public void unregister(HURL url){
        
    }
    
    public void register(HURL url){
        
    }
    //@Override
    public void subscribe(HURL url, NotifyListener listener) {
        if (url == null || listener == null) {
            LoggerUtil.warn("[{}] subscribe with malformed param, url:{}, listener:{}", registryClassName, url, listener);
            return;
        }
        LoggerUtil.warn("[{}] Listener ({}) will subscribe to url ({}) in Registry [{}]", registryClassName, listener, url);
        doSubscribe(url.createCopy(), listener);
    }

    @Override
    public void unsubscribe(HURL url ,NotifyListener listener) {
        if (url == null || listener == null) {
            LoggerUtil.warn("[{}] unsubscribe with malformed param, url:{}, listener:{}", registryClassName, url, listener);
            return;
        }
        LoggerUtil.info("[{}] Listener ({}) will unsubscribe from url ({}) in Registry [{}]", registryClassName, listener, url
                );
        doUnsubscribe(url.createCopy(), listener);
    }

    /**
     * 发现注册服务的URL,直接从本地缓存中取
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<HURL> discover(HURL url) {
        if (url == null) {
            LoggerUtil.warn("[{}] discover with malformed param, refUrl is null", registryClassName);
            return Collections.EMPTY_LIST;
        }
        url = url.createCopy();
        List<HURL> results = new ArrayList<HURL>();

        List<HURL> categoryUrls = subscribedCategoryResponses.get(url);
        synchronized (subscribedCategoryResponses) {
	        if (CollectionUtils.isNotEmpty(categoryUrls)) {
	           
	                for (HURL tempUrl : categoryUrls) {
	                    results.add(tempUrl.createCopy());
	                }
	            
	        } else {
	            List<HURL> urlsDiscovered = doDiscover(url);
	            //add 把查询到的数据同步更新到缓存中,正常情况下发现的数据应该都有
	             
	            subscribedCategoryResponses.put(url, urlsDiscovered);
	            //
	            
	            if (urlsDiscovered != null) {
	                for (HURL u : urlsDiscovered) {
	                    results.add(u.createCopy());
	                }
	            }
	        }
       }
        return results;
    }

     

    protected List<HURL> getCachedUrls(HURL url) {
        List<HURL> rsUrls = subscribedCategoryResponses.get(url);
        List<HURL> urls = new ArrayList<HURL>();
        synchronized (subscribedCategoryResponses) {
	        if (rsUrls == null || rsUrls.size() == 0) {
	            return null;
	        }
	
	        for (HURL tempUrl : rsUrls) {
	            urls.add(tempUrl.createCopy());
	        }
        }
         return urls;
    }

    

    /**
     * 移除不必提交到注册中心的参数。这些参数不需要被client端感知。
     *
     * @param url
     */
    private HURL removeUnnecessaryParmas(HURL url) {
        // codec参数不能提交到注册中心，如果client端没有对应的codec会导致client端不能正常请求。
        url.getParameters().remove(HURLParamType.codec.getName());
        return url;
    }

   
    protected abstract void doSubscribe(HURL url, NotifyListener listener);

    protected abstract void doUnsubscribe(HURL url, NotifyListener listener);

    protected abstract List<HURL> doDiscover(HURL url);

    protected abstract void doAvailable(HURL url);

    protected abstract void doUnavailable(HURL url);
}

