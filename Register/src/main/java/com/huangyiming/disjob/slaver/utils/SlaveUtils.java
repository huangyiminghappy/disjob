package com.huangyiming.disjob.slaver.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.transaction.CuratorTransactionFinal;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.utils.ZKPaths;

import com.google.gson.Gson;
import com.huangyiming.disjob.common.Constants;
import com.huangyiming.disjob.common.exception.ZKNodeException;
import com.huangyiming.disjob.common.util.LocalHost;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.register.cache.ZKJobCache;
import com.huangyiming.disjob.register.domain.DisJobServerInfo;
import com.huangyiming.disjob.register.domain.Job;
import com.huangyiming.disjob.register.domain.SlaveNode;
import com.huangyiming.disjob.register.job.JobOperationServiceImpl;
import com.huangyiming.disjob.register.job.WeightedRoundRobinScheduling;
import com.huangyiming.disjob.register.repository.ZnodeApiCuratorImpl;
import com.huangyiming.disjob.register.rpc.ConcurrentHashSet;

/**
 * <pre>
 * 
 *  File: SalveUtils.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  集群工具类
 * 
 *  Revision History
 *  Date,					Who,					What;
 *  2016年6月14日				Disjob				Initial.
 *
 * </pre>
 */
public class SlaveUtils
{
   public static LeaderLatch leaderLatch = null;
   static ZnodeApiCuratorImpl nodeApi = new ZnodeApiCuratorImpl();

    /**
     * 获取可用的ip列表
     * @param client
     * @return
     */
    public static  ConcurrentHashSet<DisJobServerInfo> getAvailableAlaveIps(CuratorFramework client){
        String slaveNode = Constants.ROOT+Constants.DISJOB_SERVER_NODE_ROOT+Constants.DISJOB_SERVER_NODE_SLAVE;
        ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
        List<String> currentChilds = znode.getChildren(client, slaveNode);
        ConcurrentHashSet<DisJobServerInfo> result = new ConcurrentHashSet<DisJobServerInfo>();
        if(CollectionUtils.isNotEmpty(currentChilds)){
            for(String slaveIp : currentChilds){
                String dataNode = slaveNode + Constants.PATH_SEPARATOR + slaveIp+ Constants.DISJOB_SERVER_NODE_SLAVE_STATUS;
                if(znode.checkExists(client, dataNode)){
                    //暂时不对status的值做验证,因为目前服务不可用自动status节点就移除啦
                    //TODO这里可以做IP权重设置,读取配置文件设置ip对应权重
                    DisJobServerInfo slaveServer = new DisJobServerInfo(slaveIp, Constants.DISJOB_SLAVE_DEFAULT_WEIGHT);
                    result.add(slaveServer);
                }
            }
        }
        return result;
    }
    
    /**
     * 根据job对象更改disJob/job的对应节点值
     * @param job
     * @param client
     * @param nodeApi
     */
    public static void updateJobNode(Job job,CuratorFramework client){
        String json = new Gson().toJson(job);
        String groupName = job.getGroupName();
        String jobName = job.getJobName();
        String jobNode = nodeApi.makePath(Constants.ROOT, Constants.APP_JOB_NODE_ROOT, Constants.PATH_SEPARATOR+groupName,Constants.PATH_SEPARATOR+jobName,Constants.APP_JOB_NODE_CONFIG);
        nodeApi.update(client, jobNode, json); 
    }
    
    /**
     * 根据job对象更改disJob/job的对应节点值
     * @param job
     * @param client
     * @param nodeApi
     * @throws Exception 
     */
    public static CuratorTransactionFinal updateJobNodeByTranstions(Job job,CuratorFramework client,CuratorTransaction transaction) throws Exception{
        ZnodeApiCuratorImpl nodeApi = new ZnodeApiCuratorImpl();
        String json = new Gson().toJson(job);
        String groupName = job.getGroupName();
        String jobName = job.getJobName();
        String jobNode = nodeApi.makePath(Constants.ROOT, Constants.APP_JOB_NODE_ROOT, Constants.PATH_SEPARATOR+groupName,Constants.PATH_SEPARATOR+jobName,Constants.APP_JOB_NODE_CONFIG);
        nodeApi.update(client, jobNode, json);
//       return nodeApi.addUpdateToTransaction(transaction, jobNode, json);
        return null;
    }
    
    /**
     * 根据IP获得该slave节点下对应的组和job信息
     * @param client
     * @param ip
     * @return
     */
    public static ConcurrentHashMap<String,String>  getGroupAndJobMapByIp(CuratorFramework client,String ip){
        ZnodeApiCuratorImpl nodeApi = new ZnodeApiCuratorImpl(); 
        String ipNode = nodeApi.makePath(Constants.ROOT, Constants.DISJOB_SERVER_NODE_ROOT, Constants.DISJOB_SERVER_NODE_SLAVE,Constants.PATH_SEPARATOR+ip,Constants.DISJOB_SERVER_NODE_SLAVE_EXECUTION);
        List<String> groupList = null;
        try{
            groupList = nodeApi.getChildren(client, ipNode);
        }catch(ZKNodeException e){
            LoggerUtil.warn("getGroupAndJobMapByIp can not get get "+ipNode + " children ",e);
        }
        ConcurrentHashMap<String,String> result = new ConcurrentHashMap<String, String>();
        if(CollectionUtils.isNotEmpty(groupList)){
            for(String group : groupList){
                String jobData = nodeApi.getData(client, ipNode+Constants.PATH_SEPARATOR+group);
                if(StringUtils.isNoneEmpty(jobData)){
                    result.put(group,jobData);
                    LoggerUtil.info("ip node :"+ ipNode + " ,group : "+group + ",jobData: "+jobData );
                }
            }
        }else{
            LoggerUtil.info("ip "+ip +" is down,but no job in it,so no distributing");
        }
        return result;
    }
    
    /**
     * 每次分配前才会刷新最新IP缓存,因为IP是有权重信息的,每次分配后相关的权重信息都会改变,所以获取可用ip的时候不能覆盖已有IP,只能增加
     * @param client
     */
    public static ConcurrentHashSet<DisJobServerInfo> refreshSlaveIp(CuratorFramework client){
        //刷新IP
        ConcurrentHashSet<DisJobServerInfo> result = new ConcurrentHashSet<DisJobServerInfo>();
        ConcurrentHashSet<DisJobServerInfo> list = SlaveUtils.getAvailableAlaveIps(client);
        if(CollectionUtils.isNotEmpty(list)){
            for(DisJobServerInfo info : list){
                if(!ZKJobCache.ipList.contains(info)){
                    result.add(info);
                }
            }
            
            ZKJobCache.ipList.addAll(result);
            for(DisJobServerInfo filter:ZKJobCache.ipList){
                if(!list.contains(filter)){
                    ZKJobCache.ipList.remove(filter);
                }
            }
        }
        return ZKJobCache.ipList;
    }
    
    /**
     * Slave节点挂掉后要把该节点上面的group和job信息清空掉
     * @param ip
     * @param client
     */
    public static void clearSlaveJob(String ip,CuratorFramework client){
        LoggerUtil.info("ip: "+ip + " has been down, remove all jobs in "+ ip);
        ZnodeApiCuratorImpl nodeApi =  new ZnodeApiCuratorImpl();
        String executionNode = nodeApi.makePath(Constants.ROOT, Constants.DISJOB_SERVER_NODE_ROOT,Constants.DISJOB_SERVER_NODE_SLAVE,Constants.PATH_SEPARATOR+ip,Constants.DISJOB_SERVER_NODE_SLAVE_EXECUTION);
        List<String> groupList = nodeApi.getChildren(client, executionNode);
        if(CollectionUtils.isNotEmpty(groupList)){
            for(String group : groupList){
                String groupNode = executionNode + Constants.PATH_SEPARATOR + group;
                nodeApi.deleteByZnode(client, groupNode);
            }
        }
      }
    
    /**
     * Slave节点挂掉后要把该节点上面的group和job信息清空掉
     * @param ip
     * @param client
     * @throws Exception 
     */
    public static void clearSlaveJobByTransaction(String ip,CuratorFramework client,CuratorTransaction transaction) throws Exception{
        LoggerUtil.info("ip: "+ip + " has been down, remove all jobs in "+ ip);
        LoggerUtil.trace("ip: "+ip + " has been down, remove all jobs in "+ ip);
        ZnodeApiCuratorImpl nodeApi =  new ZnodeApiCuratorImpl();
        String executionNode = nodeApi.makePath(Constants.ROOT, Constants.DISJOB_SERVER_NODE_ROOT,Constants.DISJOB_SERVER_NODE_SLAVE,Constants.PATH_SEPARATOR+ip,Constants.DISJOB_SERVER_NODE_SLAVE_EXECUTION);
        List<String> groupList = nodeApi.getChildren(client, executionNode);
        if(CollectionUtils.isNotEmpty(groupList)){
            for(String group : groupList){
                String groupNode = executionNode + Constants.PATH_SEPARATOR + group;
                LoggerUtil.info("delete group node :"+groupNode);
                LoggerUtil.trace("delete group node :"+groupNode);
                nodeApi.deleteByZnode(client, groupNode);
                //nodeApi.addDeleteToTransaction(transaction, groupNode);
            }
        }
      }
    
    
    /**
	 * 1.得到放哪个可用的ip下
	 * @param client
	 * @param group
	 * @param jobName
	 * @throws Exception 
	 */
	public static String distributeSlave(CuratorFramework client, String group,String jobName,CuratorTransaction transaction) throws Exception {
        DisJobServerInfo slaver =  WeightedRoundRobinScheduling.GetBestSlaveServer(client);
        if(slaver == null){
            LoggerUtil.warn("WeightedRoundRobinScheduling.GetBestSlaveServer get the DisJobServerInfo is null, so return itself, but this phenomenon is not common. because at least it should return itself ip info ");
            //重试一次
            SlaveUtils.refreshSlaveIp(client);
            DisJobServerInfo trySlaver =  WeightedRoundRobinScheduling.GetBestSlaveServer(client);
            if(trySlaver != null){
            	return trySlaver.getIp();
            }else{
            	return new LocalHost().getIp();            	
            }
        }
        return slaver.getIp();
    }
	
	/**
	 * 1.如果某ip无对应group则创建/group/jobname
	 * 2.如果某ip下有group且叠加jobname,更新该节点的值
	 * 3.最后返回节点被分配给的新的slaveIP
	 * 
	 * 添加同步, 因为在rpc上同时创建没有任务组不存在的任务时,会发生都创建任务组节点的情况
	 * @param client
	 * @param group
	 * @param jobName
	 * @param transaction
	 * @param ip
	 */
	public static synchronized void updateSlaveExecution(CuratorFramework client,String group, String jobName, CuratorTransaction transaction,String ip) {
		ZnodeApiCuratorImpl nodeApi = new ZnodeApiCuratorImpl();
		String groupNode = nodeApi.makePath(Constants.ROOT,Constants.DISJOB_SERVER_NODE_ROOT,Constants.DISJOB_SERVER_NODE_SLAVE,Constants.PATH_SEPARATOR + ip,Constants.DISJOB_SERVER_NODE_SLAVE_EXECUTION,Constants.PATH_SEPARATOR + group);
		String jobs = nodeApi.getData(client, groupNode);
		if (StringUtils.isEmpty(jobs)) {
			nodeApi.createPersistent(client, groupNode, jobName);
			LoggerUtil.info("job distribute node not exists,so create slave node,  job to be distribute to "+ ip+ ",groupNode is "+ groupNode+ ",jobname is " + jobName);
			LoggerUtil.trace("job distribute node not exists,so create slave node,  job to be distribute to "+ ip+ ",groupNode is "+ groupNode+ ",jobname is " + jobName);
		} else {
			// 正常情况下是不会存在jobs里包含jobname的情况,如果存在则不处理
			String[] array = jobs.split(Constants.TRANSFER_CHAR+ Constants.JOB_SEPARATOR);
			if (array != null && array.length > 0) {
				List<String> list = Arrays.asList(array);
				if ((!list.contains(jobName))&& (StringUtils.isNoneBlank(jobName))) {
					StringBuilder sb = new StringBuilder(jobs);
					sb.append(Constants.JOB_SEPARATOR + jobName);
					jobs = sb.toString();
				}
			}
			LoggerUtil.info("job to be distribute to " + ip + ",groupNode is "+ groupNode + ",current jobname is " + jobName+ ",after add this job  is " + jobs);
			LoggerUtil.trace("job to be distribute to " + ip + ",groupNode is "+ groupNode + ",current jobname is " + jobName+ ",after add this job  is " + jobs);
			nodeApi.update(client, groupNode, jobs);
		}
	}
	
	  /**
		 * 1.该组对应job放到指定IP上
		 * 2.如果某ip无对应group则创建/group/jobname
		 * 3.如果某ip下有group且叠加jobname,更新该节点的值
		 * 4.最后返回节点被分配给的新的slaveIP
		 * 
		 * @param client
		 * @param group
		 * @param jobName
		 * @throws Exception 
		 */
		public static synchronized String distributeSlaveToIp(CuratorFramework client, final String group,final String jobName,final String ip) throws Exception {
	        ZnodeApiCuratorImpl nodeApi = new ZnodeApiCuratorImpl();
 	        String groupNode = nodeApi.makePath(Constants.ROOT, Constants.DISJOB_SERVER_NODE_ROOT, Constants.DISJOB_SERVER_NODE_SLAVE,Constants.PATH_SEPARATOR+ip,Constants.DISJOB_SERVER_NODE_SLAVE_EXECUTION,Constants.PATH_SEPARATOR+group);
	        String jobs = nodeApi.getData(client, groupNode);
	        //如果缓存中没有则创建节点,要核对节点为空串和节点不存在的情况的值
	        if(StringUtils.isEmpty(jobs) ){
	        	//节点不存在则直接创建,这里需要采用事务方式处理
 	             nodeApi.createPersistent(client, groupNode, jobName);
	             LoggerUtil.info("job distribute node not exists,so create slave node,  job to be distribute to "+ip+ ",groupNode is "+groupNode+",jobname is "+jobName);
	         }else{
	              if(StringUtils.isNoneBlank(jobs)){
	                //正常情况下是不会存在jobs里包含jobname的情况,如果存在则不处理
	            	 String [] jobArray = jobs.split(Constants.TRANSFER_CHAR+Constants.JOB_SEPARATOR);
	            	 List<String> jobList = Arrays.asList(jobArray);
	                 if((!jobList.contains(jobName)) && StringUtils.isNoneBlank(jobName)){
	                    StringBuilder sb = new StringBuilder(jobs);
	                    sb.append(Constants.JOB_SEPARATOR+jobName);
	                    jobs = sb.toString();
	                 }
	             } 
	             LoggerUtil.info("job to be distribute to "+ip+ ",groupNode is "+groupNode+ ",current jobname is "+jobName + ",after add this job  is "+jobs);
 	             //更新job节点的值,不能使用事务,必须立刻提交
	             nodeApi.update(client, groupNode, jobs);
 	        }
   	        return ip;
	    }
		
		
    /**
     * 
     * 对不可用IP上的任务进行分配
     * 1.得到挂掉的机器上的group和对应的job信息
     * 2.循环对每个group上的job重新分配ip,在新slave上创建group/job或者叠加group/job信息
     * 3.对如果在不可用ip上是正在运行状态的job改为可运行状态
     * 
     * @param ip
     * @throws Exception 
     */
    public static void distributeSlave(String oldIp,CuratorFramework client ,CuratorTransaction transaction) throws Exception{
        ZnodeApiCuratorImpl nodeApi = new ZnodeApiCuratorImpl();
         ConcurrentHashMap<String,String> jobMap =  SlaveUtils.getGroupAndJobMapByIp(client, oldIp);
         if(jobMap !=null && jobMap.size() >0){
            Iterator<String> item = jobMap.keySet().iterator();
            //更新可用ip
            SlaveUtils.refreshSlaveIp(client);
            
            while(item.hasNext()){
                String group = item.next();
                 if(StringUtils.isNoneBlank(jobMap.get(group))){
                    String [] jobArray = jobMap.get(group).split(Constants.TRANSFER_CHAR+Constants.JOB_SEPARATOR);
               	    if(jobArray !=null && jobArray.length>0){
                     //每一个job和groupname都对应某一个IP
                     for(String jobName : jobArray){
                    	 if(StringUtils.isNoneBlank(jobName)){
                    		 LoggerUtil.info(oldIp+ " is down ,to distribute groupname is "+group + " , jobname is "+jobName );
                     		 LoggerUtil.trace(oldIp+ " is down ,to distribute groupname is "+group + " , jobname is "+jobName );
                    		 //分配job到slave上,返回新机器的ip
                    		 String slaveIp =  distributeSlave(client, group, jobName,transaction);
                    		 //更改job状态信息
                    		 updateJobNodeInfo(oldIp, client, transaction, nodeApi,group, jobName, slaveIp);
                    		 updateSlaveExecution(client, group, jobName, transaction, slaveIp);
                     	 }
                     }                    
                 } 
              }
            }           
         }
       }

    
    /**
     * job被重新分配其他机器上后更改job状态信息
     * 1.job状态如果是正在运行改为可运行
     * 2.job的slaveIp改为分配后的ip
     * @param oldIp  原id
     * @param client
     * @param transaction
     * @param nodeApi
     * @param group job组名
     * @param jobName  job任务名
     * @param slaveIp job新分配的Ip
     * @throws Exception
     */
	private static void updateJobNodeInfo(String oldIp,
			CuratorFramework client, CuratorTransaction transaction,
			ZnodeApiCuratorImpl nodeApi, String group, String jobName,
			String slaveIp) throws Exception {
		String jobPath = nodeApi.makePath(Constants.ROOT, Constants.APP_JOB_NODE_ROOT,Constants.PATH_SEPARATOR+group,Constants.PATH_SEPARATOR+jobName, Constants.APP_JOB_NODE_CONFIG);
		String data =  nodeApi.getData(client, jobPath);
		Job job = new Job();
		if(StringUtils.isNotEmpty(data)){
		   job =  new Gson().fromJson(data, Job.class);
		}
		job.setSlaveIp(slaveIp);
		if(job.getJobStatus() == 2){//如果正在运行的任务被分配后状态改为可运行
		   LoggerUtil.info(" job in " +oldIp + ", group name is "+ group + " ,jobname is "+jobName +" is running,so set status to canrunning" );
		   job.setJobStatus(1);
		}
		SlaveUtils.updateJobNode(job, client);
	}
    
    
     /**
      * 得到集群中所有已经down机的机器但是有节点没有distribute的ip
      * @param client
      * @return
      */
    public static List<String> getAllOfflineIpsToDistribute(CuratorFramework client){
     	 String path = nodeApi.makePath(Constants.ROOT,Constants.DISJOB_SERVER_NODE_ROOT,Constants.DISJOB_SERVER_NODE_SLAVE);
    	 List<String> ips = nodeApi.getChildren(client, path);
    	 List<String> needDistributeIps = new ArrayList<String>();
    	 if(CollectionUtils.isNotEmpty(ips)){
    		 for(String ip : ips){
    			String statusNode = nodeApi.makePath(path,  Constants.PATH_SEPARATOR+ip, Constants.DISJOB_SERVER_NODE_SLAVE_STATUS);
     			 //如果该节点不存在,则查看
    			 if(!nodeApi.checkExists(client, statusNode)){
    				 String executionNode = nodeApi.makePath(path,  Constants.PATH_SEPARATOR+ip, Constants.DISJOB_SERVER_NODE_SLAVE_EXECUTION);
    				 List<String> groupList = new ArrayList<String>();
    				 try{
    					 groupList = nodeApi.getChildren(client, executionNode);
    				 }catch(ZKNodeException e){
    					 LoggerUtil.error(" get  "+executionNode + " children error",e);
    				 }
    				 if(CollectionUtils.isNotEmpty(groupList)){
    					 needDistributeIps.add(ip);
    				 }
      			 }
      		 }
    	 }
    	 return needDistributeIps;
    }
    
    /**
     * 清除集群中所有job
     * @param client
     * @param transaction
     * @throws Exception
     */
    public static void clearAllClustersJobs(CuratorFramework client,CuratorTransaction transaction) throws Exception{
     	String slaveNode = Constants.ROOT+Constants.DISJOB_SERVER_NODE_ROOT+ Constants.DISJOB_SERVER_NODE_SLAVE;//"/disJob/scheduler/slave";
    	List<String> slaveList =   nodeApi.getChildren(client, slaveNode);
 		if(CollectionUtils.isNotEmpty(slaveList)){
 			for(String slaveIp : slaveList){
 				String ipNode = slaveNode+ "/" + slaveIp;
 				String executionNode = ipNode + "/" + "execution";
 				List<String> groupList = nodeApi.getChildren(client, executionNode);
 				if(CollectionUtils.isNotEmpty(groupList)){
 					for(String group:groupList){
 						String groupNode = executionNode + "/" + group;
 						nodeApi.deleteByZnode(client, groupNode);
 						//nodeApi.addDeleteToTransaction(transaction, groupNode);
  					}
 				}
 			}
 			 
 			
 		}	
    }
    
    public static void  distributeAllJobs(CuratorFramework client) throws Exception{
   		Shard<SlaveNode> shard = com.huangyiming.disjob.slaver.utils.Shard.initSlaveIpInShard(client);
   		ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
  		String jobNode = ZKPaths.makePath(Constants.ROOT, Constants.APP_JOB_NODE_ROOT);
 		List<String> groupList = znode.getChildren(client, jobNode);
		if(CollectionUtils.isNotEmpty(groupList) && groupList.size()>0){
			for(String groupName :groupList ){
				String groupNode = jobNode + Constants.PATH_SEPARATOR + groupName;
				 List<String> jobAll = znode.getChildren(client, groupNode) ;
				 if(CollectionUtils.isNotEmpty(jobAll)){
					for(String jobName:jobAll){
						String job1Node = groupNode+Constants.PATH_SEPARATOR+jobName;
						String configNode = job1Node+ Constants.APP_JOB_NODE_CONFIG;
						String json = znode.getData(client, configNode);
				        if(StringUtils.isEmpty(json)){
				            continue;
				        }
				        Job zkJob = new Job();
				        zkJob =  new Gson().fromJson(json, Job.class);
				       
				        if(zkJob.getJobStatus() == 0){ //没激活的不需要分配
				        	continue;
				        }
				        String groupJob = groupName + jobName;
 				        String slaveIp = shard.getIpByKey(groupJob);
 				        
 				        try{
				            SlaveUtils.distributeSlaveToIp(client, groupName, jobName, slaveIp);
 				        }catch(Exception e){
 				        	LoggerUtil.error("dis error "+groupName +","+jobName);
  				        }
 				        zkJob.setSlaveIp(slaveIp);
 				        SlaveUtils.updateJobNode(zkJob, client);
					}
				}
			}
		}
    }
    
    /**
     * 根据组名和job名设置zk节点config上lastfiretime为当前时间
     * @param client
     * @param group
     * @param jobName
     */
    public static void  setLastFireTimeByGroupNameAndJobName(CuratorFramework client,String group,String jobName){
    	ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
        String jobPath = znode.makePath(Constants.ROOT, Constants.APP_JOB_NODE_ROOT,Constants.PATH_SEPARATOR+group,Constants.PATH_SEPARATOR+jobName, Constants.APP_JOB_NODE_CONFIG);
        String data =  znode.getData(client, jobPath);
        Job zkJob = new Job();
        if(StringUtils.isNotEmpty(data)){
           zkJob =  new Gson().fromJson(data, Job.class);
        } 
    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        zkJob.setLastFireTime(sdf.format(new Date()));
        updateJobNode(zkJob, client);
    }
    
    /**
     * 全量更新session与ip的绑定的map  map<project,List<ip:port>>
     * @param client
     * @param znode
     * @param sessionHostMap
     */
    public static ConcurrentHashMap<String, List<String>> buildSessionHostMap(CuratorFramework client) {
    	ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
    	ConcurrentHashMap<String, List<String>> sessionHostMap = com.huangyiming.disjob.register.cache.ZKJobCache.sessionHostMap; 
 		String sessionNode = com.huangyiming.disjob.common.Constants.DISJOB_SERVER_NODE_SESSION;
		//得到绑定会话源与iphost关系
		List<String> sessionSource = znode.getChildren(client, sessionNode);
		
		//得到绑定会话源与iphost关系
		if(CollectionUtils.isNotEmpty(sessionSource)){
			for(String sourceNode:sessionSource ){
				 List<String> list = znode.getChildren(client, sessionNode+Constants.PATH_SEPARATOR+sourceNode);
					if(list == null){
						sessionHostMap.put(sourceNode, new ArrayList<String>());
					}else{
						sessionHostMap.put(sourceNode, list);
					}
			}
		}
		return sessionHostMap;
 	}
    
    /**
     * 全量更新session与group的绑定的map  map<project,List<session>>
     * @param client
     * @param znode
     * @param sessionHostMap
     */
    public static ConcurrentHashMap<String, List<String>> buildProjectSessionMap(CuratorFramework client) {
      	String projectNode = com.huangyiming.disjob.common.Constants.DISJOB_SERVER_NODE_PROJECT;
    	List<String> sessions = nodeApi.getChildren(client, projectNode);
    	ConcurrentHashMap<String, List<String>> sessionProjectMap = new ConcurrentHashMap<String, List<String>>(); 
 		//得到绑定会话源与iphost关系
		if(CollectionUtils.isNotEmpty(sessions)){
			for(String sessionName:sessions ){
	   			List<String> projects = nodeApi.getChildren(client, projectNode+Constants.PATH_SEPARATOR+sessionName);
	   			for(String project : projects){
	   				List<String> sessionList = sessionProjectMap.get(project);
	   				if(sessionList == null){
	   				    sessionList = new ArrayList<String>();
	   					sessionProjectMap.put(project, sessionList);
	   				}
	   				if(!sessionList.contains(sessionName)){
 	   					sessionList.add(sessionName);
	   				}
	   			    sessionProjectMap.put(project, sessionList);
	   			}
 			}
		}
		return sessionProjectMap;
    }
    
    public static String buildRpcUrlByJob(Job job){
     	return buildRpcUrl(job);
    }
    
    /**
     * 根据job的参数构造出rpcurl
     * @param job
     * @return
     */
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
     * 根据group和ipport得到要删除的zookeeper node节点path列表
     * @param client
     * @param group
     * @param ipPort
     * @return
     */
    public static List<String> getProviderUrlNodeByGroupAndHost(CuratorFramework client,String group,String ipPort){
		ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
		String node = znode.makePath(Constants.ROOT, Constants.DISJOB_RPC_NODE_ROOT, Constants.PATH_SEPARATOR+group);	  
		List<String> jobNameList = znode.getChildren(client, node);
		List<String> result = new ArrayList<String>();
		if(CollectionUtils.isNotEmpty(jobNameList)){
			for(String jobName : jobNameList){
				
				String deletePath = znode.makePath(node, Constants.PATH_SEPARATOR+jobName, Constants.PATH_SEPARATOR+Constants.DISJOB_PROVIDERS,Constants.PATH_SEPARATOR+ipPort);
				result.add(deletePath);
			}
		}
		return result;
 	}
    
    /**
     * 根据group和ipport得到要删除的zookeeper node节点path列表
     * @param client
     * @param group
     * @param ipPort
     * @return
     */
    public static void addProviderUrlNodeByGroupAndHost(CuratorFramework client,String group,String ipPort){
		ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
		String node = znode.makePath(Constants.ROOT, Constants.DISJOB_RPC_NODE_ROOT, Constants.PATH_SEPARATOR+group);	  
		List<String> jobNameList = znode.getChildren(client, node);
 		if(CollectionUtils.isNotEmpty(jobNameList)){
			for(String jobName : jobNameList){
				SlaveUtils.buildRpcUrlByParams(group, client, ipPort, jobName);
			}
		}
  	}
    
    /**
     * 根据参数构造rpcurl.兼容一期的。一期注册的job,如果在某个会话绑定一个job group.这原先这个 group 下面的job name 的相关配置信息
     * 有我们现在手动给他们创建。并和一期不同的事创建的节点时持久节点
     * @param groupName
     * @param client
     * @param ipHost
     * @param jobName
     */
	public static  void buildRpcUrlByParams(String groupName, CuratorFramework client, String ipHost, String jobName) {
		ZnodeApiCuratorImpl znodeApi = new ZnodeApiCuratorImpl();

		String jobPath = znodeApi.makePath(Constants.ROOT, Constants.APP_JOB_NODE_ROOT,Constants.PATH_SEPARATOR+groupName,Constants.PATH_SEPARATOR+jobName, Constants.APP_JOB_NODE_CONFIG);
		Job job = znodeApi.getData(client, jobPath, Job.class);
		String filePath = job.getFilePath();
		String className = job.getClassName();
		String methodName = job.getMethodName();
		String cronExpression = job.getCronExpression();
		String rpcNodePath = String.format(JobOperationServiceImpl.pathFormat, groupName, jobName, ipHost);
		String newRpcUrl;
		if(StringUtils.isEmpty(cronExpression)){
			newRpcUrl = String.format(JobOperationServiceImpl.dataFormatCommon, ipHost, jobName, groupName, filePath, className, methodName);
		}else{
			newRpcUrl = String.format(JobOperationServiceImpl.dataFormat, ipHost, jobName, groupName, filePath, className, methodName, cronExpression);
		}
		if(!znodeApi.checkExists(client, rpcNodePath)){
			znodeApi.createPersistent(client, rpcNodePath, newRpcUrl);
		}
	}
    /**
     * 根据group得到jobname列表
     * @param client
     * @param group
     * @param ipPort
     * @return
     */
    public static List<String> getJobNameListByGroup(CuratorFramework client,String group){
		ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
		String node = znode.makePath(Constants.ROOT, Constants.DISJOB_RPC_NODE_ROOT, Constants.PATH_SEPARATOR+group);
		List<String> jobNameList = new ArrayList<String>();
		if(znode.checkExists(client, node)){
			jobNameList = znode.getChildren(client, node);
		}
		  
 		return jobNameList;
 	}
     
    /**
     * 删除zk上路径列表
     * @param client
     * @param nodePathList
     */
    public static void deleteNodeList(CuratorFramework client,List<String> nodePathList){
		ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
		if(CollectionUtils.isNotEmpty(nodePathList)){
			for(String deleteNode : nodePathList){
				try{
					if(znode.checkExists(client, deleteNode)){
						znode.deleteByZnode(client, deleteNode);						
					}
				}catch(Exception e){
					LoggerUtil.error("delete zk node :"+deleteNode +" error",e);
				}
		    }
		}
    	
    }
    
    /**
     * 添加zk上路径列表
     * @param client
     * @param nodePathList
     *//*
    public static void addNodeList(CuratorFramework client,List<String> nodePathList){
		ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
		if(CollectionUtils.isNotEmpty(nodePathList)){
			for(String deleteNode : nodePathList){
				try{
					if(znode.checkExists(client, deleteNode)){
						//znode.deleteByZnode(client, deleteNode);						
					}
				}catch(Exception e){
					LoggerUtil.error("delete zk node :"+deleteNode +" error",e);
				}
		    }
		}
    	
    }*/
    
    public static  void printMap(CuratorFramework client) {
		for(Map.Entry<String, List<String>> entry: ZKJobCache.sessionHostMap.entrySet()){
			String session = entry.getKey();
			 List<String> hosts = entry.getValue();
			 LoggerUtil.debug("haha session = "+session);
			  for(String host: hosts){
				  LoggerUtil.debug("haha host = "+host);
			  }
			 
		 }
		ConcurrentHashMap<String, List<String>> projectSessionMap =  SlaveUtils.buildProjectSessionMap(client);
		 for(Map.Entry<String, List<String>> entry: projectSessionMap.entrySet()){
			 
				String project = entry.getKey();
				 List<String> sessions = entry.getValue();
				 LoggerUtil.debug("haha project = "+project);
				  for(String session: sessions){
					  LoggerUtil.debug("haha session = "+session);
				  }
				 
			 }
		 LoggerUtil.debug("haha ===========project end=================");
	}
    
    public static LeaderLatch getLeaderLatch(){
    	while(leaderLatch == null){
    		try {
				TimeUnit.MILLISECONDS.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	return leaderLatch;
    }
}

