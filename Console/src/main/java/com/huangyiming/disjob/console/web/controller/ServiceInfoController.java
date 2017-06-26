package com.huangyiming.disjob.console.web.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.quartz.JobKey;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huangyiming.disjob.common.util.StringHandleUtil;
import com.huangyiming.disjob.console.cron.CronResult;
import com.huangyiming.disjob.console.cron.CronTransferUtil;
import com.huangyiming.disjob.console.cron.YearFactor;
import com.huangyiming.disjob.console.cron.factors.DayOfMonthFactor;
import com.huangyiming.disjob.console.cron.factors.DayOfWeekFactor;
import com.huangyiming.disjob.console.cron.factors.MonthFactor;
import com.huangyiming.disjob.console.cron.factors.TimeFactor;
import com.huangyiming.disjob.register.domain.DisJobServerInfo;
import com.huangyiming.disjob.register.job.DisJobServerService;
import com.google.gson.Gson;
import com.huangyiming.disjob.monitor.db.domain.PageResult;

/**
 * <pre>
 * 
 *  File: ServiceBasicController.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  基础服务控制器实现类
 * 
 *  Revision History
 *
 *  Date：		2016年6月6日
 *  Author：		Disjob
 *
 * </pre>
 */
@Controller
@RequestMapping("/service/ser/basic")
public class ServiceInfoController extends BaseController {
	
	@Resource
	DisJobServerService service;

	@RequestMapping("/list")
	@ResponseBody
	public PageResult list(@RequestParam(value="name", required=true) String name,
							@RequestParam(value="limit", required=true) int pageSize,
							@RequestParam(value="offset", required=true) int offset) {
		List<DisJobServerInfo> infos = service.getServerInfos();
		int total = 0;
		if(infos != null){
			total = infos.size();
			if(total >= offset+pageSize){//如果有足够的数据，则取页面大小的数量
				infos = infos.subList(offset, offset+pageSize);
			}else{//否则取完剩下的
				infos = infos.subList(offset, total);
			}
		}
		return new PageResult().setTotal(total).setRows(infos);
	}
	
	@RequestMapping("/subJob")
	@ResponseBody
	public PageResult subJob(@RequestParam(value="ip", required=true) String ip,
								@RequestParam(value="limit", required=true) int pageSize,
								@RequestParam(value="offset", required=true) int offset) {
		List<JobKey> result = new LinkedList<JobKey>();
		int total = 0;
		if(StringHandleUtil.isNoneEmpty(ip)){
			Map<String, List<String>> infos = service.getServerJob(new DisJobServerInfo(ip));
			if(infos != null){
				Iterator<String> itor = null;
				for(String gName: infos.keySet()){
					itor = infos.get(gName).iterator();
					while(itor.hasNext()){
						result.add(new JobKey(itor.next(), gName));
					}
				}
			}
			total = result.size();
			if(total >= offset+pageSize){//如果有足够的数据，则取页面大小的数量
				result = result.subList(offset, offset+pageSize);
			}else{//否则取完剩下的
				result = result.subList(offset, total);
			}
		}
		return new PageResult().setTotal(total).setRows(result);//封装返回
	}
	
	@RequestMapping("/transfer")
	@ResponseBody
	public CronResult transfer(@RequestParam(value="crontab", required=true) String crontab) {
		return CronTransferUtil.fromCrontabToQuartz(crontab);
	}
	
	@RequestMapping("/transferBatch")
	@ResponseBody
	public List<CronResult> transferBatch(@RequestParam(value="crontabs", required=true) String crontabs) {
		String[] crontabsArray = new Gson().fromJson(crontabs,String[].class);
		return CronTransferUtil.fromCrontabToQuartz(crontabsArray);
	}
	
	@RequestMapping("/generateCron")
	@ResponseBody
	public String generateCron(@RequestParam(value = "second", required = true) String second,
			@RequestParam(value = "minute", required = true) String minute,
			@RequestParam(value = "hour", required = true) String hour,
			@RequestParam(value = "dayOfMonth", required = true) String dayOfMonth,
			@RequestParam(value = "month", required = true) String month,
			@RequestParam(value = "weekOfMonth", required = true) String week,
			@RequestParam(value = "year", required = false) String year) {
		
		TimeFactor secondFactor = new Gson().fromJson(second,TimeFactor.class);
		TimeFactor minuteFactor = new Gson().fromJson(minute,TimeFactor.class);
		TimeFactor hourFactor = new Gson().fromJson(hour,TimeFactor.class);
		DayOfMonthFactor dayOfMonthFactor = new Gson().fromJson(dayOfMonth,DayOfMonthFactor.class).setDefaultValue("*");
		MonthFactor monthFactor = new Gson().fromJson(month,MonthFactor.class).setDefaultValue("*");
		DayOfWeekFactor weekOfMonthFactor = new Gson().fromJson(week,DayOfWeekFactor.class).setDefaultValue("*");
		YearFactor yearFactor = new Gson().fromJson(year,YearFactor.class).setDefaultValue("*");
		
		return CronTransferUtil.generateCron(secondFactor, minuteFactor, hourFactor,
				dayOfMonthFactor, monthFactor, weekOfMonthFactor, yearFactor);
	}
}
