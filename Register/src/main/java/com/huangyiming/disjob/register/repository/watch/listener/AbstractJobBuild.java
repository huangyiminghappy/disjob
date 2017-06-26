package com.huangyiming.disjob.register.repository.watch.listener;

 
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.utils.ZKPaths;

import com.huangyiming.disjob.common.Constants;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.register.auth.node.GroupAuthNode;
import com.huangyiming.disjob.register.domain.Job;
import com.huangyiming.disjob.register.repository.ZnodeApiCuratorImpl;
import com.google.gson.Gson;

/**
 * <pre>
 * 
 *  File: AbstractJobBuild.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  监听/disJob/rpc和监听/disJob/rpc/group的时候共同的父类,
 *  功能：用于监听后创建节点
 * 
 *  Revision History
 *  Date,					Who,					What;
 *  2016年5月25日				Disjob				Initial.
 *
 * </pre>
 */ 
public abstract class AbstractJobBuild implements PathChildrenCacheListener{   
    public void buildJobByRPC(CuratorFramework client, String path) {
    	
         String node = path;
         node = node.replace(Constants.DISJOB_RPC_NODE_ROOT, Constants.APP_JOB_NODE_ROOT);
         ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
         String [] temp =  node.split(Constants.PATH_SEPARATOR);
          //如果是类似/disJob/job/oms/server1结构则直接判断是否存在/disJob/job/oms/server1/config->Jobstatus,不存在则创建并设置值
         if(temp.length >=5){
        	String groupName = temp[3];
     		String jobName = temp[4];
            buildJob(client, node, znode, groupName,jobName);        
        }else{ //代表的是添加了组则创建组
            if (znode.checkExists(client, node)){
            	return ;
            } 
			LoggerUtil.debug("begin build group " + node);
			String nodename = ZKPaths.getNodeFromPath(node);
			znode.createPersistent(client, node, new GroupAuthNode(client, nodename).createACLs());
			java.util.List<String> jobList = znode.getChildren(client, node);// 构造组的时候再看下是否有job产生
			if (CollectionUtils.isEmpty(jobList)) {
				return ;
			}
			String[] temp1 = node.split(Constants.PATH_SEPARATOR);
			String groupName = temp1[3];
			for (String jobName : jobList) {
				buildJob(client, node, znode, groupName, jobName);
			}
         }
      }

    /**
     * 如果监听到disJob/rpc/group/servevr节点的新增则创建/disJob/job/group/job/config节点
     * @param client
     * @param node
     * @param znode
     * @param temp
     */
	private void buildJob(CuratorFramework client, String node,ZnodeApiCuratorImpl znode, String groupName,String jobName) {
		if(!StringUtils.isNoneEmpty(jobName)){
			return;
		}
		String tempPath= znode.makePath(node, Constants.DISJOB_CONFIG);
		if (znode.checkExists(client, tempPath)){
			return ;
		}
		Job job = new Job();
		job.setJobName(jobName);
		job.setGroupName(groupName);
		job.setJobStatus(0);//设为未激活
		String json = new Gson().toJson(job);
		LoggerUtil.debug("begin build " + tempPath + " value is " + json);
		try {
			String groupNodePath = znode.makePath(Constants.ROOT,Constants.APP_JOB_NODE_ROOT, groupName);
			znode.createPersistent(client, tempPath, json.getBytes(), client.getACL().forPath(groupNodePath));
		} catch (Exception e) {
			LoggerUtil.error("create node " + tempPath + " , value " + json+ " error", e);
		}
	}
	
 }
