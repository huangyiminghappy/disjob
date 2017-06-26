package com.huangyiming.disjob.monitor.db.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huangyiming.disjob.monitor.db.domain.DBJobBasicInfo;

/**
 * <pre>
 * 
 *  File: DBJobExeFailMapper.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  任务执行失败记录数据库增、珊、改、查操作
 * 
 *  Revision History
 *
 *  Date：		2016年6月23日
 *  Author：		Disjob
 *
 * </pre>
 */
public interface DBJobBasicInfoMapper {
	/**查询分页任务信息记录（添加Page后缀是方便分页插件进行过滤处理）：采用动态SQL
	 * @return 返回符合分页的任务信息列表
	 */
	List<DBJobBasicInfo> findAllPage(@Param("pageOffset")int  pageOffset, @Param("pageSize")int pageSize);
	
	/**根据任务组、任务名查询分页任务信息记录（添加Page后缀是方便分页插件进行过滤处理）：采用动态SQL
	 * @param groupName 任务组
	 * @param jobName 任务名
	 * @return 返回符合分页的任务信息列表
	 */
	List<DBJobBasicInfo> findByGnameAndJnamePage(@Param("gName")String groupName, @Param("jName")String jobName,@Param("pageOffset")int  pageOffset, @Param("pageSize")int pageSize);
	
	/**根据时间查询分页任务信息记录（添加Page后缀是方便分页插件进行过滤处理）：采用动态SQL
	 * @param where  包含了组名、任务名、时间的查询条件语句
	 * @return 返回符合分页的任务信息列表
	 */
	List<DBJobBasicInfo> findByTimePage(@Param("gName")String groupName, @Param("jName")String jobName,@Param("sTime")String startTime, @Param("eTime")String endTime,@Param("pageOffset")int  pageOffset, @Param("pageSize")int pageSize);
	
	/** 根据任务uuid查询任务信息
	 * @param uuid 任务uuid
	 * @return 任务信息
	 */
	DBJobBasicInfo findByUuid(String uuid);
	
	public DBJobBasicInfo getTheLeastScheduler(@Param("group")String group,@Param("jobName")String jobName);
	
	/** 查询执行成功的任务信息，具备分页（添加Page后缀是方便分页插件进行过滤处理）：采用动态SQL
	 * @return  返回符合分页的任务信息列表
	 */
	List<DBJobBasicInfo> findSuccessJobsPage(@Param("pageOffset")int  pageOffset, @Param("pageSize")int pageSize);
	
	/** 查询执行失败的任务信息，具备分页（添加Page后缀是方便分页插件进行过滤处理）：采用动态SQL
	 * @return  返回符合分页的任务信息列表
	 */
	List<DBJobBasicInfo> findFailJobsPage(@Param("pageOffset")int  pageOffset, @Param("pageSize")int pageSize);
	
	/**插入任务信息记录
	 * @param info  任务信息（必须包含任务uuid、组、任务名）
	 */
	void insert(DBJobBasicInfo info);
	
	/**更新任务信息记录
	 * @param info  任务信息（必须包含任务uuid）
	 */
	int update(DBJobBasicInfo info);
	
	/**删除任务信息记录
	 * @param uuid 任务uuid
	 */
	void delete(String uuid);
	
	/** 统计执行失败的任务数
	 * @return  失败的任务数量
	 */
	long getFailSize();
	
	/** 统计成功执行的任务数
	 * @return  成功的任务数量
	 */
	long getSuccessSize();
	
	/**根据查询条件统计任务数
	 * @param where  查询统计条件
	 * @return 符合条件的任务数
	 */
	long getAllSizeByWhere(@Param("gName")String groupName, @Param("jName")String jobName,@Param("sTime")String startTime, @Param("eTime")String endTime);
	
	/** 统计所有任务数
	 * @return 总任务数
	 */
	long getAllSize();
	
	/** 最近一次执行的job信息
	 * @return 最近一次执行的job信息
	 */
	int latestExeResult(@Param("gName")String groupName,@Param("jName")String jobName);
	
	void batchUpdateIsTimeOut(@Param("gName")String groupName,@Param("jName")String jobName);
	
	public void updateJobTimeOut(String pk_uuid);
	
	String findJobGroupByRequestId(String requestId);
}
