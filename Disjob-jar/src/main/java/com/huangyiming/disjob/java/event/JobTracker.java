package com.huangyiming.disjob.java.event;

import com.huangyiming.disjob.event.AbstractEventObject;
import com.huangyiming.disjob.event.ObjectEvent;
import com.huangyiming.disjob.event.ObjectListener;
import com.huangyiming.disjob.java.DisJobConstants;
import com.huangyiming.disjob.java.ExecutorBuilder;
import com.huangyiming.disjob.java.bean.RpcContainer;
import com.huangyiming.disjob.java.job.JobAction;
import com.huangyiming.disjob.java.job.SharingJobCommand;
import com.huangyiming.disjob.java.service.DisJobConfigService;
import com.huangyiming.disjob.java.spring.SpringJobAction;
import com.huangyiming.disjob.java.spring.SpringSharingJobCommand;
import com.huangyiming.disjob.java.utils.StringUtils;
import com.huangyiming.disjob.quence.Action;
import com.huangyiming.disjob.quence.ActionQueue;
import com.huangyiming.disjob.quence.BaseActionQueue;

public class JobTracker extends AbstractEventObject<RpcContainer>{

	private BaseActionQueue baseActionQueue ;
	public JobTracker() {
		this.baseActionQueue = new BaseActionQueue(ExecutorBuilder.getExecutor());
	}
	
	@Override
	public void attachListener() {
		this.addListener(new ObjectListener<RpcContainer>() {
			
			public void onEvent(ObjectEvent<RpcContainer> event) {
				RpcContainer rpcContainer = event.getValue();
				if(rpcContainer.getMsg() == null){
					return ;
				}
				
				if(rpcContainer.getMsg().getData() == null){
					return ;
				}
				
				//SharingRequestId 为null 表示不是分片的job,否则是
				if(StringUtils.isEmpty(rpcContainer.getMsg().getData().getSharingRequestId())){
					if(DisJobConfigService.getStartupType() == DisJobConstants.StartUpType.SPRING_START_UP){
						enQueue(new SpringJobAction(rpcContainer));
					}else{
						enQueue(new JobAction(rpcContainer));
					}
				}else{
					if(DisJobConfigService.getStartupType() == DisJobConstants.StartUpType.SPRING_START_UP){
						ExecutorBuilder.getSharingExecutor().execute(new SpringSharingJobCommand(rpcContainer));
					}else{
						ExecutorBuilder.getSharingExecutor().execute(new SharingJobCommand(rpcContainer));
					}
				}
			}
		}, EventType.RPC_REQUEST_HANDLER);
	}
	
	private void enQueue(Action action){
		if(action.getActionQueue() ==null){
			action.setActionQueue(baseActionQueue);
		}
		this.baseActionQueue.enqueue(action);
	}
	
	public void notifyRpcHandler(RpcContainer rpcContiner){
		ObjectEvent<RpcContainer> objectEvent = new ObjectEvent<RpcContainer>(rpcContiner, EventType.RPC_REQUEST_HANDLER);
		notifyListeners(objectEvent);
	}
	
	public ActionQueue getActionQueue(){
		
		return this.baseActionQueue;
	}
}
