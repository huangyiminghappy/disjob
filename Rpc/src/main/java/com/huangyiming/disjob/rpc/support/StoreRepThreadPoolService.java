package com.huangyiming.disjob.rpc.support;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.huangyiming.disjob.common.thread.ExecutorFactory;
import com.huangyiming.disjob.common.thread.ThreadPoolBuilder;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.monitor.util.MonitorSpringWorkFactory;
import com.huangyiming.disjob.quence.BaseActionQueue;
import com.huangyiming.disjob.quence.Executor;
import com.huangyiming.disjob.rpc.action.ExecuteStateAction;
import com.huangyiming.disjob.rpc.codec.RpcRequest;
import com.huangyiming.disjob.rpc.codec.RpcResponse;
import com.huangyiming.disjob.rpc.utils.ScheduleResponse;

@Service("storeRepThreadPoolService")
public class StoreRepThreadPoolService {
    
	private ConcurrentHashMap<String, BaseActionQueue> jobAction = new ConcurrentHashMap<String, BaseActionQueue>();
	private ConcurrentHashMap<String, Condition> jobConditions = new ConcurrentHashMap<String, Condition>();
	private ConcurrentHashMap<String, RpcRequest> rpcRequestMap = new ConcurrentHashMap<String, RpcRequest>();
	private ReentrantLock lock = new ReentrantLock();
	@PostConstruct
	protected void start() {
		/**
		 * 创建job 往数据库里插入一条数据和返回的数据存数据时使用异步处理
		 */
		ExecutorFactory.offerExecutor(new Executor(ThreadPoolBuilder.getInstance().builderExcutorThreadPool()));
		/**
		 * rpc 返回时，也是放到一个线程池去处理
		 */
		ExecutorFactory.offerExecuteStateExecutor(new Executor(ThreadPoolBuilder.getInstance().builderExeStateExecutorThreadPool()));
	}
    
    /**
     * 记录每一个request id 所对应的key(group+job name).方便rpc 返回消息时，通过request id 找到自己的job queue
     * @param response
     * @param rpcClient
     * @param request
     */
    public void initStateQueue(ScheduleResponse response){
    	String key = response.getGroupName()+"_"+response.getJobName();
    	Condition condition =null ;
    	synchronized (this) {
    		condition = jobConditions.get(key);
    		if(condition == null){
    			condition = lock.newCondition();
    			jobConditions.put(key, condition);
    		}
		}
    	
    	BaseActionQueue queue = jobAction.get(key);
    	synchronized (condition) {
    		queue = jobAction.get(key);
    		if( queue == null){
    			queue = new BaseActionQueue(ExecutorFactory.getExecuteStateExecutor());
    			jobAction.put(key, queue);
    		}
    	}
    	
    	MonitorSpringWorkFactory.getJobService().setRequestIdKey(response.getRequestId(), key);
    }
    
    /**
     * job 执行的服务端返回的消息进行处理
     * @param rep
     */
    public void submit(RpcResponse rep){
    	String key = MonitorSpringWorkFactory.getJobService().getRequestIdKey(rep.getRequestId());//compose of group and job name
    	if(StringUtils.isNoneEmpty(key)){
	    	jobAction.get(key).enqueue(new ExecuteStateAction(rep));
		}else{
			LoggerUtil.warn(rep.getRequestId()+" 所对应的 key: is null.不能被更新进度时间。");
		}
    }
    @PreDestroy
    protected void shutdown(){
    	ExecutorFactory.getExecutor().stop();
    	ExecutorFactory.getExecuteStateExecutor().stop();
    }
    
    public RpcRequest getRpcRequest(String requestId) {
		return rpcRequestMap.get(requestId);
	}

	public void removeRpcRequest(String requestId) {
		rpcRequestMap.remove(requestId);
	}
	
	public void putRpcRequest(RpcRequest rpcRequest){
		if(rpcRequest == null || rpcRequest.getData() == null){
			return ;
		}
		LoggerUtil.debug("put rpcrequest ="+rpcRequest.toString());
		rpcRequestMap.put(rpcRequest.getData().getRequestId(), rpcRequest);
	}
}
