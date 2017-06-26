package com.huangyiming.disjob.monitor.rms;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import com.huangyiming.disjob.common.util.LoggerUtil;

public class MonitorClient {

	private HttpClient client;
	private String sendString;
	public MonitorClient(String sendString) {
		this.sendString = sendString; 
	}

	{
		//重试客户端, 重试三次, 时间 10S. 
		client = new DefaultHttpClient();
 		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 8000); 
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 8000);

	}

	public MonitorResponse execute(MonitorRequest monitorPostMethod) {
		MonitorResponse monitorResponse = null;
		try {
			LoggerUtil.info("[ HttpClient] execute start: "+sendString);
			HttpResponse httpResponse = client.execute(monitorPostMethod.getHttpPost());
			LoggerUtil.info("[ HttpClient] execute end: "+sendString+"; httpResponse:"+httpResponse);
			monitorResponse = MonitorResponse.getMonitorResponse(httpResponse);
		}catch (UnknownHostException e){
			LoggerUtil.error("无法连接到地址 :" + e.getMessage());
		} catch (IOException e) {
			LoggerUtil.error("MonitorClient 发送http请求时出错", e);
		}catch (Exception e) {
			LoggerUtil.error("MonitorClient 发送http请求时出错 [others]",e);
		}
		return monitorResponse;
	}
}
