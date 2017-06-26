package com.huangyiming.disjob.monitor.alarm.service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.huangyiming.disjob.common.Constants;
import com.huangyiming.disjob.common.thread.ExecutorFactory;
import com.huangyiming.disjob.common.util.DateUtil;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.common.util.ThreadSafeTreeMap;
import com.huangyiming.disjob.monitor.pojo.MessagePiple;
import com.huangyiming.disjob.monitor.rms.CommonRMSMonitor;
import com.huangyiming.disjob.monitor.rms.MonitorType;
import com.huangyiming.disjob.monitor.service.JobService;
import com.huangyiming.disjob.monitor.util.MonitorSpringWorkFactory;
import com.huangyiming.disjob.quence.Command;
import com.huangyiming.disjob.quence.TaskExecuteException;
import com.huangyiming.disjob.monitor.alarm.pojo.AlarmInfo;
import com.huangyiming.disjob.monitor.alarm.pojo.AlarmType;

/**
 * 
 * @author Disjob
 *
 */
@Service("messagePipleService")
public class MessagePipleService {
	
	private ConcurrentHashMap<String, MessagePiple> messagesPipleMap = new ConcurrentHashMap<String, MessagePiple>() ;
	
	private TreeMap<MessagePiple, String> sortMessagesPipleMap = new ThreadSafeTreeMap<MessagePiple, String>();
	
	@PostConstruct
	public void init(){
		
		ExecutorFactory.getScheduledExecutorService().scheduleAtFixedRate(new Command() {
			
			@Override
			public void executeSuccess() {
//				LoggerUtil.debug("at the time:"+DateUtil.getFormat(new Date(), DateUtil.YYYY_MM_DD_HH_MM_SS)+" scan all of the job whether time out or not.");
			}
			
			@Override
			public void executeException(String execeptionMsg) {
//				LoggerUtil.error("at the time:"+DateUtil.getFormat(new Date(), DateUtil.YYYY_MM_DD_HH_MM_SS)+" scan all of the job whether time out or not catch the exeception:"+execeptionMsg);
			}
			
			@Override
			public void execute() throws TaskExecuteException {
				Set<MessagePiple> messagePiples = new HashSet<MessagePiple>(sortMessagesPipleMap.keySet());
				for(MessagePiple messagePiple : messagePiples){
					long offsetTime = (System.currentTimeMillis() - messagePiple.getEndTime()) /1000;//单位：s
					offsetTime = (offsetTime <=0 ? 0 : offsetTime );
					if( (offsetTime / (messagePiple.getTimeOut()-1)) >= Constants.TIMEOUT_TIMES){
						String requestId = messagePiple.getRequestId();
						MessagePipleService.this.messagesPipleMap.remove(requestId);
						MessagePipleService.this.sortMessagesPipleMap.remove(messagePiple);
						AlarmInfo alarmInfo = new AlarmInfo(messagePiple.getGroupName(), messagePiple.getJobName(), String.valueOf(AlarmType.JOB_TIME_OUT), messagePiple.getRequestId()); 
						CommonRMSMonitor.sendBusiness(MonitorType.Business.JOB_TIMEOUT, "job time out " + alarmInfo.toString(), requestId);
						MonitorSpringWorkFactory.getRtxMsgPushService().notify(alarmInfo);
						MonitorSpringWorkFactory.getDBJobBasicInfoService().updateJobTimeOut(messagePiple.getRequestId());
						//一个周期处理，卸载相关数据
						JobService jobService = MonitorSpringWorkFactory.getJobService();
						jobService.removeJobTracker(requestId);
						jobService.removeAlarmCondition(requestId);
						LoggerUtil.info(requestId+"; remove at:"+DateUtil.getFormat(new Date(), DateUtil.YYYY_MM_DD_HH_MM_SS));
					}
				}
			}
		}, 1, 1, TimeUnit.SECONDS);
	}
	/**
	 * 开始调度一个job 的时候 就会记录这个 job 的一个 超时时间
	 * @param messagePiple
	 */
	public void putMessagePiple(MessagePiple messagePiple){
		
		messagesPipleMap.putIfAbsent(messagePiple.getRequestId(), messagePiple);
		sortMessagesPipleMap.put(messagePiple, messagePiple.getRequestId());
	}
	
	/**
	 * 
	 * @param requestId
	 * @return
	 */
	public MessagePiple getMessagePiple (String requestId){
		
		return messagesPipleMap.get(requestId);
	}
	
	/**
	 * 
	 * @param requestId
	 * @param processTime
	 */
	public void offerMessage(String requestId,long processTime){
		
		MessagePiple messagePiple = this.messagesPipleMap.get(requestId);
		if(messagePiple == null){
			return ;
		}
		
		try {
			String infor = "";
			if(processTime > messagePiple.getTimeOut()){
				infor = "group name:"+messagePiple.getGroupName()+";job name:"+messagePiple.getJobName()+";receive time: "+messagePiple.getReceiveMessageTime()+";end time:"+DateUtil.getFormat(new Date(messagePiple.getEndTime()),DateUtil.YYYY_MM_DD_HH_MM_SS)+"; "+messagePiple.getRequestId() + " 已经超时："+messagePiple.getTimeOut();
				//AlarmInfo alarmInfo = new AlarmInfo(messagePiple.getGroupName(), messagePiple.getJobName(), String.valueOf(AlarmType.JOB_TIME_OUT), messagePiple.getRequestId()); 

				AlarmInfo alarmInfo = new AlarmInfo(messagePiple.getGroupName(), messagePiple.getJobName(),String.valueOf(AlarmType.JOB_TIME_OUT),messagePiple.getRequestId());
				CommonRMSMonitor.sendBusiness(MonitorType.Business.JOB_TIMEOUT, alarmInfo.toString(), requestId);
				MonitorSpringWorkFactory.getRtxMsgPushService().notify(alarmInfo);
				MonitorSpringWorkFactory.getDBJobBasicInfoService().updateJobTimeOut(messagePiple.getRequestId());
				LoggerUtil.warn(infor);
			}else{
				infor = "group name:"+messagePiple.getGroupName()+";job name:"+messagePiple.getJobName()+";receive time: "+messagePiple.getReceiveMessageTime()+";end time:"+DateUtil.getFormat(new Date(messagePiple.getEndTime()),DateUtil.YYYY_MM_DD_HH_MM_SS)+"; "+messagePiple.getRequestId() + " 已经耗时："+processTime+";TIME OUT:"+messagePiple.getTimeOut();
				LoggerUtil.info(infor);
			}
		}finally{
			LoggerUtil.info(requestId+"; remove at:"+DateUtil.getFormat(new Date(), DateUtil.YYYY_MM_DD_HH_MM_SS));
			this.messagesPipleMap.remove(requestId);
			this.sortMessagesPipleMap.remove(messagePiple);
		}
	}
}
