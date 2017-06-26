package com.huangyiming.disjob.monitor.db.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huangyiming.disjob.common.Constants;
import com.huangyiming.disjob.monitor.db.mappers.DBMonitorAlarmMapper;
import com.huangyiming.disjob.monitor.util.DBCommonUtil;
import com.huangyiming.disjob.monitor.db.domain.DBMonitorAlarmInfo;

@Service("monitorAlarmService")
public class DBMonitorAlarmService {

	@Autowired
	private DBMonitorAlarmMapper mapper;
	
	public boolean insert(DBMonitorAlarmInfo info){
		if(StringUtils.isEmpty(info.getApplication())){
			info.setApplication(Constants.APPLICATION);
		}
		try{
			mapper.insert(info);
			return true;
		}catch(Throwable e){
			DBCommonUtil.logError(this.getClass(), e);
		}
		return false;
	}
	
	public DBMonitorAlarmInfo find(String index){
		try{
			return mapper.findByIndex(new DBMonitorAlarmInfo(index));
		}catch(Throwable e){
			DBCommonUtil.logError(this.getClass(), e);
		}
		return null;
	}
}
