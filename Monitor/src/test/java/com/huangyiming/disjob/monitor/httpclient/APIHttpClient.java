package com.huangyiming.disjob.monitor.httpclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.huangyiming.disjob.monitor.rms.util.MD5Util;
import com.google.gson.JsonObject;

public class APIHttpClient {
	// 接口地址
	private static String apiURL = "http://www.rms110.com/api-source?project_code=DisJob_services";
	private Log logger = LogFactory.getLog(this.getClass());
 	private HttpClient httpClient = null;
	private HttpPost method = null;
	private long startTime = 0L;
	private long endTime = 0L;
	private int status = 0;

	/**
	 * 接口地址
	 * 
	 * @param url
	 */
	public APIHttpClient(String url) {

		if (url != null) {
			this.apiURL = url;
		}
		if (apiURL != null) {
			httpClient = new DefaultHttpClient();
			method = new HttpPost(apiURL);

		}
	}

	/**
	 * 调用 API
	 * 
	 * @param parameters
	 * @return
	 */
	public String post(String parameters) {
		String body = null;
		logger.info("parameters:" + parameters);

		if (method != null & parameters != null
				&& !"".equals(parameters.trim())) {
			try {

				// 建立一个NameValuePair数组，用于存储欲传送的参数
				method.addHeader("Content-type",
						"application/json; charset=utf-8");
				method.setHeader("Accept", "application/json");
				method.setEntity(new StringEntity(parameters));
				startTime = System.currentTimeMillis();

				HttpResponse response = httpClient.execute(method);
				endTime = System.currentTimeMillis();
				int statusCode = response.getStatusLine().getStatusCode();

				logger.info("statusCode:" + statusCode);
				logger.info("调用API 花费时间(单位：毫秒)：" + (endTime - startTime));
				if (statusCode != HttpStatus.SC_OK) {
					logger.error("Method failed:" + response.getStatusLine());
					status = 1;
				}

				// Read the response body
				body = EntityUtils.toString(response.getEntity());

			} catch (IOException e) {
				// 网络错误
				status = 3;
			} finally {
				logger.info("调用接口状态：" + status);
			}

		}
		return body;
	}

	public static void main(String[] args) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
 		APIHttpClient ac = new APIHttpClient(apiURL);
		/*
		 * JsonArray arry = new JsonArray(); JsonObject j = new JsonObject();
		 * j.addProperty("orderId", "中文"); j.addProperty("createTimeOrder",
		 * "2015-08-11"); arry.add(j);
		 */
		JsonObject obj = new JsonObject();
		// obj.addProperty("token", "RqDcKC1BYNIORNZ6ndZ4");
		JsonObject data = new JsonObject();
		data.addProperty("point_code", "SJC93229");
		data.addProperty("error_code", "230101");
		data.addProperty("server_ip", "10.32.1.234");
		data.addProperty("server_name", "Disjob");
		data.addProperty("notice_time", "2016-11-02 18:00:00");
		JsonObject info = new JsonObject();
		info.addProperty("info", "this is a test");
		data.add("content", info);
		data.addProperty("level", 2);
		data.addProperty("is_test", 0);
		obj.add("data", data);
		System.out.println("data:" + data);

		String str = "RqDcKC1BYNIORNZ6ndZ4" + data.toString();
		String token = MD5Util.textToMD5L32(str);
		System.out.println("token:" + token);
		obj.addProperty("token", token);
		System.out.println("obj:" + obj.toString());

		System.out.println(ac.post(obj.toString()));
	}

	public static String EncoderByMd5(String str)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		// 确定计算方法
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		// BASE64Encoder base64en = new BASE64Encoder();
		// 加密后的字符串
		// String newstr=base64en.encode(md5.digest(str.getBytes("utf-8")));
		String newstr = new String(md5.digest(str.getBytes("utf-8")), "UTF-8");

		return newstr;
	}

	/**
	 * 0.成功 1.执行方法失败 2.协议错误 3.网络错误
	 * 
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the startTime
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * @return the endTime
	 */
	public long getEndTime() {
		return endTime;
	}

}
