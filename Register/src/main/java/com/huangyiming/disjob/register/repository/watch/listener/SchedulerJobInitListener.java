package com.huangyiming.disjob.register.repository.watch.listener;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.utils.ZKPaths;
import org.quartz.JobKey;
import org.quartz.SchedulerException;

import com.huangyiming.disjob.common.Constants;
import com.huangyiming.disjob.common.model.JobInfo;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.register.core.jobs.StatelessJobFactory;
import com.huangyiming.disjob.register.core.service.GeneralSchedulerService;
import com.huangyiming.disjob.register.repository.ZnodeApi;

/**
 * PathChildrenCache监听的listener 
 * 监听路径：/disJob/scheduler/slave/ip/execution
 * 目的：如果路径的子节点发生变更，说明disJob slave server的job发生变更 在这里要动态对quartz
 * scheduler中的job进行更新，更新必须实时
 * 
 * @author Disjob
 */
public class SchedulerJobInitListener implements PathChildrenCacheListener {

	private GeneralSchedulerService generalSchedulerService;

	private ZnodeApi nodeApi;

	private CuratorFramework client;

	public SchedulerJobInitListener(CuratorFramework client, GeneralSchedulerService generalSchedulerService, ZnodeApi nodeApi) {
		this.client = client;
		this.generalSchedulerService = generalSchedulerService;
		this.nodeApi = nodeApi;
	}

	@Override
	public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) {
		String groupPath = event.getData().getPath();
		 
		synchronized (SchedulerJobInitListener.class) {
			String groupNode = ZKPaths.getNodeFromPath(event.getData().getPath());
			String jobNameStr = nodeApi.getData(client, groupPath);
			String [] jobNameArray = {};
			
			switch (event.getType()) {
			case CHILD_ADDED:
				LoggerUtil.info("JOB_ADDED:" + event.getData().getPath());
				updateSchedulerJob(jobNameStr, jobNameArray, groupNode);
				break;
			case CHILD_UPDATED:
				LoggerUtil.info("JOB_UPDATED:" + event.getData().getPath());
				updateSchedulerJob(jobNameStr, jobNameArray, groupNode);
				break;
			case CHILD_REMOVED:
				LoggerUtil.info("JOB_REMOVED:" + event.getData().getPath());
				generalSchedulerService.deleteJobGroup(groupNode);
				break;
			default:
				break;
			}
		}
	}
	
	private void updateSchedulerJob(String jobNameStr, String [] jobNameArray, String groupNode){
		
		//generalSchedulerService.deleteJobGroup(groupNode);
		System.out.println(Thread.currentThread().getName());
		if(StringUtils.isEmpty(jobNameStr)){
			LoggerUtil.debug(" updateSchedulerJob returned jobNameArray : " + jobNameStr + " | groupNode : " + groupNode);
			return ;
		}

		jobNameArray = jobNameStr.split(Constants.TRANSFER_CHAR + Constants.JOB_SEPARATOR);
		LoggerUtil.debug(" updateSchedulerJob jobNameArray : " + jobNameStr + " | groupNode : " + groupNode);
		for (String jobName : jobNameArray) {
			if(StringUtils.isEmpty(jobName)){
				continue;
			}

			String jobRootPath = ZKPaths.makePath(Constants.ROOT, Constants.APP_JOB_NODE_ROOT);
			String jobPath = ZKPaths.makePath(jobRootPath, Constants.PATH_SEPARATOR + groupNode,
					Constants.PATH_SEPARATOR + jobName, Constants.APP_JOB_NODE_CONFIG);
			JobInfo job = nodeApi.getData(client, jobPath, JobInfo.class);
 			if(job !=null && (job.getJobStatus() == 3 || job.getJobStatus() == 0)){	//鏈縺娲�or 浠呮彁浜�杩樻病鏈塩ron琛ㄨ揪寮�
				LoggerUtil.warn("jobinfo schedule failure job status:" + job.getJobStatus() + " jobGroup:" + groupNode + " jobName:" + jobName);
				continue;
			}
			if(job != null){
				LoggerUtil.debug("jobinfo path:" + jobPath);
				LoggerUtil.debug("jobinfo json:" + nodeApi.getData(client, jobPath));
				LoggerUtil.debug("jobinfo string:" + job.toString());
				 
			    job.setJobClass(StatelessJobFactory.class);
			    LoggerUtil.debug("Init StatelessJob job to shceduler, jobGroup:" + groupNode + " jobName:" + jobName);
			    boolean exist=false;
				try {
					exist = generalSchedulerService.isExistScheduler(job);
				} catch (SchedulerException e) {
				}
			    if(!exist){
				   generalSchedulerService.update(job);
			    }
			  
				LoggerUtil.debug("Init job to shceduler, jobGroup:" + groupNode + " jobName:" + jobName);
			}else{
				LoggerUtil.warn("Can not find job, groupName:" + groupNode + " jobName:" + jobName +" on /disJob/job node");
			}	
		}
		  try {
			  checkAndRemoveJob(Arrays.asList(jobNameArray),groupNode);
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			LoggerUtil.warn("remove  job error info ");
		}
	}
	
	/**
	 * 
	 * TODO.
	 *
	 * @param jobNames
	 * @param groupName
	 * @throws SchedulerException
	 */
	private void checkAndRemoveJob(List<String>jobNames,String groupName) throws SchedulerException {
		// TODO Auto-generated method stub
		Set<JobKey>  jobKeys=generalSchedulerService.getJobKeysByGroupName(groupName);
		if(CollectionUtils.isEmpty(jobKeys)){
			return;
		}
		for(JobKey jobKey:jobKeys){
			if(!jobNames.contains(jobKey.getName())){
				generalSchedulerService.delete(jobKey.getName(), groupName);
				LoggerUtil.info("delete job[ groupName="+groupName+",jobName"+jobKey.getName()+"]");
			}
		}
	}
	
}
