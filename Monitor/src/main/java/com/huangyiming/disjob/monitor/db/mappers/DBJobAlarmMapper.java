package com.huangyiming.disjob.monitor.db.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huangyiming.disjob.monitor.db.domain.DBJobAlarmMapping;

/**
 * <pre>
 * 
 *  File: DBJobExeFailMapper.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  任务报警映射关系的增、珊、改、查操作
 * 
 *  Revision History
 *
 *  Date：		2016年6月23日
 *  Author：		Disjob
 *
 * </pre>
 */
public interface DBJobAlarmMapper {
	/**查询所有的映射关系
	 * @return 返回符合分页的任务信息列表
	 */
	List<DBJobAlarmMapping> findAll(@Param("pageOffset")int  pageOffset, @Param("pageSize")int pageSize);
	
	/**插入任务报警映射
	 * @param info  任务报警映射信息
	 */
	void insert(DBJobAlarmMapping info);
	
	/**更新报警映射记录
	 * @param info  映射信息
	 */
	void update(DBJobAlarmMapping info);
	
	/**删除报警映射记录
	 * @param uuid 记录uuid
	 */
	void delete(String groupName);
	
	/**查询报警映射记录
	 * @param uuid 记录uuid
	 */
	DBJobAlarmMapping search(String groupName);
	
	/** 统计总数
	 * @return 总数
	 */
	long getAllSize();
}
