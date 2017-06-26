package com.huangyiming.disjob.register.job;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.transaction.CuratorTransactionFinal;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.KeeperException;
import org.springframework.stereotype.Service;

import com.huangyiming.disjob.common.Constants;
import com.huangyiming.disjob.common.exception.DisJobFrameWorkException;
import com.huangyiming.disjob.common.exception.ZKNodeException;
import com.huangyiming.disjob.common.exception.ZKNodeException2;
import com.huangyiming.disjob.common.model.JobGroup;
import com.huangyiming.disjob.common.util.Bean2MapAndMap2Bean;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.register.auth.AuthConstants;
import com.huangyiming.disjob.register.auth.service.AuthService;
import com.huangyiming.disjob.register.cache.ZKJobCache;
import com.huangyiming.disjob.register.center.pool.ThreadLocalClient;
import com.huangyiming.disjob.register.core.jobs.JobFireFactory;
import com.huangyiming.disjob.register.domain.DisJobServerInfo;
import com.huangyiming.disjob.register.domain.Job;
import com.huangyiming.disjob.register.repository.ZnodeApi;
import com.huangyiming.disjob.register.repository.ZnodeApiCuratorImpl;
import com.huangyiming.disjob.register.rpc.ConcurrentHashSet;
import com.huangyiming.disjob.register.rpc.SubscribeService;
import com.huangyiming.disjob.slaver.utils.SlaveUtils;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.huangyiming.disjob.monitor.db.domain.DBUser;
import com.huangyiming.disjob.monitor.db.domain.PageResultAndCategories;

/**
 * 
 * job后台操作服务类
 * @author Disjob
 *
 */
@Service("jobOperationService")
public class JobOperationServiceImpl implements JobOperationService {
	
	@Resource
	private ZnodeApi znodeApi;
	
	@Resource
	private ThreadLocalClient threadLocalClient;
	
	
	@Resource
	public   DisJobServerService initServerExecuteJobService;
	
	@Resource
	public   SubscribeService subscribeService;

	@Resource
	private AuthService authService;
	
	@Override
	public Job getJob() {
		//CuratorFramework client = threadLocalClient.getCuratorClient().getCuratorClient();
		return null;
	}

	//@Override
	public List<Job> getJobList(String groupName) throws ZKNodeException2{
		try {
			CuratorFramework client = threadLocalClient.getCuratorClient().getCuratorClient();
			List<Job> jobLst = new ArrayList<Job>();
			if(StringUtils.isNotEmpty(groupName)){
				List<String> jobStrList = client.getChildren().forPath(Constants.ROOT + Constants.PATH_SEPARATOR + groupName);
				for(String jobName : jobStrList){
					Job job = new Job();
				    String path = ZKPaths.makePath(Constants.ROOT, Constants.PATH_SEPARATOR + groupName ,Constants.APP_JOB_NODE_CONFIG);
				    List<String> jobFileds = client.getChildren().forPath(path);
				    Map<String, Object> map = new HashMap<String, Object>();
				    for(String filed : jobFileds){
				    	Object value = client.getData().forPath(path + Constants.PATH_SEPARATOR + filed);
				    	map.put(filed, value);
				    }
				    Bean2MapAndMap2Bean.transMap2Bean(map, job);
				    job.setGroupName(groupName);
				    job.setJobName(jobName);
				    jobLst.add(job);
				}
			}else{
					
			}
			return jobLst;
		} catch (Exception e) {
			throw new ZKNodeException2("Get job by gorup name occurs exception！", e);
		}
	}

	public static final String pathFormat = ZKPaths.makePath(Constants.ROOT, Constants.DISJOB_RPC_NODE_ROOT, "%s", "%s", Constants.DISJOB_PROVIDERS, "%s"); 
	public static final String dataFormatCommon = "disJob://%s/%s?serverGroup=%s&phpFilePath=%s&className=%s&methodName=%s&version=1";
	public static final String dataFormat = "disJob://%s/%s?serverGroup=%s&phpFilePath=%s&className=%s&methodName=%s&cron=%s&version=1";
	/**
	 *  String data = "disJob://10.40.6.100:9501/%s?serverGroup=%s&phpFilePath=/usr/local/php-test/TestService.php&className=TestService&methodName=test1&version=1";
	 *  String path = "/disJob-dev/rpc/%s/%s/providers/10.40.6.100:9501";
	 */
	@Override
	public boolean addJob(Job job, String username) throws ZKNodeException2 {
		String jobName = job.getJobName();
		String groupName = job.getGroupName();
		String rpcJobPath = znodeApi.makePath(Constants.ROOT, Constants.DISJOB_RPC_NODE_ROOT,
				Constants.PATH_SEPARATOR + groupName, Constants.PATH_SEPARATOR + jobName);
		threadLocalClient.setAdminCuratorClient();
		CuratorFramework client = threadLocalClient.getCuratorClient().getCuratorClient();
		if(znodeApi.checkExists(client, rpcJobPath)){
			throw new DisJobFrameWorkException("任务已经存在, 任务组[" + groupName + "], 任务[" + jobName + "]");
		}
		checkJobBeforeUpdateAndSave(job);
		List<String> hosts = getHostByGroupName(client, groupName);
		if (!hosts.isEmpty()) {
			String jobPath = znodeApi.makePath(Constants.ROOT, Constants.APP_JOB_NODE_ROOT,
					Constants.PATH_SEPARATOR + groupName, Constants.PATH_SEPARATOR + jobName);
			String jobConfigPath = znodeApi.makePath(jobPath, Constants.APP_JOB_NODE_CONFIG);

			// root / job 节点处理
			String groupNodePath = znodeApi.makePath(Constants.ROOT, Constants.APP_JOB_NODE_ROOT, groupName);
			znodeApi.create(client, jobPath, new byte[0]);
			try {
				znodeApi.createPersistent(client, jobConfigPath, new Gson().toJson(job),
						client.getACL().forPath(groupNodePath));
			} catch (Exception e) {
				LoggerUtil.error("addJob with acl got an exception , job[" + job + "] ", e);
				return false;
			}
			// root / rpc 节点处理
			String filePath = job.getFilePath();
			String method = job.getMethodName();
			String className = job.getClassName();
			String cron = job.getCronExpression();
			for (String host : hosts) {
				String path = String.format(pathFormat, groupName, jobName, host);
				String data = String.format(dataFormat, host, jobName, groupName, filePath, className, method, cron);
				znodeApi.create(client, path, data);
			}
			return true;
		} else {
			throw new DisJobFrameWorkException(String.format("任务组 [%s] 绑定的会话中找不到可用的地址", groupName));
		}
	}

	private void checkJobBeforeUpdateAndSave(Job job) {
		boolean boardcast = job.isIfBroadcast();
		if (boardcast) {
			String parameters = job.getParameters();
			if (StringUtils.isNotEmpty(parameters) && parameters.contains(",")) {
				throw new DisJobFrameWorkException("广播模式的任务不支持分片式的任务参数,参数中不可以包含[,]符号");
			}
		}
	}
	
	private List<String> getHostByGroupName(CuratorFramework client, String groupName) {
		
		List<String> sessions = SlaveUtils.buildProjectSessionMap(client).get(groupName);
		List<String> hostList = Lists.newArrayList();
		if(sessions != null){
			for(String session : sessions){
				List<String> hostListBySession = ZKJobCache.sessionHostMap.get(session);
				hostList.addAll(hostListBySession);
			}
			if(hostList.isEmpty()){
				throw new DisJobFrameWorkException(String.format("任务组  [%s] 绑定的会话找不到url", groupName));
			}else{
				return hostList;
			}
		}else{
			throw new DisJobFrameWorkException(String.format("任务组  [%s] 没有绑定任何会话", groupName));
		}
	}

	@Override
	public boolean updateJob(Job job) {
		if (null == job) {
			LoggerUtil.warn("update job,job is null");
			return false;
		}
		LoggerUtil.info("begin update job is " + job.toString());
		CuratorFramework client = threadLocalClient.getCuratorClient().getCuratorClient();
		checkJobBeforeUpdateAndSave(job);
		return updateJob(client, job);
	}

	 
    /**
     * 暂停
     * @param job
     * @return
     */
	@Override
	public boolean suspendJob(Job job) {
		if (null == job) {
			LoggerUtil.warn("suspendJob job,job is null");
			return false;
		}
		LoggerUtil.info("begin update job is " + job.toString());
		try {
			CuratorFramework client = threadLocalClient.getCuratorClient().getCuratorClient();
			CuratorTransaction transaction = znodeApi.startTransaction(client);
			
			String group = job.getGroupName();
			String jobName = job.getJobName();
			Job stopJob = new Job();
			LoggerUtil.info("job be suspend, job is " + job.toString());
			String jobPath = znodeApi.makePath(Constants.ROOT,Constants.APP_JOB_NODE_ROOT, Constants.PATH_SEPARATOR+ group, Constants.PATH_SEPARATOR + jobName,Constants.APP_JOB_NODE_CONFIG);
			String data = znodeApi.getData(client, jobPath);
			if (StringUtils.isNoneBlank(data)) {
				stopJob = new Gson().fromJson(data, Job.class);
				// 虽然slave上的该job被移除了,/disJob/job的config记录了该job状态,config上的slaveip记录了是从哪台机器暂停remove的,故不改config的slaveip值
				String slaveIp = stopJob.getSlaveIp();
				String jobNode = znodeApi.makePath(Constants.ROOT,Constants.DISJOB_SERVER_NODE_ROOT,Constants.DISJOB_SERVER_NODE_SLAVE,Constants.PATH_SEPARATOR + slaveIp,Constants.DISJOB_SERVER_NODE_SLAVE_EXECUTION,Constants.PATH_SEPARATOR + group);
				String jobs = znodeApi.getData(client, jobNode);
				
				/**
				 * 1、先更改/disJob/job/config 下面的信息，然后通知slave节点重新装载job.这个时候 被暂停的job 不会装到quartz 里面去
				 * 2、得到 /slave/job上某group上移除该job值后得到的job值
				 * 3、如果group下的job的值为空串->单个job被移除,则直接remove掉该job节点
				 */
				stopJob.setJobStatus(3);
				stopJob.setSlaveIp(""); // 更改disJob/job的对应job节点的值
				CuratorTransactionFinal transactionFinal = SlaveUtils.updateJobNodeByTranstions(stopJob, client, transaction);
				
				if (StringUtils.isNoneBlank(jobs)) {
					String newJob = removeJobName(jobs, jobName);
					if ("".equals(newJob)) {
//						znodeApi.addDeleteToTransaction(transaction, jobNode);
						znodeApi.deleteByZnode(client, jobNode);
					}else {
//						znodeApi.addUpdateToTransaction(transaction, jobNode,newJob);
						znodeApi.update(client, jobNode, newJob);
					}
				}
//				znodeApi.commitTransaction(transactionFinal);
			}
			LoggerUtil.info("after update job is " + job.toString());
		} catch (RuntimeException e) {
			LoggerUtil.error("update updateJob error, job is " + job, e);
			return false;
		} catch (KeeperException.NoAuthException ke) {
			throw new ZKNodeException(ke);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;

	}

	/**
	 * 恢复
	 * @param job
	 * @return
	 */
	@Override
	public boolean resumeJob(Job job) {
		if( null == job){
			LoggerUtil.warn("resumeJob job,job is null");	
			return false;
		}
 		
		   
	      LoggerUtil.info("begin update job is "+job.toString());
	      try{
	         CuratorFramework client = threadLocalClient.getCuratorClient().getCuratorClient();
			 CuratorTransaction  transaction = znodeApi.startTransaction(client);

	         String group = job.getGroupName();
	         String jobName = job.getJobName();
             String jobPath = znodeApi.makePath(Constants.ROOT, Constants.APP_JOB_NODE_ROOT,Constants.PATH_SEPARATOR+group,Constants.PATH_SEPARATOR+jobName, Constants.APP_JOB_NODE_CONFIG);
             Job resumeJob = new Job();
	         String data =  znodeApi.getData(client, jobPath);
             if(StringUtils.isNotEmpty(data)){
               // Job stopJob = new Job();
            	resumeJob =  new Gson().fromJson(data, Job.class);
             }
   	         //更改激活状态的时候发布到/disJob/scheduler/slave上,不允许从激活到未激活,所以只要是status=0的就直接进入激活流程
 	        //恢复状态->给job分配一个slave
             LoggerUtil.info("job resume, job is "+job.toString());
             SlaveUtils.refreshSlaveIp(client);
             String slaveIp = com.huangyiming.disjob.slaver.utils.SlaveUtils.distributeSlave(client, group, jobName,transaction);
             //设置分配后ip
             resumeJob.setSlaveIp(slaveIp);
             //恢复则把job状态设为1
             resumeJob.setJobStatus(1);
 	         //更改disJob/job的对应job节点的值
             CuratorTransactionFinal transactionFinal = SlaveUtils.updateJobNodeByTranstions(resumeJob, client, transaction);
             com.huangyiming.disjob.slaver.utils.SlaveUtils.updateSlaveExecution(client, group, jobName, transactionFinal, resumeJob.getSlaveIp());
//             znodeApi.commitTransaction(transactionFinal);
	         
	         LoggerUtil.info("after update job is "+job.toString());
	         
	        } catch (Exception e) {
	            LoggerUtil.error("update updateJob error, job is "+job,e);
	            return false;
 			}
	        return true;
	    
	}
	
	 
	public boolean createGroup(JobGroup jobGroup, String username){
		String groupName = jobGroup.getGroupName();
		threadLocalClient.setAdminCuratorClient();
		CuratorFramework client = threadLocalClient.getCuratorClient().getCuratorClient();
		ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
		String newGroupPath = ZKPaths.makePath(Constants.ROOT, Constants.DISJOB_RPC_NODE_ROOT, groupName);
		if(znode.checkExists(client, newGroupPath)){
			throw new DisJobFrameWorkException("任务组已经存在 " + groupName);
		}else{
			authService.assign(username, groupName, AuthConstants.OWNER);
			try {
				znode.create(client, newGroupPath, (Object)null);				
			} catch (Exception e) {
				authService.unAssign(username, groupName, AuthConstants.OWNER);
				throw new DisJobFrameWorkException("任务创建失败 ," + e.getMessage());
			}
			return true;
		}
	}
	

    @Override
    public List<String> getAllGroup()
    {
        CuratorFramework client = threadLocalClient.getCuratorClient().getCuratorClient();
        ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
        List<String> currentChilds = znode.getChildren(client, Constants.ROOT+Constants.DISJOB_RPC_NODE_ROOT);
        List<String> result = new ArrayList<String>();
        if(CollectionUtils.isNotEmpty(currentChilds)){
             for(String str : currentChilds){
                 result.add(str);
             }
        }
        return result;
     }
    
    @Override
    public List<String> getAllGroup(DBUser dbUser)
    {
    	CuratorFramework client = threadLocalClient.getCuratorClient().getCuratorClient();
    	ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
    	String groupPath;
    	if("超级管理员".equals(dbUser.getRoleName()) || "访客".equals(dbUser.getRoleName())){
    		groupPath = Constants.ROOT + Constants.APP_JOB_NODE_ROOT;
    	}else{
    		groupPath = ZKPaths.makePath(AuthConstants.userRootPath, dbUser.getUsername());
    	}
    	List<String> result = new ArrayList<String>();
    	if(znode.checkExists(client, groupPath)){
    		List<String> currentChilds = znode.getChildren(client, groupPath);
    		if(CollectionUtils.isNotEmpty(currentChilds)){
    			for(String str : currentChilds){
    				result.add(str);
    			}
    		}
    	}
    	return result;
    }

    @Override
    public List<Job> getJobListByGroup(String groupName)
    {
        CuratorFramework client = threadLocalClient.getCuratorClient().getCuratorClient();
       ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
       if(!StringUtils.isNoneEmpty(groupName)){
       	return null;
       }
       List<String> currentChilds = znode.getChildren(client, Constants.ROOT+Constants.DISJOB_RPC_NODE_ROOT+Constants.PATH_SEPARATOR+groupName);
       List<Job> result = new ArrayList<Job>();
       if(CollectionUtils.isNotEmpty(currentChilds)){
           for(String jobName:currentChilds){
               String data =  znode.getData(client, Constants.ROOT+Constants.APP_JOB_NODE_ROOT+Constants.PATH_SEPARATOR+groupName+Constants.PATH_SEPARATOR+jobName+Constants.APP_JOB_NODE_CONFIG);
               if(StringUtils.isNotEmpty(data)){
                   Job  job =  new Gson().fromJson(data, Job.class);
                   result.add(job);
               }
           }
       }
       return result;
    }

    @Override
	public Job getJobByGroupAndJobName(String groupName, String jobName) {
		CuratorFramework client = threadLocalClient.getCuratorClient().getCuratorClient();
		ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
		if (!StringUtils.isNoneEmpty(groupName)) {
			return null;
		}
		List<String> currentChilds = znode.getChildren(client, Constants.ROOT + Constants.DISJOB_RPC_NODE_ROOT + Constants.PATH_SEPARATOR + groupName);
		Job result = null;
		if (CollectionUtils.isNotEmpty(currentChilds)) {
			for (String tmJob : currentChilds) {
				if (tmJob.equals(jobName)) {
					String data = znode.getData(client, Constants.ROOT + Constants.APP_JOB_NODE_ROOT + Constants.PATH_SEPARATOR + groupName + Constants.PATH_SEPARATOR + tmJob + Constants.APP_JOB_NODE_CONFIG);
					if (StringUtils.isNotEmpty(data)) {
						Job job = new Gson().fromJson(data, Job.class);
						if (StringUtils.isEmpty(job.getFilePath())) {
							String node = znodeApi.makePath(Constants.ROOT, Constants.DISJOB_RPC_NODE_ROOT, groupName);
							String rpcNodePath = znodeApi.makePath(node, jobName, Constants.DISJOB_PROVIDERS);
							if (znodeApi.checkExists(client, rpcNodePath)) {
								if (znodeApi.getChildren(client, rpcNodePath).size() != 0) {
									String hostNodeName = znodeApi.getChildren(client, rpcNodePath).get(0);
									String hostPath = znodeApi.makePath(rpcNodePath, hostNodeName);
									String disJobRPCHostdata = znodeApi.getData(client, hostPath);

									fillJobData(disJobRPCHostdata, job);
								}
							}
						}
						result = job;
						return result;
					}
				}
			}
		}
		return result;

	}
    
    public void fillJobData(String rpcString, Job job){
    	try {
    		String[] rpcArray = rpcString.split("&");
    		String slaveIp = rpcArray[0].split("\\//")[1].split("/")[0];
//    		job.setSlaveIp(slaveIp);
    		String[] rpcInfo = Arrays.copyOfRange(rpcArray, 1, rpcArray.length-1);
    		for(String temp : rpcInfo){
    			String[] ele = temp.split("=");
    			if(ele.length == 2){
    				String eleValue = ele[1];
    				switch (ele[0]) {
    				case "phpFilePath":
    					job.setFilePath(eleValue);
    					break;
    				case "className":
    					job.setClassName(eleValue);
    					break;
    				case "methodName":
    					job.setMethodName(eleValue);
    				default:
    					break;
    				} 
    			}
    		}
		} catch (Exception e) {
			LoggerUtil.error("识别到该job缺少filePath, 从rpc/provider解析相关数据时异常 :" + e);
			throw new DisJobFrameWorkException("识别到该job缺少filePath, 从rpc/provider解析相关数据时异常 :" + e);
		}
    }
    
    /**
     * 根据节点上job的列表字符串移除某jobname后返回job节点字符串
     * @param jobName
     * @param targetJob
     * @return
     */
    private   static String removeJobName(String jobName,String targetJob){
    	if(!StringUtils.isNoneBlank(targetJob)){
    		throw new RuntimeException("need to be remove job is blank,and jobs is "+jobName);
    	}
        String[] array = jobName.split(com.huangyiming.disjob.common.Constants.TRANSFER_CHAR+Constants.JOB_SEPARATOR);
        List<String> list = Arrays.asList(array);
        ArrayList<String> tempList =new ArrayList<String>(list);
        int index = tempList.indexOf(targetJob);
        if(index >-1){
         	tempList.remove(index);
        }
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < tempList.size(); i++)
        { 
            sb.append(tempList.get(i).trim()+Constants.JOB_SEPARATOR);
        }
        String newStr = sb.toString();
        if(StringUtils.isNoneEmpty(newStr)){
             return newStr.substring(0, newStr.length() - 1);
        }
        return "";
         
    }

    /**
     * 1.清除/disJob/schedule/slave/ip下清除所有job
     * 2.根据/disJob/job/group/job/config分配job
     * 
     */
	@Override
    public int averageDistributeSlaveJob() throws Exception {
		
        CuratorFramework client = threadLocalClient.getCuratorClient().getCuratorClient();
        ConcurrentHashSet<DisJobServerInfo> availableIps =  com.huangyiming.disjob.slaver.utils.SlaveUtils.refreshSlaveIp(client);
        if(availableIps.size() < 2 ){ //集群中可用机器少于2台,拒绝job分配
        	return com.huangyiming.disjob.register.job.JobOperatorStatus.WARN.getValue();
        }
        CuratorTransaction transaction = client.inTransaction();
 		SlaveUtils.clearAllClustersJobs(client,transaction);
 		transaction = client.inTransaction();
  		SlaveUtils.distributeAllJobs(client);
		return com.huangyiming.disjob.register.job.JobOperatorStatus.SUCCESS.getValue();
 	}
	
	@Override
	public PageResultAndCategories getJobListByGroupAndCategory(String groupName, String category) {
		SortedSet<String> ssCategories = new TreeSet<>(); 
		PageResultAndCategories pac = new PageResultAndCategories();
		CuratorFramework client = threadLocalClient.getCuratorClient().getCuratorClient();
		ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
		if (!StringUtils.isNoneEmpty(groupName)) {
			return null;
		} 
		List<String> currentChilds = znode.getChildren(client,
				Constants.ROOT + Constants.APP_JOB_NODE_ROOT + Constants.PATH_SEPARATOR + groupName);
		List<Job> result = new ArrayList<Job>();
		if (CollectionUtils.isNotEmpty(currentChilds)) {
			for (String jobName : currentChilds) {
				
				Job job = znode.getData(client, Constants.ROOT + Constants.APP_JOB_NODE_ROOT + Constants.PATH_SEPARATOR + groupName
								+ Constants.PATH_SEPARATOR + jobName + Constants.APP_JOB_NODE_CONFIG, Job.class);
				if (job != null) {
					String jobCategory = job.getCategory();
					if(!StringUtils.isEmpty(jobCategory)){
						ssCategories.add(jobCategory.trim());
					}
					if ("all".equals(category) || StringUtils.isEmpty(category) || category.equals(jobCategory)) {
						result.add(job);
					}
				}
			}
		}
		pac.setRows(result);
		pac.setTotal(result.size());
		pac.setCategories(ssCategories);
		return pac;
	}

	/**
	 * 在页面端修改job走此方法
	 */
	@Override
	public boolean updateJob(CuratorFramework client, Job job) {
		try {
			ZnodeApiCuratorImpl nodeApi = new ZnodeApiCuratorImpl();
			CuratorTransaction transaction = nodeApi.startTransaction(client);
			String group = job.getGroupName();
			String jobName = job.getJobName();
			String jobPath = znodeApi.makePath(Constants.ROOT,Constants.APP_JOB_NODE_ROOT, Constants.PATH_SEPARATOR+ group, Constants.PATH_SEPARATOR + jobName,Constants.APP_JOB_NODE_CONFIG);
			String data = znodeApi.getData(client, jobPath);
			Job zkJob = new Job();
			if (StringUtils.isNotEmpty(data)) {
				zkJob = new Gson().fromJson(data, Job.class);
			}
			boolean reloadToQuartz = false ;
			/**
			 * 1、更改激活状态的时候发布到/disJob/scheduler/slave上,不允许从激活到未激活,所以只要是status=0
			 * 的就直接进入激活流程 2、如果corn表达式为空且是未激活状态,则进行激活处理
			 */
			if (job.getJobStatus() == 0 && StringUtils.isNotEmpty(job.getCronExpression())) {
				LoggerUtil.info("job be activity, job is " + job.toString());
				job.setJobStatus(1);// 改为可运行
				SlaveUtils.refreshSlaveIp(client);
				String slaveIp = com.huangyiming.disjob.slaver.utils.SlaveUtils.distributeSlave(client, group, jobName, transaction);
				job.setSlaveIp(slaveIp);
				reloadToQuartz = true ;
			}
			//先更新 config 中的信息,再去读
			CuratorTransactionFinal transactionFinal = SlaveUtils.updateJobNodeByTranstions(job, client, transaction);
			
			
			//通知slave 节点 将job 重新装到quartz 里面去
			synchronized (this.getClass()) {
				if(reloadToQuartz){
					com.huangyiming.disjob.slaver.utils.SlaveUtils.updateSlaveExecution(client, group, jobName, transactionFinal, job.getSlaveIp());
				}
				
				if(StringUtils.isEmpty(zkJob.getSlaveIp())){
					zkJob.setSlaveIp(job.getSlaveIp());
				}
				if(reloadToQuartz || checkUpdateQuartz(job,zkJob)) {
					String groupNode = nodeApi.makePath(Constants.ROOT,Constants.DISJOB_SERVER_NODE_ROOT,Constants.DISJOB_SERVER_NODE_SLAVE,
							Constants.PATH_SEPARATOR + zkJob.getSlaveIp(),Constants.DISJOB_SERVER_NODE_SLAVE_EXECUTION,Constants.PATH_SEPARATOR + group);
					LoggerUtil.info("cornexpress has been update ,so update "+ groupNode);
					String jobs = nodeApi.getData(client, groupNode);
//				nodeApi.addUpdateToTransaction(transaction, groupNode, jobs);
					znodeApi.update(client, groupNode, jobs);
				}
			}

//			znodeApi.commitTransaction(transactionFinal);				
			LoggerUtil.info("after update job is " + job.toString());
		} catch (RuntimeException e) {
			LoggerUtil.error("update updateJob error, job is " + job, e);
			return false;
		}catch(KeeperException.NoAuthException ke){
	    	throw new ZKNodeException(ke);
	    } catch (Exception e) {
			LoggerUtil.error("update updateJob error, job is " + job, e);
			return false;
		}
		
		return true ;
	}
	
	/**
	 * 检测是否需要重新装载到quartz 
	 * @param targetJob 最新的 job 参数
	 * @param zkJob 在zk 上面的参数
	 * @return
	 */
	private boolean checkUpdateQuartz(Job targetJob,Job zkJob){
		if(zkJob.getJobStatus() == 3){
			return false; //暂停状态的job
		}
		String targetCronExpre = targetJob.getCronExpression();
		//1、cron 表达式改了，重新装载 到 quartz中
		if(StringUtils.isNoneEmpty(targetCronExpre) && !targetCronExpre.equalsIgnoreCase(zkJob.getCronExpression())){
			return true ;
		}
		
		//2、修改了最后一次触发的时间
		if(StringUtils.isNoneEmpty(zkJob.getLastFireTime()) && !zkJob.getLastFireTime().equalsIgnoreCase(targetJob.getLastFireTime())){
			return true ;
		}
		
		//3、结束时间也修改了
		if(StringUtils.isNoneEmpty(zkJob.getEndTime()) && !zkJob.getEndTime().equalsIgnoreCase(targetJob.getEndTime())){
			return true ;
		}
		
		//4 是否广播模式修改了
		if(zkJob.isIfBroadcast() != targetJob.isIfBroadcast()){
			return true;
		}
		return false ;
	}
	
	@Override
	public void fireNow(String groupName, String jobName) {
		CuratorFramework client = threadLocalClient.getCuratorClient().getCuratorClient();
		String jobPath = znodeApi.makePath(Constants.ROOT, Constants.APP_JOB_NODE_ROOT,Constants.PATH_SEPARATOR+groupName,Constants.PATH_SEPARATOR+jobName, Constants.APP_JOB_NODE_CONFIG);
		Job job = znodeApi.getData(client, jobPath, Job.class);
        JobFireFactory jobFactory = new JobFireFactory();
        jobFactory.now(job);
	}

	/**
	 * 在页面端新添加一个job走此方法
	 */
	@Override
	public boolean saveJob(Job job) {
           CuratorFramework client = threadLocalClient.getCuratorClient().getCuratorClient();
 		   //页面注册的job属性
		   //Job job = new Job("10.40.6.89", 9501, "testbang1", "test2020", "/usr/local/php-test/TestService.php", "TestService", "test","0/20 * * * * ?");
 		   ZnodeApiCuratorImpl znodeApi = new ZnodeApiCuratorImpl();
		   String rpcPath = znodeApi.makePath(Constants.ROOT, Constants.DISJOB_RPC_NODE_ROOT,Constants.PATH_SEPARATOR+job.getGroupName(),Constants.PATH_SEPARATOR+job.getJobName(), Constants.PATH_SEPARATOR+Constants.DISJOB_PROVIDERS);
		   ifrefreshCache(client);
		   
		   ConcurrentHashMap<String, List<String>> projectSessionMap =  SlaveUtils.buildProjectSessionMap(client);
 		   List<String> sessionList  = projectSessionMap.get(job.getGroupName());
 		   //循环得到组对应的会话列表
		   for(String session : sessionList){
			    List<String> hosts = ZKJobCache.sessionHostMap.get(session);
			    //循环会话对应的iphost列表
	 		    if(CollectionUtils.isNotEmpty(hosts)){
			    	for(String host:hosts){
 	    		    	String rpcNode = znodeApi.makePath(rpcPath, host);
	    		    	if(StringUtils.isNotEmpty(host)){
	    		    		String [] array = host.split(":");
	    		    		String tmpHost = array[0];
	    		    		int tmpPort = Integer.parseInt(array[1]);
	    		    		job.setHost(tmpHost);
	    		    		job.setPort(tmpPort);
	    		    	}
	    		    	if(znodeApi.checkExists(client, rpcNode)){
	    		    		znodeApi.update(client, rpcNode, SlaveUtils.buildRpcUrl(job));
	    		    	}else{
	    		    		znodeApi.createPersistent(client, rpcNode, SlaveUtils.buildRpcUrl(job));
	    		    	}
	    		    }
			    }
 		   }
		    
		String jobPath = znodeApi.makePath(Constants.ROOT, Constants.APP_JOB_NODE_ROOT,Constants.PATH_SEPARATOR+job.getGroupName(),Constants.PATH_SEPARATOR+job.getJobName(), Constants.APP_JOB_NODE_CONFIG);
        //String data =  znodeApi.getData(client, jobPath);
   	    String json = new Gson().toJson(job);
        if(znodeApi.checkExists(client, jobPath)){
        	znodeApi.update(client, jobPath, json);
        }else{
        	znodeApi.createPersistent(client, jobPath, json);
        }
        //设置为未激活又有cron表达式会自动被分配
        job.setJobStatus(0);
        updateJob(job);
		return false;
	}

	/**
	 * 新绑定会话和组
	 */
	@Override
	public boolean bindSessionAndGroup(String sessionName, String groupName) {
		boolean flag = false;
		CuratorFramework client = threadLocalClient.getCuratorClient().getCuratorClient();
 		String path = znodeApi.makePath(Constants.DISJOB_SERVER_NODE_PROJECT, Constants.PATH_SEPARATOR+sessionName, Constants.PATH_SEPARATOR+groupName);
		if(!znodeApi.checkExists(client, path)){
			znodeApi.createPersistent(client, path, null);
		}
		
    	ConcurrentHashMap<String, List<String>> projectSessionMap =  SlaveUtils.buildProjectSessionMap(client);
     	  
      	 
 		String node = znodeApi.makePath(Constants.ROOT, Constants.DISJOB_RPC_NODE_ROOT, Constants.PATH_SEPARATOR+groupName);	  
		 
		/**
		 * 1.根据group对应的sessionList得到对应的hostIP列表	
		 * 2.根据group下找到所有job，每个job添加该hostIp节点,而rpcurl的值则根据该job其他的rpcurl而修改，只是hostip改为新增加的hostip
		 */
 		List<String > sessionList = projectSessionMap.get(groupName);
 		if(CollectionUtils.isNotEmpty(sessionList)){
 			for(String session : sessionList){
 				//得到session对应iphosts
 				List<String> hosts = ZKJobCache.sessionHostMap.get(session);
 				if(CollectionUtils.isNotEmpty(hosts)){
 					
  					for(String ipHost : hosts){
    					List<String> jobNameList = SlaveUtils.getJobNameListByGroup(client, groupName);
  						if(CollectionUtils.isNotEmpty(jobNameList)){
   							if(CollectionUtils.isNotEmpty(jobNameList)){
  								for(String jobName : jobNameList){
  									List<String> rpcList = new ArrayList<String>();
									try {
										rpcList = subscribeService.getProvidesByService(groupName, jobName);
									} catch (Exception e) {
										LoggerUtil.error("bindSessionAndGroup get getProvidesByService error ,groupName="+groupName+",jobName="+jobName,e);
									}
									//根据group,jobname添加新的iphost,iphost对应的rpc地址的值则根据job之前的值来替换.最后把新得到的rpcurl添加到节点上
  									if(rpcList !=null && rpcList.size() >0){
  										String rocUrl = rpcList.get(0);
   											String tmp = rocUrl.split("\\//")[1].split("\\/")[0];
  											String newRpcUrl = rocUrl.replaceFirst(tmp, ipHost);
  											String rpcNodePath = znodeApi.makePath(node, Constants.PATH_SEPARATOR+jobName, Constants.PATH_SEPARATOR+Constants.DISJOB_PROVIDERS,Constants.PATH_SEPARATOR+ipHost);		
  											if(!znodeApi.checkExists(client, rpcNodePath)){
  			  									znodeApi.createPersistent(client, rpcNodePath, newRpcUrl);
  			  								}
   									}else{
   										SlaveUtils.buildRpcUrlByParams(groupName, client, ipHost, jobName);
   									}
  								}
  							}
  						}
 					}
 				}
 			}
 		}
    	flag = true;
		return flag;
	}

 
	
	public static void main(String[] args) {
		 String str ="disJob://10.40.6.89:9501/test300?serverGroup=oms100test2&phpFilePath=/usr/local/php-test/TestService.php&className=TestService&methodName=test&version=1";
		 System.out.println(str.split("\\//")[1].split("\\/")[0]);
		 String str1 = str.split("\\//")[1].split("\\/")[0];
		 System.out.println(str.replaceFirst(str1, "10.40.6.100:9502"));
		 
	}
	 
	
	/**
	 * 修改绑定会话和组
	 */
	@Override
	public boolean bindSessionAndGroup(String sessionName, String unaviableGroup ,String newGroup) {
		CuratorFramework client = threadLocalClient.getCuratorClient().getCuratorClient();
		if(unaviableGroup.equals(newGroup)){
			return true;
		}
		deleteUnAvaliableRpcUrls(unaviableGroup, client);
		//移除session与group的关系
		String path = znodeApi.makePath(Constants.DISJOB_SERVER_NODE_PROJECT, Constants.PATH_SEPARATOR+sessionName, Constants.PATH_SEPARATOR+unaviableGroup);
		znodeApi.deleteByZnode(client, path);
		bindSessionAndGroup(sessionName, newGroup);
		return true;
  	}

	private void removeBindGroupInfo(String jobGroup){
		CuratorFramework client = threadLocalClient.getCuratorClient().getCuratorClient();
		deleteUnAvaliableRpcUrls(jobGroup, client);
		List<String> sessions = znodeApi.getChildren(client, Constants.DISJOB_SERVER_NODE_SESSION);
		for(String session : sessions){
			String sessionGroupPath = ZKPaths.makePath(Constants.DISJOB_SERVER_NODE_PROJECT, session, jobGroup);
			if(znodeApi.checkExists(client, sessionGroupPath)){
				znodeApi.deleteByZnode(client, sessionGroupPath);				
			}
		}
	}
	/**
	 * 根据组名找到对应不可用的providers的rpc节点并移除
	 * @param oldGroup
	 * @param client
	 */
	private void deleteUnAvaliableRpcUrls(String oldGroup,
			CuratorFramework client) {
		List<String> nodeList = new ArrayList<String>();
    	ConcurrentHashMap<String, List<String>> projectSessionMap =  SlaveUtils.buildProjectSessionMap(client);

		List<String> sessions = projectSessionMap.get(oldGroup);
		if(CollectionUtils.isNotEmpty(sessions)){
			for(String session : sessions){
				List<String> hosts = ZKJobCache.sessionHostMap.get(session);
				if(CollectionUtils.isNotEmpty(hosts)){
					for(String ipPort : hosts){
						nodeList.addAll(SlaveUtils.getProviderUrlNodeByGroupAndHost(client, oldGroup, ipPort));
					}
				}
			}
			 SlaveUtils.deleteNodeList(client, nodeList);
			 
 		}
	}
 
	/**
	 * 如果缓存为空则全量刷缓存
	 * @param client
	 */
	private void ifrefreshCache(CuratorFramework client){
		 
		if(ZKJobCache.sessionHostMap.size() == 0){
			SlaveUtils.buildSessionHostMap(client);
 		}
	}

	@Override
	public void bindJob(List<String> sessions, List<String> groupNames) {
		if(CollectionUtils.isNotEmpty(sessions) && CollectionUtils.isNotEmpty(groupNames)){
			for(String session : sessions){
				for(String groupName : groupNames){
					bindSessionAndGroup(session, groupName);
				}
			}
		}
		
	}

	@Override
	public List<JobGroup> getAllJobGroupForPageList() {
		List<JobGroup> jobGroups = Lists.newArrayList();
		CuratorFramework client = threadLocalClient.getCuratorClient().getCuratorClient();
        ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
        List<String> groupList = znode.getChildren(client, Constants.ROOT+Constants.DISJOB_RPC_NODE_ROOT);
        if(CollectionUtils.isNotEmpty(groupList)){
             for(String groupName : groupList){
            	 JobGroup jobGroup = new JobGroup(groupName);
            	 
            	 List<String> sessionList = znode.getChildren(client, Constants.DISJOB_SERVER_NODE_PROJECT);
            	 for(String sessionName : sessionList){
            		 String publishSessionGroupPath = ZKPaths.makePath(Constants.DISJOB_SERVER_NODE_PROJECT, sessionName, groupName);
            		 if(znode.checkExists(client, publishSessionGroupPath)){
            			 jobGroup.addBindSession(sessionName);
            			 jobGroup.setBinded(true);
            		 }
            	 }
            	 jobGroups.add(jobGroup);
             }
        }
        return jobGroups;
	}

	@Override
	public void reBindJob(List<String> sessions, List<String> groupNames) {
		for(String groupName : groupNames){
			removeBindGroupInfo(groupName);
			for(String session : sessions){
				bindSessionAndGroup(session, groupName);
			}
		}
	}
}
