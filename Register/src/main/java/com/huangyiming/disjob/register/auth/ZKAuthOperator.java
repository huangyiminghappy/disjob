package com.huangyiming.disjob.register.auth;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.ZKPaths;

import com.huangyiming.disjob.register.repository.ZnodeApi;
import com.huangyiming.disjob.register.repository.ZnodeApiCuratorImpl;

public class ZKAuthOperator {

	private String username;
	
	private CuratorFramework client;
	
	public ZKAuthOperator(String username, CuratorFramework client){
		this.username = username;
		this.client = client;
	}
	
	/**
	 * 授权
	 * @param group
	 * @param type 权限类型	reader  owner
	 */
	public void assign(String group, String type){
		String authPath = ZKPaths.makePath(AuthConstants.userRootPath, username, group, type);
		ZnodeApi znode = new ZnodeApiCuratorImpl();
		if(!znode.checkExists(client, authPath)){
			znode.createPersistent(client, authPath, null);
		}
	}
	
	/**
	 * 去掉授权
	 * @param group
	 * @param type
	 */
	public void unAssign(String group, String type){
		String authPath = ZKPaths.makePath(AuthConstants.userRootPath, username, group, type);
		ZnodeApi znode = new ZnodeApiCuratorImpl();
		if(znode.checkExists(client, authPath)){
			znode.deleteByZnode(client, authPath);
		}
		String groupPath = ZKPaths.makePath(AuthConstants.userRootPath, username, group);
		if(znode.checkExists(client, groupPath) && znode.getChildren(client, groupPath).isEmpty()){
			znode.deleteByZnode(client, groupPath);
		}
	}
}
