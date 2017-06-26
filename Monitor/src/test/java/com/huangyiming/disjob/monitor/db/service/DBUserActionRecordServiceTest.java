package com.huangyiming.disjob.monitor.db.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.huangyiming.disjob.monitor.db.service.DBUserActionRecordService;

public class DBUserActionRecordServiceTest extends AbstractTest{

	@Autowired
	DBUserActionRecordService service;
	
	@Test
	public void createUserActionRecord(){
//		DBUserActionRecord record = new DBUserActionRecord();
//		record.setPermitItem("permit..");
//		record.setUsername("username-test");
//		record.setResult(DBUserActionRecord.ActionResult.SUCCESS);
//		record.setActionParam("564613");
//		service.createUserActionRecord(record);
	}
	  
	@Test
	public void selectUserActionRecordByPaging(){
		System.out.println(service.selectUserActionRecordList(25, 0, "job409").size());
	}
	
	@Test
	public void selectUserActionRecordCount(){
		System.out.println(service.selectUserActionRecordCount(null));
	}
}
