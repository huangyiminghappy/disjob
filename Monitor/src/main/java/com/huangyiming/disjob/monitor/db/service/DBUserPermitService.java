package com.huangyiming.disjob.monitor.db.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huangyiming.disjob.monitor.db.mappers.DBPermitItemMapper;
import com.huangyiming.disjob.monitor.db.mappers.DBUserPermitMapper;
import com.huangyiming.disjob.monitor.db.domain.DBPermitItem;
import com.huangyiming.disjob.monitor.db.domain.DBUserPermit;

@Service("dbUserPermitService")
public class DBUserPermitService {

	@Autowired
	private DBUserPermitMapper userPermitMapper;
	
	@Autowired
	private DBPermitItemMapper permitItermMapper;
	
	/**
	 * 查询是否具有某权限	对于一个查询来说,如果具有查询的父页面权限,则默认具有该查询权限;如果设置了没有该查询条件,那么结果则是没有查询权限
	 * @param username
	 * @param permitItem
	 * @return
	 */
	public boolean hasPermit(String username, String permitItem){
		
		DBPermitItem dbPermitItem = permitItermMapper.selectByUri(permitItem);
		if(dbPermitItem != null){
			String permitId = dbPermitItem.getId();
			if(!dbPermitItem.isEnabled()){
				return true;
			}else{
				if(userPermitMapper.select(new DBUserPermit(username, permitId)) == null){
					if(dbPermitItem.getOperateType() != null && dbPermitItem.getOperateType().equals("SELECT")){
						return hasParentPagePermit(username, dbPermitItem.getParentId());						
					}
					return false;
				}else{
					return true;
				}
			}
		}else{
			return true;
		}
	}
	
	private boolean hasParentPagePermit(String username, String permitId) {
		DBPermitItem permitItem = permitItermMapper.selectById(permitId);
		if("PAGE".equals(permitItem.getItemType())){
			return userPermitMapper.select(new DBUserPermit(username, permitId)) != null;
		}
		return false;
	}

	/**
	 * 创建用户的权限
	 * @param permit
	 */
	public void createPermitItem(DBUserPermit permit){
		if(userPermitMapper.select(permit) == null){
			userPermitMapper.insert(permit);			
		}
	}
	
	/**
	 * 
	 */
	public void removePermitItem(DBUserPermit permit){
		userPermitMapper.delete(permit);
	}
}
