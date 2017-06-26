package com.huangyiming.disjob.monitor.db.mappers;

import java.util.List;

import org.apache.ibatis.annotations.SelectProvider;

import com.huangyiming.disjob.monitor.db.domain.DBStatistics;
import com.huangyiming.disjob.monitor.db.dynamicsql.StatisticsSqlProvider;
import com.huangyiming.disjob.monitor.db.dynamicsql.StatisticsSqlProvider.DBCondition;
import com.huangyiming.disjob.monitor.db.dynamicsql.StatisticsSqlProvider.DBTable;

/**
 * <pre>
 * 
 *  File: DBJobExeFailMapper.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  统计SQL语句生成和映射,实现统计映射处理
 * 
 *  Revision History
 *
 *  Date：		2016年6月23日
 *  Author：		Disjob
 *
 * </pre>
 */
public interface DBStatisticsMapper {
	
	//'%Y-%m-%d %H'-----时统计
	//'%Y-%m-%d'-----天统计
	//@Select("select DATE_FORMAT(createtime, '%Y-%m-%d') as date , count(*) as count from disJob_job_exefail group by date")//---所有分组
	//@Select("select DATE_FORMAT(createtime, '%Y-%m-%d') as date, count(*) as count from disJob_job_exefail group by date limit 0,#{limit} ")//-----前limit个分组
	//@Select("select DATE_FORMAT(now(), '%Y-%m-%d') as date , count(*) as count from disJob_job_exefail where to_days(createtime) = to_days(now())")//今天
	//@Select("select DATE_FORMAT(createtime, '%Y-%m-%d') as date , count(*) as count from disJob_job_exefail where period_diff(date_format(now() , '%Y%m%d') , date_format(createtime, '%Y%m%d')) = 1")//昨天
	//@Select("select DATE_FORMAT(createtime, '%Y-%m-%d') as date , count(*) as count from disJob_job_exefail where date_sub(curdate(), INTERVAL 7 DAY) <= date(createtime) group by date")//近7天
	//@Select("select DATE_FORMAT(createtime, '%Y-%m-%d') as date , count(*) as count from disJob_job_exefail where date_format(createtime, '%Y%m') = date_format(curdate() , '%Y%m') group by date")//本月
	//@Select("select DATE_FORMAT(createtime, '%Y-%m-%d') as date , count(*) as count from disJob_job_exefail where period_diff(date_format(now() , '%Y%m') , date_format(createtime, '%Y%m')) = 1 group by date")//上个月
	/**采用动态SQL生成类生成SQL
	 * @param condition 统计条件，包含了表、日期
	 * @return 返回统计数据
	 */
	@SelectProvider(type=StatisticsSqlProvider.class, method="buildDateSql")
	List<DBStatistics> dateStatistics(DBCondition condition);
	
	/**采用动态SQL生成类生成SQL
	 * @param table 表名，采用枚举，防止错误的统计
	 * @return 返回表的所有记录数
	 */
	@SelectProvider(type=StatisticsSqlProvider.class, method="buildJobSql")
	long jobStatistics(DBTable table);
}
