package com.huangyiming.disjob.java.listener;

/**
 * all of the event type will define in this interface
 * @author Disjob
 *
 */
public interface EventType {

	public static final int START_UP = 1 ;
	
	public static final int DISJOB_STOP = 2 ;
	
	public static final int START_FINISH = 3 ;//
	
	public static final int DYNAMIC_JOB = 4 ;
	
	public static final int RPC_REQUEST_HANDLER = 5 ;//rpc 请求到来 事件通知
	
	public static final int REGISTER_JOB = 6 ;
}
