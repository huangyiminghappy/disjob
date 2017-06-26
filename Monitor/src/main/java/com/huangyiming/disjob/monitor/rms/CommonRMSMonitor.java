package com.huangyiming.disjob.monitor.rms;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.monitor.rms.pojo.RMSMonitorInfo;
import com.huangyiming.disjob.monitor.rms.pojo.SendMonitorMessage;
import com.huangyiming.disjob.monitor.service.RMSMonitorService;
import com.huangyiming.disjob.monitor.util.MonitorSpringWorkFactory;

public class CommonRMSMonitor extends BaseRMSMonitor {
	
	//这是统一按类型分
	public final static Map<String,AbstractRMSSendCondition> monitorIndexSendCondition = new ConcurrentHashMap<String,AbstractRMSSendCondition>();
	
	//按业务分。比如job 超时监控只监控本身的 一个 job.key-> group_jobname
	public final static Map<String,AbstractRMSSendCondition> businessSendCondition = new ConcurrentHashMap<String,AbstractRMSSendCondition>();
	
	public final static List<String> businessMnitorIndex = 
			Arrays.asList(MonitorType.Business.JOB_RPC_LIST_EMPTY.getIndex(),
					MonitorType.Business.JOB_TIMEOUT.getIndex(),
					MonitorType.Business.RPC_RESPONSE_EXCEPTION.getIndex());
	
	public CommonRMSMonitor(RMSMonitorInfo monitorInfo, String message) {
		super(monitorInfo, message);
	}

	private static RMSMonitorService rmsMonitorService = MonitorSpringWorkFactory.getRMSMonitorService();
	
	private static RMSMonitorInfo doBusinessMonitor(String monitorIndex, String message, String requestId){
		RMSMonitorInfo monitorInfo = rmsMonitorService.getMonitorInfoByTypeAndRequestId(monitorIndex, requestId);
		return send(monitorIndex,monitorInfo,message);
	}
	
	public static RMSMonitorInfo sendNetWork(MonitorType.NetWork monitorType,String message) {
		RMSMonitorInfo monitorInfo = rmsMonitorService.getMonitorInfoByType(monitorType.getIndex());
		return send(monitorType.getIndex(), monitorInfo, message);
	}
	
	public static void handlerException(String index, String message, String requestId, Exception e) {
		try {
			LoggerUtil.error("[RMS Monitor] got an exception when send rms monitor , monitorIndex [" + index + "],requestId [" + requestId + "],message [" + message + "]", e.getMessage());			
		} catch (Exception e2) {
			LoggerUtil.error("[RMS Monitor] almost donot run here, just for avoid call pro interrupted");
		}
	}

	public static RMSMonitorInfo sendSystem(MonitorType.System monitorType,String message) {
		RMSMonitorInfo monitorInfo = rmsMonitorService.getMonitorInfoByType(monitorType.getIndex());
		return send(monitorType.getIndex(), monitorInfo, message);
	}
	
	public static void handlerException(String index, String message, Exception e) {
		handlerException(index, message, "-", e);
	}

	public static RMSMonitorInfo sendSystem(MonitorType.System monitorType,String message, String requestId) {
		RMSMonitorInfo monitorInfo = rmsMonitorService.getMonitorInfoByType(monitorType.getIndex());
		message = message + "[requestId is " + requestId + "]";
		return send(monitorType.getIndex(), monitorInfo, message);
	}
	
	public static RMSMonitorInfo sendBusiness(MonitorType.Business monitorType, String message, String requestId) {
		try {
			return doBusinessMonitor(monitorType.getIndex(), message ,requestId);			
		} catch (Exception e) {
			handlerException(monitorType.getIndex(), message ,requestId, e);
		}
		return null;
	}
	/**
	 * 业务方 发送报警 调用接口
	 * @param monitorType 
	 * @param message
	 * @param jobGroup
	 * @param jobName
	 * @return
	 */
	public static RMSMonitorInfo sendBusiness(MonitorType.Business monitorType, String message, String jobGroup,String jobName) {
		try {
			RMSMonitorInfo monitorInfo = rmsMonitorService.getMonitorInfo(monitorType.getIndex(), jobGroup, jobName);
			return send(monitorType.getIndex(), monitorInfo, message);
		} catch (Exception e) {
			handlerException(monitorType.getIndex(), message, e);
		}
		return null;
	}
	private static RMSMonitorInfo send(String monitorIndex,RMSMonitorInfo monitorInfo, String message){
		AbstractRMSSendCondition sendCondition = null ;
		if(businessMnitorIndex.contains(monitorIndex)){//跟业务方相关的报警机制
			String key = monitorInfo.getGroup()+"_"+monitorInfo.getJobName();
			sendCondition = checkAndGetCondition(businessSendCondition,key,monitorInfo);
		}else{
			sendCondition = checkAndGetCondition(monitorIndexSendCondition,monitorIndex,monitorInfo);
		}

		sendCondition.setSendMonitorMessage(new SendMonitorMessage(monitorIndex,monitorInfo, message));
		sendCondition.handler();
		return monitorInfo;
	}
	
	private static AbstractRMSSendCondition checkAndGetCondition(Map<String,AbstractRMSSendCondition> mapCondition,String key,RMSMonitorInfo monitorInfo){
		AbstractRMSSendCondition sendCondition = null ;
		if(mapCondition.get(key) == null){
			mapCondition.put(key, new RMSSendCondition(monitorInfo,monitorInfo.getSendCondition()));
			sendCondition = mapCondition.get(key);
		}else{
			sendCondition = mapCondition.get(key);
			if(sendCondition instanceof RMSSendCondition){
				RMSSendCondition rmsSendCondtion = (RMSSendCondition) sendCondition;
				if(rmsSendCondtion.getValue() != monitorInfo.getSendCondition()){
					rmsSendCondtion.updateSendCondition(monitorInfo.getSendCondition());//如果发现数据库更新条件次数，则应该同步内存中的数据
				}
			}
		}
		return sendCondition;
	} 
}
