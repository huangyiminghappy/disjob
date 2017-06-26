package com.huangyiming.disjob.java.service;

import io.netty.channel.Channel;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.huangyiming.disjob.java.CronExpression;
import com.huangyiming.disjob.java.DisJobConstants;
import com.huangyiming.disjob.java.ExecutorBuilder;
import com.huangyiming.disjob.java.action.SendBeginExeTimeAction;
import com.huangyiming.disjob.java.action.SendCompleteTimeAction;
import com.huangyiming.disjob.java.bean.JobInfo;
import com.huangyiming.disjob.java.bean.RpcContainer;
import com.huangyiming.disjob.java.bean.SchedulerParam;
import com.huangyiming.disjob.java.core.annotation.JobDec;
import com.huangyiming.disjob.java.core.rpc.RpcRequest;
import com.huangyiming.disjob.java.core.rpc.RpcResponse;
import com.huangyiming.disjob.java.event.JobTracker;
import com.huangyiming.disjob.java.job.DisJob;
import com.huangyiming.disjob.java.job.JobExecuteStatus;
import com.huangyiming.disjob.java.job.RegisterDisJob;
import com.huangyiming.disjob.java.job.RegisterDisJobAction;
import com.huangyiming.disjob.java.utils.Log;
import com.huangyiming.disjob.java.utils.StringUtils;
import com.huangyiming.disjob.java.utils.TimeUtils;
import com.huangyiming.disjob.quence.Action;
import com.huangyiming.disjob.quence.BaseActionQueue;

public class JobService{

	private JobService(){
	}
	
	private final static ConcurrentHashMap<String, JobTracker> JOB_TRACKER_MAP = new ConcurrentHashMap<String, JobTracker>();
	
	private static ConcurrentHashMap<String, Class<? extends DisJob>> DYNAMIC_JOBS = new ConcurrentHashMap<String, Class<? extends DisJob>>();
	
	private static ConcurrentHashMap<String, DisJob> DISJOB_OBJECT_MAP = new ConcurrentHashMap<String, DisJob>();
	
	private static ConcurrentHashMap<String, SoftReference<BaseActionQueue>> GROUP_REGISTER_ACTION = new ConcurrentHashMap<String, SoftReference<BaseActionQueue>>();
	
	private static ConcurrentHashMap<Integer, BaseActionQueue> CHANNEL_ACTION = new ConcurrentHashMap<Integer, BaseActionQueue>();
	
	private static ConcurrentHashMap<String,List<RpcResponse>> RPC_FAIL_RESPONSES_MAP = new ConcurrentHashMap<String,List<RpcResponse>>();
	
	private static ConcurrentHashMap<String, String> CLASS_GROUP_MAP = new ConcurrentHashMap<String, String>();//class name -> group
	
	/**
	 * 
	 * @param key job 所对应的此处job rpc de id。
	 * @return
	 */
	public static JobTracker getJobTracker(String key){
		if(StringUtils.isEmpty(key)){
			return null ;
		}
		
		JobTracker jobTracker = JOB_TRACKER_MAP.putIfAbsent(key,new JobTracker());
		if(jobTracker == null){
			jobTracker = JOB_TRACKER_MAP.get(key);
		}
		
		return jobTracker;
	}
	
	public static JobTracker removeJobTracker(String requestId){
		
		return JOB_TRACKER_MAP.remove(requestId);
	}
	
	public static void setDisJobClass(String className,Class<? extends DisJob> disJobClass){
		
		DYNAMIC_JOBS.putIfAbsent(className, disJobClass);
	}
	
	public static Class<? extends DisJob> getDisJobClass(String className){
		
		return DYNAMIC_JOBS.get(className);
	}
	
	public static void setDisJobInstance(String className,DisJob disJob){
		
		DISJOB_OBJECT_MAP.putIfAbsent(className, disJob);
	}
	
	public static DisJob getDisJobInstance(String className){
		
		return DISJOB_OBJECT_MAP.get(className);
	}
	/**
	 * 
	 * @param className
	 * @return
	 */
	public static boolean initJob(String className){
		LinkedList<String> classNames = new LinkedList<String>();
		classNames.add(className);
		return initJob(classNames);
	}
	
	/**
	 * 
	 * @param classNames
	 * @return
	 */
	public static boolean initJob(List<String> classNames){
		List<JobInfo> jobInfos = new ArrayList<JobInfo>(classNames.size());
		try {
			for(String className:classNames){
				JobInfo jobInfo = getJobInfo(className);
				if(jobInfo == null){
					continue;
				}
				jobInfos.add(jobInfo);
			}
		} catch (Exception e) {
			Log.error(JobService.class.getName()+" on initJob:",e);
			e.printStackTrace();
			return false ;
		}
		
		return doInitJob(jobInfos);
	}
	
	public static boolean doInitJob(List<JobInfo> jobInfos){
		for(JobInfo jobInfo:jobInfos){
			SoftReference<BaseActionQueue> tmpReference = new SoftReference<BaseActionQueue>(new BaseActionQueue(ExecutorBuilder.getJobExecutor()));
			SoftReference<BaseActionQueue> groupQueue = GROUP_REGISTER_ACTION.putIfAbsent(jobInfo.getGroupName(),tmpReference);
			if(groupQueue==null){
				groupQueue = GROUP_REGISTER_ACTION.get(jobInfo.getGroupName());
			}
			CLASS_GROUP_MAP.putIfAbsent(jobInfo.getClassName(), jobInfo.getGroupName());
			groupQueue.get().enqueue(new RegisterDisJobAction(new RegisterDisJob(jobInfo),groupQueue.get()));
		}
		return true ;
	}
	
	public static void enqueue(Channel channel,Action action){
		BaseActionQueue actionQueue = CHANNEL_ACTION.putIfAbsent(channel.hashCode(),new BaseActionQueue(ExecutorBuilder.getJobExecutor()));
		if(actionQueue == null){
			actionQueue = CHANNEL_ACTION.get(channel.hashCode());
		}
		actionQueue.enqueue(action);
	}
	
	private static boolean checkCron(String cron){
		if(StringUtils.isEmpty(cron)){
			return true; //可以允许为空
		}
		return CronExpression.isValidExpression(cron);
	}
	
	@SuppressWarnings("unchecked")
	private static JobInfo getJobInfo(String className){
		Class<? extends DisJob> clazz = JobService.getDisJobClass(className) ;
		if(clazz == null){
			try {
				clazz = (Class<? extends DisJob>) Class.forName(className);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				Log.error("get job info exception:", e);
				throw new RuntimeException(e);
			}
			short startUpType = DisJobConfigService.getStartupType() ;
			/**
			 * 因为如果不是spring 启动，那么job 的实例是通过反射得到的。故这里预先缓存这个类的class
			 */
			if(startUpType == DisJobConstants.StartUpType.JAVA_APPLICATION||startUpType == DisJobConstants.StartUpType.WEB_SERVLET_START_UP){
				JobService.setDisJobClass(className, clazz);
			}
		}
		JobDec jobdec = (JobDec) clazz.getAnnotation(JobDec.class);
		if(jobdec == null){
			return null ;
		}
		
		String groupName = jobdec.group();
		String jobName = jobdec.jobName();
		String quartz = jobdec.quartz();
		
		if(StringUtils.isEmpty(groupName)||StringUtils.isEmpty(jobName)){
			Log.error(clazz.getName()+" group name is empty or job name.");
			throw new IllegalArgumentException(clazz.getName()+ " job doesn‘t set group and jobname value.");
		}
		if(!checkCron(quartz)){
			throw new IllegalArgumentException(clazz.getName()+ " cron express invalidate.[cron="+quartz+"]");
		}
		boolean fireNow = jobdec.fireNow();
		return new JobInfo(clazz.getName(),groupName,jobName,quartz,fireNow);
	}
	
	public static void putFailRpcResponse(Channel channel,RpcResponse rpcResponse){
		String clientIp = ClientLinkedService.getRemoterAddress(channel);
		List<RpcResponse> rpcResponses = RPC_FAIL_RESPONSES_MAP.putIfAbsent(clientIp,new ArrayList<RpcResponse>());
		if(rpcResponses == null){
			rpcResponses = RPC_FAIL_RESPONSES_MAP.get(clientIp);
		}
		synchronized (clientIp) {
			rpcResponses.add(rpcResponse);
		}
		RPC_FAIL_RESPONSES_MAP.put(clientIp, rpcResponses);
	}
	
	public static List<RpcResponse> getFailRpcResponse(Channel channel){
		if(channel == null){
			return null;
		}
		String clientIp = ClientLinkedService.getRemoterAddress(channel);
		List<RpcResponse> tmpFailRpcR = new ArrayList<RpcResponse>();
		List<RpcResponse> failRpcreponses = RPC_FAIL_RESPONSES_MAP.putIfAbsent(clientIp,new ArrayList<RpcResponse>());
		if(failRpcreponses == null){
			failRpcreponses = RPC_FAIL_RESPONSES_MAP.get(clientIp);
		}
		synchronized (clientIp) {
			tmpFailRpcR.addAll(failRpcreponses);
		}
		return tmpFailRpcR;
	}
	
	public static void handlerExecuter(RpcContainer rpcContainer,DisJob action,Channel channel){
		if(action == null){
			return ;
		}
		RpcRequest request = rpcContainer.getMsg();
		RpcResponse response = new RpcResponse();
		response.setException(null);
		String requestId =request.getData().getRequestId();
		String parameters = request.getData().getParameters();
		long start = System.currentTimeMillis();
		SchedulerParam schedulerParam = new SchedulerParam(requestId, parameters, request.getData().getSharingRequestId());
		//2、start to execute the job
		JobService.enqueue(channel, new SendBeginExeTimeAction(rpcContainer, new Date()));
		action.beforeExecute(schedulerParam);
		try{
			action.execute(schedulerParam);
			action.executeSuccess(new SchedulerParam(requestId, parameters, request.getData().getSharingRequestId()));
			response.setCode(String.valueOf(JobExecuteStatus.SUCCESS));
		}catch(Exception e){
			StackTraceElement[] ste = e.getStackTrace();
			response.setException(ste[0].toString()+"; "+e.toString());
			response.setCode(String.valueOf(JobExecuteStatus.FAIL));
			action.executeFail(schedulerParam);
		}
		response.setJobCompleteTime(TimeUtils.local2Utc(new Date()));
		response.setProcessTime(System.currentTimeMillis()-start);
		response.setRequestId(request.getData().getRequestId());
		//3、
		JobService.enqueue(channel, new SendCompleteTimeAction(rpcContainer, response));
	}
	
	@SuppressWarnings("unchecked")
	public static DisJob getDisJobAction(String className, String methodName){
		Object job = null ;
		DisJob action = null;
		if(DisJobConfigService.getStartupType() == DisJobConstants.StartUpType.WEB_SERVLET_START_UP || DisJobConfigService.getStartupType() == DisJobConstants.StartUpType.JAVA_APPLICATION){
			action = getDisJobInstance(className);
			if(action == null){
				/**
				 * 是因为有动态加载的job,如果取出为null,定不是动态添加的job。因为动态添加的job 时已经put 进去了。
				 */
				Class<? extends DisJob> clazz = JobService.getDisJobClass(className);
				boolean isDynamic = false ;
				if(clazz != null){
					isDynamic = true ;
				}
				try {
					clazz = (Class<? extends DisJob>) (clazz == null ? Class.forName(className):clazz);
					job = clazz.newInstance();
				} catch (Exception e) {
					Log.error(e.getMessage());
					return action;
				}
				
				if(!isDynamic){
					JobService.setDisJobClass(className, clazz);
				}
				
				if(DisJob.class.isInstance(job) && "execute".equals(methodName)){
					action = (DisJob) job;
					JobService.setDisJobInstance(className, action);
				}
			}
		}
		return action;
	}
}
