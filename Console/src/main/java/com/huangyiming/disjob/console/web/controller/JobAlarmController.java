package com.huangyiming.disjob.console.web.controller;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huangyiming.disjob.common.model.Result;
import com.huangyiming.disjob.monitor.db.service.DBJobAlarmMappingService;
import com.huangyiming.disjob.monitor.db.domain.DBJobAlarmMapping;
import com.huangyiming.disjob.monitor.db.domain.PageResult;

/**
 * <pre>
 * 
 *  File: JobAlarmController.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  任务报警信息控制器
 * 
 *  Revision History
 *
 *  Date：		2016年9月7日
 *  Author：		Disjob
 *
 * </pre>
 */
@Controller
@RequestMapping("/service/job/alarm")
public class JobAlarmController extends BaseController {
	
	@Resource
	private DBJobAlarmMappingService service;
		
	@RequestMapping("/list")
	@ResponseBody
	public PageResult list(@RequestParam(value="groupName", required=true) String groupName,
							@RequestParam(value="limit", required=true) int pageSize,
							@RequestParam(value="offset", required=true) int offset) {
		if(StringUtils.isNoneEmpty(groupName)){
			DBJobAlarmMapping info = service.search(groupName);
			List<DBJobAlarmMapping> infos = new LinkedList<DBJobAlarmMapping>();
			if(info != null)
				infos.add(info);
			return new PageResult().setTotal(infos.size()).setRows(infos);
		}else{
			return service.findAll(offset, pageSize);
		}
	}

	@RequestMapping("/add")
	@ResponseBody
	public Result add(DBJobAlarmMapping info) throws Exception {
		Result res = new Result();
		if(!service.insert(info)){
			res.setSuccessful(false);
			res.setMsg("添加失败");
		}
		return res;
	}
	
	@RequestMapping("/update")
	@ResponseBody
	public Result update(DBJobAlarmMapping info) throws Exception {
		Result res = new Result();
		if(!service.update(info)){
			res.setSuccessful(false);
			res.setMsg("更新失败");
		}
		return res;
	}
	
	@RequestMapping("/delete")
	@ResponseBody
	public Result delete(@RequestParam(value="groupName", required=true) String groupName) throws Exception {
		Result res = new Result();
		if(!service.delete(groupName)){
			res.setSuccessful(false);
			res.setMsg("删除失败");
		}
		return res;
	}
}
