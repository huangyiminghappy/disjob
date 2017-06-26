package com.huangyiming.disjob.register.repository.watch.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

import com.huangyiming.disjob.common.Constants;
import com.huangyiming.disjob.common.util.LocalHost;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.register.repository.ZnodeApiCuratorImpl;
import com.huangyiming.disjob.slaver.utils.SlaveUtils;

/**
 * 
 * <pre>
 * 
 *  File: ServerSessionListener.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  监听disJob/session/会话名称/ip:port会话节点
 *  功能：监听disJob/session/会话名称下临时节点的增加和移除事件
 *  2.监听到临时会话节点变动则动态更改/disJob/rpc/group/jobname/providers/ip:port节点的值并维护一份缓存
 *  Revision History
 *
 *  Date：		2016年11月15日
 *  Author：		Disjob
 *
 * </pre>
 */
public class ServerSessionListener implements PathChildrenCacheListener{

	@Override
	public void childEvent(CuratorFramework client, PathChildrenCacheEvent event)throws Exception {
		  ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
		  String masterIp = SlaveUtils.getLeaderLatch().getLeader().getId();
		  boolean isMaster = false;
		  if (StringUtils.isNotEmpty(masterIp)
					&& masterIp.equals(new LocalHost().getIp())) { // master断线后选出新的master然后旧的master恢复连接,此时已经不再是master,不能创建job
				isMaster = true;
		  }
			
 		 if(event.getType() == org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.CHILD_ADDED){
 	         String path = event.getData().getPath();//->path:/disJob/session/会话名称/ip:port
 	         LoggerUtil.debug("ServerSessionListener add path: "+path); 
  	         //1 查看该session对应的组是哪个
 	         String [] array =  path.split(Constants.PATH_SEPARATOR);
 	         String sessionName = array[3];
 	         //有新的session添加进来,需要判断该session之前是不是绑定了组,如果之前绑定了组,代表该session是断线后重新连接上来,应该恢复该session对应provider下的节点值
 	         //
 	        if (isMaster) {
 	        	try {
 	        		String groupNode = znode.makePath(Constants.DISJOB_SERVER_NODE_PROJECT,Constants.PATH_SEPARATOR+sessionName);
 	        		if(znode.checkExists(client, groupNode)){
 	        			List<String> groupList = znode.getChildren(client, groupNode);
 	        			if(CollectionUtils.isNotEmpty(groupList)){
 	        				for(String group : groupList){
 	        					SlaveUtils.addProviderUrlNodeByGroupAndHost(client, group, array[4]);
 	        				}
 	        			}
 	        		}
				} catch (Exception e) {
					LoggerUtil.error("ServerSessionListener.CHILD_ADDED path=" + path, e);
				}
			 }
 	         
  	   		 ConcurrentHashMap<String, List<String>> sessionHostMap = com.huangyiming.disjob.register.cache.ZKJobCache.sessionHostMap;
	    	 if(sessionHostMap.size() == 0 ){
  	    		 sessionHostMap = SlaveUtils.buildSessionHostMap(client);
	    	 }else{
	    		 addSessionHostMap(array, sessionName, sessionHostMap);
	    	 }
 		}
		
		 if(event.getType() == org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.CHILD_REMOVED){
			//->path:/disJob/session/会话名称/ip:port
				String path = event.getData().getPath();
				LoggerUtil.debug("ServerSessionListener delete path="+path);
 	  	   		 ConcurrentHashMap<String, List<String>> sessionHostMap = com.huangyiming.disjob.register.cache.ZKJobCache.sessionHostMap;

				if (StringUtils.isNotEmpty(path)) {
					 String [] array =  path.split(Constants.PATH_SEPARATOR);
					 String sessionName = array[3];
					 List<String> hostList = sessionHostMap.get(sessionName);
					 if(CollectionUtils.isNotEmpty(hostList)){
						 hostList.remove(array[4]);
					 }
					 //移除缓存
					 sessionHostMap.put(sessionName, hostList);
					 //只有master才会删除
					 if (isMaster) {
			 	         String groupNode = znode.makePath(Constants.DISJOB_SERVER_NODE_PROJECT,Constants.PATH_SEPARATOR+sessionName);
	 					 List<String> groupList = znode.getChildren(client, groupNode);
	 					 List<String> deleteAllResult = new ArrayList<String>();
	 					 if(CollectionUtils.isNotEmpty(groupList)){
	 						 try {
	 							 for(String group : groupList){
	 								 //得到需要remove的节点
	 								 deleteAllResult.addAll(SlaveUtils.getProviderUrlNodeByGroupAndHost(client, group, array[4]));
	 							 }
	 							 SlaveUtils.deleteNodeList(client, deleteAllResult);
							} catch (Exception e) {
								LoggerUtil.error("ServerSessionListener.CHILD_REMOVED remove group got an exception : " + e);
							}
 	 					 }
					 }
				}
 	       }
 
	}

	private void addSessionHostMap(String[] array, String sessionName,ConcurrentHashMap<String, List<String>> sessionHostMap) {
		List<String> list = sessionHostMap.get(sessionName);
		 if(list == null){
				sessionHostMap.put(sessionName, new ArrayList<String>());
		  }else{
			  if(!list.contains(array[4])){
				  list.add(array[4]);
			  }
			  sessionHostMap.put(sessionName, list);
		  }
	}

}
