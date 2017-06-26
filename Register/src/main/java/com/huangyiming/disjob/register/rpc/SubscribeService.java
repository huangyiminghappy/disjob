package com.huangyiming.disjob.register.rpc;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.stereotype.Service;

import com.huangyiming.disjob.common.util.DeepCopy;
import com.huangyiming.disjob.common.util.LocalHost;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.monitor.db.service.DBJobBasicInfoService;
import com.huangyiming.disjob.register.domain.Job;
import com.huangyiming.disjob.register.utils.ZooKeeperRegistryUtils;
import com.huangyiming.disjob.rpc.client.HURL;
import com.huangyiming.disjob.monitor.db.domain.DBJobBasicInfo;

/**
 * <pre>
 * 
 *  File: SubscribeService.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  订阅和发现接口
 * 
 *  Revision History
 *  Date,					Who,					What;
 *  2016年5月14日				Disjob				Initial.
 *
 * </pre>
 */
@Service("subscribeService")
public class SubscribeService implements ISubscribeService ,IServiceDiscovery{
	
	@Resource
	public   DBJobBasicInfoService  jobBasicInfoService;
    @Override
    public boolean DoSubscribe(HURL url)
    {   //订阅的host和port是指的本地的IP和端口
        
          final NotifyListener notifyListener = new NotifyListener() {
            @Override
            public void notify(HURL registryUrl, List<HURL> urls) {
            	
            }
        };
         ZooKeeperRegistryUtils.zookeeperRegistry.doSubscribe(url, notifyListener);
      
        return false;
    }


    @Override
    public boolean UnDoSubscribe(CuratorFramework client, HURL url)
    {
        
        final NotifyListener notifyListener = new NotifyListener() {
            @Override
            public void notify(HURL registryUrl, List<HURL> urls) {}
        };
        ZooKeeperRegistryUtils.zookeeperRegistry.unsubscribe(url, notifyListener);
        return false;
    }


    /**
     * 根据组名和服务名查询该服务对应的RPC列表
     */
    @Override
    public List<String> getProvidesByService(String group,String serviceName) throws Exception
    {
        HURL clientUrl = new HURL(group,  new LocalHost().getIp(),serviceName);

        List<HURL> urlList =  ZookeeperRegistry.subscribedCategoryResponses.get(clientUrl) !=null ?ZookeeperRegistry.subscribedCategoryResponses.get(clientUrl):null;
        
        List<String> resultList = new ArrayList<String>();
        synchronized (ZookeeperRegistry.subscribedCategoryResponses) {
 	        if(CollectionUtils.isNotEmpty(urlList)){
	            getStrUrlListByHurls(urlList, resultList);
	        }
	        else{
	            urlList = ZooKeeperRegistryUtils.zookeeperRegistry.discover(clientUrl);
	            getStrUrlListByHurls(urlList, resultList);
	        }
    	
	   }
        
        return resultList;
    }

    /**
     * 
     * 把HURL对象转化为string的list对象.
     *
     * @param urlList
     * @param resultList
     */
    private void getStrUrlListByHurls(List<HURL> urlList, List<String> resultList)
    {
             for(HURL hurl : urlList){
                resultList.add(hurl.getClientUrl());
            }        
    }
 
    /**
     * 根据HURL查询服务列表,构造HURL时候必须传group和serverName
     */
    @Override
    public List<HURL> doDiscover(HURL url)
    {
 
        List<HURL> urlList = ZooKeeperRegistryUtils.zookeeperRegistry.discover(url);
        
        return urlList;
    }


	@Override
	public List<String> getconsumersByService(String group, String serviceName)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


	 /**
	  * 从缓存中group和serviceName对应的hurl列表
	  * @param group
	  * @param serviceName
	  * @return
	  */
	public static List<HURL> getHURListProvidesByService(String group,String serviceName) {
		HURL clientUrl = new HURL(group, new LocalHost().getIp(), serviceName);
		List<HURL> urlList = ZookeeperRegistry.subscribedCategoryResponses.get(clientUrl);
		List<HURL> resultList = new ArrayList<HURL>();
		int checkUrlListCount = 3 ;
		do{
			synchronized (ZookeeperRegistry.subscribedCategoryResponses) {
	  			if (CollectionUtils.isEmpty(urlList)) {
	  				try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						LoggerUtil.error("getHURListProvidesByService sleep 1s error",e);
					}
					urlList = ZooKeeperRegistryUtils.zookeeperRegistry.discover(clientUrl);
					ZookeeperRegistry.subscribedCategoryResponses.put(clientUrl, urlList);
				}
				try {
					  resultList = DeepCopy.copy(urlList);
				} catch (Exception e) {
					LoggerUtil.error("getHURListProvidesByService DeepCopy.copy error",e);
				}
			}
		}while(CollectionUtils.isEmpty(resultList) && checkUrlListCount-- > 0);
		
		return resultList;
	}
  
	public static void main(String[] args) {
		HURL clientUrl = new HURL("group", new LocalHost().getIp(), "abc");
		List<HURL> list =new ArrayList<HURL>();
		HURL a = new HURL("group1", new LocalHost().getIp(), "abc");

		list.add(a);
 		ZookeeperRegistry.subscribedCategoryResponses.put(clientUrl, list);
 		List<HURL> resultList = new ArrayList<HURL>();
 		try {
 			resultList = DeepCopy.copy(list);
 		} catch (Exception e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
 		for(HURL u :list){
 			u.setServerGroup("test");
 		}
 		for(HURL u :resultList){
 			 System.out.println(u.getServerGroup());
 		}
 		
	}

 


public static String buildRpcUrl(Job job){
	StringBuffer sb = new StringBuffer("");
	sb.append("disJob://").append(job.getHost());
	sb.append(":").append(job.getPort());
	sb.append("/").append(job.getJobName()).append("?").append("serverGroup=");
	sb.append(job.getGroupName()).append("&phpFilePath=");
	sb.append(job.getFilePath());
	sb.append("&className=").append(job.getClassName());
	sb.append("&methodName=").append(job.getMethodName());
	sb.append("&version=1");
	return sb.toString();
 
}


	
	

	/**
	 * 根据请求id得到对应的服务ip端口列表
	 */
	@Override
	public List<HURL> getProvidesByRequestId(String requestId)
			throws Exception {
		DBJobBasicInfo info = jobBasicInfoService.findByUuid(requestId);
		List<HURL> result = new ArrayList<HURL>();
		if(info == null){
			return result;
		} 
		return getHURListProvidesByService(info.getGroupName(), info.getJobName());
  	}

}

