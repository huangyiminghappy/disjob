package com.huangyiming.disjob.monitor.db.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.huangyiming.disjob.monitor.db.domain.DBUserPermit;
import com.huangyiming.disjob.monitor.db.service.DBUserPermitService;

public class DBUserPermitServiceTest extends AbstractTest{

	@Autowired
	DBUserPermitService service;
	
	@Test
	public void createUserActionRecord(){
		DBUserPermit userPermit = new DBUserPermit();
		userPermit.setPermitIterm("userpermit");
		userPermit.setUsername("username");
		service.createPermitItem(userPermit);
	}
	
	@Test
	public void removeUserPermit(){
		DBUserPermit userPermit = new DBUserPermit("username", "userpermit");
		service.removePermitItem(userPermit);
	}
	
	@Test
	public void hasPermit(){
		boolean result = service.hasPermit("test1", "/page/service/serverInfo");
		System.out.println(result);
	}
	
	@Test
	public void hasPermit2(){
		assert !service.hasPermit("test1", "74444");
	}
}
