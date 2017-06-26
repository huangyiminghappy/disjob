package com.huangyiming.disjob.monitor.db.service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.huangyiming.disjob.monitor.db.domain.DBJobBasicInfo;
import com.huangyiming.disjob.monitor.db.domain.PageResult;
import com.huangyiming.disjob.monitor.db.service.DBJobBasicInfoService;

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
public class DBJobBasicInfoServiceTest extends AbstractTest {
	
	@Autowired
	private DBJobBasicInfoService service;
	
	@Test
	public void test0() {
		
		/*DBJobBasicInfo info = new DBJobBasicInfo();
		info.setUuid("uuid-123456789");
		info.setGroupName("u-gName");
		info.setJobName("u-jName");
		info.setTimeConsuming(100);
		System.out.println("[ 测试插入    ]"+service.create(info));
		info = service.findByUuid("uuid-123456789");
		System.out.println("[ 测试查询    ]"+info);
		info.setBusinessSip("192.168.0.210");
		System.out.println("[ 测试更新    ]"+service.update(info));
		info = service.findByUuid("uuid-123456789");
		System.out.println("[ 测试查询    ]"+info);
		System.out.println("[ 测试删除    ]"+service.delete("uuid-123456789"));*/
		
		/*for(int index=0;index < 100;index++){
			DBJobBasicInfo info = new DBJobBasicInfo();
			info.setUuid("uuid-"+index);
			info.setGroupName("u-gName");
			info.setJobName("u-jName");
			info.setTimeConsuming(100);
			info.setExecuteStart(new Date().toLocaleString());
			info.setExecuteEnd(new Date().toLocaleString());
			info.setCurrentStatus(index%2==0?'1':'0');
			System.out.println("[ 测试插入    ]"+service.create(info));
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}*/
		
		/*System.out.println("--------------------------------findAll  1----------------------"+service.getAllSize());
		PageResult res= service.findByGnameAndJname("u-gName", "u-jName", 0, 10);
		for(Object in : res.getRows())
			System.out.println(in);
		System.out.println("--------------------------------findAll  2----------------------"+service.getAllSize());
		res= service.findByGnameAndJname("u-gName", "u-jName", 10, 10);
		for(Object in : res.getRows())
			System.out.println(in);
		System.out.println("--------------------------------findSuccess----------------------"+service.getSuccessSize());
		for(DBJobBasicInfo in : service.findSuccessJobs(1,10))
			System.out.println(in);
		System.out.println("--------------------------------findFail----------------------"+service.getFailSize());
		for(DBJobBasicInfo in : service.findFailJobs(2,10))
			System.out.println(in);*/
		System.out.println("测试："+service.getAllSize());
		//System.out.println("测试where统计："+service.getAllSizeByWhere("u-gName","u-jName","2016-08-23 16:13:47","2099-01-01 00:00:00"));
		for(Object in : service.findByTime("u-gName","u-jName", "2016-08-23 16:13:47", null, 0, 10).getRows()){
			System.out.println(in);
		}
	}
	
	@Test
	public void findJobGroupByRequestId(){
		String requestId = "8aa081795865f173015865f1749c0001";
		System.err.println(service.findJobGroupByRequestId(requestId));
		System.out.println("");
	}
}
