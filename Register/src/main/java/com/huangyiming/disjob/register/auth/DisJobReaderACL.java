package com.huangyiming.disjob.register.auth;

import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;

public class DisJobReaderACL extends ACL{

	public DisJobReaderACL(String adminAccount) {
		super(AuthConstants.PERMS_READ, new Id(AuthConstants.SCHEME, AuthUtil.algorithm(adminAccount)));
	}

}
