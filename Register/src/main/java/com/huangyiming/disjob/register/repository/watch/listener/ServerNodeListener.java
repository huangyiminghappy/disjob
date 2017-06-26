package com.huangyiming.disjob.register.repository.watch.listener;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.quartz.CronExpression;

import com.huangyiming.disjob.common.Constants;
import com.huangyiming.disjob.common.util.LocalHost;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.register.core.util.RegisterSpringWorkFactory;
import com.huangyiming.disjob.register.domain.Job;
import com.huangyiming.disjob.register.repository.ZnodeApi;
import com.huangyiming.disjob.register.repository.ZnodeApiCuratorImpl;
import com.huangyiming.disjob.register.rpc.NotifyListener;
import com.huangyiming.disjob.register.rpc.ZkNodeType;
import com.huangyiming.disjob.register.utils.RegisterUtils;
import com.huangyiming.disjob.register.utils.ZooKeeperRegistryUtils;
import com.huangyiming.disjob.rpc.client.HURL;
import com.huangyiming.disjob.rpc.client.proxy.RpcClientCache;
import com.huangyiming.disjob.rpc.client.proxy.ServerLinkedService;
import com.huangyiming.disjob.slaver.utils.SlaveUtils;
import com.google.gson.Gson;

/**
 * <pre>
 * 
 *  File: Test.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  路径:/disJob/rpc/group/servicename/provide
 *  功能:服务节点的子节点变化的监听,监听该节点变化,维护group,service构造的HURL对象对应的List<HURL>列表
 * 
 *  Revision History
 *  Date,					Who,					What;
 *  2016年5月26日				Disjob				Initial.
 *
 * </pre>
 */
public class ServerNodeListener implements PathChildrenCacheListener{
	private HURL url;
	private NotifyListener notifyListener;
	private ZnodeApi znode = RegisterSpringWorkFactory.getZnodeApi();

	public ServerNodeListener(HURL url, NotifyListener notifyListener) {
		this.url = url;
		this.notifyListener = notifyListener;
	}

    @Override
    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
       if(event.getType() == org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.CHILD_ADDED){
           String serverUrl = RegisterUtils.toNodeTypePath(url, ZkNodeType.PROVIDER);
           List<String> currentChilds = znode.getChildren(client, serverUrl);
           String path = event.getData().getPath();//->path:/disJob/rpc/group/jobname/providers/ip:port
           LoggerUtil.info("add child :"+path);
		   if (StringUtils.isNotEmpty(path)) {
				String hostPort = path.substring(serverUrl.length() + 1);
				if (event.getType() == org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.CHILD_ADDED) {
					if (!currentChilds.contains(hostPort)) {
						currentChilds.add(hostPort);// 如果直接添加某组某服务某ip的服务地址,确保job能监控到并添加到job中去
					}
					String[] uri = path.split(Constants.PATH_SEPARATOR);
					if (uri != null&& uri.length >= 5&& uri[5].equalsIgnoreCase(Constants.DISJOB_PROVIDERS)) {
						String jobPath = path.substring(0,path.indexOf(Constants.DISJOB_PROVIDERS) - 1).replace("rpc", "job");//-> disJob/job/group/jobname/
						if (!znode.checkExists(client, jobPath)) {
							znode.makeDirs(client, jobPath);
						}
					}
				}
				ServerLinkedService.clientProvidersMap.put(hostPort, hostPort);
		   }
		   ZooKeeperRegistryUtils.notify(url, notifyListener, RegisterUtils.nodeChildsToUrls(client, serverUrl, currentChilds));
		   String masterIp = SlaveUtils.getLeaderLatch().getLeader().getId();
		   if (StringUtils.isNotEmpty(masterIp)&& masterIp.equals(new LocalHost().getIp())) { 
			   checkTriggerJob(client, path);
		   }
       }  
       if(event.getType() == org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.CHILD_REMOVED){
			String serverUrl = RegisterUtils.toNodeTypePath(url,ZkNodeType.PROVIDER);
			List<String> currentChilds = znode.getChildren(client, serverUrl);
			
			String path = event.getData().getPath();
			if (StringUtils.isNotEmpty(path)) {
				String hostPort = path.substring(serverUrl.length() + 1);
				//if (event.getType() == org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.CHILD_REMOVED) {
					if (currentChilds.contains(hostPort)) {
						currentChilds.remove(hostPort);
					}
				//}
				ServerLinkedService.clientProvidersMap.remove(hostPort);
				//RpcClientCache.removeRpcClient(hostPort);
				if(!CollectionUtils.isEmpty(currentChilds)){
					ServerLinkedService.mergerFailRpcRequest(hostPort, currentChilds.get(0));
				}else{
					//所有的 provider 都已经宕机了，则将发送失败的消息 clear 掉
					ServerLinkedService.sendFailRpcRequestMap.remove(hostPort);
				}
			}
			//只有大于1的情况下才通知,如果一个provider节点的rpc地址只剩一个了则不同步更新缓存
			if(currentChilds.size() >1){
 				ZooKeeperRegistryUtils.notify(url, notifyListener,RegisterUtils.nodeChildsToUrls(client, serverUrl, currentChilds));
			}
       }
     }
    
   /**
    * 检测是否需要立即触发job.
    * @param client
    * @param path /disJob/rpc/group/jobname/providers/ip:port
    */
	private void checkTriggerJob(final CuratorFramework client, String path) {
		Boolean isCheck = RegisterUtils.CHECK_TRIGGER_MAP.putIfAbsent(getKey(path), true);
		if(isCheck!=null&&isCheck == true){//同一个job 两台不同的机器只需要处理一次
			return ;
		}
		final ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
		String jobJson = znode.getData(client, path);
		HURL hurl = HURL.valueOf(jobJson);
		String cron = hurl.getCron();
		if (StringUtils.isEmpty(cron)) {
			return;
		}

		if (!CronExpression.isValidExpression(cron)) {
			return;
		}

		//先读一次zk 上面的这个job config 信息。如果有就参考之前的配置信息不覆盖
		final String serverGroup = hurl.getServerGroup();
		final String serverName = hurl.getServerName();
		String jobConfig = TryOperate.read(10L, new WaitCondition<String>() {
			@Override
			public String exe() {
				return znode.getJobConfig(client, serverGroup, serverName);
			}
		}); 
		Job job = null ;
		if(StringUtils.isEmpty(jobConfig)){
			LoggerUtil.error("read data from config node is null. path is " + path);
			return;
		}else{
			job = new Gson().fromJson(jobConfig, Job.class);
			if(StringUtils.isEmpty(job.getCronExpression())){
				job.setCronExpression(cron);//使用最后一次修改的cron 表达式
			}
			job.setFireNow(hurl.isFireNow());
		}
		RegisterSpringWorkFactory.getJobOperationService().updateJob(client,job);
	}
	
	public static class TryOperate{
		private static final Long interval = 100L; 
		
		/**
		 * @param l 超时时间 (milliseconds)
		 * @param waitCondition
		 * @return
		 */
		public static <T> T read(long l, WaitCondition<T> waitCondition) {
			T t ;
			Long start = System.currentTimeMillis();
			while((t = waitCondition.exe()) == null){
				if(System.currentTimeMillis() - start > l * 1000L){
					LoggerUtil.warn("TryOperate.read get an outoftime");
					return null;
				}
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					LoggerUtil.error("TryOperate.read get an exception: " + e);
				}
			}
			return t;
		}
		
		public static <T> T read(int i, WaitCondition<T> waitCondition) {
			T t ;
			while((t = waitCondition.exe()) == null || i != 0){
				i --;
			}
			return t;
		}
	}
	public static interface WaitCondition<T>{
		public T exe();
	}
	
	private String getKey(String path){
		
		return path.substring(0,path.lastIndexOf("/"));
	}
}
