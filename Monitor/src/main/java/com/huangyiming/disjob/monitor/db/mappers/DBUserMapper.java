package com.huangyiming.disjob.monitor.db.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huangyiming.disjob.monitor.db.domain.DBUser;

/**
 * <pre>
 * 
 *  File: DBUserMapper.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  数据库用户操作
 * 
 *  Revision History
 *
 *  Date：		2016年9月7日
 *  Author：		Disjob
 *
 * </pre>
 */
public interface DBUserMapper {
	/**查询用户是否存在用户
	 * @param userName  用户名
	 * @param password	密码
	 * @return  返回用户信息
	 */
	DBUser findUser(@Param("uname")String  userName, @Param("pwd")String password);
	
	/**
	 * 不包含参数中的username
	 * @param list
	 * @return
	 */
	List<String> getAllUsername(List<String> list);
}
