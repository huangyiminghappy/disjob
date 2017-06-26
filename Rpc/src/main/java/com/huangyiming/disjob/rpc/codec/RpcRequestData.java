package com.huangyiming.disjob.rpc.codec;

public class RpcRequestData {
	
	private String requestId;
	private String path;
	private String className;
	private String methodName;
	private String parameters;
 	private String sharingRequestId;
 	private boolean isOnlytask;
	
	public boolean isOnlytask() {
		return isOnlytask;
	}

	public void setOnlytask(boolean isOnlytask) {
		this.isOnlytask = isOnlytask;
	}

	public String getSharingRequestId() {
		return sharingRequestId;
	}

	public void setSharingRequestId(String sharingRequestId) {
		this.sharingRequestId = sharingRequestId;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		if(path == null || "null".equalsIgnoreCase(path.trim())){
			System.err.println(path);
		}
		this.path = path;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	@Override
	public String toString() {
		return "RpcRequestData [requestId=" + requestId + ", path=" + path + ", className=" + className
				+ ", methodName=" + methodName + ", parameters=" + parameters + ", sharingRequestId=" + sharingRequestId
				+ ", isOnlytask=" + isOnlytask + "]";
	}

	 

	 
	
}
