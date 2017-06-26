package com.huangyiming.disjob.console.web.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huangyiming.disjob.monitor.db.service.DBUserActionRecordService;
import com.huangyiming.disjob.monitor.db.domain.PageResult;

@Controller
@RequestMapping("/service/user")
public class UserController {

	@Autowired
	@Qualifier("dbUserActionRecordService")
	DBUserActionRecordService userActionRecordService;
	//limit:10
	//offset:5
	@RequestMapping("/userActionList")
	@ResponseBody
	public PageResult pauseJobExecution(
			@RequestParam(value="limit", required=true) int limit,
			@RequestParam(value="offset", required=true) int offset,
			@RequestParam(value="search", required=false) String search){
		if(search != null)search = search.trim();
		PageResult pageResult = new PageResult();
		pageResult.setRows(userActionRecordService.selectUserActionRecordList(limit, offset, search));
		pageResult.setTotal(userActionRecordService.selectUserActionRecordCount(search));
		return pageResult;
	}
	
	@InitBinder    
    protected void initBinder(HttpServletRequest request,  
        ServletRequestDataBinder binder) throws Exception {  
        binder.registerCustomEditor(Date.class,   
                new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), true));  
    }  
}
