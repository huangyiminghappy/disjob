package com.huangyiming;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Before;
import org.junit.Test;

import com.huangyiming.disjob.common.Constants;
import com.huangyiming.disjob.common.util.LocalHost;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.register.center.pool.ConsoleCuratorClient;
import com.huangyiming.disjob.register.center.pool.ThreadLocalClient;
import com.huangyiming.disjob.register.domain.Job;
import com.huangyiming.disjob.register.job.JobOperationService;
import com.huangyiming.disjob.register.repository.ZnodeApi;
import com.huangyiming.disjob.register.repository.ZnodeApiCuratorImpl;
import com.huangyiming.disjob.register.repository.watch.WatchApiCuratorImpl;
import com.huangyiming.disjob.register.repository.watch.listener.JobGroupListener;
import com.huangyiming.disjob.register.rpc.SubscribeService;
import com.huangyiming.disjob.register.rpc.ZookeeperRegistry;
import com.huangyiming.disjob.rpc.client.HURL;
import com.huangyiming.disjob.slaver.utils.SlaveUtils;
import com.google.gson.Gson;
 
/**
 * <pre>
 * 
 *  File: SubTest.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  测试订阅,发现接口
 * 
 *  Revision History
 *  Date,                   Who,                    What;
 *  2016年5月14日              Disjob             Initial.
 *
 * </pre>
 */
public class SubscribeTest extends BaseJunitTest
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
                  .connectString("10.40.6.100:2181,10.40.6.101:2181,10.40.6.102:2181")
                  .sessionTimeoutMs(5000)
                  .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                  .build();
          curatorClient.start();
          threadLocalClient.setCuratorClient(); 
	    	ConsoleCuratorClient curatorClient = threadLocalClient.getCuratorClient();
	        if (null == curatorClient || !curatorClient.isConnected()) {
	        	System.out.println("get ZK client failed！");
	            return ;
	        }
    }

    
 
	
     
 
 
 
    @Test
    public void testSub1() throws Exception{
       
        clientUrl = new HURL("oms3", new LocalHost().getIp(),   "test3");
        subscribeService.DoSubscribe(clientUrl);
         
       // znode.createEphemeral(curatorClient, "/disJob/rpc/oms/test/providers/192.168.2221.666:0", "disJob://77777:888/test1?phpFilePath=/usr/local/rpc-project/test.php&className=Test&methodName=start&version=0.1");
      //   subscribeService.doDiscover(clientUrl);
        for(int i =0;i<Integer.MAX_VALUE;i++){
            Thread.sleep(10000);
            List<HURL> ll = subscribeService.doDiscover(clientUrl);
            if(CollectionUtils.isNotEmpty(ll)){
                for(HURL str :ll){
                    System.out.println("invoke1111111111111 = "+str.getClientUrl());
                } 
            }
            
        }
       
        
        
       // Client.close();
        
    }
    @Test
    public void testSub() throws Exception{
       
       /* SubscribeService sub = new SubscribeService();
        ZookeeperRegistry zookeeperRegistry = new ZookeeperRegistry();
        zookeeperRegistry.setZkClient(curatorClient);
        sub.setRegistry(zookeeperRegistry);*/
      //  subscribeService.DoSubscribe(clientUrl);
        ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
        
        String urls = "/disJob/rpc/group28/job1/providers/10.40.6.89:9501";
        String strs [] = urls.split("/");
        znode.createEphemeral(curatorClient, urls, "disJob://10.40.6.89:9501/test1?serverGroup=oms1&phpFilePath=/usr/local/php-test/TestService.php&className=TestService&methodName=test&version=0.1");

       //   subscribeService.doDiscover(clientUrl);
        Thread.sleep(2000);
        List<String> ll = subscribeService.getProvidesByService(strs[3], strs[4]);
        znode.deleteByZnode(curatorClient, urls);
        Thread.sleep(5000);

        List<String> ll1 = subscribeService.getProvidesByService(strs[3], strs[4]);
        if(CollectionUtils.isEmpty(ll1)){
        	System.out.println("被移除================");
        }else{
        	System.out.println("没有被移除================");

        }
      /*  System.out.println(ll);
        for(String str :ll){
            System.out.println("invoke1111111111111 = "+str);
        }
        List<String> ll21 = subscribeService.getProvidesByService("oms", "test10");
        for(String str :ll21){
            System.out.println("invoke2222222222 = "+str);
        }*/
        Thread.sleep(Integer.MAX_VALUE);
       // Client.close();
        
    }
    
    @Test
    public void add() throws Exception{
        //client.create("/grobalegrow/demo/2/server/192.168.1.99:2199", "disJob://192.168.59:2189/testurl?className=classya&methodName=woqu&version=1&test=00".getBytes(Charset.forName("UTF-8")), CreateMode.PERSISTENT);
        //client.create("/grobalegrow/demo/2/server/192.168.2.99:2199", "hello".getBytes("utf-8"), CreateMode.PERSISTENT);
       // curatorClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/disJob/rpc/group2/test2/providers/10.40.6.102:0", "disJob://test102:102/test?jobGroup=oms&phpFilePath=/usr/local/rpc-project/test2.php&className=Test&methodName=start&version=0.1".getBytes(Charset.forName("UTF-8")));
        //new ZnodeApiCuratorImpl().deleteByRecursion(curatorClient, "/disJob/rpc/group2/test2");
        new ZnodeApiCuratorImpl().deleteByRecursion(curatorClient, "/disJob/rpc/group/oms");

    }
    
    
    
    @Test
    public void testSlaveNode() throws Exception{/*
        for(int i=0;i<Integer.MAX_VALUE;i++){
            Thread.sleep(5000);
            ConcurrentHashMap<DisJobServerInfo, ConcurrentHashMap<String,String>> map =   com.huangyiming.disjob.register.cache.ZKJobCache.jobInfoMap;
           System.out.println("map==null"+(map.size()));
            if(map !=null){
                Iterator<DisJobServerInfo> item = map.keySet().iterator();
                while(item.hasNext()){
                    DisJobServerInfo ip = item.next();
                    System.out.println("ip= "+ ip.getIp());
                }
            }
        }
        
        
        Thread.sleep(Integer.MAX_VALUE);
       // Client.close();
        
    */}
     
    
   
    @Test
    public void testUrl(){
        //disJob://10.40.6.100:9502/test2?jobGroup=oms&phpFilePath=/usr/local/rpc-project/test2.php&className=Test&methodName=start&version=0.1

        String url = "disJob://10.40.6.100:9502/test2?serverGroup=oms&phpFilePath=/usr/local/rpc-project/test2.php&className=Test&methodName=start&version=0.1";
        HURL hurl = HURL.valueOf(url);
        System.out.println(hurl);
    }
    
    /**
     * 根据服务名发现服务列表
     * TODO.
     *
     */
    @Test
    public void discover(){
        
        /*
        HURL clientUrl = new HURL("oms",EJobConstants.PROTOCOL_MOTAN, "10.40.6.100", 2181, "test");
        ZookeeperRegistry registry = new ZookeeperRegistry(clientUrl, curatorClient);
        sub.setRegistry(registry);
        
        sub.doDiscover(clientUrl);
        ConcurrentHashMap<HURL, Map<String, List<HURL>>> map = sub.registry.getSubscribedCategoryResponses();
        if(map !=null){
            Map<String, List<HURL>> m1=  map.get(clientUrl);
            if(m1!=null && m1.containsKey("server")){
                List<HURL> list = m1.get("server");
                for(HURL u : list){
                    System.out.println("u1="+u);
                }
            }
            
        }
    */
        try
        {
            
            @SuppressWarnings("unused")
             
            
            List<String> str = subscribeService.getProvidesByService("oms2","server1");
            for(String str1: str){
                System.out.println(str1);
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    
    }
   
    @Test
    public void testUpdateJob(){
        List<String> list = jobOperationService.getAllGroup();
        for(String str:list){
            System.out.println("group name is "+str);
        }
        List<Job> joblist = jobOperationService.getJobListByGroup("group5");
        for(Job job : joblist){
            System.out.println("jobname is "+job.getJobName());
        }
        Job j = jobOperationService.getJobByGroupAndJobName("group5", "oms5");
        System.out.println("job is "+j.getJobName());
        Job job = new Job();
        job.setGroupName("group5");
        job.setJobName("oms5");
        
        jobOperationService.updateJob(job);
    }
    @Test
    public void testPrintNode(){
        ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
        threadLocalClient.setCuratorClient();
        try
        {
            //set /disJob/scheduler/slave/192.168.56.1/execution 'jobname1|jobname2'

            //znode.createPersistent(curatorClient, "/disJob/scheduler/slave/192.168.56.1/execution/group1", "jobname1|jobname2");
            //znode.createPersistent(curatorClient, "/iii/jjj1", "1");
            //znode.makeDirs(curatorClient, "/a/b");
           // znode.createPersistent(curatorClient, "/a/c", "");
            //znode.createEphemeral(curatorClient, "/disJob/rpc/group4/oms4/providers/4ip", "disJob://10.40.6.100:9502/test2?serverGroup=oms&phpFilePath=/usr/local/rpc-project/test2.php&className=Test&methodName=start&version=0.1");
          
            
            for(int i =0;i<Integer.MAX_VALUE;i++){
                printNodes();
                Thread.sleep(10000);
                /*if(i==2){
                    System.out.println("begin update status =1");
                    Job job = new Job();
                    job.setGroupName("group");
                    job.setJobName("service");
                    job.setJobStatus(1);
                    job.setDesc("测试");
                    jobOperationService.updateJob(job);
                }
                
                if(i==4){
                    System.out.println("begin update status =0");
                    Job job = new Job();
                    job.setGroupName("group");
                    job.setJobName("service");
                    job.setJobStatus(0);
                    job.setDesc("测试11111");
                    jobOperationService.updateJob(job);
                }
                if(i==2){
                    System.out.println("============================begin update status =0");
                    Job job = new Job();
                    job.setGroupName("group4");
                    job.setJobName("oms4");
                    job.setJobStatus(1);
                    job.setDesc("测试444");
                    jobOperationService.updateJob(job);
                    //znode.createEphemeral(curatorClient, "/disJob/rpc/group5/oms5/providers/5ip", "disJob://10.40.6.100:9502/test2?serverGroup=oms&phpFilePath=/usr/local/rpc-project/test2.php&className=Test&methodName=start&version=0.1");

                }
                if(i==3){
                    System.out.println("============================begin update status =1");
                    Job job = new Job();
                    job.setGroupName("group5");
                    job.setJobName("oms5");
                    job.setJobStatus(1);
                    job.setDesc("测试222");
                    jobOperationService.updateJob(job);
                }
                if(i==4){
                    System.out.println("============================begin update status =1");
                    Job job = new Job();
                    job.setGroupName("group5");
                    job.setJobName("oms5");
                    job.setJobStatus(0);
                    job.setDesc("测试222");
                    jobOperationService.updateJob(job);
                }*/
            }
            
            
            
            Thread.sleep(Integer.MAX_VALUE);
            
           // curatorClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/123");
            ///disJob/rpc/oms/test/providers/192.168.99.990:2199
           
           // znode.makeDirs(curatorClient, "/disJob/scheduler/slave/10.37.1.214/status");
           //String test =  znode.makePath("/disJob/test", "567","789");
           //System.out.println(test);
           // curatorClient.create().withMode(CreateMode.EPHEMERAL).forPath("/disJob/rpc/test8/test8");
            
        }
        catch (Exception e)
        {
             e.printStackTrace();
        }
 
    }
    
    @Test
    public void testSerializable(){
        ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
        try
        {
            String str ="abcefg";
            Job job = new Job();
            job.setDesc("1234");
             
           // String s = (String) SerializeUtil.deserialize(znode.getData(curatorClient, "/test/ser1").getBytes(),String.class);
            //System.out.println(s);
            
            
             
            
           
            
        }
        catch (Exception e)
        {
             e.printStackTrace();
        }
 
    }
    
    @Test
    public void deleteByRecursion() throws Exception{
        ZnodeApi znodeApi = new ZnodeApiCuratorImpl();
        znodeApi.deleteByRecursion(curatorClient, "/disJob/rpc");

        znodeApi.deleteByRecursion(curatorClient, "/disJob/job");
        znodeApi.deleteByRecursion(curatorClient, "/disJob/scheduler/slave");
      /*  znodeApi.makeDirs(curatorClient, "/disJob/rpc");
        znodeApi.makeDirs(curatorClient, "/disJob/job");
        znodeApi.makeDirs(curatorClient,  "/disJob/scheduler/slave");*/

        /*znodeApi.deleteByRecursion(curatorClient, "/disJob/job/oms1");
         znodeApi.deleteByRecursion(curatorClient, "/disJob/job/oms2");
         znodeApi.deleteByRecursion(curatorClient, "/disJob/job/oms3");*/
 /*      znodeApi.deleteByRecursion(curatorClient, "/disJob/job/oms6");
        znodeApi.deleteByRecursion(curatorClient, "/disJob/job/oms7");  */
  

        
       
         /*znodeApi.deleteByRecursion(curatorClient, "/disJob/rpc/oms1");
        znodeApi.deleteByRecursion(curatorClient, "/disJob/rpc/oms2");
        znodeApi.deleteByRecursion(curatorClient, "/disJob/rpc/oms3");
        znodeApi.deleteByRecursion(curatorClient, "/disJob/rpc/oms6");
        znodeApi.deleteByRecursion(curatorClient, "/disJob/rpc/oms7");*/   
        /*znodeApi.deleteByZnode(curatorClient, "/disJob/scheduler/slave/192.168.56.1"); 

         znodeApi.deleteByZnode(curatorClient, "/disJob/scheduler/slave/192.168.56.1/status"); 
         znodeApi.deleteByRecursion(curatorClient, "/disJob/scheduler/slave/192.168.56.1"); 
*/
       // znodeApi.deleteByRecursion(curatorClient, "/disJob/scheduler/slave/192.168.56.1"); 
        /*  znodeApi.deleteByRecursion(curatorClient, "/disJob/scheduler/slave/10.40.6.185");
        znodeApi.deleteByRecursion(curatorClient, "/disJob/scheduler/slave/10.40.6.184"); 

        znodeApi.deleteByRecursion(curatorClient, "/disJob/scheduler/slave/10.40.6.183"); 
         znodeApi.deleteByRecursion(curatorClient, "/disJob/scheduler/slave/192.168.56.1");  
         
         
         
         
         
         
         znodeApi.deleteByRecursion(curatorClient, "/disJob/scheduler/slave/10.37.1.214");  */ 
 
        

       // znodeApi.deleteByRecursion(curatorClient, "/disJob/scheduler/slave/192.168.238.1");
     
        // znodeApi.deleteByRecursion(curatorClient, "/disJob/scheduler/slave/192.168.56.1");
        

       

         //znodeApi.deleteByRecursion(curatorClient, "/disJob/rpc/test");
     }
    
    /**
     * 
     * 监听rpc节点变化动态构造JOB节点.
     *
     * @throws InterruptedException
     */
    @SuppressWarnings("unused")
    @Test
    public void testCuratorChildListener() throws InterruptedException{
        WatchApiCuratorImpl watch = new WatchApiCuratorImpl();
          //String parentNode = "/disJob/rpc/oms/test/providers";
        String parentNode ="/test";
          ZookeeperRegistry zookeeperRegistry = new ZookeeperRegistry();
          zookeeperRegistry.setZkClient(curatorClient);
      watch.pathChildrenWatch(curatorClient,parentNode , false,  new JobGroupListener());
      Thread.sleep(10000);
      ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
      znode.deleteByZnode(curatorClient, "/test/del");
      //    parentNode = "/disJob/rpc/test8";
       // watch.pathChildrenWatch(curatorClient, parentNode, false, new JobNameListener(parentNode, serverMap));
        
       /* try
        {
            curatorClient.create().withMode(CreateMode.EPHEMERAL).forPath("/disJob/rpc/test8");
        }
        catch (Exception e)
        {
             e.printStackTrace();
        }*/
        Thread.sleep(Integer.MAX_VALUE);
    }
    @Test
    public void checkJob(){
        ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        List<String> l1= new ArrayList<String>();
        l1.add("server1");
        l1.add("server2");
        l1.add("test1");
        l1.add("test2");
        map.put("/disJob/rpc/oms", l1);
        for(Map.Entry<String, List<String>> entry:map.entrySet()){
            String groupNode = entry.getKey();
            List<String> serverList = entry.getValue();
            //存在则关注修改和添加server节点
           if(znode.checkExists(curatorClient,groupNode) ){
               if(CollectionUtils.isNotEmpty(serverList)){
                   for(String serverName :serverList){
                       String serverNode = groupNode + "/"+ serverName;
                       if(znode.checkExists(curatorClient,serverNode)){
                           
                       }
                   }
               }
           }
        }
        
    }
   
   @Test
    public void createJobByRpc(){
        
        ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
         
      /*  List<String> list = new ArrayList<String>();
        list.add("/disJob/rpc/test1");
        list.add("/disJob/rpc/test2");
        list.add("/disJob/rpc/test3");
        final ConcurrentHashMap<String, List<String>> serverMap = new ConcurrentHashMap<String, List<String>>();
        List<String> list1 = new ArrayList<String>();
        list1.add("/disJob/rpc/test1/a1");
        list1.add("/disJob/rpc/test1/a2");
        list1.add("/disJob/rpc/test1/a3");
        serverMap.put("/disJob/rpc/test1", list1);
        List<String> list2 = new ArrayList<String>();
        list2.add("/disJob/rpc/test2/b1");
        list2.add("/disJob/rpc/test2/b2");
        list2.add("/disJob/rpc/test2/b3");
        serverMap.put("/disJob/rpc/test2", list2);*/
        
        for(Map.Entry<String,List<String>> entry:serverMap.entrySet()){
            String groupNode = entry.getKey();
            List<String> serverNodes = entry.getValue();
            for(String serverNode :serverNodes){
                serverNode = serverNode.replace("rpc", "job");
                if(!znode.checkExists(curatorClient, serverNode)){
                    znode.makeDirs(curatorClient, serverNode);
                }
                 
            }
            //znode.makePath(parent, firstChild, restChildren)
        }
        
    }
    
    
    
    
    
    
    
    
    public static void printNodes() throws Exception
  {/*
        List<String> list =    ZKJobCache.groupList;
        System.out.println("===========grouplist==============");
        if(CollectionUtils.isNotEmpty(list)){
            for(String ls : list){
                System.out.println("group node :"+ls);
            }
        }
        ConcurrentHashMap<String, List<String>> serverMap = ZKJobCache.serverMap;
        System.out.println("===========serverMap==============");
        if(serverMap!=null && serverMap.size()>0){
            Iterator<String> items = serverMap.keySet().iterator();
            while(items.hasNext()){
                String key = items.next();
                System.out.println("groupname ================== "+key);
                List<String> servers =  serverMap.get(key);
                if(CollectionUtils.isNotEmpty(servers)){
                    for(String str : servers){
                        System.out.println("serverName = "+str);
                    }
                }
                 
            }
        }
        ConcurrentHashMap<String, Set<Job>> groupJobMap =  ZKJobCache.groupJobMap;
        System.out.println("===========groupJobMap=================");

        if(groupJobMap!=null && groupJobMap.size()>0){

            Iterator<String> items = groupJobMap.keySet().iterator();
            while(items.hasNext()){
                String key = items.next();
                System.out.println("groupname ================== "+key);
                Set<Job> jobs =  groupJobMap.get(key);
                if(CollectionUtils.isNotEmpty(jobs)){
                    for(Job jj : jobs){
                        System.out.println("job is  = "+jj.toString());
                    }
                }
                 
            }
               
        }
        
        ConcurrentHashMap<DisJobServerInfo, ConcurrentHashMap<String,String>> jobInfoMap = ZKJobCache.jobInfoMap;
        System.out.println("===========jobInfoMap=================");
        if(jobInfoMap!=null && jobInfoMap.size()>0){
            Iterator<DisJobServerInfo> items = jobInfoMap.keySet().iterator();
            while(items.hasNext()){
                DisJobServerInfo slaveServer = items.next();
                System.out.println("ip is "+slaveServer.getIp());
                ConcurrentHashMap<String,String> map = jobInfoMap.get(slaveServer);
                Iterator<String> item = map.keySet().iterator();
                while(item.hasNext()){
                    String group = item.next();
                    String jobnames = map.get(group);
                    System.out.println("group is "+group+ ",jobnames is "+jobnames);
                }
            }
            
        }
        
       
     Iterator<String> items = serverMap.keySet().iterator();
      while(items.hasNext()){
          String key = items.next();
          System.out.println(key+","+serverMap.get(key));
      }
      for(Map.Entry<String, List<String>> entry : serverMap.entrySet()){
          System.out.println("group node :"+ entry.getKey());
          for(String str :entry.getValue()){
              System.out.println("server node :"+ entry.getKey() +"/"+ str);
          }
      }
  */}
    
   
    
    @Test  
    public void testBytes(){  
        //字节数  
        //中文：ISO:1 GBK：2 UTF-8:3     
        //数字或字母： ISO:1 GBK:1 UTF-8:1  
        String username = "中";  
        try {  
            //得到指定编码的字节数组    字符串--->字节数组  
            byte[] u_iso=username.getBytes("ISO8859-1");  
            byte[] u_gbk=username.getBytes("GBK");  
            byte[] u_utf8=username.getBytes("utf-8");  
            System.out.println(u_iso.length);  
            System.out.println(u_gbk.length);  
            System.out.println(u_utf8.length);  
            //跟上面刚好是逆向的，字节数组---->字符串  
            String un_iso=new String(u_iso, "ISO8859-1");  
            String un_gbk=new String(u_gbk, "GBK");  
            String un_utf8=new String(u_utf8, "utf-8");  
            System.out.println(un_iso);  
            System.out.println(un_gbk);  
            System.out.println(un_utf8);          
            //有时候必须是iso字符编码类型，那处理方式如下  
            String un_utf8_iso=new String(u_utf8, "ISO8859-1");       
            //将iso编码的字符串进行还原  
            String un_iso_utf8=new String(un_utf8_iso.getBytes("ISO8859-1"),"UTF-8");  
            System.out.println(un_iso_utf8);                  
              
        } catch (UnsupportedEncodingException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
    }  
    
    @Test
    public void testupdatejson(){
        ZnodeApiCuratorImpl nodeApi = new ZnodeApiCuratorImpl();
        Job job = new Job();
        job.setDesc("测试");
        job.setGroupName("group1");
        job.setJobName("name1");
        job.setJobPath("/1/2/测试");
        job.setJobStatus(1);
        String json = new Gson().toJson(job);
        nodeApi.update(curatorClient, "/disJob/job/test456group/test456job/config", json);
        
        //get /disJob/job/test456group/test456job/config
    }
    
    @Test
    public void getChild(){
        String ip = "192.168.56.1";
        ZnodeApiCuratorImpl znode =  new ZnodeApiCuratorImpl();
        String path = znode.makePath(Constants.ROOT, Constants.DISJOB_SERVER_NODE_ROOT, Constants.DISJOB_SERVER_NODE_SLAVE, Constants.PATH_SEPARATOR+ip,Constants.DISJOB_SERVER_NODE_SLAVE_EXECUTION);
        List<String> list = znode.getChildren(curatorClient, path);
        for(String str : list){
            System.out.println(str);
        }
    }
    
    
    public static void main(String[] args)
    {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        List<String> list = new ArrayList<String>();
        list.add("123");
        map.put("hello", list);
        Iterator<List<String>> item = map.values().iterator();
        while(item.hasNext()){
            List<String> l = item.next();
            l.remove("123");
            l.add("456");
        }
        List<String>  o = map.get("hello");
        for(String str : o){
            System.err.println(str);
        }
        
         
     }
    
    @Test
    public void childRemoveListener(){
         WatchApiCuratorImpl watcher = new WatchApiCuratorImpl();
         ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
         znode.createEphemeral(curatorClient, "/disJob/job/oms/test/config/unavailble", "1");
         try
        {
            Thread.sleep(Integer.MAX_VALUE);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void testupdateJob(){
        String group = "oms5";
        
        String jobName ="test5";
        ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();

        String jobPath = znode.makePath(Constants.ROOT, Constants.APP_JOB_NODE_ROOT,Constants.PATH_SEPARATOR+group,Constants.PATH_SEPARATOR+jobName, Constants.APP_JOB_NODE_CONFIG);
        String data =  znode.getData(curatorClient, jobPath);
        Job job = new Job();
        if(StringUtils.isNotEmpty(data)){
              job =  new Gson().fromJson(data, Job.class);
        }
        job.setJobStatus(0);
         SlaveUtils.updateJobNode(job, curatorClient);
        
    }

    
    
  /*  @Test
    public void addSlaveExecute() throws Exception{
         
        SlaveServer slaveServer = new SlaveServer("192.168.1.1", Constants.DISJOB_SLAVE_DEFAULT_WEIGHT);
       
        ArrayList<JobExecutionInfo> list = new ArrayList<JobExecutionInfo>();
        
        JobExecutionInfo jobExecutionInfo = new JobExecutionInfo();
        jobExecutionInfo.setJobName("job1");
        jobExecutionInfo.setRunning(1);
        jobExecutionInfo.setCompleted(0);
        
        JobExecutionInfo jobExecutionInfo1 = new JobExecutionInfo();
        jobExecutionInfo1.setJobName("job2");
        jobExecutionInfo1.setRunning(0);
        jobExecutionInfo1.setCompleted(1);
        
        list.add(jobExecutionInfo1);
        list.add(jobExecutionInfo);
        
        ConcurrentHashMap<String,ArrayList<JobExecutionInfo>> map = new ConcurrentHashMap<String, ArrayList<JobExecutionInfo>>();
        map.put("group1", list);
        
        System.out.println("==============");
        JSONArray jsonArray = JSONArray.fromObject(list);
        String str = jsonArray.toString();
        System.out.print(jsonArray);
        System.out.println("==============");
        JSONArray toArray = JSONArray.fromObject(str);
        List<JobExecutionInfo> toList =  (List<JobExecutionInfo>)JSONArray.toCollection(toArray, JobExecutionInfo.class);
        for(JobExecutionInfo jb : toList ){
            System.out.println(jb.getJobName());
        }
        
        String str1 = new String(curatorClient.getData().forPath("/disJob/scheduler/slave/192.168.56.1/execution/group11"));
        System.out.println(str1);
        
        
    }*/
    
    
    @Test
    public void testAddNode() throws Exception{
       ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
       //znode.createPersistent(curatorClient, "/test/test", null);
       znode.createPersistent(curatorClient, "/data1", null);
       znode.createEphemeral(curatorClient, "/data1/test", "disJob://10.40.6.100:9502/test2?serverGroup=oms&phpFilePath=/usr/local/rpc-project/test2.php&className=Test&methodName=start&version=0.1");
      // znode.update(curatorClient, "/disJob/job/oms5/test5/config", "{"groupName":"oms5","jobName":"test5","cronExpression":"0/10 * * * * ?","jobPath":"1","parameters":"1","shardingCount":0,"fetchDataCount":0,"failover":true,"misfire":true,"scheduleMode":"1","jobStatus":1,"desc":"测试"}");
      // {"groupName":"oms5","jobName":"test5","cronExpression":"0/10 * * * * ?","jobPath":"1","parameters":"1","shardingCount":0,"fetchDataCount":0,"failover":true,"misfire":true,"scheduleMode":"1","jobStatus":1,"desc":"测试"}

        Thread.sleep(20000);
    }
    @Test
    public void ChildListenerTest() throws InterruptedException{
        WatchApiCuratorImpl node = new WatchApiCuratorImpl();
        node.pathChildrenWatch(curatorClient, "/data1", false, new PathChildrenCacheListener()
        {
            
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception
            {
                if( event.getType() == org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.CHILD_ADDED){
                    String path = event.getData().getPath();
                    System.out.println("add path:"+path );
                }
                if( event.getType() == org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.CHILD_REMOVED){
                    String path = event.getData().getPath();
                    System.out.println("remove path:"+path);
                }
                   
                
            }
        });
        Thread.sleep(Integer.MAX_VALUE);
    }
    
  
    @Test
    public void NodeCacheListenerTest2() throws InterruptedException{
        final NodeCache nodeCache = new NodeCache(curatorClient, "/data/test");
        try
        {
            nodeCache.start(true);
        }
        catch (Exception e)
        {
            LoggerUtil.error("client start error",e);
        }
        addListener(nodeCache);
        Thread.sleep(Integer.MAX_VALUE);
    }
    
    private static void addListener(final NodeCache cache) {
        // a PathChildrenCacheListener is optional. Here, it's used just to log
        // changes
        NodeCacheListener listener = new NodeCacheListener() {

            @Override
            public void nodeChanged() throws Exception {
                if (cache.getCurrentData() != null)
                    System.out.println("Node changed: " + cache.getCurrentData().getPath() + ", value: " + new String(cache.getCurrentData().getData()));
            }
        };
        cache.getListenable().addListener(listener);
    }
    
    @Test
    public void testSub2() throws Exception{
       ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
       System.out.println(StringUtils.isEmpty(znode.getData(curatorClient, "/data/bb")));
        
    }
    
    
}

