package com.huangyiming.disjob.monitor.rms;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import com.huangyiming.disjob.common.util.LoggerUtil;
import com.google.gson.Gson;

public class MonitorResponse{
  
	private String code;
	
	private String msg;
	
	private String[] errors;
	
	public static MonitorResponse getMonitorResponse(HttpResponse httpResponse) {
		MonitorResponse monitorResponse = null;
		try {
			java.lang.String entity = EntityUtils.toString(httpResponse.getEntity());
			monitorResponse = new Gson().fromJson(entity, MonitorResponse.class);
		} catch (Exception e) {
			LoggerUtil.error("MonitorResponse 将httpResponse转换为String时出错", e);
		}
		return monitorResponse;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String[] getErrors() {
		return errors;
	}

	public void setErrors(String[] errors) {
		this.errors = errors;
	}

	public java.lang.String toString() {
		return "MonitorResponse [code:" + code + ",msg:" + msg + "]";
	}
}
