package com.huangyiming.disjob.monitor.event;

import com.huangyiming.disjob.common.EventType;
import com.huangyiming.disjob.common.thread.ExecutorFactory;
import com.huangyiming.disjob.event.AbstractEventObject;
import com.huangyiming.disjob.event.ObjectEvent;
import com.huangyiming.disjob.event.ObjectListener;
import com.huangyiming.disjob.quence.Action;
import com.huangyiming.disjob.quence.ActionQueue;
import com.huangyiming.disjob.quence.BaseActionQueue;
import com.huangyiming.disjob.monitor.DBJobBasicInfoOpAction;
import com.huangyiming.disjob.monitor.db.domain.DBJobBasicInfo;

public class JobTracker extends AbstractEventObject<DBJobBasicInfo>{

	private BaseActionQueue baseActionQueue ;
	private String groupName;
	private String jobName ;
	public JobTracker(String groupName,String jobName) {
		this.groupName = groupName;
		this.jobName = jobName;
		this.baseActionQueue = new BaseActionQueue(ExecutorFactory.getExecutor());
	}
	
	public void attachListener() {
		this.addCreateDBBasicInfoEvent();
		this.addUpdateDBBasicInfoEvent();
	}
	
	/**
	 * 添加调度一个job 时往数据库里添加一个进度信息的事件
	 */
	private void addCreateDBBasicInfoEvent(){
		this.addListener(new ObjectListener<DBJobBasicInfo>() {
			@Override
			public void onEvent(ObjectEvent<DBJobBasicInfo> event) {
				DBJobBasicInfo info = event.getValue();
				if(info !=null){
					enQueue(new DBJobBasicInfoOpAction(info, DBJobBasicInfoOpAction.BASICINFO_CREATE_OP));
				}
			}
		}, EventType.CREATE_DBBASICINFO);
	}
	
	private void addUpdateDBBasicInfoEvent(){
		this.addListener(new ObjectListener<DBJobBasicInfo>() {
			@Override
			public void onEvent(ObjectEvent<DBJobBasicInfo> event) {
				DBJobBasicInfo info = event.getValue();
				if(info != null){
					enQueue(new DBJobBasicInfoOpAction(info, DBJobBasicInfoOpAction.BASICINFO_UPDATE_OP));
				}
			}
		}, EventType.UPDATE_DBBASICINFO);
	}
	
	private void enQueue(Action action){
		if(action.getActionQueue() ==null){
			action.setActionQueue(baseActionQueue);
		}
		this.baseActionQueue.enqueue(action);
	}
	
	public void notifyCreateDBBasicInfoEvent(DBJobBasicInfo info){
		ObjectEvent<DBJobBasicInfo> objectEvent = new ObjectEvent<DBJobBasicInfo>(info, EventType.CREATE_DBBASICINFO);
		notifyListeners(objectEvent);
	}
	
	public void notifyUpdateDBBasicInfoEvent(DBJobBasicInfo info){
		ObjectEvent<DBJobBasicInfo> updateEvent = new ObjectEvent<DBJobBasicInfo>(info, EventType.UPDATE_DBBASICINFO);
		notifyListeners(updateEvent);
	}
	
	public ActionQueue getActionQueue(){
		
		return this.baseActionQueue;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
}

