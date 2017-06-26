package com.huangyiming.disjob.monitor.alarm.service;

import com.huangyiming.disjob.monitor.alarm.pojo.AlarmInfo;

public interface MsgPushService {

	public void notify(String jobGroup, String location, String type,String reason);

	public void notify(AlarmInfo alarmInfo);
}
