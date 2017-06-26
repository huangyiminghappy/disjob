package com.huangyiming.disjob.monitor.alarm.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.huangyiming.disjob.common.util.LocalHost;
import com.huangyiming.disjob.monitor.util.DBCommonUtil;
import com.huangyiming.disjob.monitor.db.domain.DBJobAlarmMapping;

/**
 * <pre>
 * 
 *  File: RtxMsgPush.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  负责执行RTX报警通知，注释：由于rtx采用GBK的编码，而工程是utf-8，目前该类文件是GBK格式
 * 
 *  Revision History
 *
 *  Date：		2016年8月4日
 *  Author：		Disjob
 *
 * </pre>
 */
@Service("rtxMsgPushService")
public class RtxMsgPushService extends AbstractMsgPushService{
	
	public static LocalHost local = new LocalHost();
	
	
	private RtxMsgPushService() {

	}
	//推送消息实现
	public void push(String jobGroup,String params){
		if(StringUtils.isNoneEmpty(params)){
			params.replaceAll(" ", "");
		}
		DBJobAlarmMapping info = service.search(jobGroup);
		if(info != null){
			String rtxList = info.getAlarmRtx();
			if(info.isOnOff() && StringUtils.isNoneEmpty(rtxList)){
				for(String receiver:rtxList.split(",")){
					URL url = null;
					try {
						url = new URL( "http" , host, port,"/" + sendImg + "?&receiver=" + receiver + "&sender=" + local.getIp() + sender + "&msg=" + params);
						HttpURLConnection httpconn  =  (HttpURLConnection) url.openConnection();
						if(200 != (int) httpconn.getResponseCode()){
							DBCommonUtil.logError(this.getClass(), new Exception("rtx回复码："+httpconn.getResponseCode()+",异常！"));
						}
						httpconn.disconnect();
					} catch (MalformedURLException e) {
						DBCommonUtil.logError(this.getClass(), e);
					} catch (IOException e){
						DBCommonUtil.logError(this.getClass(), e);
					}
				}
			}
		}
	}
	
}