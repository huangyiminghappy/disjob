package com.huangyiming.disjob.monitor.db.dynamicsql;

import java.util.List;

import org.apache.ibatis.jdbc.SQL;

import com.huangyiming.disjob.monitor.util.DatePart;

/**
 * <pre>
 * 
 *  File: DynamicSqlProvider.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  动态SQL生成器，实现表--统计SQL语句的动态生成。
 * 
 *  Revision History
 *
 *  Date：		2016年6月28日
 *  Author：		Disjob
 *
 * </pre>
 */
public class StatisticsSqlProvider {
	
	//注意：这里"%Y-%m-%d %H"的格式化方式和DatePart的格式化方式
	public enum DBCondition{
		Today("ix_created_at","%Y-%m-%d %H","to_days(ix_created_at) = to_days(now())","timeSeg") {
			@Override
			public List<String> getDates() {
				return DatePart.today();
			}
		},//今天
		YesterDay("ix_created_at","%Y-%m-%d %H","period_diff(date_format(now() , '%Y%m%d') , date_format(ix_created_at, '%Y%m%d')) = 1","timeSeg") {
			@Override
			public List<String> getDates() {
				return DatePart.yesterday();
			}
		},//昨天
		Nearly7Days("ix_created_at","%Y-%m-%d","date_sub(curdate(), INTERVAL 7 DAY) <= date(ix_created_at)","timeSeg") {
			@Override
			public List<String> getDates() {
				return DatePart.last7days();
			}
		},//进7天
		ThisMonth("ix_created_at","%Y-%m-%d","date_format(ix_created_at, '%Y%m') = date_format(curdate() , '%Y%m')","timeSeg") {
			@Override
			public List<String> getDates() {
				return DatePart.thisMonth();
			}
		},//本月
		LastMonth("ix_created_at","%Y-%m-%d","period_diff(date_format(now() , '%Y%m') , date_format(ix_created_at, '%Y%m')) = 1","timeSeg") {
			@Override
			public List<String> getDates() {
				return DatePart.lastMonth();
			}
		},//上个月
		ThisYear("ix_created_at","%Y-%m","date_format(ix_created_at, '%Y') = date_format(curdate() , '%Y')","timeSeg") {
			@Override
			public List<String> getDates() {
				return DatePart.thisYear();
			}
		};//上个月
		private String cField;//要查询的字段
		private String format;//格式化
		private String where;//SQL的where语句
		private String groupBy;//分组字段
		private String table;//要操作的表
		private String wgroupName;//where语句的要操作任务组
		private String wjobName;//where语句的要操作任务名
		
		private DBCondition(String cField,String format,String where,String groupBy){
			this.cField = cField;
			this.format = format;
			this.where = where;
			this.groupBy = groupBy;
		}
		
		public abstract List<String> getDates();
		
		public String getcField() {
			return cField;
		}
		public String getWhere() {
			return where;
		}
		public String getGroupBy() {
			return groupBy;
		}
		public String getTable() {
			return table;
		}
		public void setTable(String table) {
			this.table = table;
		}

		public String getWgroupName() {
			return wgroupName;
		}

		public void setWgroupName(String wgroupName) {
			this.wgroupName = wgroupName;
		}

		public String getWjobName() {
			return wjobName;
		}

		public void setWjobName(String wjobName) {
			this.wjobName = wjobName;
		}
	}
	public enum DBTable{
		JobBasicInfoTable("disJob_job_basicinfo"),
		JobProgressTable("disJob_job_exeprogress");//进度表
		private String name;
		private DBTable(String name){
			this.name = name;
		}
		public String value() {
			return name;
		}
	}
	/**
	 * 根据条件生成SQL
	 * @param condition 封装了查询条件，包括表名、查询字段名、where语句、分组语句
	 * @return 生成的SQL语句
	 */
	public String buildJobSql(final DBTable table){
		return new SQL() {{
			SELECT("count(*)");
		    FROM(table.value());
		}}.toString();
	}
	/**
	 * 根据条件生成SQL
	 * @param condition 封装了查询条件，包括表名、查询字段名、where语句、分组语句
	 * @return 生成的SQL语句
	 */
	public String buildDateSql(final DBCondition condition){
		String where = null;
		if(condition.getWgroupName() != null && condition.getWjobName() != null){//添加任务统计
			where = "ix_group_name=\'"+condition.getWgroupName()+"\' and ix_job_name=\'"+condition.getWjobName()+"\' and "+condition.getWhere();
		}else{//全部统计
			where = condition.getWhere();
		}
		return build2(condition.getcField(),condition.format,condition.getTable(),where,condition.getGroupBy());
	}
	//生成查询表指定字段、where条件、根据字段分组的SQL
	private String build2(final String timeField,final String format,final String tableName,final String where,final String groupBy){
		return new SQL() {{
			SELECT("date_format("+timeField+", '"+format+"') as timeSeg , sum(case when ix_current_status = 1 then 1 else 0 end) as successNum ,sum(case when ix_current_status = '0' then 1 else 0 end) as failNum");
		    FROM(tableName);
		    WHERE(where);
		    GROUP_BY(groupBy);
		}}.toString();
	}
}
