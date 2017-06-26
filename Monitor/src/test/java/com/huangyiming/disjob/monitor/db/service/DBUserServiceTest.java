package com.huangyiming.disjob.monitor.db.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.huangyiming.disjob.monitor.db.domain.DBJobAlarmMapping;
import com.huangyiming.disjob.monitor.db.domain.PageResult;
import com.huangyiming.disjob.monitor.db.service.DBUserService;

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
public class DBUserServiceTest extends AbstractTest {
	
	@Autowired
	private DBUserService service;
	
	@Test
	public void test() {
		System.out.println(service.findUser("admin","admin"));
		System.out.println(service.findUser("oms","123456"));
	}
}
