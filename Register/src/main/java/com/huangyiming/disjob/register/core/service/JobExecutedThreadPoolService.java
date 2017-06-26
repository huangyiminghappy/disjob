package com.huangyiming.disjob.register.core.service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.huangyiming.disjob.common.thread.ExecutorFactory;
import com.huangyiming.disjob.common.thread.ThreadPoolBuilder;
import com.huangyiming.disjob.common.util.LocalHost;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.common.util.UUIDHexGenerator;
import com.huangyiming.disjob.monitor.rms.CommonRMSMonitor;
import com.huangyiming.disjob.monitor.rms.MonitorType;
import com.huangyiming.disjob.quence.ActionQueue;
import com.huangyiming.disjob.quence.BaseActionQueue;
import com.huangyiming.disjob.quence.Executor;
import com.huangyiming.disjob.register.auth.node.GlobalAuthNode;
import com.huangyiming.disjob.register.core.RpcParameter;
import com.huangyiming.disjob.register.core.action.UpdateLastFireTimeAction;
import com.huangyiming.disjob.register.domain.Job;
import com.huangyiming.disjob.register.repository.watch.listener.ConnectionStateListenerImpl;
import com.huangyiming.disjob.register.rpc.SubscribeService;
import com.huangyiming.disjob.register.rpc.ZookeeperRegistry;
import com.huangyiming.disjob.rpc.action.SchedulerJobAction;
import com.huangyiming.disjob.rpc.client.HURL;
import com.huangyiming.disjob.rpc.client.proxy.RpcClient;
import com.huangyiming.disjob.rpc.client.proxy.RpcClientCache;
import com.huangyiming.disjob.rpc.cluster.loadbalance.RoundRobinLoadBalance;
import com.huangyiming.disjob.rpc.codec.Header;
import com.huangyiming.disjob.rpc.codec.RpcRequest;
import com.huangyiming.disjob.rpc.codec.RpcRequestData;
import com.huangyiming.disjob.rpc.utils.RpcSpringWorkFactory;
import com.huangyiming.disjob.rpc.utils.ScheduleResponse;

/**
 * 处理rpc执行后修改job执行时间
 * @author Disjob
 *
 */
@Service("jobExecutedThreadPoolService")
public class JobExecutedThreadPoolService {
    
	private CuratorFramework client;
	
	@Value("${zk.host}")
	private String ZKHost;
	
	
	@Resource
	public   ZookeeperRegistry zookeeperRegistry;
	
	private ConcurrentHashMap<String, ActionQueue> jobRpcQueue = new ConcurrentHashMap<String, ActionQueue>();
	
	public CuratorFramework getClient() {
		return client;
	}
 
	/**
	 * 因为job执行完后要更新zk上执行时间,所以构造zkclient
	 */
	@PostConstruct
	public void init(){
		Builder builder = CuratorFrameworkFactory.builder().connectString(ZKHost).retryPolicy(new ExponentialBackoffRetry(1500, 3));
		builder.sessionTimeoutMs(6000);
		builder.connectionTimeoutMs(6000);
		builder.authorization(new GlobalAuthNode(ZKHost).getAuthInfos());
		client = builder.build();
		client.getConnectionStateListenable().addListener(new ConnectionStateListenerImpl());
		client.start();
		try {
			client.blockUntilConnected(1, TimeUnit.SECONDS);
		}catch(Exception e){
			LoggerUtil.error("com.huangyiming.disjob.register.core.service.impl.GeneralSchedulerServiceImpl.init() not connect zk");
		}
		 
	}
    
    @PostConstruct
    protected void start(){
    	
		ExecutorFactory.setSubmitRpcExecutor(new Executor(ThreadPoolBuilder.getInstance().builderSubmitRpcThreadPool()));
    }
    
    
    public void submit(com.huangyiming.disjob.register.domain.Job job){
    	getRpcQueue(job).enqueue(new UpdateLastFireTimeAction(job));

    }
    
    public void submit(Job job,HURL hurl,String parameters,String sharingRequestId){
    	
    	//因为php的phpFilePath可能为空,目前发下客户端收到的className,methodName,path都为空,不论是java还是php正常情况下classname都不会为空
    	HURL tmpHurl = checkNetDisConnect(hurl,3);
		
    	if(tmpHurl == null){
    		LoggerUtil.error("group:"+job.getGroupName()+"; job name:"+job.getJobName()+" send the rpc is error. "+hurl.toAllString());
    		return;
    	}
		
    	RpcParameter rpcParameter = new RpcParameter(job,tmpHurl,parameters,sharingRequestId);
    	 //1、根据JobDataMap提供的数据和HURL对象组装调用数据
    	RpcRequest request = new RpcRequest();
    	Header header = new Header();
    	header.setType((byte)1);
    	header.setVersion(1);
    	
    	RpcRequestData data = new RpcRequestData();
    	data.setRequestId(UUIDHexGenerator.generate());
    	data.setPath(rpcParameter.getHurl().getPhpFilePath());
    	data.setClassName(rpcParameter.getHurl().getClassName());
    	data.setMethodName(rpcParameter.getHurl().getMethodName());
    	data.setParameters(rpcParameter.getParameters());
    	data.setSharingRequestId(rpcParameter.getSharingRequestId());
    	request.setHeader(header);
    	request.setData(data);
    	
    	//2、组装调度结果数据
    	ScheduleResponse sRep = new ScheduleResponse();
    	sRep.setGroupName(rpcParameter.getJob().getGroupName());
    	sRep.setJobName(rpcParameter.getJob().getJobName());
    	sRep.setRequestId(data.getRequestId());
    	sRep.setScheduleStartTime(new Date());
    	sRep.setScheduleServerIp(new LocalHost().getIp());
    	sRep.setExecuteServerIp(rpcParameter.getHurl().getHost());
    	RpcClient rpcClient = RpcClientCache.get(rpcParameter.getHurl());
    	//3、有可能走到这里的时候这个服务端已经下线了，可能拿到为null,因此需要再去发现一组服务
    	if(rpcClient == null){
    		List<HURL> hurls = SubscribeService.getHURListProvidesByService(job.getGroupName(), job.getJobName());
    		if(hurls == null|| hurls.isEmpty()){
    			CommonRMSMonitor.sendBusiness(MonitorType.Business.JOB_RPC_LIST_EMPTY, job.getGroupName()+"_"+job.getJobName()+" 任务可用地址为空", job.getGroupName(), job.getJobName());
    			return ;
    		}
    		
    		tmpHurl = new RoundRobinLoadBalance(hurls).select();
    		rpcClient = RpcClientCache.get(tmpHurl);
    	}
    	
    	if(rpcClient == null){
    		return ;
    	}
    	
    	sRep.setScheduleEndTime(new Date());
    	sRep.setTimeOut(rpcParameter.getJob().getTimeOut());
    	RpcSpringWorkFactory.getStoreRepThreadPoolService().putRpcRequest(request);
    	//4、发送rpc 请求
    	RpcSpringWorkFactory.getStoreRepThreadPoolService().initStateQueue(sRep);//(sRep, rpcClient, request);
    	getRpcQueue(job).enqueue(new SchedulerJobAction(sRep, rpcClient, request));
    	getRpcQueue(job).enqueue(new UpdateLastFireTimeAction(job));
    }
    
    /**
     * 二期会话绑定 有可能出现网络抖动的情况，因此这里需要做个检测策略
     * @param hurl
     * @return
     */
    private HURL checkNetDisConnect(HURL hurl,int checkCount){
    	if(checkHurlParamter(hurl)){
    		return hurl;
    	}
    	
    	HURL tmpHurl = hurl ;
		do {
			LoggerUtil.debug("hurl classname is null:" + hurl.toAllString());
			try {
				TimeUnit.SECONDS.sleep(1);// 如果失效，则等个1秒钟再去zk 上面取数据
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			List<HURL> urlLst = zookeeperRegistry.doDiscover(hurl);
			hurl = new RoundRobinLoadBalance(urlLst).select();
			if (checkHurlParamter(hurl)) {
				LoggerUtil.debug("after get hurl classname is not null:"+ hurl.toAllString());
				tmpHurl = hurl;
				ZookeeperRegistry.subscribedCategoryResponses.put(hurl, urlLst);//有效则更新list
				break;
			}
			checkCount--;
		}while(checkCount>0);
		
    	if(checkCount>0){
    		LoggerUtil.debug("after "+checkCount+" check the hurl, get hurl classname is "+tmpHurl.toAllString());
    	}else{
    		LoggerUtil.debug("after "+checkCount+" check the hurl, get hurl is invalidate untile. the hurl is :"+tmpHurl.toAllString());
    		tmpHurl = null ;
    	}
    	
    	return tmpHurl ;
    }
    
    /**
     * 检测 hurl 是否合法。如果则返回 true,否则返回 false.通过断点发现：class name 和 method name 可能会出现 "null" 和 "" 这两种情况。
     * 因此需要对这两种不合法的 字段进行额外判断处理
     * @param hurl
     * @return
     */
	private boolean checkHurlParamter(HURL hurl) {
		if(hurl == null){
			return false ;
		}
		
		if(hurl.getClassName() == null || hurl.getClassName().trim().length() <= 0 || "null".equalsIgnoreCase(hurl.getClassName())){
			return false ;
		}
		
		if(hurl.getMethodName() == null || hurl.getMethodName().trim().length() <= 0 || "null".equalsIgnoreCase(hurl.getMethodName())){
			return false ;
		}
		
		return true ;
	}
    
    
	private ActionQueue getRpcQueue(Job job) {
		String jobKey = job.getGroupName()+"_"+job.getJobName();
		ActionQueue queue = jobRpcQueue.putIfAbsent(jobKey,new BaseActionQueue(ExecutorFactory.getSubmitRpcExecutor()));
		if(queue == null){
			queue = jobRpcQueue.get(jobKey);
		}
		LoggerUtil.info("key:"+jobKey+"; queue:"+queue.getQueue().size());
		return queue;
	}
    
    @PreDestroy
    protected void shutdown(){
    	ExecutorFactory.getSubmitRpcExecutor().stop();
    }

    public static void main(String[] args) {
    	HURL  hurl = new HURL("group","disJob://","localhost",9501,"test job","/test.php","","null","1.0.0",null);
    	hurl = new JobExecutedThreadPoolService().checkNetDisConnect(hurl, 3);
    	System.out.println(hurl);
    }
}
