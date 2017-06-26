package com.huangyiming.disjob.monitor.db.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huangyiming.disjob.monitor.db.mappers.DBUserMapper;
import com.huangyiming.disjob.monitor.util.DBCommonUtil;
import com.google.common.collect.Lists;
import com.huangyiming.disjob.monitor.db.domain.DBUser;

/**
 * <pre>
 * 
 *  File: DBUserService.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  用户信息处理服务
 * 
 *  Revision History
 *
 *  Date：		2016年9月7日
 *  Author：		Disjob
 *
 * </pre>
 */
@Service("dbUserService")/** 用户信息服务*/
public class DBUserService {
	
	@Autowired
	private DBUserMapper mapper;
	
	public DBUser findUser(String  userName, String password){
		if(StringUtils.isEmpty(userName))
			return null;
		try{
			return mapper.findUser(userName,password);//查找
		}catch(Throwable e){
			e.printStackTrace();
			DBCommonUtil.logError(this.getClass(), e);
			return null;
		}
	}
	
	public List<String> getAllUsername(){
		List<String> list = Lists.newArrayList("admin","visitor");
		return mapper.getAllUsername(list );
		
	}
}
