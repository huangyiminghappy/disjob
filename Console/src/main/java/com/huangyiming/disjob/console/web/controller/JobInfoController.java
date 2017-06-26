package com.huangyiming.disjob.console.web.controller;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.huangyiming.disjob.common.exception.DisJobCronException;
import com.huangyiming.disjob.common.exception.ZKNodeException2;
import com.huangyiming.disjob.common.model.Result;
import com.huangyiming.disjob.common.util.StringHandleUtil;
import com.huangyiming.disjob.console.SystemDefault;
import com.huangyiming.disjob.console.cron.CronResult;
import com.huangyiming.disjob.console.cron.CronTransferUtil;
import com.huangyiming.disjob.console.util.AppHelper;
import com.huangyiming.disjob.console.util.CronExpression;
import com.huangyiming.disjob.console.util.CronUtils;
import com.huangyiming.disjob.register.domain.CronInfo;
import com.huangyiming.disjob.register.domain.Job;
import com.huangyiming.disjob.register.job.JobOperationService;
import com.google.gson.Gson;
import com.huangyiming.disjob.monitor.db.domain.DBUser;
import com.huangyiming.disjob.monitor.db.domain.PageResultAndCategories;

/**
 * <pre>
 * 
 *  File: ScheduleJobController.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  job控制器实现
 * 
 *  Revision History
 *
 *  Date：		2016年5月19日
 *  Author：		Disjob
 *
 * </pre>
 */
@Controller
@RequestMapping("/service/job/info")
public class JobInfoController extends BaseController {
	
	@Resource
	private JobOperationService jobOperationService;
		
	@RequestMapping("/list")
	@ResponseBody
	public List<Job> list() {
		return null;
	}
	
	@RequestMapping("/jobExecution")
	@ResponseBody
	public Result jobExecution(@RequestParam(value="jobName", required=true) String jobName,@RequestParam(value="groupName", required=true) String groupName)  throws Exception {
		Result res = new Result(true);
		Job job = this.jobOperationService.getJobByGroupAndJobName(groupName, jobName);
		if(job != null && job.getExe() != null){
			res.setData(job.getExe());
		}else{
			res.setSuccessful(false);
		}
		return res;
	}
	
	@RequestMapping("/search")
	@ResponseBody
	public PageResultAndCategories search(@RequestParam(value="groupName", required=true) String groupName,
			@RequestParam(value="category", required=false) String category) {
		PageResultAndCategories pac = new PageResultAndCategories();
		if(StringUtils.isEmpty(groupName)){
			pac.setTotal(0).setRows(new LinkedList<Job>());
			return pac;
		}
		pac = this.jobOperationService.getJobListByGroupAndCategory(groupName,category);
		return pac;
	}

	@RequestMapping("/update")
	@ResponseBody
	public Result update(HttpSession session,Job job) throws Exception {
		String pageCron = job.getCronExpression();
		if(pageCron.trim().split(CronUtils.SPACE).length == 5 || pageCron.trim().split(CronUtils.SPACE).length == 1){
			CronResult transferResult = CronTransferUtil.fromCrontabToQuartz(pageCron);
			if(transferResult.isTransferSuccess()){
				job.setCronExpression(transferResult.getQuartzCronExpression());				
			}else{
				return new Result(false,"输入的表达式不合法");
			}
		}
		if(!CronExpression.isValidExpression(job.getCronExpression()))
			return new Result(false,"请输入正确的cron表达式");
		if(this.jobOperationService.updateJob(job)){
			AppHelper.accessLog(session.getAttribute(SystemDefault.USER_SESSION_KEY), "update", job.toString());
			return new Result();
		}else
			return new Result(false,"更新任务参数失败");
	}
		
	@RequestMapping("/pause")
	@ResponseBody
	public Result pause(HttpSession session,Job job) throws Exception {
		job.setCronExpression(StringHandleUtil.deleteExtraSpaceRegular(job.getCronExpression()));//去除多余的空格
		if(this.jobOperationService.suspendJob(job)){
			AppHelper.accessLog(session.getAttribute(SystemDefault.USER_SESSION_KEY), "pause", job.toString());
			return new Result();
		}else
			return new Result(false,"暂停任务失败");
	}
	
	@RequestMapping("/resume")
	@ResponseBody
	public Result resume(HttpSession session,Job job) throws Exception {
		job.setCronExpression(StringHandleUtil.deleteExtraSpaceRegular(job.getCronExpression()));//去除多余的空格
		if(this.jobOperationService.resumeJob(job)){
			AppHelper.accessLog(session.getAttribute(SystemDefault.USER_SESSION_KEY), "resume", job.toString());
			return new Result();
		}else
			return new Result(false,"恢复任务失败");
	}
	
	@RequestMapping("/batchHandle")
	@ResponseBody
	public Result batch(HttpSession session,@RequestParam(value="type", required=true) int type,@RequestParam(value="groupName", required=true) String gName,@RequestParam(value="jobNames", required=true) String jNames) throws Exception {
		if(StringUtils.isNoneEmpty(gName) && StringUtils.isNoneEmpty(jNames))
			return batchHandle(session,type,gName,jNames);
		return new Result(false,"无效的参数！");
	}
	private Result batchHandle(HttpSession session,int type,String gName,String jNames){
		Job job = null;
		List<String> jobFaults = new LinkedList<>();
		String[] jobArray = jNames.split(",");
		boolean haveFault = false;
		String batchFlag = null;
		switch(type){
		case 0://暂停
			batchFlag = "暂停";
			for(String jobName:jobArray){
				job = new Job();
				job.setGroupName(gName);
				job.setJobName(jobName);
				if(this.jobOperationService.suspendJob(job)){
					AppHelper.accessLog(session.getAttribute(SystemDefault.USER_SESSION_KEY), "batch pause", job.toString());
				}else{
					haveFault = true;
					jobFaults.add(jobName);
				}
			}
			break;
		case 1://恢复
			batchFlag = "恢复";
			for(String jobName:jobArray){
				job = new Job();
				job.setGroupName(gName);
				job.setJobName(jobName);
				if(this.jobOperationService.resumeJob(job)){
					AppHelper.accessLog(session.getAttribute(SystemDefault.USER_SESSION_KEY), "batch resume", job.toString());
				}else{
					haveFault = true;
					jobFaults.add(jobName);
				}
			}
			break;
		default:
			return new Result(false,"无效的批量操作！");
		}
		if(haveFault)
			new Result(false,jobFaults.toString()+"，如上任务执行-"+batchFlag+"-失败！");
		return new Result();
	}
	
	@RequestMapping("/divideJob")
	@ResponseBody
	public Result divideJob() {
		Result back = new Result(false);
		try {
			int res = jobOperationService.averageDistributeSlaveJob();
			switch(res){
			case 0://成功不做任务处理
				back.setSuccessful(true);
				break;
			case 1:
				back.setMsg("失败：集群中在线的job节点少于2！");
				break;
			default:
				back.setMsg("失败：未知！");
				break;
			}
		} catch (Exception e1) {
			back.setMsg("失败："+e1.getMessage());
			AppHelper.errorLog(this.getClass(), e1);
		}
		return back;
	}
	
	@RequestMapping("/fireNow")
	@ResponseBody
	public Result firenow(@RequestParam(value="jobName", required=true) String jobName,
			@RequestParam(value="jobGroup", required=true) String jobGroup) {
		try {
			jobOperationService.fireNow(jobGroup, jobName);
		} catch (Exception e) {
			return new Result(e.getMessage());
		}
		return new Result();
	}
	
	//新的界面编辑
	@RequestMapping("/jobDetailInfo")
	public ModelAndView jobDetailInfo(@RequestParam(value="groupName", required=false) String groupName,
			@RequestParam(value="jobName", required=false) String jobName,
			@RequestParam(value="method", required=false) String method) {
		if("addNew".equals(method)){
			ModelAndView mav = new ModelAndView("job/jobDetailInfo");
			mav.addObject("job", new Job());
			return mav;
		}
		ModelAndView mav;
		Job job = jobOperationService.getJobByGroupAndJobName(groupName, jobName);
		try {
			//校验是否能够转换, 跳转到不同的页面
			CronUtils.transferFromCronExpression(job.getCronExpression());			
			mav = new ModelAndView("job/jobDetailInfo");
		} catch (DisJobCronException e) {
			mav = new ModelAndView("job/jobSimpleDetailInfo");
		}
		mav.addObject(job);
		return mav;
	}
	
	/**
	 * jobDetail 保存操作
	 * @return
	 */
	@RequestMapping("/saveJobDetail")
	@ResponseBody
	public Result saveJob(@RequestParam(value="job", required=true) String jobInfo,
			@RequestParam(value="cron", required=false) String cronInfo,
			@RequestParam(value="method", required=false) String method,
			HttpSession session){
		Job job = new Gson().fromJson(jobInfo, Job.class);
		if(!StringUtils.isEmpty(cronInfo)){
			CronInfo cron = new Gson().fromJson(cronInfo, CronInfo.class);
			job.setCronExpression(CronUtils.transferToCronExpression(cron));			
		}
		String pageCron = job.getCronExpression();
		if(pageCron.trim().split(CronUtils.SPACE).length == 5 || pageCron.trim().split(CronUtils.SPACE).length == 1){
			CronResult transferResult = CronTransferUtil.fromCrontabToQuartz(pageCron);
			if(transferResult.isTransferSuccess()){
				job.setCronExpression(transferResult.getQuartzCronExpression());				
			}else{
				return new Result(false,"输入的表达式不合法");
			}
		}
		if("addNew".equals(method)){
			Result result = new Result();
			try {
				job.setJobStatus(0);
				DBUser user = (DBUser) session.getAttribute(SystemDefault.USER_SESSION_KEY);
				result.setSuccessful(jobOperationService.addJob(job, user.getUsername()));
			} catch (ZKNodeException2 e) {
				result.setSuccessful(false);
				result.setMsg(e.getMessage());
			}
			return result;
		}
		return new Result(jobOperationService.updateJob(job));
	}
	
	@RequestMapping("/jobAddNew")
	public ModelAndView jobAddnewPage() {
		ModelAndView mav = new ModelAndView("job/jobDetailInfo");
		mav.addObject("job", new Job());
		return mav;
	}
}
