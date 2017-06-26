package com.huangyiming.disjob.register.auth;

import java.util.List;

import org.apache.curator.framework.AuthInfo;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;

import com.huangyiming.disjob.common.Constants;
import com.google.common.collect.Lists;

public class AuthConstants {

	public static final String SCHEME = "digest";
	
	public static final String WORLD_ANYONE = "world:anyone";
	
	public static final String AUTH_ACCOUNT = "auth:auth";
	
	public static final String ADMIN_ACCOUNT = "admin:admin";
	
	public static final String VISITOR_ACCOUNT = "visitor:visitor";
	
	public static final ACL defaultAuthACL = new DisJobOwnerACL(AUTH_ACCOUNT);
	public static final List<ACL> defaultAuthACLs = Lists.newArrayList(defaultAuthACL);
	public static final AuthInfo defaultAuthInfo = new DisJobAuthInfo(AUTH_ACCOUNT.getBytes());
	public static final List<AuthInfo> defaultAuthInfos = Lists.newArrayList(defaultAuthInfo);
	
	public static final String root = Constants.ROOT;
	public static final String authRootPath = root + "/auth";
	public static final String userRootPath = root + "/auth/user";
	public static final String groupRootPath = root + "/auth/group";
	public static final String globalRootPath = root + "/auth/global";
	public static final String visitorRootPath = globalRootPath + "/visitor";
	public static final String adminRootPath = globalRootPath + "/admin";
	
	public static final String READER = "reader";
	public static final String OWNER = "owner";
	public static final String COLON_READER = ":" + READER;
	public static final String COLON_OWNER = ":" + OWNER;
	 
	public static final int PERMS_ALL = ZooDefs.Perms.ALL;
	public static final int PERMS_READ = ZooDefs.Perms.READ;

	public static final AuthInfo defaultAdminAuthInfo = new DisJobAuthInfo(ADMIN_ACCOUNT.getBytes());;
}
