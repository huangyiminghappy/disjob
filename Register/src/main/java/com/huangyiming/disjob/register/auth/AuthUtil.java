package com.huangyiming.disjob.register.auth;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.AuthInfo;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.framework.imps.DefaultACLProvider;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.register.center.RegistryExceptionHandler;
import com.google.common.collect.Lists;
   
public class AuthUtil {

	private static class AuthACLProvider extends DefaultACLProvider{
		@Override
		public List<ACL> getAclForPath(String path) {
			if(path.startsWith(AuthConstants.authRootPath)){ //auth权限信息仅针对auth节点
				return AuthConstants.defaultAuthACLs;					
			}else{
				return super.getAclForPath(path);
			}
		}
	}
	
	public static CuratorFramework getClient(String zkHost){
		Builder builder = CuratorFrameworkFactory.builder().connectString(zkHost)
				.retryPolicy(new ExponentialBackoffRetry(1500, 3));
		builder.sessionTimeoutMs(6000);
		builder.connectionTimeoutMs(6000);
		List<AuthInfo> globalAuthInfos = Lists.newArrayList();
		globalAuthInfos.add(AuthConstants.defaultAuthInfo);
		builder.authorization(globalAuthInfos);
		builder.aclProvider(new AuthACLProvider());
		CuratorFramework client = builder.build();
		client.start();
		try {
			client.blockUntilConnected(3, TimeUnit.SECONDS);
		} catch (final Exception ex) {
			 RegistryExceptionHandler.handleException(ex);
		}
		return client;
	}
	
	public static String algorithm(String usernamepassworld) {
		try {
			return DigestAuthenticationProvider.generateDigest(usernamepassworld);
		} catch (NoSuchAlgorithmException e) {
			LoggerUtil.error("生成acl的id时,在环境中找不到相关算法.", e);
		}
		return usernamepassworld;
	}
	
	public static void closeClient(CuratorFramework client){
		CloseableUtils.closeQuietly(client);
	}

	public static CuratorFramework getAdminClient(String zkHost) {
		Builder builder = CuratorFrameworkFactory.builder().connectString(zkHost)
				.retryPolicy(new ExponentialBackoffRetry(1500, 3));
		builder.sessionTimeoutMs(6000);
		builder.connectionTimeoutMs(6000);
		List<AuthInfo> globalAuthInfos = Lists.newArrayList();
		globalAuthInfos.add(AuthConstants.defaultAuthInfo);
		globalAuthInfos.add(AuthConstants.defaultAdminAuthInfo);
		builder.authorization(globalAuthInfos);
		builder.aclProvider(new AuthACLProvider());
		CuratorFramework client = builder.build();
		client.start();
		try {
			client.blockUntilConnected(3, TimeUnit.SECONDS);
		} catch (final Exception ex) {
			 RegistryExceptionHandler.handleException(ex);
		}
		return client;
	}
	
}
