package com.huangyiming;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Before;
import org.junit.Test;

import com.huangyiming.disjob.common.Constants;
import com.huangyiming.disjob.common.util.LocalHost;
import com.huangyiming.disjob.register.center.pool.ThreadLocalClient;
import com.huangyiming.disjob.register.domain.DisJobServerInfo;
import com.huangyiming.disjob.register.domain.Job;
import com.huangyiming.disjob.register.job.JobOperationService;
import com.huangyiming.disjob.register.job.WeightedRoundRobinScheduling;
import com.huangyiming.disjob.register.repository.ZnodeApiCuratorImpl;
import com.huangyiming.disjob.register.rpc.SubscribeService;
import com.huangyiming.disjob.rpc.client.HURL;
import com.huangyiming.disjob.slaver.utils.SlaveUtils;
import com.google.gson.Gson;

/**
 * <pre>
 * 
 *  File: ConsoleApiTest.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  TODO
 * 
 *  Revision History
 *  Date,					Who,					What;
 *  2016年6月14日				Disjob				Initial.
 *
 * </pre>
 */
public class ConsoleApiTest extends BaseJunitTest
{
    @Resource
    public SubscribeService subscribeService;
    @Resource
    public JobOperationService  jobOperationService;
    @Resource   
    public ThreadLocalClient threadLocalClient;
    HURL zkUrl = null;
    HURL clientUrl = null;
    ZkClient client = null;
    CuratorFramework curatorClient = null;
    SubscribeService sub = new SubscribeService();
    final List<String> groupList = new ArrayList<String>();
    final ConcurrentHashMap<String, List<String>> serverMap = new ConcurrentHashMap<String, List<String>>();
    @Before
    public void init(){
         // zkUrl = new HURL("zookeeper", "10.40.6.100", 2181, "service4");
         // clientUrl = new HURL(EJobConstants.PROTOCOL_MOTAN, "10.40.6.100", 2181, "2");
        LocalHost localHost = new LocalHost();
        //clientUrl = new HURL("oms",EJobConstants.PROTOCOL_MOTAN, localHost.getIp(), 1, "test");
        clientUrl = new HURL("oms", localHost.getIp(),   "test8");
        client = new ZkClient("10.40.6.100:2181"); 
        curatorClient = CuratorFrameworkFactory.builder()
                  .connectString("10.40.6.100:2181")
                  .sessionTimeoutMs(5000)
                  .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                  .build();
          curatorClient.start();
    }
    
    @Test
    public void testAllGroup(){
        ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
        List<String> currentChilds = znode.getChildren(curatorClient, Constants.ROOT+Constants.PATH_SEPARATOR+Constants.DISJOB_RPC_NODE_ROOT);
         if(CollectionUtils.isNotEmpty(currentChilds)){
             for(String str : currentChilds){
                 System.out.println("groupName is "+ str);
             }
         }
    }
    @Test
    public void getJobListByGroup()
    {   String groupName = "oms6";
        ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
        List<String> currentChilds = znode.getChildren(curatorClient, Constants.ROOT+Constants.DISJOB_RPC_NODE_ROOT+Constants.PATH_SEPARATOR+groupName);
         List<Job> result = new ArrayList<Job>();
       if(CollectionUtils.isNotEmpty(currentChilds)){
           for(String jobName:currentChilds){
               String data =  znode.getData(curatorClient, Constants.ROOT+Constants.APP_JOB_NODE_ROOT+Constants.PATH_SEPARATOR+groupName+Constants.PATH_SEPARATOR+jobName+Constants.APP_JOB_NODE_CONFIG);
               if(StringUtils.isNotEmpty(data)){
                   Job  job =  new Gson().fromJson(data, Job.class);
                   result.add(job);
                   System.out.println(job.toString());
               }
           }
       }
    }
    @Test
    public void getJobByGroupAndJobName()
    {
        String groupName ="oms" ; 
        String jobName="test";
        
         ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
        List<String> currentChilds = znode.getChildren(curatorClient, Constants.ROOT+Constants.DISJOB_RPC_NODE_ROOT+Constants.PATH_SEPARATOR+groupName);
         Job result=null;
       if(CollectionUtils.isNotEmpty(currentChilds)){
           for(String tmJob:currentChilds){
               if(tmJob.equals(jobName)){
               String data =  znode.getData(curatorClient, Constants.ROOT+Constants.APP_JOB_NODE_ROOT+Constants.PATH_SEPARATOR+groupName+Constants.PATH_SEPARATOR+tmJob+Constants.APP_JOB_NODE_CONFIG);
               if(StringUtils.isNotEmpty(data)){
                   Job  job =  new Gson().fromJson(data, Job.class);
                   result = job;
                   System.out.println(result.toString());
                   return;
               }
               }
           }
       }
    }
    @Test
    public void testWeightedRoundRobinScheduling(){
        ConcurrentHashMap<String,String>  map =  SlaveUtils.getGroupAndJobMapByIp(curatorClient, "192.168.238.1");
        Iterator<DisJobServerInfo> item = SlaveUtils.getAvailableAlaveIps(curatorClient).iterator();
        Iterator<DisJobServerInfo> item1 = SlaveUtils.getAvailableAlaveIps(curatorClient).iterator();
        while(item1.hasNext()){
            DisJobServerInfo info1 = item1.next();
            System.out.println("yes:"+info1.getIp());
        }
        List<DisJobServerInfo> list = new ArrayList<DisJobServerInfo>();
        list.add(new DisJobServerInfo("192.168.1.1",1));
        list.add(new DisJobServerInfo("192.168.1.2",1));

        list.add(new DisJobServerInfo("192.168.1.3",1));

        list.add(new DisJobServerInfo("192.168.1.4",1));
        list.add(new DisJobServerInfo("192.168.1.5",1));
        list.add(new DisJobServerInfo("192.168.1.6",1));
        list.add(new DisJobServerInfo("192.168.1.7",1));
        list.add(new DisJobServerInfo("192.168.1.8",1));
      /*  for(int i = 0;i<10;i++){
            
            DisJobServerInfo info =   com.huangyiming.disjob.register.job.WeightedRoundRobinScheduling.GetBestSlaveServerTest(curatorClient,list);
            System.out.println(info.getIp());  
        }*/
        SlaveUtils.refreshSlaveIp(curatorClient);
        for(int i = 0;i<10;i++){
        
        DisJobServerInfo slaver =  WeightedRoundRobinScheduling.GetBestSlaveServer(curatorClient);
        System.out.println(slaver.getIp());  
        }
       }
    
    @Test
    public void testGetGroupAndJobMapByIp(){
        
        ConcurrentHashMap<String,String> jobMap =  SlaveUtils.getGroupAndJobMapByIp(curatorClient, "10.40.6.243");
        System.out.println(jobMap);
    }
    
  
   
    @Test
    public void testClearSlave(){
        ZnodeApiCuratorImpl nodeApi =  new ZnodeApiCuratorImpl();
        String ip = "192.168.56.1";
        String jobPath = nodeApi.makePath(Constants.ROOT, Constants.DISJOB_SERVER_NODE_ROOT,Constants.DISJOB_SERVER_NODE_SLAVE,Constants.PATH_SEPARATOR+ip,Constants.DISJOB_SERVER_NODE_SLAVE_EXECUTION);
        List<String> groupList = nodeApi.getChildren(curatorClient, jobPath);
        if(CollectionUtils.isNotEmpty(groupList)){
            for(String group : groupList){
                String groupNode = jobPath + Constants.PATH_SEPARATOR + group;
                nodeApi.deleteByZnode(curatorClient, groupNode);
            }
        }
    }

    @Test
    public void testCreateMasterHost(){
        ZnodeApiCuratorImpl nodeApi =  new ZnodeApiCuratorImpl();
        /*nodeApi.createPersistent(curatorClient, "/disJob/scheduler/master/host", "192.168.1.1");
        String ip = nodeApi.getData(curatorClient, "/disJob/scheduler/master/host");*/
       String data =  nodeApi.getData(curatorClient, "/disJob/scheduler/slave/192.168.56.1/status");
       System.out.println(StringUtils.isEmpty(data));
       System.out.println( data); 
       //nodeApi.update(curatorClient, Constants.DISJOB_SERVER_NODE_MASTER_IP, new LocalHost().getIp()); 
    }
    @Test
    public void testAdd(){
        ZnodeApiCuratorImpl nodeApi =  new ZnodeApiCuratorImpl();
        /*nodeApi.createPersistent(curatorClient, "/disJob/scheduler/master/host", "192.168.1.1");
        String ip = nodeApi.getData(curatorClient, "/disJob/scheduler/master/host");*/
      //  nodeApi.createPersistent(curatorClient, "/disJob/rpc/group1/test1", null);
        nodeApi.createPersistent(curatorClient, "/disJob/scheduler/slave/1.1.1.1/status","READY");
        nodeApi.createPersistent(curatorClient, "/disJob/scheduler/slave/1.1.1.1/execution/oms/server","job1|job2");


       //nodeApi.update(curatorClient, Constants.DISJOB_SERVER_NODE_MASTER_IP, new LocalHost().getIp()); 
    }
    
    @Test
    public void testGetPrivoderUrl(){
    	List<HURL> urls = 	subscribeService.getHURListProvidesByService("grouptest6", "jobtest6");
    	if(CollectionUtils.isNotEmpty(urls)){
    		for(HURL hurl:urls){
    			System.out.println(hurl);
    		}
    	}
    }
    
}

