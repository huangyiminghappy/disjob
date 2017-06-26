package com.huangyiming.disjob.monitor.db.service;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huangyiming.disjob.common.util.DateUtil;
import com.huangyiming.disjob.monitor.db.mappers.DBJobExeProgressMapper;
import com.huangyiming.disjob.monitor.util.DBCommonUtil;
import com.huangyiming.disjob.monitor.db.domain.DBJobExeProgress;

/**
 * <pre>
 * 
 *  File: DBJobInfoService.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  任务执行进度记录表服务类
 * 
 *  Revision History
 *
 *  Date：		2016年6月24日
 *  Author：		Disjob
 *
 * </pre>
 */
@Service("jobExeProgressService")
//@Transactional
public class DBJobExeProgressService {
	/** 数据库进度信息表操作映射服务类 */
	@Autowired
	private DBJobExeProgressMapper mapper;
	/**可分页的查询所有进度信息（目前暂未用到）
	 * @param pageNo 当前页
	 * @param pageSize 页大小
	 * @return DBJobExeProgress类型的list格式数据
	 */
	public List<DBJobExeProgress> findAll(int pageOffset, int pageSize)
	{
		try{
			return mapper.findAllPage(pageOffset,pageSize);
		}catch(Throwable e){
			DBCommonUtil.logError(this.getClass(), e);
			return new LinkedList<DBJobExeProgress>();
		}
	}
	/**更加任务uuid查询相关的进度数据
	 * @param uuid 任务的uuid
	 * @return DBJobExeProgress类型的list格式数据
	 */
	public List<DBJobExeProgress> findByUuid(String uuid){
		if(StringUtils.isNoneEmpty(uuid)){
			try{
				return mapper.findByUuid(uuid);
			}catch(Throwable e){
				DBCommonUtil.logError(this.getClass(), e);
			}
		}
		return new LinkedList<DBJobExeProgress>();
	}
	/**创建表记录（即新增）
	 * @param info DBJobExeProgress对象，uuid为必须字段
	 * @return 操作成功返回true，否则返回false
	 */
	public boolean create(DBJobExeProgress info) {
		if(null == info)
			return false;
		info.setCreatedAt(DateUtil.getUtc());
		try{
			mapper.insert(info);
		}catch(Throwable e){
			DBCommonUtil.logError(this.getClass(), new Exception(info.toString()+" -- "+e.getMessage()));
			return false;
		}
		return true;
	}
	/**更新表记录（目前暂未用到）
	 * @param info DBJobExeProgress对象，表记录ID为必须字段
	 * @return 操作成功返回true，否则返回false
	 */
	public boolean update(DBJobExeProgress info) {
		if(null == info)
			return false;
		try{
			mapper.update(info);
		}catch(Throwable e){
			DBCommonUtil.logError(this.getClass(), new Exception(info.toString()+" -- "+e.getMessage()));
			return false;
		}
		return true;
	}
	/**删除表记录（目前暂未用到）
	 * @param id 表记录ID
	 * @return 操作成功返回true，否则返回false
	 */
	public boolean delete(long id) {
		try{
			mapper.delete(id);
		}catch(Throwable e){
			DBCommonUtil.logError(this.getClass(), e);
			return false;
		}
		return true;
	}
}
