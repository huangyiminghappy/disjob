package com.huangyiming.disjob.register.auth.node;

import java.util.ArrayList;
import java.util.List;

import org.apache.curator.framework.AuthInfo;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.ACL;

import com.huangyiming.disjob.register.auth.AuthConstants;
import com.huangyiming.disjob.register.auth.AuthZKRegistry;
import com.huangyiming.disjob.register.auth.DisJobAuthInfo;
import com.huangyiming.disjob.register.auth.DisJobOwnerACL;
import com.huangyiming.disjob.register.repository.ZnodeApi;
import com.huangyiming.disjob.register.repository.ZnodeApiCuratorImpl;

class AdminAuthNode{

	protected CuratorFramework client;

	protected ZnodeApi znode = new ZnodeApiCuratorImpl();
	
	private String nodepath = AuthConstants.adminRootPath;
	
	public AdminAuthNode(CuratorFramework client){
		this.client = client;
	}
	
	public List<AuthInfo> getAuthInfos(){
		List<AuthInfo> authInfos = new ArrayList<>();
		if(znode.getData(client, AuthConstants.adminRootPath) == null){
			new AuthZKRegistry(client).init();
		}
		authInfos.add(new DisJobAuthInfo(znode.getByteData(client, nodepath)));
		return authInfos;
	}
	
	public List<ACL> getACLs() {
		List<ACL> acllist = new ArrayList<>();
		String adminAccount = znode.getData(client, nodepath);
		acllist.add(new DisJobOwnerACL(adminAccount));
		return acllist;
	}
}
