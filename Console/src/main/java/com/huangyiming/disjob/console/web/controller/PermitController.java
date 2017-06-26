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
import com.huangyiming.disjob.monitor.db.service.DBPermitItemService;
import com.huangyiming.disjob.monitor.db.service.DBUserService;
import com.huangyiming.disjob.monitor.db.service.PermitService;
import com.huangyiming.disjob.monitor.db.domain.DBPermitItem;

@Controller
@RequestMapping("/service/permit")
public class PermitController {

	@Autowired
	@Qualifier("dbUserService")
	private DBUserService userService;
	
	@Resource
	private DBPermitItemService permitItemService;
	
	@Autowired
	private PermitService permitService;

	@RequestMapping("/auth")
	@ResponseBody
	public Result auth(@RequestParam(value = "permititem", required = true) String permititem,
			@RequestParam(value = "username", required = true) String username) throws Exception {
		permitService.addUserPermit(username, permititem);
		return new Result();
	}

	@RequestMapping("/unAuth")
	@ResponseBody
	public Result unAuth(@RequestParam(value = "permititem", required = true) String permititem,
			@RequestParam(value = "username", required = true) String username) throws Exception {
		permitService.removeUserPermit(username, permititem);
		return new Result();
	}
	
	@RequestMapping("/getUserList")
	@ResponseBody
	public List<String> getUserList() throws Exception {
		return userService.getAllUsername();
	}
	
	@RequestMapping("/getPermitList")
	@ResponseBody
	public List<DBPermitItem> getPermitList() throws Exception {
		return permitItemService.getAllPermit();
	}
	
	@RequestMapping("/getPermitInfo")
	@ResponseBody
	public boolean getPermitInfo(@RequestParam(value = "permititem", required = true) String permititem,
			@RequestParam(value = "username", required = true) String username) throws Exception {
		return permitService.hasPermit(username, permititem);
	}
	
}
