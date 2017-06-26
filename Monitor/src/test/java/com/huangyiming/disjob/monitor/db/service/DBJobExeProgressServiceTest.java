package com.huangyiming.disjob.monitor.db.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.huangyiming.disjob.monitor.db.domain.DBJobExeProgress;
import com.huangyiming.disjob.monitor.db.service.DBJobExeProgressService;

/**
 * <pre>
 * 
 *  File: DBJobInfoServiceTest.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  测试任务进度信息的增、珊、该、查
 * 
 *  Revision History
 *
 *  Date：		2016年6月24日
 *  Author：		Disjob
 *
 * </pre>
 */
public class DBJobExeProgressServiceTest extends AbstractTest {
	
	@Autowired
	private DBJobExeProgressService service;
	
	@Test
	public void test0() {
		System.out.println("-------11--------------------");
		for(DBJobExeProgress info:service.findByUuid("2"))
			System.out.println(info);
	}

	@Test
	public void test1() {
		for(int index=0;index<10;index++){
		DBJobExeProgress info = new DBJobExeProgress();
		info.setUuid("uuid-1234");
		info.setBusinessSip("127.0.0."+index);
		service.create(info);
		}
		for(DBJobExeProgress in:service.findByUuid("uuid-1234"))
			System.out.println(in);
		DBJobExeProgress info = new DBJobExeProgress();
		info.setUuid("uuid-1234");
		info.setBusinessSip("127.1.1.1");
		info.setId(320);
		service.update(info);
		for(DBJobExeProgress in:service.findByUuid("uuid-1234"))
			System.out.println(in);
		service.delete(321);
		
		for(DBJobExeProgress in:service.findByUuid("uuid-1234"))
			System.out.println(in);
		System.out.println("-------findAll(0,10)--------------------");
		for(DBJobExeProgress in:service.findAll(0,10))
			System.out.println(in);
		
		/*System.out.println("-------findAll(1,10)--------------------");
		for(DBJobExeProgress in:service.findAll(1,10))
			System.out.println(in);
		
		System.out.println("-------findAll(2,10)--------------------");
		for(DBJobExeProgress in:service.findAll(2,10))
			System.out.println(in);
		
		System.out.println("-------findAll(3,10)--------------------");
		for(DBJobExeProgress in:service.findAll(3,10))
			System.out.println(in);
		
		System.out.println("-------findAll(2,10)--------------------");
		for(DBJobExeProgress in:service.findAll(2,10))
			System.out.println(in);*/
	}
}
