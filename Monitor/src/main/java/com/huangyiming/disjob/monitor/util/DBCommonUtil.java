package com.huangyiming.disjob.monitor.util;

import com.huangyiming.disjob.common.util.LoggerUtil;

/**
 * <pre>
 * 
 *  File: CheckFieldUtil.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  域检查通用方法类
 * 
 *  Revision History
 *
 *  Date：		2016年6月24日
 *  Author：		Disjob
 *
 * </pre>
 */
public class DBCommonUtil {
	
	private static String ERROR = "[DisJob-monitor][com.huangyiming.disjob.monitor.db.service] {%s} exception : %s";
	
	//日志处理
	public static void logError(Class<?> classz,Throwable e){
		LoggerUtil.error(String.format(DBCommonUtil.ERROR, new Object[]{classz.getSimpleName(),e.getMessage()}));
	}
	
	public static void logInfo(String msg){
		LoggerUtil.info(msg);
	}
	//任务信息的当前状态标志
	public static enum JobStatus {
		Success('1'),Fail('0');//标识无效、成功、失败
		private char status;
		private JobStatus(char status){
			this.status = status;
		}
		public char get(){
			return status;
		}
		public static char value(char status){
			if(Fail.get() == status)
				return Fail.get();
			return Success.get();
		}
	}
}
