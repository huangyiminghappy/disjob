package com.huangyiming.disjob.console.web.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.huangyiming.disjob.common.model.JobGroup;
import com.huangyiming.disjob.common.model.Result;
import com.huangyiming.disjob.console.SystemDefault;
import com.huangyiming.disjob.register.job.DisJobServerService;
import com.huangyiming.disjob.register.job.JobOperationService;
import com.google.gson.Gson;
import com.huangyiming.disjob.monitor.db.domain.PageResult;

@Controller
@RequestMapping("/service/job/bind")
@SessionAttributes(SystemDefault.USER_SESSION_KEY)
public class JobBindController {

	@Resource
	private DisJobServerService disJobServerService;
	
	@Resource
	private JobOperationService jobOperationService;
	
	@RequestMapping("/getBindSession")
	@ResponseBody
	public List<String> getGroupList(HttpSession session) {
		
		return disJobServerService.getSessionsList();
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/doBind")
	@ResponseBody
	public Result doBind(HttpSession session,
			@RequestParam(value="sessions", required=true)String sessions,
			@RequestParam(value="groupNames", required=true)String groupNames) {
		if(!StringUtils.isEmpty(sessions) && !StringUtils.isEmpty(groupNames)){
			List<String> sessionList = new Gson().fromJson(sessions, List.class);
			List<String> groupNameList = new Gson().fromJson(groupNames, List.class);
			jobOperationService.bindJob(sessionList, groupNameList);
			return new Result();			
		}else{
			return new Result(false);
		}
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/doReBind")
	@ResponseBody
	public Result doReBind(HttpSession session,
			@RequestParam(value="sessions", required=true)String sessions,
			@RequestParam(value="groupNames", required=true)String groupNames) {
		if(!StringUtils.isEmpty(sessions) && !StringUtils.isEmpty(groupNames)){
			List<String> sessionList = new Gson().fromJson(sessions, List.class);
			List<String> groupNameList = new Gson().fromJson(groupNames, List.class);
			jobOperationService.reBindJob(sessionList, groupNameList);
			return new Result();			
		}else{
			return new Result(false);
		}
	}
	
	@RequestMapping("/getJobGroupList")
	@ResponseBody
	public List<JobGroup> getJobGroupList(@RequestParam(value="search", required=false) String search) {
		List<JobGroup> groupNameList = jobOperationService.getAllJobGroupForPageList();
		return groupNameList;
	}
}
