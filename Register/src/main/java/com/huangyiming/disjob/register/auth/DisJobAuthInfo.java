package com.huangyiming.disjob.register.auth;

import org.apache.curator.framework.AuthInfo;

public class DisJobAuthInfo extends AuthInfo{

	public DisJobAuthInfo(String scheme, byte[] auth) {
		super(scheme, auth);
	}
	
	public DisJobAuthInfo(byte[] auth) {
		super(AuthConstants.SCHEME, auth);
	}
	
	public DisJobAuthInfo(String auth) {
		super(AuthConstants.SCHEME, auth.getBytes());
	}

}
