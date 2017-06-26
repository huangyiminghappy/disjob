package com.huangyiming.disjob.register.core.jobs;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;

import com.huangyiming.disjob.common.exception.DisJobFrameWorkException;
import com.huangyiming.disjob.common.util.DateUtil;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.common.util.UUIDHexGenerator;
import com.huangyiming.disjob.monitor.rms.CommonRMSMonitor;
import com.huangyiming.disjob.monitor.rms.MonitorType;
import com.huangyiming.disjob.monitor.util.MonitorSpringWorkFactory;
import com.huangyiming.disjob.register.core.service.JobExecutedThreadPoolService;
import com.huangyiming.disjob.register.core.sharing.JobShardingStrategy;
import com.huangyiming.disjob.register.core.sharing.JobShardingStrategyFactory;
import com.huangyiming.disjob.register.core.sharing.JobShardingStrategyOption;
import com.huangyiming.disjob.register.core.util.CoreConstants;
import com.huangyiming.disjob.register.core.util.RegisterSpringWorkFactory;
import com.huangyiming.disjob.register.rpc.SubscribeService;
import com.huangyiming.disjob.rpc.client.HURL;
import com.huangyiming.disjob.rpc.cluster.loadbalance.RoundRobinLoadBalance;
import com.huangyiming.disjob.slaver.utils.SharingItem2StrategyOption;
import com.huangyiming.disjob.monitor.db.domain.DBJobBasicInfo;

/**
 * <pre>
 * 
 *  File: AbstractJobFactory.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  添加了业务逻辑实现方法，由于有状态和无状态的区别就是有无@DisallowConcurrentExecution注解，
 *  所有只需要在job的execute方法中调用该executeJobInternal方法即可
 * 
 *  Revision History
 *
 *  Date：		2016年6月15日
 *  Author：		Disjob
 *
 * </pre>
 */
public abstract class AbstractJobFactory implements Job{
	
 	
	protected void executeJobInternal(JobDataMap map){
		String groupName = (String)map.get(CoreConstants.GROUP_NAME);
        String jobName = (String)map.get(CoreConstants.JOB_NAME);
        String parameters = (String)map.get(CoreConstants.JOB_PARAMS);
        boolean isBroadcast = map.getBoolean(CoreConstants.IS_BROADCAST);
        com.huangyiming.disjob.register.domain.Job job = (com.huangyiming.disjob.register.domain.Job) map.get(CoreConstants.JOB_CONFIG);
       
        boolean isCanScheduler = checkCanScheduler(job);
        
        if(isCanScheduler){
	        try {
	        	processJob(groupName, jobName, parameters, job, isBroadcast);			
			} catch (DisJobFrameWorkException e) {
				LoggerUtil.error(e.getMessage());
			}
        }
	}

	private boolean checkCanScheduler(com.huangyiming.disjob.register.domain.Job job) {
		boolean isCanScheduler = true ;
        if(StringUtils.isNoneEmpty(job.getEndTime())){//1、处理有无 结束时间的
         	long endTime = DateUtil.parse(job.getEndTime()).getTime();
         	isCanScheduler = System.currentTimeMillis() < endTime ;
        }
        
        if(!isCanScheduler){//job 的结束时间到了，就终止下面的判断，直接返回 false 即可
        	LoggerUtil.info("[ DELETE JOB ]"+job.toString()+" 的结束时间到了。从 quartz 中移除");
        	RegisterSpringWorkFactory.getGeneralSchedulerService().delete(job.getJobName(),job.getGroupName());
        	return isCanScheduler;
        }
        
        long timeOut = (job.getTimeOut()>0 ? job.getTimeOut():1) * 1000;//毫秒
        DBJobBasicInfo JobSchedulerInfo = MonitorSpringWorkFactory.getDBJobBasicInfoService().getTheLeastScheduler(job.getGroupName(), job.getJobName());
        if(StringUtils.isNoneEmpty(job.getFilePath())&&JobSchedulerInfo!=null&&StringUtils.isEmpty(JobSchedulerInfo.getExecuteEnd())){//2、处理调度job没有返回结束时间，3倍的tiem out 时间内不能立即执行
        	LoggerUtil.info("file path:"+ job.getFilePath()+"; class name:"+job.getClassName()+"; method:"+job.getMethodName() +" ;group:"+job.getGroupName()+"; job name:"+job.getJobName()+" 没有结束时间，等待3 倍的 timeout["+job.getTimeOut()+"] 后继续调度。");
        	Date schedulerTime = DateUtil.parse(JobSchedulerInfo.getScheduleStart());
        	isCanScheduler = (System.currentTimeMillis()-schedulerTime.getTime())/timeOut > 2;
        }
        
        //
		return isCanScheduler;
	}

	protected void processJob(String groupName, String jobName, String parameters,com.huangyiming.disjob.register.domain.Job job, boolean isBroadcast) {
		job.setGroupName(groupName);
        job.setJobName(jobName);
        LoggerUtil.info("process job:"+groupName +","+jobName+ " running "+ new Date());
       
        List<HURL> urlLst = SubscribeService.getHURListProvidesByService(groupName, jobName);
        LoggerUtil.trace("exe job :" + job + " | urlList :" + urlLst + " | parameters : " + parameters + " | date:" + new Date());
        if(CollectionUtils.isEmpty(urlLst)){
        	CommonRMSMonitor.sendBusiness(MonitorType.Business.JOB_RPC_LIST_EMPTY, groupName+"_"+jobName+" 任务可用地址为空", groupName, jobName);
        	throw new DisJobFrameWorkException("get rpc List<HURL> is null , job not executed");
        }
         // String param = "ip=192.168.1.1:name=123&ip=192.168.1.1&age=13,123:name=123&ip=192.168.2.1&age=13,456:name=456&ip=192.168.2.1&age=13,79796416:name=456&ip=192.168.3.1&age=13";
        if(StringUtils.isNotEmpty(parameters)){
        	sharingProcessRequest(job, urlLst, isBroadcast);
        }else{
        	processRequest(job, urlLst, isBroadcast);
        }
	}

	private void processRequest(com.huangyiming.disjob.register.domain.Job job,List<HURL> urlLst, boolean isBroadcast) {
		if(isBroadcast){
			for(HURL hurl : urlLst){
				RegisterSpringWorkFactory.getJobExecutedThreadPoolService().submit(job, hurl,job.getParameters(), "");
			}
		}else{
			HURL hurl =  new RoundRobinLoadBalance(urlLst).select();
			
			if(hurl == null){
				throw new DisJobFrameWorkException("no hurl for the job, groupName:" + job.getGroupName() + " jobName:" + job.getJobName() + " ,job not executed");
			}
			RegisterSpringWorkFactory.getJobExecutedThreadPoolService().submit(job, hurl,job.getParameters(), "");
		}
	}

	private void sharingProcessRequest(com.huangyiming.disjob.register.domain.Job job,List<HURL> urlLst, boolean isBroadcast) {
		JobShardingStrategyOption option = SharingItem2StrategyOption.convert2StrategyOption(job.getJobName(), job.getParameters(),urlLst);
		//目前只有一种策略所以传空
         JobShardingStrategy strategy = JobShardingStrategyFactory.getStrategy("");
         Map<HURL, List<String>> hurlParam = strategy.sharding(urlLst, option);
          if(hurlParam !=null && hurlParam.size() >0){
        	  JobExecutedThreadPoolService jobExecutedThreadPoolService = RegisterSpringWorkFactory.getJobExecutedThreadPoolService();
        	  for(Map.Entry<HURL, List<String>> entry: hurlParam.entrySet()){
        		  HURL hurl = entry.getKey();
        		  List<String> list = entry.getValue();
        		   if(CollectionUtils.isNotEmpty(list)){
        			   String sharingRequestId = UUIDHexGenerator.generate();
        			   for(String requestParam : list){
        				   System.out.println(hurl.getHurlKey() +",param:"+requestParam);
           				   jobExecutedThreadPoolService.submit(job, hurl, requestParam,sharingRequestId);
        			   }
        		   }
        	  }
          }else{
        	  //格式不符合规范,相当于没设置分片
        	  processRequest(job, urlLst, isBroadcast);
          }
	}
	
	public static void main(String[] args) {
		boolean f1 = true ;
		System.out.println(true | f1);
	}

	@SuppressWarnings("unused")
	private static void test_1() {
		Map<String, String> map = new HashMap<String, String>();
        String param = "192.168.1.1:name=123&ip=192.168.1.1&age=13,123:name=123&ip=192.168.2.1&age=13,456:name=456&ip=192.168.2.1&age=13,79796416:name=456&ip=192.168.3.1&age=13";
        String[] array = param.split(",");
        for(String str:array){
        	String[] array1 = str.split(":");
        	 
        		map.put(array1[0], array1[1]);
        		
        	 
        }
        for(Map.Entry<String, String> entry:map.entrySet()){
        	System.out.println("key:"+entry.getKey()+",value:"+entry.getValue());
        }
        
        List<HURL> urlLst = new ArrayList<HURL>();
       HURL test1 =  new HURL("group1", "host1", "job1");
       HURL test3 =  	   new HURL("group2", "host2", "job2");
       HURL test4 =  	   new HURL("group3", "host3", "job3");
       HURL test2 =  	   new HURL("group22", "host22", "job22");
       test1.setPhpFilePath("11");
       test2.setPhpFilePath("22");

       test3.setPhpFilePath("33");

       test4.setPhpFilePath("44");

        urlLst.add(test1);
        urlLst.add(test2);
        urlLst.add(test3);
        urlLst.add(test4);

        for(int i=0;i<100;i++){
        	
        	HURL hurl =  new RoundRobinLoadBalance(urlLst).select();
        	 System.out.println(hurl.getServerGroup());
        }
	}
}
