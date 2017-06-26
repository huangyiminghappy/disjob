package com.huangyiming.disjob.console.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huangyiming.disjob.common.Constants;
import com.huangyiming.disjob.monitor.rms.CommonRMSMonitor;
import com.huangyiming.disjob.monitor.rms.MonitorType;
import com.huangyiming.disjob.monitor.rms.SelfTestRMSMonitor;
import com.huangyiming.disjob.monitor.rms.pojo.RMSMonitorInfo;

@Controller
@RequestMapping("/rms")
public class RMSController {

	@ResponseBody
	@RequestMapping("/reset")
	public String reset() {
		SelfTestRMSMonitor.reset();
		return "OK";
	}
	
	@ResponseBody
	@RequestMapping("/shutdown")
	public String shutdown() {
		SelfTestRMSMonitor.shutdown();
		return "OK";
	}
	
	@ResponseBody
	@RequestMapping("/start")
	public String start() {
		SelfTestRMSMonitor.start();
		return "OK";
	}
	
	/**
	 * 测试job 执行超时 发送报警 次数控制
	 * @param message
	 * @return
	 */
	@RequestMapping("/test/sendJobTimeOut")
	@ResponseBody
	public RMSMonitorInfo sendJobTimeOut(String uuid){
		return CommonRMSMonitor.sendBusiness(MonitorType.Business.JOB_TIMEOUT, "sendJobTimeOut",uuid);
	}
	
	/**
	 * job 执行后返回 异常信息报警
	 * @param message
	 * @return
	 */
	@RequestMapping("/test/sendReturnExeception")
	@ResponseBody
	public RMSMonitorInfo sendReturnExeception(String uuid){
		return CommonRMSMonitor.sendBusiness(MonitorType.Business.RPC_RESPONSE_EXCEPTION,"file:/data/www/pms/daemon/provider/auto_update_deals_status.php is not exists!", uuid);
	}
	
	/**
	 * disJob server 部分可用时 报警
	 * @return
	 */
	@RequestMapping("/test/disJobSimgleAvaliable")
	@ResponseBody
	public RMSMonitorInfo sendSeriousDisJobSimgleAvaliable(){
		return CommonRMSMonitor.sendSystem(MonitorType.System.SERIOUS_DISJOB_SIMGLE_AVAILABLE, "sendSeriousDisJobSimgleAvaliable");
	}
	
	/**
	 * 从池中 borrow a netty channel occur an error
	 * @return
	 */
	@RequestMapping("/test/borrowChannelError")
	@ResponseBody
	public RMSMonitorInfo sendBorrowChannelError(){
		return CommonRMSMonitor.sendSystem(MonitorType.System.SERIOUS_BORROWCHANNEL_ERROR, "exception occur when borrowObject from pool ");
	}
	
	/**
	 * disJob 这边系统执行异常：
	 * @return
	 */
	@RequestMapping("/test/disJobExeception")
	@ResponseBody
	public RMSMonitorInfo sendDisJobExeception(){
		return CommonRMSMonitor.sendSystem(MonitorType.System.DISJOB_EXCEPTION, "disJob exeception ");
	}
	
	/**
	 * 
	 */
	@RequestMapping("/test/netConnRefuse")
	@ResponseBody
	public RMSMonitorInfo sendNetConnRefuse(){
		return CommonRMSMonitor.sendNetWork(MonitorType.NetWork.SYSTEM_CONNECT_REFUSE, "test net connect refuse");
	}
	@RequestMapping("/test/rpcUrlEmpty")
	@ResponseBody
	public RMSMonitorInfo sendRpcListEmpty(@RequestParam(value = "group", required = true)String group,@RequestParam(value = "jobName", required = true)String jobName){
		return CommonRMSMonitor.sendBusiness(MonitorType.Business.JOB_RPC_LIST_EMPTY, group+"_"+jobName+" 任务可用地址为空", group, jobName);
	}
	
	@RequestMapping("/test/zknode")
	@ResponseBody
	public String getZknode(){
		return Constants.ROOT;
	}
}
