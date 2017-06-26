package com.huangyiming.disjob.register.auth.node;

import java.util.List;

import org.apache.curator.framework.AuthInfo;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.ACL;

import com.huangyiming.disjob.register.auth.AuthConstants;
import com.huangyiming.disjob.register.auth.AuthUtil;

/**
 *	/disJob/auth/global 下全局账号的访问, 其下的admin和visitor作为一个整体
 * @author chengangxiong
 *
 */
public class GlobalAuthNode{

	private CuratorFramework client;
	
	private AdminAuthNode adminAuthNode;
	
	private VisitorAuthNode visitorAuthNode;
	
	public GlobalAuthNode(String zkHost) {
		client = AuthUtil.getAdminClient(zkHost);
		init();
	}

	private void init() {
		adminAuthNode = new AdminAuthNode(client);
		visitorAuthNode = new VisitorAuthNode(client);
	}

	public GlobalAuthNode(CuratorFramework client) {
		this.client = client;
		init();
	}
	
	public List<AuthInfo> getAuthInfos(){
		List<AuthInfo> authInfos = adminAuthNode.getAuthInfos();
		authInfos.addAll(visitorAuthNode.getAuthInfos());
		authInfos.add(AuthConstants.defaultAuthInfo);
		AuthUtil.closeClient(client);
		return authInfos ;
	}
	
	public List<ACL> getACLs() {
		List<ACL> acllist = adminAuthNode.getACLs();
		acllist.addAll(visitorAuthNode.getACLs());
		return acllist ;
	}
}
