package com.huangyiming.disjob.monitor.db.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.huangyiming.disjob.monitor.db.domain.DBPermitItem;
import com.huangyiming.disjob.monitor.db.service.DBPermitItemService;

public class DBPermitItemServiceTest extends AbstractTest {

	@Autowired
	DBPermitItemService service;
	
	@Test
	public void createUserActionRecord(){
		for(DBPermitItem item : service.getAllPermit()){
			System.out.println(item);
		}
	}
	
	@Test
	public void getPermitById(){
		System.out.println(service.getPermitById("5").getUrl());
		assert service.getPermitById("5").getUrl().equals("/page/monitor/cronTransfer");
	}
	
	@Test
	public void getPermitByUri(){
		System.out.println(service.getPermitByUri("/page/job/alarm").getId());
		assert service.getPermitById("/page/job/alarm").getId().equals("1");
	}
	
}
