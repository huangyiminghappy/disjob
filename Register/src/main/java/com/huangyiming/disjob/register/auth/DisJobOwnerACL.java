package com.huangyiming.disjob.register.auth;

import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;

public class DisJobOwnerACL extends ACL{

	public DisJobOwnerACL(String adminAccount) {
		super(AuthConstants.PERMS_ALL, new Id(AuthConstants.SCHEME, AuthUtil.algorithm(adminAccount)));
	}

}
