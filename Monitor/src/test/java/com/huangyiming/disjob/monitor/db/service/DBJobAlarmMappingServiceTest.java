package com.huangyiming.disjob.monitor.db.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.huangyiming.disjob.monitor.db.domain.DBJobAlarmMapping;
import com.huangyiming.disjob.monitor.db.domain.PageResult;
import com.huangyiming.disjob.monitor.db.service.DBJobAlarmMappingService;

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
public class DBJobAlarmMappingServiceTest extends AbstractTest {
	
	@Autowired
	//@Qualifier("jobAlarmMappingService")
	private DBJobAlarmMappingService service;
	
	@Test
	public void test() {
		/*for(int index=0;index<20;index++){
			DBJobAlarmMapping info = new DBJobAlarmMapping();
			info.setGroupName("groupName-a-"+index);
			info.setAlarmRtx("8082");
			System.out.println("inert："+service.insert(info));
		}*/
		PageResult r = service.findAll(0, 20);
		System.out.println(r.getTotal());
		for(Object info:r.getRows()){
			System.out.println(info);
		}
		/*System.out.println("delete："+service.delete("groupName--2"));
		DBJobAlarmMapping info = new DBJobAlarmMapping();
		info.setGroupName("groupName--1");
		info.setAlarmRtx("8082,8083");
		System.out.println("update："+service.update(info));*/
	}
}
