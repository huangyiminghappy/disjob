package com.huangyiming.disjob.monitor.rms;

public class MonitorType {

	/**
	 * 
	 * @author Disjob
	 *
	 */
	public enum Business{

		JOB_TIMEOUT("5"), //任务超时报警.这种类型的监控是看每一个 job 的，而不是按类型分
		JOB_RPC_LIST_EMPTY("10"), //发送rpc 时发现 url 为null
		RPC_RESPONSE_EXCEPTION("6"); //系统调用后返回错误信息
		
		private String index;
		private Business(String index){
			this.index = index;
		}
		public String getIndex(){
			return index;
		}
		
	}
	
	public enum System {

		SERIOUS_DISJOB_SIMGLE_AVAILABLE("7"),
		
		SERIOUS_BORROWCHANNEL_ERROR("8"),//borrow a netty channel 异常达十次(严重)
		
		DISJOB_EXCEPTION("1"), // disJob发生异常
		
		CONNECT_WAITING_TIMEOUT("2"); // 连接等待超时
		
		private String index;
		
		private System(String index){
			this.index = index;
		}
		public String getIndex(){
			return index;
		}
	}
	
	public enum NetWork {

		CHANNEL_UNAVAIABLE("3"),	//网络出现闪断
		
		SYSTEM_CONNECT_REFUSE("4"); //系统连接不上
		private String index;
		
		private NetWork(String index){
			this.index = index;
		}
		public String getIndex(){
			return index;
		}
	}
}
