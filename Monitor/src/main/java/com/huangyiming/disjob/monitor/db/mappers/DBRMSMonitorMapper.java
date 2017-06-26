package com.huangyiming.disjob.monitor.db.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huangyiming.disjob.monitor.rms.pojo.RMSMonitorInfo;
import com.huangyiming.disjob.monitor.db.domain.DBMonitorErrorInfo;

public interface DBRMSMonitorMapper {

	public DBMonitorErrorInfo getMonitorErrorInfo(@Param("monitorIndex")String monitorIndex);

	public List<RMSMonitorInfo> getSelfTestMonitorInfo();

	public String getProjectCodeByGroupName(@Param("groupName") String groupName);
	
	public String getTokenByProjectCode(@Param("projectCode") String projectCode);
	
}
