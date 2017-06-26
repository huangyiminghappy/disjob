package com.huangyiming.disjob.monitor.db.service;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.huangyiming.disjob.monitor.db.domain.DBStatistics;
import com.huangyiming.disjob.monitor.db.dynamicsql.StatisticsSqlProvider.DBCondition;
import com.huangyiming.disjob.monitor.db.dynamicsql.StatisticsSqlProvider.DBTable;
import com.huangyiming.disjob.monitor.db.service.DBStatisticsService;

/**
 * <pre>
 * 
 *  File: DBJobInfoServiceTest.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  表数据统计测试
 * 
 *  Revision History
 *
 *  Date：		2016年6月24日
 *  Author：		Disjob
 *
 * </pre>
 */
public class DBStatisticsServiceTest extends AbstractTest {
	
	@Autowired
	private DBStatisticsService service;
	
	@Test
	public void test0() {
		System.out.println(DBCondition.Today.getDates());
		/*for(DBTable name:DBTable.values()){
			System.out.println(name+"----------------------统计----------------"+service.jobStatistics(name));
		}*/
	}

	@Test
	public void test1() {
		
		List<DBStatistics> result = null;
		
		System.out.println("----------------------今天----------------");
		//for(DBTable name:DBTable.values()){
			result = service.specificJob("grouptest2", "jobtest5",DBTable.JobBasicInfoTable,DBCondition.ThisYear);
			for(DBStatistics s: result)
				System.out.println("----------："+s);
		//}
		/*System.out.println("----------------------昨天----------------");
		//for(DBTable name:DBTable.values()){
			result = service.dateStatistics(DBTable.JobBasicInfoTable,DBCondition.YesterDay);
			for(DBStatistics s: result)
				System.out.println("----------："+s);
		//}
		System.out.println("----------------------进7天----------------");
		//for(DBTable name:DBTable.values()){
			result = service.dateStatistics(DBTable.JobBasicInfoTable,DBCondition.Nearly7Days);
			for(DBStatistics s: result)
				System.out.println("----------："+s);
		//}
		System.out.println("----------------------本月----------------");
		//for(DBTable name:DBTable.values()){
			result = service.dateStatistics(DBTable.JobBasicInfoTable,DBCondition.ThisMonth);
			for(DBStatistics s: result)
				System.out.println("----------："+s);
		//}
		System.out.println("----------------------上个月----------------");
		//for(DBTable name:DBTable.values()){
			result = service.dateStatistics(DBTable.JobBasicInfoTable,DBCondition.LastMonth);
			for(DBStatistics s: result)
				System.out.println("----------："+s);
		//}
		System.out.println("----------------------今年----------------");
		//for(DBTable name:DBTable.values()){
			result = service.dateStatistics(DBTable.JobBasicInfoTable,DBCondition.ThisYear);
			for(DBStatistics s: result)
				System.out.println("----------："+s);
		//}
*/	}
}
