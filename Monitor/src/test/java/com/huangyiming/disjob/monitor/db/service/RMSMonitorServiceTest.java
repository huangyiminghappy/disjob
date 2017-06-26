package com.huangyiming.disjob.monitor.db.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.huangyiming.disjob.monitor.rms.pojo.RMSMonitorInfo;
import com.huangyiming.disjob.monitor.service.RMSMonitorService;

public class RMSMonitorServiceTest extends AbstractTest{

	@Autowired
	private RMSMonitorService monitorService;
	
	@Test
	public void getAllSelfTestPointInfo(){
		int selfTestPointInfoSize = monitorService.getAllSelfTestPointInfo().size();
		System.err.println(selfTestPointInfoSize);
	} 
	
	@Test
	public void getMonitorInfoByTypeAndRequestId() {
		String requestId = "8aa0817958c274630158c274fe56000d";
		RMSMonitorInfo monitorInfo = monitorService.getMonitorInfoByTypeAndRequestId("2", requestId);
		System.err.println(monitorInfo);
	}
	
	@Test
	public void getMonitorInfoByType() {
		RMSMonitorInfo monitorInfo = monitorService.getMonitorInfoByType("2");
		System.err.println(monitorInfo);
	}
}
