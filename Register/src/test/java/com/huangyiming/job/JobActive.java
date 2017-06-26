package com.huangyiming.job;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.transaction.CuratorTransactionFinal;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
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
import com.huangyiming.disjob.slaver.utils.SlaveUtils;
import com.google.gson.Gson;
import com.huangyiming.BaseJunitTest;
public class JobActive extends BaseJunitTest{
	
	 @Resource
	    public JobOperationService  jobOperationService;
	 
		@Resource
		private ThreadLocalClient threadLocalClient;
		
	     
	 
	 CuratorFramework client = null;
 	    @Before
	    public void init(){
	        LocalHost localHost = new LocalHost();
 	        client = CuratorFrameworkFactory.builder()
	                  .connectString("10.40.6.100:2181,10.40.6.101:2181,10.40.6.102:2181")
	                  .sessionTimeoutMs(5000)
	                  .retryPolicy(new ExponentialBackoffRetry(1000, 3))
	                  .build();
	          client.start();
	          
	          threadLocalClient.setCuratorClient(); 
		    	ConsoleCuratorClient curatorClient = threadLocalClient.getCuratorClient();
		        if (null == curatorClient || !curatorClient.isConnected()) {
		        	System.out.println("get ZK client failed！");
		            return ;
		        }
	    }
 	    //生成job
 	  @Test
	 public void saveJob(){
		 ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
		    for(int i =1;i<=1000;i++){
		    	int val = (i-1)/20 + 1 ;
		    	String groupName = "grouptest_"+val;
		    	String jobName = "jobtest_"+i;
		    	String prividerPath = "/php-disJob/rpc"+"/"+groupName+"/"+jobName+"/providers";
		    	znode.createPersistent(client, prividerPath+"/10.40.6.89:9501", "disJob://10.40.6.89:9501/test1?serverGroup=oms1&phpFilePath=/usr/local/php-test/TestService.php&className=TestService&methodName=test&version=0.1");
		    }
		    System.out.println("create rpc and copy job success");
	     try {
			Thread.sleep(Integer.MAX_VALUE);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	//激活
 	   @Test
 		 public void activeJob(){
 		    for(int i =1;i<=600;i++){
 		    	int val = (i-1)/20 + 1 ;
		    	String groupName = "cronJob_"+val;
		    	String jobName = "fireNowJobTemp_"+i;
				Job job = new Job();
				job.setGroupName(groupName);
				job.setJobName(jobName);
				job.setJobStatus(0);
				if (i <= 600) {
					job.setCronExpression("0 0/5 * * * ?");
				} else {
					job.setCronExpression("0 0/50 * * * ?");
				}
				job.setDesc("test " + groupName + "-" + jobName);
				jobOperationService.updateJob(job);
 			}
 			   System.out.println("update success");
 			  try {
 					Thread.sleep(Integer.MAX_VALUE);
 				} catch (InterruptedException e) {
 					e.printStackTrace();
 				}
 			 
 		 }
 	   
 	   @Test
 	   public void testClearJob() throws Exception{

 	        CuratorTransaction transaction = client.inTransaction();
 	       // CuratorTransactionFinal curatorTransactionFinal = new org.apache.curator.framework.imps.CuratorTransactionImpl (client);
 	 		SlaveUtils.clearAllClustersJobs(client,transaction);
 	 		
 	 		CuratorTransactionFinal curatorTransactionFinal = (CuratorTransactionFinal)transaction;
 	 		//没办法
 	 		curatorTransactionFinal.commit(); 	  
 	 		ZnodeApiCuratorImpl znodeApi = new ZnodeApiCuratorImpl();
 	 	   /* znodeApi.deleteByRecursion(client, "/disJob-dev/job");
 	        znodeApi.deleteByRecursion(client, "/disJob-dev/scheduler/slave");  
 	 	       znodeApi.deleteByRecursion(client, "/disJob-dev/rpc");

 	        znodeApi.makeDirs(client, "/disJob-dev/rpc");
 	        znodeApi.makeDirs(client, "/disJob-dev/job");
 	        znodeApi.makeDirs(client, "/disJob-dev/scheduler/slave"); */
 	 	 	znodeApi.deleteByRecursion(client, "/disJob/job");
 	        //znodeApi.deleteByRecursion(client, "/disJob/scheduler/slave");  
 	 	       znodeApi.deleteByRecursion(client, "/disJob/rpc");

 	        znodeApi.makeDirs(client, "/disJob/rpc");
 	        znodeApi.makeDirs(client, "/disJob/job");
 	      //  znodeApi.makeDirs(client, "/disJob/scheduler/slave");
 	 		

 	 		System.out.println("清除完成");
 	   }
 	   
	    @Test
	 public void testRpcJobCopy(){
		 
		 ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
	        
		    for(int i =1;i<Integer.MAX_VALUE;i++){
		      	String groupName = "";
		    	if(i%10==0){
			    	  groupName = "grouptest"+5;
		    	}
		    	else if(i%10==5){
			    	  groupName = "grouptest"+6;
		    	}
		    	else if(i%10==3){
			    	  groupName = "grouptest"+7;
		    	}else{
		    	groupName = "grouptest"+8;
		    	}
		    	String jobName = "jobtest"+i;
		    	String prividerPath = "/disJob/rpc"+"/"+groupName+"/"+jobName+"/providers";
		    	//System.out.println(prividerPath);
		    	//System.out.println(prividerPath+"/192.168.1."+i+":8080");
		    	 //znode.makeDirs(client, prividerPath);
		    	//znode.createEphemeral(client, prividerPath+"/192.168.1."+i+":8080", "disJob://10.40.6.89:9501/test1?serverGroup=oms1&phpFilePath=/usr/local/php-test/TestService.php&className=TestService&methodName=test&version=0.1");
		    	try{ 
		    	znode.createPersistent(client, prividerPath+"/10.40.6.89:9501", "disJob://10.40.6.89:9501/test1?serverGroup=oms1&phpFilePath=/usr/local/php-test/TestService.php&className=TestService&methodName=test&version=0.1");
		    	}catch(Exception e){
		    		e.printStackTrace();
		    		System.out.println("i====================="+i);
		    		break;
		    	}
		    	try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	String str =  znode.getData(client, "/disJob/job/"+groupName+"/"+jobName+"/config");
		    	if(StringUtils.isEmpty(str)){
		    		System.out.println("not copy job groupName:"+groupName +" , jobName:"+jobName);
		    	}
		    	
		    }
		    System.out.println("======================over=================");
		    try {
				Thread.sleep(Integer.MAX_VALUE);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	     
		 
		 
	 }
 	  @Test
 	    public void deleteByRecursion() throws Exception{
 	        ZnodeApi znodeApi = new ZnodeApiCuratorImpl();
 	        
 	      /* znodeApi.deleteByRecursion(client, "/disJob/scheduler/master");
	         znodeApi.makeDirs(client, "/disJob/scheduler/master");*/
	        //znodeApi.createPersistent(client, "/disJob/scheduler/master/host", "10.40.6.100");
	        
 	        znodeApi.deleteByRecursion(client, "/disJob/job");
	      //  znodeApi.deleteByRecursion(client, "/disJob/scheduler/slave");  
	 	       znodeApi.deleteByRecursion(client, "/disJob/rpc");

	        znodeApi.makeDirs(client, "/disJob/rpc");
	        znodeApi.makeDirs(client, "/disJob/job");
	       // znodeApi.makeDirs(client,  "/disJob/scheduler/slave"); 
	       /* znodeApi.deleteByRecursion(client, "/disJob/scheduler/master");
	         znodeApi.makeDirs(client, "/disJob/scheduler/master");*/
 	         //znodeApi.deleteByRecursion(client, "/disJob/scheduler/master");
	        // znodeApi.makeDirs(client, "/disJob/scheduler/master");
 	       /*znodeApi.deleteByRecursion(client, "/disJob/scheduler/master");
 	         znodeApi.makeDirs(client, "/disJob/scheduler/master");
 	        znodeApi.createPersistent(client, "/disJob/scheduler/master/host", "10.40.6.100");*/
 	        /* znodeApi.deleteByRecursion(client, "/disJob/rpc");

 	        znodeApi.deleteByRecursion(client, "/disJob/job");
 	        znodeApi.deleteByRecursion(client, "/disJob/scheduler/slave");   
 	        znodeApi.makeDirs(client, "/disJob/rpc");
 	        znodeApi.makeDirs(client, "/disJob/job");
 	        znodeApi.makeDirs(client,  "/disJob/scheduler/slave"); */
 	     //  znodeApi.makeDirs(client, "/disJob/scheduler/master");
 	       
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
 	        //znodeApi.deleteByRecursion(client, "/disJob/scheduler/master"); 
 	       
 	       // znodeApi.deleteByZnode(client, "/disJob/scheduler/slave/192.168.56.1"); 

 	         //znodeApi.deleteByZnode(client, "/disJob/scheduler/slave/192.168.56.1/status"); 
 	        // znodeApi.deleteByRecursion(client, "/disJob/scheduler/slave/192.168.56.1"); 

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
 	  
 	  
	    
	    @Test
	 public void saveJob1(){
		 
		 ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
		 String groupName = "";
		    for(int i =1;i<3;i++){
		    	if(i%2 == 0){
			    	  groupName = "aaaaa"+i;
 		    	}else{
			    	  groupName = "huanqiujob"+i;

 		    	}
		    	String jobName = "jobtest"+i;
		    	String prividerPath = "/disJob/rpc"+"/"+groupName+"/"+jobName+"/providers";
		    	System.out.println(prividerPath);
		    	System.out.println(prividerPath+"/192.168.1."+i+":8080");
		    	 //znode.makeDirs(client, prividerPath);
		    	//znode.createEphemeral(client, prividerPath+"/192.168.1."+i+":8080", "disJob://10.40.6.89:9501/test1?serverGroup=oms1&phpFilePath=/usr/local/php-test/TestService.php&className=TestService&methodName=test&version=0.1");
		    	 znode.createPersistent(client, prividerPath+"/10.40.6.89:9501", "disJob://10.40.6.89:9501/test1?serverGroup=oms1&phpFilePath=/usr/local/php-test/TestService.php&className=TestService&methodName=test&version=0.1");
		    	 //znode.createPersistent(client, privider, null);
		    }
		    try {
				Thread.sleep(Integer.MAX_VALUE);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	     
		 
		 
	 }
	  //激活
	   @Test
		 public void activeJob1(){
			 
			 ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
			 String groupName = "";

			  for(int i =1;i<8;i++){
			    	if(i%2 == 0){
				    	  groupName = "testgroup11";
	 		    	}else{
				    	  groupName = "testgroup22";

	 		    	}
			    	String jobName = "jobtest"+i;
			    	 Job job = new Job();
			    	 job.setGroupName(groupName);
			    	 job.setJobName(jobName);
			    	 if(i<5){
	 			    	 job.setCronExpression("0 29 * * * ?");

			    	 }else{
			    		 job.setCronExpression("0 30 * * * ?");
			    	 }
			    	 job.setDesc("test "+groupName + "-"+jobName);
			    	 
			    	jobOperationService.updateJob(job);
			    	
			    	 //znode.createPersistent(client, privider, null);
			    }
			   try {
					Thread.sleep(Integer.MAX_VALUE);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 
			 
		 }
	   
	 //暂停
		@Test
	 	public void allstop(){
	 		ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
	 		String jobNode = "/disJob/job";
 	 					List<String> groupList = znode.getChildren(client, jobNode);
	 					if(CollectionUtils.isNotEmpty(groupList)){
	 						for(String groupName :groupList ){
		 						String groupNode = jobNode + "/" + groupName;
		 						 List<String> jobAll = znode.getChildren(client, groupNode) ;
		 						if(CollectionUtils.isNotEmpty(jobAll)){
		 							
		 							for(String jobName:jobAll){
		 								String job1Node = groupNode+"/"+jobName;
		 								String configNode = job1Node+ "/" + "config";
		 								String json = znode.getData(client, configNode);
		 								Job zkJob = new Job();
		 						         if(StringUtils.isNotEmpty(json)){
		 						            zkJob =  new Gson().fromJson(json, Job.class);
		 						            //jobOperationService.suspendJob(zkJob);  
		 						           suspendJob(zkJob, client);
		 						         } 
 		 						      
		 						      
 		 								
		 							}
		 						}
		 						 
		 					}
	 				 
	 					
 	 			 
	 					}
	 		
	 		
	 	 
	 	}
		
		
		public void suspendJob(Job job,CuratorFramework client){
           ZnodeApiCuratorImpl znodeApi = new ZnodeApiCuratorImpl();
			if( null == job){
				   LoggerUtil.warn("suspendJob job,job is null");	
				   return ;
			}
	 		job.setJobStatus(3);
	        LoggerUtil.info("begin update job is "+job.toString());
	        try{
 	  		   CuratorTransaction  transaction = znodeApi.startTransaction(client);

	           String group = job.getGroupName();
	           String jobName = job.getJobName();
	         
	          //把JOB暂停->找到该job所在的ip,找到该slave上的该job然后移除该job
	              LoggerUtil.info("job be suspend, job is "+job.toString());
	              String jobPath = znodeApi.makePath(Constants.ROOT, Constants.APP_JOB_NODE_ROOT,Constants.PATH_SEPARATOR+group,Constants.PATH_SEPARATOR+jobName, Constants.APP_JOB_NODE_CONFIG);
	              String data =  znodeApi.getData(client, jobPath);
	              if(StringUtils.isNotEmpty(data)){
	                 Job stopJob = new Job();
	                 stopJob =  new Gson().fromJson(data, Job.class);
	                 //虽然slave上的该job被移除了,/disJob/job的config记录了该job状态,config上的slaveip记录了是从哪台机器暂停remove的,故不改config的slaveip值
	                 String slaveIp = stopJob.getSlaveIp();
	                 String jobNode = znodeApi.makePath(Constants.ROOT, Constants.DISJOB_SERVER_NODE_ROOT,Constants.DISJOB_SERVER_NODE_SLAVE,Constants.PATH_SEPARATOR+slaveIp,Constants.DISJOB_SERVER_NODE_SLAVE_EXECUTION,Constants.PATH_SEPARATOR+group);
	                 String jobs = znodeApi.getData(client, jobNode);
	                 if(!StringUtils.isEmpty(jobs)){
	                	 //得到 /slave/job上某group上移除该job值后得到的job值
	                     String newJob = removeJobName(jobs, jobName);
	              	 
	                  
	                  //如果group下的job的值为空串->单个job被移除,则直接remove掉该job节点
	                  if("".equals(newJob)){
	                       //znodeApi.deleteByZnode(client, jobNode);
	                 	 znodeApi.addDeleteToTransaction(transaction, jobNode);
	                  }
	                  //不为空代表该group移除该job后还有其他job此时更新该/group/job节点的值
	                  else{
	                     // znodeApi.update(client, jobNode, newJob);
	                 	 znodeApi.addUpdateToTransaction(transaction, jobNode, newJob);
	                  }
	                }
	              	
	              }
	           
	             //更改disJob/job的对应job节点的值
	            // SlaveUtils.updateJobNode(job, client); 
	              CuratorTransactionFinal  transactionFinal =  SlaveUtils.updateJobNodeByTranstions(job, client, transaction);
	              znodeApi.commitTransaction(transactionFinal);
	           
	           LoggerUtil.info("after update job is "+job.toString());
	           
	          }catch(RuntimeException e){
	              LoggerUtil.error("update updateJob error, job is "+job,e);
	              return ;
	          } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	          return ;
	      
		
		}
		
		 /**
	     * 根据节点上job的列表字符串移除某jobname后返回job节点字符串
	     * @param jobName
	     * @param targetJob
	     * @return
	     */
	    private   static String removeJobName(String jobName,String targetJob){
	    	
	        String[] array = jobName.split(com.huangyiming.disjob.common.Constants.TRANSFER_CHAR+Constants.JOB_SEPARATOR);
	        List<String> list = Arrays.asList(array);
	        ArrayList<String> tempList =new ArrayList<String>(list);
	        tempList.remove(tempList.indexOf(targetJob));
	        StringBuffer sb = new StringBuffer();
	        for(int i = 0; i < tempList.size(); i++)
	        { 
	            sb. append(tempList.get(i).trim()+Constants.JOB_SEPARATOR);
	        }
	        String newStr = sb.toString();
	        if(StringUtils.isNoneEmpty(newStr)){
	             return newStr.substring(0, newStr.length() - 1);
	        }
	        return "";
	         
	    }
	    
	    //恢复
	    @Test
	    public void resumeJob(){
	 		ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
	 		String jobNode = "/disJob/job";
 	 					List<String> groupList = znode.getChildren(client, jobNode);
	 					if(CollectionUtils.isNotEmpty(groupList)){
	 						for(String groupName :groupList ){
		 						String groupNode = jobNode + "/" + groupName;
		 						 List<String> jobAll = znode.getChildren(client, groupNode) ;
		 						if(CollectionUtils.isNotEmpty(jobAll)){
		 							
		 							for(String jobName:jobAll){
		 								String job1Node = groupNode+"/"+jobName;
		 								String configNode = job1Node+ "/" + "config";
		 								String json = znode.getData(client, configNode);
		 								Job zkJob = new Job();
		 						         if(StringUtils.isNotEmpty(json)){
		 						            zkJob =  new Gson().fromJson(json, Job.class);
		 						            jobOperationService.resumeJob(zkJob);
		 						           
		 						         } 
 		 						     
		 							}
		 						}
		 						 
		 					}
	 				  
	 					}
	 		 
	 	}
	    
	    
	    //均分job到slave上
	    @Test
	    public void averageDistributeSlaveJob() throws Exception{
	    	jobOperationService.averageDistributeSlaveJob();
	    }
	    

	    @Test
	    public void test() throws Exception{
	    	ZnodeApiCuratorImpl nodeApi = new ZnodeApiCuratorImpl();
	    	//nodeApi.deleteByRecursion(client, "/disJob/job/oms ");
	    	List<String> list = nodeApi.getChildren(client, "/disJob/job/oms ");
	    	for(String str:list){
	    		System.out.println(str);
	    	}
	    
 	    }
	    
	  //查看哪个group,job上没有分配ip
	    @Test
	    public void checkjobnovalue() throws Exception{
 	    	ZnodeApiCuratorImpl nodeApi = new ZnodeApiCuratorImpl();
	    	ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
	  		String jobNode = "/disJob/job";
	  	   int no =0;
	  	   int noip=0;
			List<String> groupList = znode.getChildren(client, jobNode);
			if(CollectionUtils.isNotEmpty(groupList)){
				for(String groupName :groupList ){
					String groupNode = jobNode + "/" + groupName;
					 List<String> jobAll = znode.getChildren(client, groupNode) ;
					 if(CollectionUtils.isNotEmpty(jobAll)){
						for(String jobName:jobAll){
							String job1Node = groupNode+"/"+jobName;
							String configNode = job1Node+ "/" + "config";
							String json = znode.getData(client, configNode);
							Job zkJob = new Job();
					        if(StringUtils.isNotEmpty(json)){
					            zkJob =  new Gson().fromJson(json, Job.class);
					            if(zkJob!=null){
					            	no++;
					            	String slaveip = zkJob.getSlaveIp();
					            	if(StringUtils.isEmpty(slaveip)){
					            		System.out.println("groupName:"+groupName+",jobName:"+jobName+" no ip");
					            		noip++;
					            	}
					            }
					        } 
					       
							/*int index = groupJob.hashCode() %slaveList.size();
							DisJobServerInfo info = slaveList.get(index);
							String ip = info.getIp();*/
					       
						}
					}
						 
				}
		 
			
	 
			}
	    System.out.println("has job number is "+no);
	    System.out.println("no job ip number is "+noip);

 	    }
	    
	    
	    
	    public List<String> getJobsByIp(String ip){
	    	ZnodeApiCuratorImpl znodeApi = new ZnodeApiCuratorImpl();
	  		List<String> list = new ArrayList<String>();

	    	int i=0;
	    	int j=0;
			CuratorFramework client = threadLocalClient.getCuratorClient().getCuratorClient();
			String slavePath = ZKPaths.makePath(Constants.ROOT, Constants.DISJOB_SERVER_NODE_ROOT, Constants.DISJOB_SERVER_NODE_SLAVE);
		    String slaveServerPath = ZKPaths.makePath(slavePath, Constants.PATH_SEPARATOR + ip);
		    String slavePathExecution = ZKPaths.makePath(slaveServerPath, Constants.DISJOB_SERVER_NODE_SLAVE_EXECUTION);
		    List<String> groupLst = znodeApi.getChildren(client, slavePathExecution);
		    for(String groupName : groupLst){
		    	String slavePathJob = ZKPaths.makePath(slavePathExecution, Constants.PATH_SEPARATOR + groupName);
		    	String jobNameStr = znodeApi.getData(client, slavePathJob);
		    	String [] jobNameArray = jobNameStr.split(Constants.TRANSFER_CHAR + Constants.JOB_SEPARATOR);
		    	List<String> jobNameLst = new ArrayList<String>();
		    	if(jobNameArray !=null &&  jobNameArray.length>0){
		    		for(String jobName : jobNameArray){
			    		jobNameLst.add(jobName);
			    		list.add(groupName+jobName);
			    		i++;
			    	}
		    	}else{
		    		j++;
		    		System.out.println("group no data "+groupName);
		    	}
		    	
		    	//groupJobMap.put(groupName, jobNameLst);
		    }
		    System.out.println("job num is "+i+",j is "+j);
		    return list;
			//return groupJobMap;
	    }
	    
	    
	    public List<String> checkjobnovalue1() throws Exception{
 	    	ZnodeApiCuratorImpl nodeApi = new ZnodeApiCuratorImpl();
	    	ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
	  		String jobNode = "/disJob/job";
	  		List<String> list = new ArrayList<String>();
	  	   int no =0;
	  	   int noip=0;
			List<String> groupList = znode.getChildren(client, jobNode);
			if(CollectionUtils.isNotEmpty(groupList)){
				for(String groupName :groupList ){
					String groupNode = jobNode + "/" + groupName;
					 List<String> jobAll = znode.getChildren(client, groupNode) ;
					 if(CollectionUtils.isNotEmpty(jobAll)){
						for(String jobName:jobAll){
							String job1Node = groupNode+"/"+jobName;
							String configNode = job1Node+ "/" + "config";
							String json = znode.getData(client, configNode);
							Job zkJob = new Job();
					        if(StringUtils.isNotEmpty(json)){
					            zkJob =  new Gson().fromJson(json, Job.class);
					            if(zkJob!=null){
					            	list.add(groupName+jobName);
					            	no++;
					            	String slaveip = zkJob.getSlaveIp();
					            	if(StringUtils.isEmpty(slaveip)){
					            		System.out.println("groupName:"+groupName+",jobName:"+jobName+" no ip");
					            		noip++;
					            	}
					            }
					        } 
					       
							/*int index = groupJob.hashCode() %slaveList.size();
							DisJobServerInfo info = slaveList.get(index);
							String ip = info.getIp();*/
					       
						}
					}
				}
		 
			
	 
			}

	    System.out.println("has job number is "+no);
	    System.out.println("no job ip number is "+noip);
	    return list;	 

 	    }
	    //查看哪个group,job没有被分配到slave上
	    @Test
	    public void testwhicth() throws Exception{
	    	String str[]={"10.40.6.100","10.40.6.183","10.40.6.184","10.40.6.185","192.168.56.1"};
	    	List<String> list= new ArrayList<String>();
	    	for(String ip:str){
	    		list.addAll(getJobsByIp(ip));
	    	}
	    	System.out.println("yifenpei"+list.size());
	    	List<String> all= new ArrayList<String>();
	    	all = checkjobnovalue1();
	    	System.out.println("all is "+all.size());
	    	
	    	all.removeAll(list);
	    	System.out.println("size is "+all.size());
	    	for(String str1:all){
	    		System.out.println("no dis is "+str1);
	    	}

	    	
	    }
	    
	    //查看具体slave上job情况
	    @Test
	    public void getJobsByIp(){
	    	String ip ="192.168.56.1";
	    	ZnodeApiCuratorImpl znodeApi = new ZnodeApiCuratorImpl();
	    	int i=0;
	    	int j=0;
			CuratorFramework client = threadLocalClient.getCuratorClient().getCuratorClient();
			String slavePath = ZKPaths.makePath(Constants.ROOT, Constants.DISJOB_SERVER_NODE_ROOT, Constants.DISJOB_SERVER_NODE_SLAVE);
		    String slaveServerPath = ZKPaths.makePath(slavePath, Constants.PATH_SEPARATOR + ip);
		    String slavePathExecution = ZKPaths.makePath(slaveServerPath, Constants.DISJOB_SERVER_NODE_SLAVE_EXECUTION);
		    List<String> groupLst = znodeApi.getChildren(client, slavePathExecution);
		    for(String groupName : groupLst){
		    	String slavePathJob = ZKPaths.makePath(slavePathExecution, Constants.PATH_SEPARATOR + groupName);
		    	String jobNameStr = znodeApi.getData(client, slavePathJob);
		    	String [] jobNameArray = jobNameStr.split(Constants.TRANSFER_CHAR + Constants.JOB_SEPARATOR);
		    	List<String> jobNameLst = new ArrayList<String>();
		    	if(jobNameArray !=null &&  jobNameArray.length>0){
		    		for(String jobName : jobNameArray){
			    		jobNameLst.add(jobName);
			    		i++;
			    	}
		    	}else{
		    		j++;
		    		System.out.println("group no data "+groupName);
		    	}
		    	
		    	//groupJobMap.put(groupName, jobNameLst);
		    }
		    System.out.println("job num is "+i+",j is "+j);
			//return groupJobMap;
	    }
	    
	   	   
}
