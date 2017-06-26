package com.huangyiming.disjob.monitor.db.mappers;

import com.huangyiming.disjob.monitor.db.domain.DBMonitorAlarmInfo;

public interface DBMonitorAlarmMapper {

	public void insert(DBMonitorAlarmInfo monitorAlarmInfo);
	
	public DBMonitorAlarmInfo findByIndex(DBMonitorAlarmInfo dbMonitorAlarmInfo);
}
