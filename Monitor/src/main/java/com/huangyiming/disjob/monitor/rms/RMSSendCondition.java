package com.huangyiming.disjob.monitor.rms;

import java.util.concurrent.atomic.AtomicInteger;

import com.huangyiming.disjob.monitor.rms.pojo.RMSMonitorInfo;

/**
 * 目前发送报警的条件机制只有次数
 * @author Disjob
 *
 */ 
public class RMSSendCondition extends AbstractRMSSendCondition{

	public RMSSendCondition(RMSMonitorInfo observiable, Integer v) {
		super(observiable, v);
	}

	/**
	 * 
	 */
	private AtomicInteger checkCount = new AtomicInteger(); ;//检测的次数

	@Override
	public boolean isFinished() {
		int count = checkCount.incrementAndGet();
		if(count >= getValue()){
			checkCount.set(0);
			return true ;
		}
		return false;
	}

	public void updateSendCondition(int target){
		synchronized (this) {
			this.value = target;
		}
	}

}
