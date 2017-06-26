package com.huangyiming.disjob.monitor.db.service;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huangyiming.disjob.monitor.db.mappers.DBJobAlarmMapper;
import com.huangyiming.disjob.monitor.util.DBCommonUtil;
import com.huangyiming.disjob.monitor.db.domain.DBJobAlarmMapping;
import com.huangyiming.disjob.monitor.db.domain.PageResult;

/**
 * <pre>
 * 
 *  File: DBJobInfoService.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  任务报警映射服务类
 * 
 *  Revision History
 *
 *  Date：		2016年6月24日
 *  Author：		Disjob
 *
 * </pre>
 */
@Service("jobAlarmMappingService")/** 任务报警映射服务类 */
public class DBJobAlarmMappingService {
	
	@Autowired
	private DBJobAlarmMapper mapper;

	/**对报警映射做分页查询处理
	 * @param pageOffset  偏移量
	 * @param pageSize	页面大小
	 * @return  PageResult类型的对象，封装了总报警映射记录、分页列表数据
	 */
	public PageResult findAll(int pageOffset, int pageSize){
		List<?> infos = null;
		long total = 0;
		try{
			total = mapper.getAllSize();
		}catch(Throwable e){
			total = 0;//恢复
		}
		if(total > 0){//如果有效，则分页查询
			try{
				infos = mapper.findAll(pageOffset,pageSize);//查找
			}catch(Throwable e){
				DBCommonUtil.logError(this.getClass(), e);
				infos = new LinkedList<DBJobAlarmMapping>();//恢复
				total = 0;//恢复
			}
		}
		return new PageResult().setTotal(total).setRows(infos);
	}
	/**插入报警映射记录
	 * @param info  映射信息
	 * @return 成功返回true，否则返回false
	 */
	public boolean insert(DBJobAlarmMapping info){
		try{
			mapper.insert(info);
			return true;
		}catch(Throwable e){
			DBCommonUtil.logError(this.getClass(), e);
		}
		return false;
	}
	
	/**更新报警映射记录
	 * @param info  映射信息
	 * @return 成功返回true，否则返回false
	 */
	public boolean update(DBJobAlarmMapping info){
		try{
			mapper.update(info);
			return true;
		}catch(Throwable e){
			DBCommonUtil.logError(this.getClass(), e);
		}
		return false;
	}
	
	/**删除报警映射记录
	 * @param groupName  任务组名
	 * @return 成功返回true，否则返回false
	 */
	public boolean delete(String groupName){
		try{
			mapper.delete(groupName);
			return true;
		}catch(Throwable e){
			DBCommonUtil.logError(this.getClass(), e);
		}
		return false;
	}
	/**查询报警映射记录
	 * @param groupName  任务组名
	 * @return 有则返回报警信息，否则返回Null
	 */
	public DBJobAlarmMapping search(String groupName){
		try{
			return mapper.search(groupName);
		}catch(Throwable e){
			DBCommonUtil.logError(this.getClass(), e);
			return null;
		}
	}
}
