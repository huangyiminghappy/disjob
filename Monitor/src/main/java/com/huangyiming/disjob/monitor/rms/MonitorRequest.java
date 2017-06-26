package com.huangyiming.disjob.monitor.rms;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

public class MonitorRequest{

	private HttpPost httpPost;

	public MonitorRequest(String url, String projectCode, String sendString) {
		httpPost = new HttpPost(url + "?project_code=" + projectCode);
		httpPost.addHeader("Content-type","application/json; charset=utf-8");
		httpPost.setHeader("Accept", "application/json");
		httpPost.setEntity(new StringEntity(sendString,ContentType.APPLICATION_JSON));
	}

	public HttpUriRequest getHttpPost() {
		return httpPost;
	}

}
