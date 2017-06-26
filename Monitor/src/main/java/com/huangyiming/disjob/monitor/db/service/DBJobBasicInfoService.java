package com.huangyiming.disjob.monitor.db.service;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huangyiming.disjob.common.util.DateUtil;
import com.huangyiming.disjob.monitor.db.mappers.DBJobBasicInfoMapper;
import com.huangyiming.disjob.monitor.util.DBCommonUtil;
import com.huangyiming.disjob.monitor.db.domain.DBJobBasicInfo;
import com.huangyiming.disjob.monitor.db.domain.PageResult;

/**
 * <pre>
 * 
 *  File: DBJobInfoService.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  任务执行失败记录表服务类
 * 
 *  Revision History
 *
 *  Date：		2016年6月24日
 *  Author：		Disjob
 *
 * </pre>
 */
@Service("jobBasicInfoService")
public class DBJobBasicInfoService {
	private static String defaultStartTime = "2016-01-01 00:00:00";
	private static String defaultEndTime = "2099-01-01 00:00:00";
	@Autowired
	private DBJobBasicInfoMapper mapper;

	/*public List<DBJobBasicInfo> findAll(int pageNo, int pageSize){
		PageHelper.setPage(pageNo,pageSize);//分页处理
		try{
			return mapper.findAllPage();
		}catch(Throwable e){
			DBCommonUtil.logError(this.getClass(), e);
			return new LinkedList<DBJobBasicInfo>();
		}
	}*/
	
	/**统计任务执行的是否完成（ix_execute_end is null and  ix_error_type is null and ix_is_timeout=0）
	 * @param groupName 任务组
	 * @param jobName 任务名
	 * @return 没执行完返回true，否则返回false
	 */
	public boolean latestExeResult(String groupName,String jobName){
		int number = mapper.latestExeResult(groupName, jobName);
		if(number > 0)
			return true;
		return false;
	}
	/**把没有执行完的任务，是否超时字段置为1（ix_execute_end is null and  ix_error_type is null and ix_is_timeout=0）
	 * @param groupName 任务组
	 * @param jobName 任务名
	 */
	public void batchUpdateIsTimeOut(String groupName,String jobName){
 		mapper.batchUpdateIsTimeOut(groupName, jobName);
	}
	
	public void updateJobTimeOut(String pk_uuid){
		mapper.updateJobTimeOut(pk_uuid);
	}
	
	/**可分页的查询符合任务组、任务名相关的任务信息数据:如果组名或任务名为null，则返回0、null，否则统计总的任务数、根据分页信息查询任务
	 * @param groupName 任务组名
	 * @param jobName   任务名
	 * @param pageNo   当前页
	 * @param pageSize 页大小
	 * @return PageResult类型的对象，封装了总任务数、分页列表数据
	 */
	public PageResult findByGnameAndJname(String groupName,String jobName,int pageOffset, int pageSize){
		List<?> infos = null;
		long total = 0;
		if (StringUtils.isEmpty(groupName) || StringUtils.isEmpty(jobName)) {
			infos = new LinkedList<DBJobBasicInfo>();
			total = 0;
		}else{
			total = getAllSizeByWhere(groupName,jobName,defaultStartTime,defaultEndTime);//统计
			try{
				infos = mapper.findByGnameAndJnamePage(groupName, jobName,pageOffset,pageSize);//查找
			}catch(Throwable e){
				DBCommonUtil.logError(this.getClass(), e);
				infos = new LinkedList<DBJobBasicInfo>();//恢复
				total = 0;//恢复
			}
		}
		return new PageResult().setTotal(total).setRows(infos);
	}
	/**可分页的查询符合任务组、任务名、执行开始时间、执行结束时间相关的任务信息数据:
	 * 	1，如果任务组、任务名为null，或执行开始时间、执行结束时间为null，则返回0和空列表
	 *  2，根据执行开始时间、结束时间拼接where查询语句，进行查询、统计后返回
	 * @param groupName  任务组
	 * @param jobName  任务名
	 * @param startTime 执行开始时间
	 * @param endTime 执行结束时间
	 * @param pageNo 当前页
	 * @param pageSize 页大小
	 * @return PageResult类型的对象，封装了总任务数、分页列表数据
	 */
	public PageResult findByTime(String groupName,String jobName,String startTime,String endTime,int pageOffset, int pageSize){
		List<?> infos = null;
		long total = 0;
		//任务组或任务名不能为null
		if (StringUtils.isEmpty(groupName) || StringUtils.isEmpty(jobName)) {
			infos = new LinkedList<DBJobBasicInfo>();
			total = 0;
		}else{
			String sTime = startTime;
			String eTime = endTime;
			if(StringUtils.isEmpty(startTime)){//执行开始时间无效则使用默认值
				sTime = defaultStartTime;
			}
			if(StringUtils.isEmpty(eTime)){//执行结束时间无效则使用默认值
				eTime = defaultEndTime;
			}
			total = getAllSizeByWhere(groupName,jobName,sTime,eTime);//统计
			try{
				infos = mapper.findByTimePage(groupName,jobName,sTime,eTime,pageOffset,pageSize);//查找
			}catch(Throwable e){
				DBCommonUtil.logError(this.getClass(), e);
				infos = new LinkedList<DBJobBasicInfo>();//恢复
				total = 0;//恢复
			}
		}
		return new PageResult().setTotal(total).setRows(infos);
	}
	/**根据任务uuid查询任务信息
	 * @param uuid  任务UUID
	 * @return  任务信息，封装于DBJobBasicInfo对象中（不存在返回Null）
	 */
	public DBJobBasicInfo findByUuid(String uuid){
		if(StringUtils.isEmpty(uuid))
			return null;
		try{
			return mapper.findByUuid(uuid);
		}catch(Throwable e){
			DBCommonUtil.logError(this.getClass(), e);
			return null;
		}
	}
	
	/**
	 * 查询某个job最近一次的调度信息。
	 * 有些业务如果发现最近一次的调度信息没有返回 执行结束时间。则需要做策略处理
	 */
	public DBJobBasicInfo getTheLeastScheduler(String group,String jobName){
		if(StringUtils.isEmpty(group) || StringUtils.isEmpty(jobName))
			return null;
		try{
			return mapper.getTheLeastScheduler(group,jobName);
		}catch(Throwable e){
			DBCommonUtil.logError(this.getClass(), e);
			return null;
		}
	}
	
	/** 可分页的查询执行成功的任务信息
	 * @param pageNo 当前页
	 * @param pageSize 页大小
	 * @return 任务信息列表（DBJobBasicInfo类型的list格式数据）
	 */
	public List<DBJobBasicInfo> findSuccessJobs(int pageOffset, int pageSize){
		try{
			return mapper.findSuccessJobsPage(pageOffset,pageSize);
		}catch(Throwable e){
			DBCommonUtil.logError(this.getClass(), e);
			return new LinkedList<DBJobBasicInfo>();
		}
	}
	/** 可分页的查询执行失败的任务信息
	 * @param pageNo 当前页
	 * @param pageSize 页大小
	 * @return 任务信息列表（DBJobBasicInfo类型的list格式数据）
	 */
	public List<DBJobBasicInfo> findFailJobs(int pageOffset, int pageSize){
		try{
			return mapper.findFailJobsPage(pageOffset,pageSize);
		}catch(Throwable e){
			DBCommonUtil.logError(this.getClass(), e);
			return new LinkedList<DBJobBasicInfo>();
		}
	}
	/** 创建任务执行信息记录（即新增）
	 * @param info  DBJobBasicInfo类型，必须包含任务uuid、任务组、任务名字段
	 * @return 保存成功返回true，否则返回false
	 */
	public boolean create(DBJobBasicInfo info){
		if(null == info)
			return false;
		info.setCreatedAt(DateUtil.getUtc());
		info.setUpdatedAt(DateUtil.getUtc());
		try{
			mapper.insert(info);
		}catch(Throwable e){
			DBCommonUtil.logError(this.getClass(), e);
			return false;
		}
		return true;
	}
	/** 更新任务执行信息记录
	 * @param info  DBJobBasicInfo类型，必须包含任务uuid字段
	 * @return 更新成功返回true，否则返回false
	 */
	public boolean update(DBJobBasicInfo info){
		
		if(null == info)
			return false;
		
		info.setUpdatedAt(DateUtil.getUtc());
		try{
			int modifyCount = mapper.update(info);
			return modifyCount == 1 ;//
		}catch(Throwable e){
			e.printStackTrace();
			DBCommonUtil.logError(this.getClass(), e);
		}
		return false;
	}
	/**删除任务信息记录
	 * @param uuid 任务uuid
	 * @return 删除成功返回true，否则返回false
	 */
	public boolean delete(String uuid){
		if(StringUtils.isEmpty(uuid))
			return false;
		try{
			mapper.delete(uuid);
		}catch(Throwable e){
			DBCommonUtil.logError(this.getClass(), e);
			return false;
		}
		return true;
	}
	/**统计失败的任务数
	 * @return long型的失败任务数量
	 */
	public long getFailSize(){
		try{
			return mapper.getFailSize();
		}catch(Throwable e){
			DBCommonUtil.logError(this.getClass(), e);
			return 0;
		}
	}
	/**统计成功的任务数
	 * @return long型的成功任务数量
	 */
	public long getSuccessSize(){
		try{
			return mapper.getSuccessSize();
		}catch(Throwable e){
			DBCommonUtil.logError(this.getClass(), e);
			return 0;
		}
	}
	/**统计总的任务数
	 * @return long型的总任务数量
	 */
	public long getAllSize(){
		try{
			return mapper.getAllSize();
		}catch(Throwable e){
			DBCommonUtil.logError(this.getClass(), e);
			return 0;
		}
	}
	/**根据查询语句进行统计
	 * @param where 符合SQL的where语句内容的查询条件
	 * @return 服务统计条件的任务总数
	 */
	private long getAllSizeByWhere(String groupName,String jobName,String startTime,String endTime){
			try{
				return mapper.getAllSizeByWhere(groupName,jobName,startTime,endTime);
			}catch(Throwable e){
				DBCommonUtil.logError(this.getClass(), e);
			}
		return 0;
	}
	
	public String findJobGroupByRequestId(String requestId){
		try{
			return mapper.findJobGroupByRequestId(requestId);
		}catch(Throwable e){
			DBCommonUtil.logError(this.getClass(), e);
		}
		return null;
	}
}
