package com.huangyiming.disjob.console.web.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.TimeAxis;
import com.github.abel533.echarts.code.AxisType;
import com.github.abel533.echarts.code.Magic;
import com.github.abel533.echarts.code.Trigger;
import com.github.abel533.echarts.data.Data;
import com.github.abel533.echarts.feature.MagicType;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.Line;
import com.github.abel533.echarts.series.MarkPoint;
import com.github.abel533.echarts.style.ItemStyle;
import com.github.abel533.echarts.style.itemstyle.Normal;
import com.huangyiming.disjob.common.model.Result;
import com.huangyiming.disjob.common.util.StringHandleUtil;
import com.huangyiming.disjob.console.SystemDefault;
import com.huangyiming.disjob.console.service.JobInfoService;
import com.huangyiming.disjob.console.util.AppHelper;
import com.huangyiming.disjob.monitor.db.service.DBJobBasicInfoService;
import com.huangyiming.disjob.monitor.db.service.DBJobExeProgressService;
import com.huangyiming.disjob.monitor.db.service.DBStatisticsService;
import com.huangyiming.disjob.monitor.db.domain.DBJobBasicInfo;
import com.huangyiming.disjob.monitor.db.domain.DBJobExeProgress;
import com.huangyiming.disjob.monitor.db.domain.DBStatistics;
import com.huangyiming.disjob.monitor.db.domain.PageResult;
import com.huangyiming.disjob.monitor.db.dynamicsql.StatisticsSqlProvider.DBCondition;
import com.huangyiming.disjob.monitor.db.dynamicsql.StatisticsSqlProvider.DBTable;

/**
 * <pre>
 * 
 *  File: ScheduleJobController.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  job执行明细控制器
 * 
 *  Revision History
 * 
 *  Date：		2016年5月19日
 *  Author：		Disjob
 *
 * </pre>
 */
@Controller
@RequestMapping("/service/monitor")
public class JobMonitorController extends BaseController {

	@Autowired
	@Qualifier("jobBasicInfoService")
	private DBJobBasicInfoService bservice;

	@Autowired
	@Qualifier("jobExeProgressService")
	private DBJobExeProgressService pservice;

	@Autowired
	@Qualifier("statisticsService")
	private DBStatisticsService sservice;

	@Resource
	private JobInfoService jobInfoService;

	/*
	 * @RequestMapping("/jobDetail")
	 * 
	 * @ResponseBody public List<DBJobBasicInfo>
	 * jobExeDetail(@RequestParam(value="groupName", required=true) String
	 * groupName,@RequestParam(value="jobName", required=true) String
	 * jobName,@RequestParam(value="page", required=true) int
	 * page,@RequestParam(value="rows", required=true) int rows) throws
	 * Exception {
	 * System.out.println("----------jobExeDetail-------------"+page+
	 * "----------"+rows); return bservice.findByGnameAndJname(groupName,
	 * jobName,1,100); }
	 */
	@RequestMapping("/stopJobExecute")
	@ResponseBody
	public Result pauseJobExecution(HttpSession session,
			@RequestParam(value = "uuid", required = true) String uuid)
			throws Exception {
		// 1为成功,0任务不存在,2为任务状态不是exec,-1代表超时
		Result res = new Result(false);
		List<String> list = jobInfoService.KillTaskByRequestId(uuid);
		if (list == null || list.size() == 0) {
			res.setMsg("disJob命令发送或接收超时");
		}
		StringBuffer msg = new StringBuffer("");
		List<Boolean> flag = new ArrayList<Boolean>();
		for (String str : list) {
			String[] array = str.split(":");
			String ipPort = array[0];
			int code = Integer.parseInt(array[1]);

			msg.append(ipPort).append(":");
			switch (code) {
			case -1:
				msg.append("超时").append(",");
				break;
			case 0:
				msg.append("任务不存在").append(",");
				break;
			case 1:
				flag.add(true);
				// res.setSuccessful(true);
				break;
			case 2:
				msg.append("任务状态不是exec").append(",");
				break;
			default:
				msg.append("接口调用异常！").append(",");
				break;
			}
		}
		if (flag.size() == list.size()) {
			res.setSuccessful(true);
		} else {
			res.setMsg(msg.toString().substring(0, msg.length() - 1));
		}
		AppHelper.accessLog(
				session.getAttribute(SystemDefault.USER_SESSION_KEY),
				"Kill Task By RequestId", uuid + "," + res.toString());
		return res;
	}

	@RequestMapping("/restartJob")
	@ResponseBody
	public Result restartJobExecution(HttpSession session,
			@RequestParam(value = "groupName", required = true) String groupName,
			@RequestParam(value = "jobName", required = true) String jobName)
			throws Exception {
		// 1为成功,0任务不存在,2为任务状态不是exec,-1代表超时
		Result res = new Result(false);

		List<String> list = jobInfoService.restartJob(groupName,jobName);
		if (list == null || list.size() == 0) {
			res.setMsg("disJob命令发送或接收超时");
		}
		StringBuffer msg = new StringBuffer("");
		List<Boolean> flag = new ArrayList<Boolean>();
		for (String str : list) {
			String[] array = str.split(":");
			String ipPort = array[0];
			int code = Integer.parseInt(array[1]);

			msg.append(ipPort).append(":");
			switch (code) {
			case -1:
				msg.append("超时").append(",");
				break;
			case 0:
				msg.append("重启失败").append(",");
				break;
			case 1:
				flag.add(true);
				// res.setSuccessful(true);
				break;
			default:
				msg.append("接口调用异常！").append(",");
				break;
			}
		}
		if (flag.size() == list.size()) {
			res.setSuccessful(true);
		} else {
			res.setMsg(msg.toString().substring(0, msg.length() - 1));
		}
		AppHelper.accessLog(
				session.getAttribute(SystemDefault.USER_SESSION_KEY),
				"restart Task By groupName", groupName + ",jobName:"+jobName + " , "+ res.toString());
		return res;
	}

	@RequestMapping("/jobDetailQuery")
	@ResponseBody
	public com.huangyiming.disjob.monitor.db.domain.PageResult jobExeDetailQuery(
			@RequestParam(value = "groupName", required = true) String groupName,
			@RequestParam(value = "jobName", required = true) String jobName,
			@RequestParam(value = "uuid", required = true) String uuid,
			@RequestParam(value = "start", required = false) String startTime,
			@RequestParam(value = "end", required = false) String endTime,
			@RequestParam(value = "limit", required = true) int pageSize,
			@RequestParam(value = "offset", required = true) int offset)
			throws Exception {
		// "asc", limit: 20, offset: 0}
		List<DBJobBasicInfo> infos = null;
		if (StringHandleUtil.isNoneEmpty(uuid)) {// 如果uuid不为null的话，优先查询uuid，否则查询日期段
			DBJobBasicInfo info = bservice.findByUuid(uuid);
			infos = new LinkedList<DBJobBasicInfo>();
			long total = 0;
			if (info != null) {
				infos.add(info);
				total = 1;
			}
			return new PageResult().setTotal(total).setRows(infos);
		} else {
			if (StringHandleUtil.isNoneEmpty(startTime)
					|| StringHandleUtil.isNoneEmpty(endTime))// 如果起始时间、结束时间的查询不为null，则按时间查询
				return bservice.findByTime(groupName, jobName, startTime,
						endTime, offset, pageSize);
			else
				return bservice.findByGnameAndJname(groupName, jobName, offset,
						pageSize);// 否则查询组和任务
		}
	}

	// 转化
	private DBCondition exchange(int type) {
		switch (type) {
		case 0:
			return DBCondition.Today;
		case 1:
			return DBCondition.YesterDay;
		case 2:
			return DBCondition.Nearly7Days;
		case 3:
			return DBCondition.ThisMonth;
		case 4:
			return DBCondition.LastMonth;
		case 5:
			return DBCondition.ThisYear;
		default:
			return null;
		}
	}

	@RequestMapping("/jobStatistics")
	@ResponseBody
	public Result jobExeStatistics(
			@RequestParam(value = "groupName", required = true) String gName,
			@RequestParam(value = "jobName", required = true) String jName,
			@RequestParam(value = "id", required = true) int type)
			throws Exception {
		Result res = new Result(true);

		List<String> sTime = new LinkedList<String>();// 日期
		List<Integer> sSeries = new LinkedList<Integer>();// 成功
		List<Integer> fSeries = new LinkedList<Integer>();// 失败
		DBCondition condition = exchange(type);
		int sSum = 0;
		int fSum = 0;
		if (condition != null) {
			for (DBStatistics info : sservice.specificJob(gName, jName,
					DBTable.JobBasicInfoTable, condition)) {
				sTime.add(info.getTimeSeg());
				sSum += info.getSuccessNum();
				sSeries.add(sSum);
				fSum += info.getFailNum();
				fSeries.add(fSum);
			}
		} else {
			AppHelper.errorLog(this.getClass(), new Exception("统计时无法解析时间段类型："
					+ type));
		}

		GsonOption option = new GsonOption();// 创建java数据转echarts的图表json
		option.legend("失败次数", "成功次数");
		option.title("失败总数:" + fSum + "\r\n成功总数:" + sSum);

		option.tooltip().trigger(Trigger.axis);

		TimeAxis xaxis = new TimeAxis();
		xaxis.type(AxisType.category);
		xaxis.boundaryGap(true);
		xaxis.setData(sTime);

		option.xAxis(xaxis);

		CategoryAxis yAxis = new CategoryAxis();
		yAxis.type(AxisType.value);
		yAxis.axisLabel().formatter("{value} 次");
		option.yAxis(yAxis);

		Line fbar = new Line("失败次数");
		fbar.setData(fSeries);
		fbar.stack("sta");
		fbar.itemStyle(new ItemStyle().normal(new Normal().color("red")));
		Line sbar = new Line("成功次数");
		sbar.setData(sSeries);
		sbar.stack("sta");
		sbar.itemStyle(new ItemStyle().normal(new Normal().color("green")));
		/*
		 * Bar sbar = new Bar("成功次数"); sbar.setData(sSeries); sbar.stack("sta");
		 * Bar fbar = new Bar("失败次数"); fbar.setData(fSeries); fbar.stack("sta");
		 */

		option.series(fbar, sbar);
		res.setData(option.toString());
		return res;
	}

	@RequestMapping("/jobProgress")
	@ResponseBody
	public Result jobExeProgress(
			@RequestParam(value = "uuid", required = true) String uuid,
			@RequestParam(value = "type", required = true) String type)
			throws Exception {
		Result res = new Result(true);

		int ctype = -1;
		try {
			ctype = Integer.parseInt(type);// 先进行验证
		} catch (NumberFormatException e) {
			AppHelper.errorLog(this.getClass(), e);
			ctype = -1;
		}

		List<DBJobExeProgress> pinfos = pservice.findByUuid(uuid);
		List<String> xData = new LinkedList<String>();
		List<Long> xSeries = new LinkedList<Long>();
		Long tmpSeries = (long) 0;
		for (DBJobExeProgress info : pinfos) {// 把数据进行整合
			xData.add(info.getDataTime());
			if (info.getContent() != null)
				try {
					tmpSeries = Long.parseLong(info.getContent());
				} catch (NumberFormatException e) {
					AppHelper.errorLog(this.getClass(), e);
					tmpSeries = (long) 0;
				}
			xSeries.add(tmpSeries);
		}
		if (ctype != -1 && xData.size() == 0)// 解决无数据但是有异常时显示不了问题--因为图表没有数据
			xData.add("0");
		if (ctype != -1 && xSeries.size() == 0)// 解决无数据但是有异常时显示不了问题--因为图表没有数据
			xSeries.add((long) 0);

		GsonOption option = new GsonOption();// 创建java数据转echarts的图表json
		option.legend("执行次数");

		option.toolbox().show(true)
				.feature(new MagicType(Magic.line, Magic.bar));

		option.tooltip().trigger(Trigger.axis)
				.formatter("执行时间 : {b} <br/> 次数：{c}");

		TimeAxis xaxis = new TimeAxis();
		xaxis.type(AxisType.category);
		xaxis.boundaryGap(false);
		xaxis.setData(xData);
		// xaxis.data("2016-07-07 01:41:23","2016-07-07 02:30:00","2016-07-07 03:51:06","2016-07-07 04:54:12","2016-07-07 05:32:45","2016-07-07 06:07:43","2016-07-07 07:17:43","2016-07-07 08:57:03");
		option.xAxis(xaxis);

		CategoryAxis yAxis = new CategoryAxis();
		yAxis.type(AxisType.value);
		yAxis.axisLabel().formatter("{value} 次");
		option.yAxis(yAxis);

		Line line = new Line();
		line.smooth(true).name("执行次数").setData(xSeries);

		setMarkPoint(ctype, uuid, line);
		// line.smooth(true).name("执行次数").data(240, 330, 600, 1000, 1700, 2800,
		// 3600,
		// 5000).itemStyle().normal().lineStyle().shadowColor("rgba(0,0,0,0.4)");
		option.series(line);
		res.setData(option.toString());
		return res;
	}

	private void setMarkPoint(int ctype, String uuid, Line line) {
		if (ctype != -1) {
			MarkPoint mp = new MarkPoint();
			mp.itemStyle().normal().color("red");
			mp.data(new Data("有执行异常，请注意！", ctype));
			// mp.geoCoord("error", "20", "10");

			line.markPoint(mp);
		}
	}
}
