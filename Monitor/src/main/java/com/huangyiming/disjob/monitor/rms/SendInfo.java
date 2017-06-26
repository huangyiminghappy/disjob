package com.huangyiming.disjob.monitor.rms;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import com.huangyiming.disjob.common.util.LocalHost;
import com.huangyiming.disjob.monitor.rms.pojo.RMSMonitorInfo;
import com.huangyiming.disjob.monitor.rms.util.MD5Util;
import com.google.gson.JsonObject;

public class SendInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	private static final String dateformat = "yyyy-MM-dd HH:mm:ss";
	
	private static final int defaultEncode = 0;
	
	private JsonObject result;
	private JsonObject data;
	private JsonObject content;
	private String token;
	
	private int selfTest = 0;
	
	public SendInfo() {
	}

	public SendInfo(String pointCode, String errorCode, String content, String token) {
		this(pointCode, errorCode, content, token, false);
	}
	public SendInfo(String pointCode, String errorCode, String content, String token, boolean selfTest) {
		setPointCode(pointCode);
		setErrorCode(errorCode);
		setContent(content);
		setToken(token);
		setSelfTest(selfTest);
	}

	public SendInfo(RMSMonitorInfo monitorInfo, String message) {
		setPointCode(monitorInfo.getPointCode());
		setErrorCode(monitorInfo.getErrorCode());
		setContent("[" + monitorInfo.getDescription() + "] " + message);
		setToken(monitorInfo.getToken());
		setSelfTest(monitorInfo.isTest());
	}

	private void setSelfTest(boolean selfTest) {
		if(selfTest){
			this.selfTest = 1;
		}else{
			this.selfTest = 0;
		}
		data.addProperty("is_test", this.selfTest);
	}

	{
		result = new JsonObject();
		data = new JsonObject();
		content = new JsonObject();
		result.add("data", data);
		data.add("content", content);

		LocalHost localhost = new LocalHost();
		data.addProperty("level", 2);
		
		data.addProperty("server_ip", localhost.getIp());
		data.addProperty("server_name", localhost.getHostName());
		data.addProperty("notice_time",
				new SimpleDateFormat(dateformat).format(new GregorianCalendar().getTime()));
	}

	public void setNoticeTime(Date noticetime) {
		data.addProperty("notice_time", new SimpleDateFormat(dateformat).format(noticetime));
	}

	public void setServerIP(String serverip) {
		data.addProperty("server_ip", serverip);
	}

	public void setServerName(String servername) {
		data.addProperty("server_name", servername);
	}

	public void setPointCode(String pointcode) {
		data.addProperty("point_code", pointcode);
	}

	public void setErrorCode(String errorcode) {
		data.addProperty("error_code", errorcode);
	}

	public void setContent(String content) {
		this.content.addProperty("info", content);

	}

	private static String escapeNonAscii(String str) {

		StringBuilder retStr = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			int cp = Character.codePointAt(str, i);
			int charCount = Character.charCount(cp);
			if (charCount > 1) {
				i += charCount - 1; // 2.
				if (i >= str.length()) {
					throw new IllegalArgumentException("truncated unexpectedly");
				}
			}

			if (cp < 128) {
				retStr.appendCodePoint(cp);
			} else {
				retStr.append(String.format("\\u%x", cp));
			}
		}
		return retStr.toString();
	}
	
	public void setToken(String token) {
		this.token = token;
	}

	public String get() {
		String tokenAndData = escapeNonAscii(new StringBuilder().append(this.token).append(data.toString()).toString());
		String token = MD5Util.textToMD5L32(tokenAndData);
		result.addProperty("token", token);
		result.addProperty("encode", defaultEncode);
		return result.toString();
	}
}
