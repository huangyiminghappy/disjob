package com.huangyiming.disjob.monitor.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huangyiming.disjob.monitor.db.mappers.DBRMSMonitorMapper;
import com.huangyiming.disjob.monitor.db.service.DBJobBasicInfoService;
import com.huangyiming.disjob.monitor.rms.pojo.RMSMonitorInfo;
import com.huangyiming.disjob.monitor.rms.util.RMSPropertityConfigUtil;
import com.huangyiming.disjob.monitor.db.domain.DBJobBasicInfo;
import com.huangyiming.disjob.monitor.db.domain.DBMonitorErrorInfo;

@Service("rmsMonitorService")
public class RMSMonitorService {

	@Autowired
	DBJobBasicInfoService jobBasicInfoService;
	
	@Autowired
	DBRMSMonitorMapper monitorMapper;
	
	public List<RMSMonitorInfo> getAllSelfTestPointInfo(){
		
		return monitorMapper.getSelfTestMonitorInfo();
	}

	public RMSMonitorInfo getMonitorInfoByTypeAndRequestId(String monitorIndex, String requestId) {
		DBJobBasicInfo dbjobBasicInfo = jobBasicInfoService.findByUuid(requestId);
		if(StringUtils.isEmpty(dbjobBasicInfo.getGroupName())){
			throw new IllegalArgumentException("get [null] groupName from requestId[" + requestId + "]");
		}
		
		return getMonitorInfo(monitorIndex, dbjobBasicInfo.getGroupName(), dbjobBasicInfo.getJobName());
	}

	public RMSMonitorInfo getMonitorInfo(String monitorIndex, String jobGroup,String jobName){
		String projectCode = monitorMapper.getProjectCodeByGroupName(jobGroup);
		if(StringUtils.isEmpty(projectCode)){
			throw new IllegalArgumentException("get [null] projectCode from groupName[" + jobGroup + "]");
		}
		String token = monitorMapper.getTokenByProjectCode(projectCode);
		if(StringUtils.isEmpty(token)){
			throw new IllegalArgumentException("get [null] token from projectCode[" + projectCode + "]");
		}
		RMSMonitorInfo monitorInfo = new RMSMonitorInfo();
		monitorInfo.setToken(token);
		monitorInfo.setProjectCode(projectCode);
		DBMonitorErrorInfo monitorErrorInfo = monitorMapper.getMonitorErrorInfo(monitorIndex);
		monitorInfo.setErrorCode(monitorErrorInfo.getErrorCode());
		monitorInfo.setPointCode(monitorErrorInfo.getPointCode());
		monitorInfo.setDescription(monitorErrorInfo.getDescription());
		monitorInfo.setAvailable(monitorErrorInfo.isAvailable());
		monitorInfo.setSendCondition(monitorErrorInfo.getSendCondition());
		monitorInfo.setGroup(jobGroup);
		monitorInfo.setJobName(jobName);
		return monitorInfo;
	}
	
	public RMSMonitorInfo getMonitorInfoByType(String monitorIndex) {
		RMSMonitorInfo monitorInfo = new RMSMonitorInfo();
		String projectCode = RMSPropertityConfigUtil.getDisJobProjectCode();
		if(StringUtils.isEmpty(projectCode)){
			throw new IllegalArgumentException("get [null] projectCode from default DisJobProjectCode");
		}
		monitorInfo.setProjectCode(projectCode);
		DBMonitorErrorInfo monitorErrorInfo = monitorMapper.getMonitorErrorInfo(monitorIndex);
		monitorInfo.setErrorCode(monitorErrorInfo.getErrorCode());
		monitorInfo.setPointCode(monitorErrorInfo.getPointCode());
		monitorInfo.setDescription(monitorErrorInfo.getDescription());
		monitorInfo.setAvailable(monitorErrorInfo.isAvailable());
		monitorInfo.setSendCondition(monitorErrorInfo.getSendCondition());
		String token = monitorMapper.getTokenByProjectCode(projectCode);
		if(StringUtils.isEmpty(token)){
			throw new IllegalArgumentException("get [null] token from projectCode[" + projectCode + "]");
		}
		monitorInfo.setToken(token);
		return monitorInfo;
	}
}
