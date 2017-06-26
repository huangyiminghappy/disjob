package com.huangyiming.disjob.java.listener;

import com.huangyiming.disjob.event.ObjectEvent;
import com.huangyiming.disjob.event.ObjectListener;
import com.huangyiming.disjob.java.bean.StartUpConfig;
import com.huangyiming.disjob.java.event.DisJobStartUp;
import com.huangyiming.disjob.java.event.EventType;

public class DisJobStartFinishListener implements ObjectListener<StartUpConfig> {
	
	private DisJobStartUp disJobStartUp ;
	
	public DisJobStartFinishListener(DisJobStartUp disJobStartUp) {
		this.disJobStartUp = disJobStartUp ;
	}
	
	public void onEvent(ObjectEvent<StartUpConfig> event) {
		this.disJobStartUp.setCurrentState(DisJobStartUp.StartUpState.STARTUP_FINISH);
		this.disJobStartUp.removeListener(EventType.START_FINISH);
		this.disJobStartUp.removeListener(EventType.START_UP);
	}
}
