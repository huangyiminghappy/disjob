package com.huangyiming.disjob.console.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <pre>
 * 
 *  File: PageController.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  页面跳转管理
 * 
 *  Revision History
 *
 *  Date：		2016年5月19日
 *  Author：		Disjob
 *
 * </pre>
 */
@Controller
@RequestMapping("/page")
public class PageController {
	// 用户管理页面
	@RequestMapping("/sysuser")
	public String sysUser() {
		return "system/user";
	}
	// 角色管理页面
	@RequestMapping("/sysrole")
	public String sysRole() {
		return "system/role";
	}
	// 菜单管理页面
	@RequestMapping("/sysmenu")
	public String sysMenu() {
		return "system/menu";
	}
	// 修改密码页面
	@RequestMapping("/syspasswd")
	public String sysPasswd() {
		return "system/password";
	}
	// job组管理页面
	@RequestMapping("/job/group")
	public String sysGroup() {
		return "job/jobGroup";
	}
	// job组管理页面
	@RequestMapping("/job/alarm")
	public String sysAlarm() {
		return "job/jobAlarm";
	}
	// job信息管理页面
	@RequestMapping("/job/info")
	public String sysJob() {
		return "job/jobInfo";
	}
	// 基础服务管理页面
	@RequestMapping("/service/serverInfo")
	public String basicService() {
		return "service/serverInfo";
	}

	// 基础服务管理页面
	@RequestMapping("/monitor/minotorInfo")
	public String monitorService() {
		return "monitor/monitorInfo";
	}

	// 任务执行明细
	@RequestMapping("/monitor/jobExeDetail")
	public String JobExeDetail() {
		return "monitor/jobExeDetail";
	}
	// 任务执行进度
	@RequestMapping("/monitor/jobExeProgress")
	public String JobExeProgress() {
		return "monitor/jobExeProgress";
	}
	// 任务执行进度
	@RequestMapping("/monitor/jobExeStatistic")
	public String JobExeStatistic() {
		return "monitor/jobExeStatistics";
	}
	// cron表达式转换
	@RequestMapping("/monitor/cronTransfer")
	public String CronTransfer() {
		return "cron";
	}
	// 授权
	@RequestMapping("/monitor/auth")
	public String auth() {
		return "auth/auth";
	}
	@RequestMapping("/permit/nopermission")
	public String nopermission() {
		return "system/nopermission";
	}
	@RequestMapping("/user/userActionList")
	public String userActionList() {
		return "user/userActionList";
	}
	@RequestMapping("/job/bind")
	public String jobBind() {
		return "job/jobBind";
	}
}