package com.huangyiming.disjob.java.core.rpc;

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

	public String getSharingRequestId() {
		return sharingRequestId;
	}

	public void setSharingRequestId(String sharingRequestId) {
		this.sharingRequestId = sharingRequestId;
	}

	@Override
	public String toString() {
		return "RpcRequestData [requestId=" + requestId + ", path=" + path + ", className=" + className
				+ ", methodName=" + methodName + ", parameters=" + parameters + "]";
	}
	
}
