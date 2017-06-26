package com.huangyiming.disjob.register.job;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;

import com.huangyiming.disjob.common.exception.ZKNodeException2;
import com.huangyiming.disjob.common.model.JobGroup;
import com.huangyiming.disjob.register.domain.Job;
import com.huangyiming.disjob.monitor.db.domain.DBUser;
import com.huangyiming.disjob.monitor.db.domain.PageResultAndCategories;

public interface JobOperationService {
	
	Job getJob();
	
	List<Job> getJobList(String groupName) throws ZKNodeException2;
	
	boolean addJob(Job job, String username) throws ZKNodeException2;
	
	boolean updateJob(Job job);
	
	boolean updateJob(CuratorFramework client ,Job job);
 
	/**
	 * 暂停
	 * @param job
	 * @return
	 */
	boolean suspendJob(Job job);
	
	/**
	 * 恢复
	 * @param job
	 * @return
	 */
	boolean resumeJob(Job job);
	
	/**
	 * 创建任务组
	 * @param group
	 * @param username 
	 * @return
	 */
	public boolean createGroup(JobGroup group, String username);
	/**
	 * 查询所有的group列表
	 * @return
	 */
 	public List<String> getAllGroup();
 	
 	/**
 	 * 根据组名查询所有的job列表
 	 * @param groupName
 	 * @return
 	 */
 	public List<Job> getJobListByGroup(String groupName);
 	
 	/**
 	 * 根据组名和job名查询具体的job列表
 	 * @param groupName
 	 * @param jobName
 	 * @return
 	 */
 	public Job getJobByGroupAndJobName(String groupName ,String jobName);
 	
 	 
 	/**
 	 * 均分job到集群slave节点
 	 * @return 0成功 ,1集群中在线的job节点少于2
 	 * @throws Exception
 	 */
	public int averageDistributeSlaveJob() throws Exception ;

	/**
	 * 根据job组和job类别来查询
	 * @param groupName
	 * @param category
	 * @return
	 */
	PageResultAndCategories getJobListByGroupAndCategory(String groupName, String category);

	List<String> getAllGroup(DBUser dbUser);
	
	void fireNow(String groupName ,String jobName);
	
	public boolean saveJob(Job job);
	
	
	public boolean bindSessionAndGroup(String sessionName,String groupName);
	
	
	public boolean bindSessionAndGroup(String sessionName, String oldGroup ,String newGroup);

	void bindJob(List<String> sessions, List<String> groupNames);

	void reBindJob(List<String> sessions, List<String> groupNames);
	
	List<JobGroup> getAllJobGroupForPageList();

}
