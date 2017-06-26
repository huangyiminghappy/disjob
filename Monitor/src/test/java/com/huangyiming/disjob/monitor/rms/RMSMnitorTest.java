package com.huangyiming.disjob.monitor.rms;

import java.io.IOException;

import org.junit.Test;

import com.huangyiming.disjob.monitor.db.service.AbstractTest;
import com.huangyiming.disjob.monitor.rms.CommonRMSMonitor;
import com.huangyiming.disjob.monitor.rms.MonitorType;
import com.huangyiming.disjob.monitor.rms.SelfTestRMSMonitor;

public class RMSMnitorTest extends AbstractTest{

	@Test
	public void selfTest(){
		//spring 初始化时候启动 , 无需手动启动
		SelfTestRMSMonitor.start();
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSendMonitor(){
		
		//[code:10007,msg:The Error Code is del]
		//CommonRMSMonitor.sendBusiness(MonitorType.Business.RPC_RESPONSE_EXCEPTION, "RPC_RESPONSE_EXCEPTION", "8aa08175586c479a01586c479ac90000");
		
		//[code:10007,msg:The Error Code is del]
		CommonRMSMonitor.sendNetWork(MonitorType.NetWork.CHANNEL_UNAVAIABLE, "CHANNEL_UNAVAIABLE");
		
		//[code:0,msg:Success]
		//CommonRMSMonitor.sendSystem(MonitorType.System.EJOB_EXCEPTION, "EJOB_EXCEPTION", "8aa08175586c479a01586c479ac90000");
		
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
