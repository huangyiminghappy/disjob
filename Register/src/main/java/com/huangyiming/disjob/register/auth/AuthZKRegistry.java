package com.huangyiming.disjob.register.auth;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.data.ACL;

import com.huangyiming.disjob.common.Constants;
import com.huangyiming.disjob.register.auth.node.GroupAuthNode;
import com.huangyiming.disjob.register.repository.ZnodeApi;
import com.huangyiming.disjob.register.repository.ZnodeApiCuratorImpl;

public class AuthZKRegistry {

	private CuratorFramework client;

	private ZnodeApi znode = new ZnodeApiCuratorImpl();

	public AuthZKRegistry(CuratorFramework client) {
		this.client = client;
	}

	public void init() {
		if(!znode.checkExists(client, Constants.ROOT)){
			znode.createPersistent(client, Constants.ROOT, null);
		}
		if(!znode.checkExists(client, AuthConstants.authRootPath)){
			znode.createPersistent(client, AuthConstants.authRootPath, null);						
		}
		if(!znode.checkExists(client, AuthConstants.groupRootPath)){
			znode.createPersistent(client, AuthConstants.groupRootPath, null);
		}
		if(!znode.checkExists(client, AuthConstants.adminRootPath)){
			znode.createPersistent(client, AuthConstants.adminRootPath, AuthConstants.ADMIN_ACCOUNT);			
		}
		if(!znode.checkExists(client, AuthConstants.visitorRootPath)){
			znode.createPersistent(client, AuthConstants.visitorRootPath, AuthConstants.VISITOR_ACCOUNT);			
		}
		znode.setACL(client, AuthConstants.authRootPath, AuthConstants.defaultAuthACLs);
		znode.setACL(client, AuthConstants.groupRootPath, AuthConstants.defaultAuthACLs);
		znode.setACL(client, AuthConstants.adminRootPath, AuthConstants.defaultAuthACLs);
		znode.setACL(client, AuthConstants.visitorRootPath, AuthConstants.defaultAuthACLs);
		//
		String jobrootpath = ZKPaths.makePath(Constants.ROOT, Constants.APP_JOB_NODE_ROOT);
		if(!znode.checkExists(client, jobrootpath)){
			znode.createPersistent(client, jobrootpath, null);
		}
		//对已有job生成权限信息
		List<String> grouplist = znode.getChildren(client, jobrootpath);
		for(String groupname : grouplist){
			String authgrouppath = ZKPaths.makePath(AuthConstants.groupRootPath, groupname);
			if(!znode.checkExists(client, authgrouppath)){
				List<ACL> groupAclList = new GroupAuthNode(client, groupname).createACLs();
				String jobGroupPath = ZKPaths.makePath(jobrootpath, groupname);
				znode.setACL(client, jobGroupPath, groupAclList);
				for(String jobName : znode.getChildren(client, jobGroupPath)){
					String jobPath = ZKPaths.makePath(jobGroupPath, jobName, Constants.APP_JOB_NODE_CONFIG);
					znode.setACL(client, jobPath, groupAclList);
				}
			}
		}
	}
}
