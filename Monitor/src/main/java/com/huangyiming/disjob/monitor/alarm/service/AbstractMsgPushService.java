package com.huangyiming.disjob.monitor.alarm.service;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;

import com.huangyiming.disjob.common.thread.ExecutorFactory;
import com.huangyiming.disjob.common.util.DateUtil;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.monitor.db.service.DBJobAlarmMappingService;
import com.huangyiming.disjob.quence.Command;
import com.huangyiming.disjob.quence.TaskExecuteException;
import com.huangyiming.disjob.monitor.alarm.pojo.AlarmInfo;


/**
 * 可提供各种类型的报警机制。目前实现的事 使用 RTX 来报警。后期可能会使用短信报警，微信报警等等其他方式。只要实现这个类的抽象方法即可
 * @author Disjob
 *
 */
public abstract class AbstractMsgPushService implements MsgPushService{

	@Resource
	protected DBJobAlarmMappingService service;
	
	@Value("${rtx.sendImg}")
	protected String sendImg;  //  RTX发送消息接口 
	
	@Value("${rtx.host}")
	protected String host;  //  RTX服务器地址 
	
	@Value("${rtx.port}")
	protected int  port;  //  RTX服务器监听端口 
	
	@Value("${rtx.sender}")
	protected String sender;// 发送人
	
	@Override
	public void notify(AlarmInfo alarmInfo) {
		ExecutorFactory.getSignalThreadPoolService().execute(new Msg(alarmInfo.getJobGroup(),"group:"+alarmInfo.getJobGroup()+",job:"+alarmInfo.getLocation()+","+alarmInfo.getType()+",requestId:"+alarmInfo.getReason()));

	}

	@Override
	public void notify(String jobGroup, String location, String type,String reason) {
 
		ExecutorFactory.getSignalThreadPoolService().execute(new Msg(jobGroup,location + "," + type + "," + reason));
	}

	public abstract void push(String jobGroup, String paramters);
	
	//保存发送消息的内容参数，可执行的任务
	public class Msg extends Command {
		private String jobGroup;
		private String params;

		public Msg(String jobGroup, String params) {
			this.jobGroup = jobGroup;
			this.params = params;
		}

		@Override
		public void executeException(String execeptionMsg) {
			LoggerUtil.error("[ alarm ] jobGroup:"+jobGroup+"; params:"+params+" push message at:"+DateUtil.getFormat(new Date(), DateUtil.YYYY_MM_DD_HH_MM_SS)+"; "+execeptionMsg);
		}

		@Override
		public void execute() throws TaskExecuteException {
			push(jobGroup, params);
		}

		@Override
		public void executeSuccess() {
			
			LoggerUtil.debug("[ alarm ] jobGroup:"+jobGroup+"; params:"+params+" push message at:"+DateUtil.getFormat(new Date(), DateUtil.YYYY_MM_DD_HH_MM_SS));
		}
	}
}
