package com.huangyiming.disjob.monitor.rms;

import com.huangyiming.disjob.common.util.DateUtil;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.event.BaseCondition;
import com.huangyiming.disjob.monitor.rms.pojo.RMSMonitorInfo;
import com.huangyiming.disjob.monitor.rms.pojo.SendMonitorMessage;
import com.huangyiming.disjob.quence.Log;

public abstract class AbstractRMSSendCondition extends BaseCondition<RMSMonitorInfo, Integer>{
	protected SendMonitorMessage monitrMessage ;
	public AbstractRMSSendCondition(RMSMonitorInfo observiable, Integer v) {
		super(observiable, v);
	}
	
	/**
	 * 每次发送前设置需要发送的 最新 报警消息
	 * @param monitrMessage
	 */
	public void setSendMonitorMessage(SendMonitorMessage monitrMessage) {
		this.monitrMessage = monitrMessage;
	}
	
	@Override
	public void handler() {
		if(this.isFinished()){
			try {
				LoggerUtil.info("[ rms monitor handler]:"+monitrMessage.getMessage()+"; handler at time:"+DateUtil.getFormatNow());
				new CommonRMSMonitor(monitrMessage.getRmsMonitorInfo(), monitrMessage.getMessage()).send();
			} catch (Exception e) {
				LoggerUtil.error("[ rms monitor handler error]:"+monitrMessage.getMessage()+"; handler at time:"+DateUtil.getFormatNow(),e);
				CommonRMSMonitor.handlerException(monitrMessage.getMonitorIndex(), monitrMessage.getMessage() , e);
			}
		}else{
			SendInfo sendInfo = new SendInfo(monitrMessage.getRmsMonitorInfo(), monitrMessage.getMessage());
			String sendString = sendInfo.get();
			Log.info(sendString +" 还未到达检测次数："+ getValue());
		}
	}
	
}
