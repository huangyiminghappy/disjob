package com.huangyiming.disjob.monitor.util;

import org.mybatis.spring.SqlSessionTemplate;

import com.huangyiming.disjob.common.util.SpringWorkFactory;
import com.huangyiming.disjob.monitor.db.service.DBJobAlarmMappingService;
import com.huangyiming.disjob.monitor.db.service.DBJobBasicInfoService;
import com.huangyiming.disjob.monitor.service.JobService;
import com.huangyiming.disjob.monitor.service.RMSConfig;
import com.huangyiming.disjob.monitor.service.RMSMonitorService;
import com.huangyiming.disjob.monitor.alarm.service.MessagePipleService;
import com.huangyiming.disjob.monitor.alarm.service.RtxMsgPushService;

public class MonitorSpringWorkFactory extends SpringWorkFactory{

	public final static MessagePipleService getMessagePipleService(){
		
		return (MessagePipleService) getWorkObject("messagePipleService") ;
	}
	
	public final static DBJobBasicInfoService getDBJobBasicInfoService(){
		
		return (DBJobBasicInfoService)getWorkObject("jobBasicInfoService");
	}
	
	public final static JobService getJobService(){
		
		return (JobService) getWorkObject("jobService");
	}
	
	public final static RtxMsgPushService getRtxMsgPushService(){
		
		return (RtxMsgPushService) getWorkObject("rtxMsgPushService");
	}
	
	public final static DBJobAlarmMappingService getAlarmMappingService(){
		return (DBJobAlarmMappingService) getWorkObject("jobAlarmMappingService");
	}
	
	public final static SqlSessionTemplate getSqlSessionTemplate(){
		
		return (SqlSessionTemplate) getWorkObject("sqlSession");
	}
	
	public final static RMSConfig getRMSConfig(){
		return (RMSConfig) getWorkObject("rmsConfig");
	}
	
	public static RMSMonitorService getRMSMonitorService() {
		return (RMSMonitorService) SpringWorkFactory.getWorkObject("rmsMonitorService");
	}
}
