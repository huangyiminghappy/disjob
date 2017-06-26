package com.huangyiming.disjob.register.auth.node;

import java.util.ArrayList;
import java.util.List;

import org.apache.curator.framework.AuthInfo;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.ZKPaths;

import com.huangyiming.disjob.register.auth.AuthConstants;
import com.huangyiming.disjob.register.auth.AuthUtil;
import com.huangyiming.disjob.register.auth.DisJobAuthInfo;
import com.huangyiming.disjob.register.repository.ZnodeApi;
import com.huangyiming.disjob.register.repository.ZnodeApiCuratorImpl;

public class UserAuthNode {

	String username;
	
	ZnodeApi znode = new ZnodeApiCuratorImpl();

	private CuratorFramework client;
	
	public UserAuthNode(String zkhost, String username) {
		this.username = username;
		client = AuthUtil.getClient(zkhost);
		
	}
	
	public UserAuthNode(CuratorFramework client, String username) {
		this.username = username;
		this.client = client;
	}

	public List<AuthInfo> getAuthInfos(){
		List<AuthInfo> authInfolist = new ArrayList<>();
		switch (username) {
		case "admin":
			authInfolist.add(new DisJobAuthInfo(znode.getByteData(client, AuthConstants.adminRootPath)));
			break;
		case "visitor":
			authInfolist.add(new DisJobAuthInfo(znode.getByteData(client, AuthConstants.visitorRootPath)));
			break;
		default:
			String userAuthPath = AuthConstants.userRootPath + "/" + username;
			if(znode.checkExists(client, userAuthPath)){
				List<String> groups = znode.getChildren(client, userAuthPath);
				for(String group : groups){
					String groupPath = userAuthPath + "/" + group;
					List<String> authTypes = znode.getChildren(client, groupPath);
					for(String authType : authTypes){
						String authAccountPath = ZKPaths.makePath(AuthConstants.groupRootPath, group, authType);
						authInfolist.add(new DisJobAuthInfo(znode.getByteData(client, authAccountPath)));
					}
				}
			}
			break;
		}
		AuthUtil.closeClient(client);
		return authInfolist ;
	}
	
	/**
	 * 取出用户 在 jobgroup 下的权限信息 [reader, owner]
	 * @param jobgroup
	 * @return
	 */
	public boolean[] getAuthInfo(String jobgroup){
		boolean[] res = new boolean[]{false,false};
		String readerPath = ZKPaths.makePath(AuthConstants.userRootPath, username, jobgroup, AuthConstants.READER);
		String ownerPath = ZKPaths.makePath(AuthConstants.userRootPath, username, jobgroup, AuthConstants.OWNER);
		res[0] = znode.checkExists(client, readerPath);
		res[1] = znode.checkExists(client, ownerPath);
		return res;
		
	}
}
