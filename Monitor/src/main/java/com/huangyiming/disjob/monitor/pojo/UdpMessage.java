package com.huangyiming.disjob.monitor.pojo;

import java.io.Serializable;

public class UdpMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//"type:1,"."requestId".$requestId."content:".$taskNum.",time:".$time;
	//{"type":"0","requestId":null,"content":0,"time":"2016-07-14 14:07:52","rep":{"requestId":"db33bed6-743a-4faa-83f5-e9684bfcd661","jobBegingTime":"2016-07-14 14:38:49","jobCompleteTime":"2016-07-14 14:38:52","processTime":5000,"timeout":0,"code":0,"exception":""}}
//{"type":"0","requestId":null,"content":0,"time":"2016-07-14 15:07:23","rep":{"requestId":"8aa8867455e7c3d90155e831b6c101bd","jobBegingTime":"2016-07-14 15:07:22","jobCompleteTime":"2016-07-14 15:12:23","processTime":5000,"timeout":0,"code":0,"exception":""}}
	private String type;
	
	private String requestId;
	
	private String content;
	
	private String time;
	
	private BaseInfoMessage rep;
	
	

	public BaseInfoMessage getRep() {
		return rep;
	}

	public void setRep(BaseInfoMessage rep) {
		this.rep = rep;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

 
 

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "UdpMessage [type=" + type + ", requestId=" + requestId + ", content=" + content + ", time=" + time
				+ ", rep=" + rep + "]";
	}

	

 

	 
	
	
	
	
	
	
	
	
	

}
