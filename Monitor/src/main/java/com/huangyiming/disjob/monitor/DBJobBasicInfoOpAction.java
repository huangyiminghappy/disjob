package com.huangyiming.disjob.monitor;

import java.util.concurrent.TimeUnit;

import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.monitor.db.service.DBJobBasicInfoService;
import com.huangyiming.disjob.monitor.util.MonitorSpringWorkFactory;
import com.huangyiming.disjob.quence.Action;
import com.huangyiming.disjob.quence.TaskExecuteException;
import com.huangyiming.disjob.monitor.db.domain.DBJobBasicInfo;

public class DBJobBasicInfoOpAction extends Action{
	public static final int BASICINFO_CREATE_OP = 1 ;
	public static final int BASICINFO_UPDATE_OP = 2 ;
	 
	private DBJobBasicInfoService jobService ;
	private DBJobBasicInfo info ;
	private int opType ;
	public DBJobBasicInfoOpAction(DBJobBasicInfo info,int opType) {
		this.jobService = MonitorSpringWorkFactory.getDBJobBasicInfoService() ;
		this.info = info ;
		this.opType = opType ;
	}
	@Override
	public void execute() throws TaskExecuteException {
		if(jobService== null){
			return ;
		}
		switch (opType) {
		case DBJobBasicInfoOpAction.BASICINFO_CREATE_OP:
			this.jobService.create(info);
			break;
		case DBJobBasicInfoOpAction.BASICINFO_UPDATE_OP:
			boolean isFlag = this.jobService.update(info);
			int failCount = 3 ;
			while(!isFlag&&failCount-->0){
				isFlag = this.jobService.update(info);
				try {
					TimeUnit.MILLISECONDS.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			if(!isFlag){
				LoggerUtil.warn(info.getUuid()+"; update after 3 count is fail :"+info.toString());
			}
			break;
		default:
			LoggerUtil.debug("DBJobBasicInfoOpAction 不支持的操作类型:"+opType);
			break;
		}
		
	}
}
