package com.huangyiming.disjob.console.util;

import com.huangyiming.disjob.common.util.LoggerUtil;

public class AppHelper {
	
	private static String ERROR = "[DisJob-console] {%s} exception : %s";
	private static String ACCESS = "[DisJob-access record] {操作：%s} , {内容：%s} , {用户：%s}";
	
	/**
	 * 将密码加密
	 * @param passwordSource
	 * @return
	 */
	public static String encryptPassword(String passwordSource) {
		return EncryptUtils.encryptByMD5(passwordSource);
	}
	  
	//日志处理
	public static void errorLog(Class classz,Throwable e){
		LoggerUtil.error(String.format(ERROR, new Object[]{classz.getName(),e.getMessage()}));
	}
	
	//日志处理
	public static void accessLog(Object user,String operator,String content){
		LoggerUtil.accessLog(String.format(ACCESS, new Object[]{operator,content,user}));
	}
}
