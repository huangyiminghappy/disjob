package com.huangyiming.disjob.monitor.db.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.huangyiming.disjob.monitor.alarm.service.RtxMsgPushService;

/**
 * <pre>
 * 
 *  File: DBJobInfoServiceTest.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  rtx测试
 * 
 *  Revision History
 *
 *  Date：		2016年6月24日
 *  Author：		Disjob
 *
 * </pre>
 */
public class RtxMsgPushServiceTest extends AbstractTest {
	
	@Autowired
	private RtxMsgPushService service;
	
	@Test
	public void test0() {
		service.notify("oms","test-local", "1", "no found uuid！");
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
