package com.huangyiming.disjob.monitor.rms;

import java.io.IOException;

import org.junit.Test;

import com.huangyiming.disjob.monitor.db.service.AbstractTest;
import com.huangyiming.disjob.monitor.rms.CommonRMSMonitor;
import com.huangyiming.disjob.monitor.rms.MonitorType;

public class CommonRMSMonitorTest extends AbstractTest{

	@Test
	public void test(){
		CommonRMSMonitor.sendSystem(MonitorType.System.CONNECT_WAITING_TIMEOUT, "aaa");
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
