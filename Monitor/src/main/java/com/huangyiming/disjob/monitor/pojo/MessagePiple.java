package com.huangyiming.disjob.monitor.pojo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.huangyiming.disjob.monitor.db.domain.DBJobBasicInfo;

/**
 * 
 * @author Disjob
 *
 */
public class MessagePiple implements Serializable,Comparable<MessagePiple>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private LinkedBlockingQueue<DBJobBasicInfo> responseMsgQueue ; 
	private String requestId ;
	private String groupName ;
	private String jobName ;
	
	private long timeOut ;//秒 单位
	private long startTime ;//毫秒值 为单位
	private long endTime ;// endTime = startTime+ timeOut，毫秒值 为单位
	private String receiveMessageTime ; //接收 job 执行完 返回消息的时间
	/**
	 * 统一处理为 秒。超时不足一秒的 给1 秒：
	 * @param timeOut
	 */
	public MessagePiple(String requestId,String groupName,String jobName,long timeOut) { 
		responseMsgQueue = new LinkedBlockingQueue<DBJobBasicInfo>(1) ;//just receive a message of job server response
		this.timeOut = timeOut < 0 ? 1 : timeOut ;
		this.timeOut += 1 ;//5s 的网络延时等其他情况的处理。超时报警 5s 的误差时间 和界面上设置的
		this.startTime = System.currentTimeMillis();
		this.endTime = startTime + timeOut * 1000 ;//结束的时间点
		this.requestId = requestId ;
		this.groupName = groupName ;
		this.jobName = jobName ;
	}
	
	public DBJobBasicInfo take() throws InterruptedException{
		long tmpTimeOut = this.endTime - System.currentTimeMillis() ; 
		tmpTimeOut = tmpTimeOut <=0 ? 0 : tmpTimeOut;
		return this.responseMsgQueue.poll(tmpTimeOut,TimeUnit.MILLISECONDS);
	}
	public void offer(DBJobBasicInfo message){
		this.receiveMessageTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		this.responseMsgQueue.offer(message);
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public long getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(long timeOut) {
		this.timeOut = timeOut;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public String getReceiveMessageTime() {
		return receiveMessageTime;
	}

	public void setReceiveMessageTime(String receiveMessageTime) {
		this.receiveMessageTime = receiveMessageTime;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (endTime ^ (endTime >>> 32));
		result = prime * result + ((requestId == null) ? 0 : requestId.hashCode());
		result = prime * result + ((responseMsgQueue == null) ? 0 : responseMsgQueue.hashCode());
		result = prime * result + (int) (startTime ^ (startTime >>> 32));
		result = prime * result + (int) (timeOut ^ (timeOut >>> 32));
		return result;
	}

	/**
	 * 按结束的先后时间排序
	 */
	@Override
	public int compareTo(MessagePiple other) {
		Long cuEndTime = this.endTime ;
		Long otherEndTime = other.endTime;
		
		int result = cuEndTime.compareTo(otherEndTime);//因为开始时间不一样，所以这里的结束时间不可能一样
		
		return (int) (result == 0 ? this.startTime-other.startTime : result) ;
	}
	
	public static void main(String[] args) {
		TreeMap<MessagePiple,String> sort = new TreeMap<MessagePiple,String>();
		sort.put(new MessagePiple("id", "g1", "j1", 3), "");
		sort.put(new MessagePiple("id", "g2", "j2", 4), "");
		sort.put(new MessagePiple("id", "g3", "j3", 5), "");
		sort.put(new MessagePiple("id", "g4", "j4", 6), "");
		sort.put(new MessagePiple("id", "g5", "j5", 7), "");
		for(Entry<MessagePiple, String> entry : sort.entrySet()){
			System.out.println(entry.getKey().getEndTime()+"; "+entry.getKey().getTimeOut());
		}
	}
}
