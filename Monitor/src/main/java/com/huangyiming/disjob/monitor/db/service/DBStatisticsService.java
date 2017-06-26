package com.huangyiming.disjob.monitor.db.service;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huangyiming.disjob.monitor.db.mappers.DBStatisticsMapper;
import com.huangyiming.disjob.monitor.util.DBCommonUtil;
import com.huangyiming.disjob.monitor.db.domain.DBStatistics;
import com.huangyiming.disjob.monitor.db.dynamicsql.StatisticsSqlProvider.DBCondition;
import com.huangyiming.disjob.monitor.db.dynamicsql.StatisticsSqlProvider.DBTable;

/**
 * <pre>
 * 
 *  File: DBJobInfoService.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  统计服务类
 * 
 *  Revision History
 *
 *  Date：		2016年6月24日
 *  Author：		Disjob
 *
 * </pre>
 */
@Service("statisticsService")
//@Transactional
public class DBStatisticsService {
	/** 数据库统计操作映射服务类 */
	@Autowired
	private DBStatisticsMapper mapper;
	
	//执行
	private List<DBStatistics> exchange(DBCondition condition){
		List<DBStatistics> infos = null;
		try{
			infos = mapper.dateStatistics(condition);//从数据库中读取
		}catch(Throwable e){
			DBCommonUtil.logError(this.getClass(), e);
			infos = new LinkedList<DBStatistics>();
		}
		
		List<DBStatistics> result = new LinkedList<DBStatistics>();//从数据库统计
		DBStatistics empty = null;
		outer:
			for(String date:condition.getDates()){
				for(DBStatistics info:infos){
					if(info.getTimeSeg().equals(date)){
						result.add(info);
						continue outer;//跳到外部循环
					}
				}
				empty = new DBStatistics();//没有相等的则进行添加
				empty.setTimeSeg(date);
				result.add(empty);
			}
		return result;
	}
	/** 具体的任务统计，指定了组名、任务名、表、可选择的条件
	 * @param groupName 任务组
	 * @param jobName 任务名
	 * @param table 要统计的表名
	 * @param condition 统计条件，选择时间段
	 * @return DBStatistics类型的list格式数据
	 */
	public List<DBStatistics> specificJob(String groupName,String jobName,DBTable table,DBCondition condition){
		if(groupName == null || jobName == null)
			return new LinkedList<DBStatistics>();
		
		condition.setWgroupName(groupName);//放入查询条件中
		condition.setWjobName(jobName);//放入查询条件中
		condition.setTable(table.value());//放入查询条件中
		
		return exchange(condition);
	}
	/**泛型的任务统计，指定了组名、任务名、表、可选择的条件
	 * @param table  要统计的表名
	 * @param condition  统计条件，选择时间段
	 * @return DBStatistics类型的list格式数据
	 */
	public List<DBStatistics> genericJob(DBTable table,DBCondition condition){
		
		condition.setTable(table.value());//放入查询条件中
		
		return exchange(condition);
	}
	/**统计所有
	 * @param table 要统计的表名
	 * @return long类型的统计数据
	 */
	public long all(DBTable table){
		try{
			return mapper.jobStatistics(table);
		}catch(Throwable e){
			DBCommonUtil.logError(this.getClass(), e);
			return 0;
		}
	}
}
