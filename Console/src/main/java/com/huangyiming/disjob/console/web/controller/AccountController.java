package com.huangyiming.disjob.console.web.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huangyiming.disjob.common.model.Result;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.console.SystemDefault;
import com.huangyiming.disjob.monitor.db.service.DBUserService;
import com.huangyiming.disjob.monitor.db.domain.DBUser;

/**
 * <pre>
 * 
 *  File: AccountController.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  登录访问web控制器
 * 
 *  Revision History
 *
 *  Date：		2016年5月19日
 *  Author：		Disjob
 *
 * </pre>
 */
@Controller
public class AccountController {
	
	@Autowired
	@Qualifier("dbUserService")
	private DBUserService pservice;
	
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String loginPage(HttpSession session, Model model) {
		DBUser user = (DBUser) session.getAttribute(SystemDefault.USER_SESSION_KEY);
		if (user == null) {			
			return "login";
		}
	
		return "main";
	}
	
	@RequestMapping(value="/login", method=RequestMethod.POST)
	@ResponseBody
	public Result login(
			@RequestParam(value = "username", required=true) String username,
			@RequestParam(value = "password", required=true) String password,
			HttpSession session) {
		DBUser user = pservice.findUser(username, password);
		if (user != null ) {
			LoggerUtil.info(String.format("【web信息】AccountController { %s } login success",user));
			session.setAttribute(SystemDefault.USER_SESSION_KEY, user);
			return new Result();
		} else {
			return new Result("用户名密码不匹配");
		}
	}
	
	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		if (session != null) {
			session.invalidate();
		}
		return "redirect:/";
	}
	
	@RequestMapping("checkSession")
	@ResponseBody
	public Result checkSession(HttpSession session) {
		if (session.getAttribute(SystemDefault.USER_SESSION_KEY) != null) {
			return new Result();
		}
		return new Result(false);
	}
}
