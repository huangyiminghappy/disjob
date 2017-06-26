package com.huangyiming.disjob.common;

public interface EventType {

	public static final int RECEIVE_RPECESS_TIME = 1 ;//接收业务服务器发送调度时间 进度 事件处理
	
	public static final int CREATE_DBBASICINFO = 2 ;//调度任务的时候，创建进度信息
	
	public static final int UPDATE_DBBASICINFO = 3 ;//调度任务的时候，创建进度信息
}
