package com.huangyiming.disjob.monitor.db.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huangyiming.disjob.monitor.db.domain.DBJobExeProgress;

/**
 * <pre>
 * 
 *  File: DBJobExeFailMapper.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  任务执行进度记录数据库增、珊、改、查操作
 * 
 *  Revision History
 *
 *  Date：		2016年6月23日
 *  Author：		Disjob
 *
 * </pre>
 */
public interface DBJobExeProgressMapper {
	/**查询分页进度数据记录（添加Page后缀是方便分页插件进行过滤处理）：进行pojo和表字段的映射
	 * @return 返回符合分页的进度信息列表
	 */
	List<DBJobExeProgress> findAllPage(@Param("pageOffset")int  pageOffset, @Param("pageSize")int pageSize);
	/**查询任务UUID相关联的任务进度信息：进行pojo和表字段的映射
	 * @param uuid  任务uuid
	 * @return 返回符合分页的进度信息列表
	 */
	List<DBJobExeProgress> findByUuid(String uuid);
	/**插入记录：进行pojo和表字段的映射
	 * @param info  任务进度信息
	 */
	void insert(DBJobExeProgress info);
	/**更新记录：进行pojo和表字段的映射
	 * @param info 任务进度信息
	 */
	void update(DBJobExeProgress info);
	/** 删除任务进度信息 
	 * @param id  表id
	 */
	void delete(long id);
}
