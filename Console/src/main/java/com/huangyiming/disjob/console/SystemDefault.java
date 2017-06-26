package com.huangyiming.disjob.console;

import java.nio.charset.Charset;

/**
 * 全局配置和定义类
 * 
 * @author Adam
 *
 */
public class SystemDefault {  
	
	/**
	 * 系统用户默认密码
	 */
	public final static String DEFAULT_USER_PASSWORD = "123456";
	
	/**
	 * 用户会话的键
	 */
	public final static String USER_SESSION_KEY = "currentUser";
	
	/**
	 * 超级管理员角色的ID
	 */
	public final static String SUPER_ROLE_ID = "root";
	public final static String USER_CARD_PASSWORD="123456";

	public final static Charset CHARSET = Charset.forName("UTF-8");
	
	public final static String ADMINISTRATOR_ROLE = "超级管理员";
 }
