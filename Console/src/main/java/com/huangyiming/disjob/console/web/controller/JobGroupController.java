package com.huangyiming.disjob.console.web.controller;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.huangyiming.disjob.common.model.JobGroup;
import com.huangyiming.disjob.common.model.Result;
import com.huangyiming.disjob.console.SystemDefault;
import com.huangyiming.disjob.register.job.JobOperationService;
import com.huangyiming.disjob.monitor.db.domain.DBUser;
import com.huangyiming.disjob.monitor.db.domain.PageResult;

@Controller
@RequestMapping("/service/job/group")
@SessionAttributes(SystemDefault.USER_SESSION_KEY)
public class JobGroupController extends BaseController {
	
	@Resource
	private JobOperationService service;
		
	@RequestMapping("/list")
	@ResponseBody
	public List<JobGroup> list(HttpSession session) {
		List<JobGroup> groups = new LinkedList<JobGroup>();
		JobGroup obj = null;
		for(String name: this.service.getAllGroup(((DBUser)session.getAttribute(SystemDefault.USER_SESSION_KEY)))){
			obj = new JobGroup();
			obj.setGroupName(name);
			groups.add(obj);
		}
		return groups;
	}

 
	
	@RequestMapping("/listPage")
	@ResponseBody
	public PageResult search(@RequestParam(value="limit", required=true) int pageSize,
							@RequestParam(value="offset", required=true) int offset,
							HttpSession session) {
		List<String> names = this.service.getAllGroup((DBUser)session.getAttribute(SystemDefault.USER_SESSION_KEY));//读取所有
		List<JobGroup> groups = new LinkedList<JobGroup>();//存储分页后的数据
		int total = 0;
		if(names != null ){
			total = names.size();
			JobGroup obj = null;
			if(total >= offset+pageSize){//如果有足够的数据，则取页面大小的数量
				names = names.subList(offset, offset+pageSize);
			}else{//否则取完剩下的
				names = names.subList(offset, total);
			}
			for(String name: names){
				obj = new JobGroup();
				obj.setGroupName(name);
				groups.add(obj);
			}
		}
		return new PageResult().setTotal(total).setRows(groups);//封装返回
	}

	@RequestMapping("/add")
	@ResponseBody
	public Result add(JobGroup group, HttpSession session) throws Exception {
		String username = ((DBUser)session.getAttribute(SystemDefault.USER_SESSION_KEY)).getUsername();
		return new Result(service.createGroup(group, username));
	}
	
	@RequestMapping("/update")
	@ResponseBody
	public Result update(JobGroup group) throws Exception {
		return new Result();
	}
	
	@RequestMapping("/delete")
	@ResponseBody
	public Result delete(@RequestParam(value="groupName", required=true) String groupName) throws Exception {
		return new Result();
	}
	
	@RequestMapping("/getGroupList")
	@ResponseBody
	public List<String> getGroupList(HttpSession session) {
		return this.service.getAllGroup(((DBUser)session.getAttribute(SystemDefault.USER_SESSION_KEY)));
	}
}
