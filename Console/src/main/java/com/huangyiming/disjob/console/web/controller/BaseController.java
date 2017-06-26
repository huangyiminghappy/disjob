package com.huangyiming.disjob.console.web.controller;

import javax.annotation.Resource;

import org.apache.zookeeper.KeeperException;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huangyiming.disjob.common.exception.DisJobFrameWorkException;
import com.huangyiming.disjob.common.exception.WebProcessingException;
import com.huangyiming.disjob.common.exception.WebUserExistsException;
import com.huangyiming.disjob.common.exception.ZKNodeException;
import com.huangyiming.disjob.common.model.Result;
import com.huangyiming.disjob.common.util.LoggerUtil;

/**
 * <pre>
 * 
 *  File: BaseController.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  基础实现控制器类
 * 
 *  Revision History
 *
 *  Date：		2016年5月19日
 *  Author：		Disjob
 *
 * </pre>
 */
public class BaseController {
	
	@Resource
	private MessageSource messageSource;
	
	@ExceptionHandler
	@ResponseBody
	public Result exception(Exception e) {
		
		String msg = null;
		if (e instanceof IllegalArgumentException) {
			msg = e.getMessage();
		} 
		else if (e instanceof WebUserExistsException) {
			msg = e.getMessage();
		} 
		else if (e instanceof WebProcessingException) {
			msg = e.getMessage();
		}else if(e instanceof ZKNodeException && e.getCause() instanceof KeeperException.NoAuthException){
			String[] path = ((KeeperException.NoAuthException)e.getCause()).getPath() != null ? ((KeeperException.NoAuthException)e.getCause()).getPath().split("/") : null;
			if(path != null && path[1].equals("job") && path.length == 3){
				msg = String.format("donnot have jobgroup %s 's data auth", path[2]);
			}else{
				LoggerUtil.error("no zk auth");
				msg = "DATA UNAUTHORIZED";		
			}
		}else if(e instanceof DisJobFrameWorkException){
			msg = e.getMessage();
		}
		else {
			LoggerUtil.error(String.format("【web异常】BaseController has  %s error",e.getClass()),e.fillInStackTrace());
			msg = "system.error";
		}
		
		String message = null;
		
		try {
			message = this.messageSource.getMessage(msg, null, null);
		} catch (NoSuchMessageException nsme) {
			message = msg;
		}
				
		return new Result(message);
	}
	
}
