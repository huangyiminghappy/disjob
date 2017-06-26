package com.huangyiming.disjob.rpc.codec;

public class RpcResponse implements Response{
	private int version;
	private int length;
    private String requestId;
    private String exception;
    private Object value;
    private String jobBegingTime;
    private String jobCompleteTime;
    private long processTime;
    private long timeout;
    private String code;
    private String jobRecvTime;
    private String sharingRequestId;
    /**
     * 是否强制杀死,1代表强制
     */
    private int killprocess;
    
    public RpcResponse(){
    	
    }
    
    public RpcResponse(Response response) {
        //this.value = response.getValue();
    	this.value = null;
        this.exception = response.getException();
        this.requestId = response.getRequestId();
        this.processTime = response.getProcessTime();
        this.timeout = response.getTimeout();
         
    }

	public String getJobRecvTime() {
		return jobRecvTime;
	}

	public void setJobRecvTime(String jobRecvTime) {
		this.jobRecvTime = jobRecvTime;
	}

	public Object getValue() {		
		return value;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	@Override
	public String getRequestId() {
		return requestId;
	}
	
	@Override
	public String getException() {
		return exception;
	}
	
	public String getJobBegingTime() {
		return jobBegingTime;
	}

	public void setJobBegingTime(String jobBegingTime) {
		this.jobBegingTime = jobBegingTime;
	}

	public String getJobCompleteTime() {
		return jobCompleteTime;
	}

	public void setJobCompleteTime(String jobCompleteTime) {
		this.jobCompleteTime = jobCompleteTime;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	@Override
	public long getProcessTime() {
		return processTime;
	}

	@Override
	public void setProcessTime(long time) {
		this.timeout = time;
	}

	@Override
	public long getTimeout() {
		return timeout;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getKillprocess() {
		return killprocess;
	}

	public void setKillprocess(int killprocess) {
		this.killprocess = killprocess;
	}

	public String getSharingRequestId() {
		return sharingRequestId;
	}

	public void setSharingRequestId(String sharingRequestId) {
		this.sharingRequestId = sharingRequestId;
	}

	@Override
	public String toString() {
		return "RpcResponse [version=" + version + ", length=" + length
				+ ", requestId=" + requestId + ", exception=" + exception
				+ ", value=" + value + ", jobBegingTime=" + jobBegingTime
				+ ", jobCompleteTime=" + jobCompleteTime + ", processTime="
				+ processTime + ", timeout=" + timeout + ", code=" + code
				+ ", jobRecvTime=" + jobRecvTime + ", sharingRequestId="
				+ sharingRequestId + ", killprocess=" + killprocess + "]";
	}
}
