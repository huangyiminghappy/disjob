package com.huangyiming.disjob.monitor.db.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huangyiming.disjob.common.util.PermitUtil;
import com.huangyiming.disjob.monitor.db.domain.DBPermitItem;
import com.huangyiming.disjob.monitor.db.domain.DBUser;
import com.huangyiming.disjob.monitor.db.domain.DBUserActionRecord;
import com.huangyiming.disjob.monitor.db.domain.DBUserPermit;

/**
 * 权限相关服务类, 供Controller使用
 * @author chengangxiong
 *
 */
@Service("permitService")
public class PermitService {

	@Autowired
	DBPermitItemService dbPermitItemService;
	
	@Autowired
	DBUserPermitService dbUserPermitService;
	
	@Autowired
	DBUserActionRecordService dbUserActionRecordService;
	
	/**
	 * filter中检查user是否具有url的权限
	 * @return
	 */
	public boolean hasPermit(DBUser dbUser, String path){
		if(PermitUtil.ADMINISTRATOR_ROLE.equals(dbUser.getRoleName())){
			return true;
		}
		return doHasPermit(dbUser.getUsername(), path);
	}
	
	/**
	 * 添加权限
	 * @param username 用户名
	 * @param permitId 权限Id
	 */
	public void addUserPermit(String username, String permitId){
		dbUserPermitService.createPermitItem(new DBUserPermit(username, permitId));
	}
	
	/**
	 * 去掉某个用户的权限
	 * @param username 用户名
	 * @param permitId 权限Id
	 */
	public void removeUserPermit(String username, String permitId){
		dbUserPermitService.removePermitItem(new DBUserPermit(username, permitId));
	}

	public boolean doHasPermit(String username, String permitItem) {
		return dbUserPermitService.hasPermit(username, permitItem);
	}
	
	public boolean hasPermit(String username, String permitid) {
		DBPermitItem dbPermitItem = dbPermitItemService.getPermitById(permitid);
		if(dbPermitItem != null){
			return doHasPermit(username, dbPermitItem.getUrl());			
		}else{
			return true;
		}
	}
	
	public void createUserActionRecord(DBUserActionRecord userActionRecord) {
		dbUserActionRecordService.createUserActionRecord(userActionRecord);
	}

	public void createUserActionRecord(String username, String pathInfo, String host, String addr) {
		createUserActionRecord(username, pathInfo, null, host, addr);
	}
	
	public void createUserActionRecord(String username, String pathInfo, String param, String host, String addr) {
		DBPermitItem permitItem = dbPermitItemService.getPermitByUri(pathInfo);
		if(permitItem != null && "ACTION".equals(permitItem.getItemType()) && !"SELECT".equals(permitItem.getOperateType())){
			DBUserActionRecord record = new DBUserActionRecord(username, permitItem.getId(), param);
			record.setHost(host);
			record.setAddr(addr);
			dbUserActionRecordService.createUserActionRecord(record);
		}
	}
}
