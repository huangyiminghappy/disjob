package com.huangyiming.disjob.console.web.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huangyiming.disjob.common.model.Result;
import com.huangyiming.disjob.monitor.db.service.DBUserService;
import com.huangyiming.disjob.register.auth.service.AuthService;

@Controller
@RequestMapping("/service/auth")
public class AuthController {

	@Resource
	private AuthService authService;
	
	@Autowired
	@Qualifier("dbUserService")
	private DBUserService pservice;

	@RequestMapping("/auth")
	@ResponseBody
	public Result auth(@RequestParam(value = "jobgroup", required = true) String groupName,
			@RequestParam(value = "username", required = true) String username,
			@RequestParam(value = "authtype", required = true) String authtype) throws Exception {
		authService.assign(username, groupName, authtype);
		return new Result();
	}

	@RequestMapping("/unAuth")
	@ResponseBody
	public Result unAuth(@RequestParam(value = "jobgroup", required = true) String groupName,
			@RequestParam(value = "username", required = true) String username,
			@RequestParam(value = "authtype", required = true) String authtype) throws Exception {
		authService.unAssign(username, groupName, authtype);
		return new Result();
	}
	
	@RequestMapping("/getUserList")
	@ResponseBody
	public List<String> getUserList() throws Exception {
		return pservice.getAllUsername();
	}
	
	@RequestMapping("/getJobgroup")
	@ResponseBody
	public List<String> getJobgroup() throws Exception {
		return authService.getAuthAvailableJobGroup();
	}
	
	@RequestMapping("/getAuthInfos")
	@ResponseBody
	public boolean[] getAuthInfos(@RequestParam(value = "jobgroup", required = true) String jobgroup,
			@RequestParam(value = "username", required = true) String username) throws Exception {
		return authService.getAuthByUsernameAndJobgroup(username, jobgroup);
	}
}
