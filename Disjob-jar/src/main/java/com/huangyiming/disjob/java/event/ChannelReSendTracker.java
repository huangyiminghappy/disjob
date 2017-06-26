package com.huangyiming.disjob.java.event;

import io.netty.channel.Channel;
import java.util.List;
import com.huangyiming.disjob.event.AbstractEventObject;
import com.huangyiming.disjob.event.ObjectEvent;
import com.huangyiming.disjob.event.ObjectListener;
import com.huangyiming.disjob.java.ExecutorBuilder;
import com.huangyiming.disjob.java.action.TolerantReSendResponseAction;
import com.huangyiming.disjob.java.core.rpc.RpcResponse;
import com.huangyiming.disjob.java.service.ClientLinkedService;
import com.huangyiming.disjob.java.service.JobService;
import com.huangyiming.disjob.quence.Action;
import com.huangyiming.disjob.quence.BaseActionQueue;
import com.huangyiming.disjob.quence.Log;

/**
 * 某一台ip 发送失败的消息  还是发给那一台 ip
 * @author Disjob
 *
 */
public class ChannelReSendTracker extends AbstractEventObject<Channel>{

	private BaseActionQueue baseActionQueue = new BaseActionQueue(ExecutorBuilder.getJobExecutor()); 
	
	@Override
	public void attachListener() {
		addListener(new ObjectListener<Channel>() {
			
			public void onEvent(ObjectEvent<Channel> event) {
				Channel channel = event.getValue();
				List<RpcResponse> failMsgs  = JobService.getFailRpcResponse(channel);
				Log.warn("ip:"+ClientLinkedService.getRemoterAddress(channel)+"; fail msg:"+failMsgs.size());
				if(failMsgs!=null&&failMsgs.size()>0){
					enqueue(new TolerantReSendResponseAction(channel,failMsgs));
				}
			}
		}, EventType.CHANNER_RESEND);
	}

	private void enqueue(Action action){
		this.baseActionQueue.enqueue(action);
	}
	
	public void notifyReSend(Channel channel){
		ObjectEvent<Channel> event = new ObjectEvent<Channel>(channel, EventType.CHANNER_RESEND);
		notifyListeners(event);
	}
}
