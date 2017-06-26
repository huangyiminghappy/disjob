package com.huangyiming.disjob.monitor.httpclient;

import java.io.UnsupportedEncodingException;

import com.huangyiming.disjob.monitor.rms.MonitorClient;
import com.huangyiming.disjob.monitor.rms.MonitorRequest;
import com.huangyiming.disjob.monitor.rms.MonitorResponse;
import com.huangyiming.disjob.monitor.rms.SendInfo;

public class MonitorSenderMain {

	public static void main(String[] args) throws UnsupportedEncodingException {

		
		String[][] testArrays = new String[][]{
			{"DisJob_services","SJC93229","230101","RqDcKC1BYNIORNZ6ndZ4"},	//t
			{"DisJob_services","SJC93229","220112","RqDcKC1BYNIORNZ6ndZ4"},	//f
			{"DisJob_services","SJC51876","220110","RqDcKC1BYNIORNZ6ndZ4"},	//f
			{"DisJob_services","SJC93005","230401","RqDcKC1BYNIORNZ6ndZ4"},	//t
			{"DisJob_services","SJC93005","230402","RqDcKC1BYNIORNZ6ndZ4"},	//t
			
			{"DisJob_services","SJC31808","220111","RqDcKC1BYNIORNZ6ndZ4"},	//t
			{"DisJob_services","SJC31808","220113","RqDcKC1BYNIORNZ6ndZ4"},	//t
			{"DisJob_services","SJC31808","220114","RqDcKC1BYNIORNZ6ndZ4"},	//t
			
			{"Financial_system","SJC31808","220111","Vklq83ilqeyAEEov1su5"},
			{"Financial_system","SJC31808","220113","Vklq83ilqeyAEEov1su5"},
			{"Financial_system","SJC31808","220114","Vklq83ilqeyAEEov1su5"}
		};
		for(String[] array : testArrays){
			testSend(array[0],array[1],array[2],array[3]);
		}
	}

//	private static final String url = "http://www.rms110.com/api-source";
	private static final String url = "http://www.rms110.com.trunk.s1cg.egomsl.com/api-source";
	private static void testSend(String projectCode, String pointCode, String errCode, String token) {
		SendInfo sendInfo = new SendInfo(pointCode, errCode, "我是中国woshizhongguo", token);
		String sendString = sendInfo.get();
		MonitorClient client = new MonitorClient("");
		MonitorResponse response = client.execute(new MonitorRequest(
				url, projectCode, sendString));
		System.err.println(response.getCode() + " || " + response.getMsg());
	}

}
