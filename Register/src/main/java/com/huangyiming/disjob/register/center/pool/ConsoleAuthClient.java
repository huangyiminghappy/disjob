package com.huangyiming.disjob.register.center.pool;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.huangyiming.disjob.register.auth.AuthConstants;

@Service("consoleAuthClient")
public class ConsoleAuthClient {

	@Value("${zk.host}")
	private String ZKHost;

	public CuratorFramework get() {
		Builder builder = CuratorFrameworkFactory.builder().connectString(ZKHost)
				.retryPolicy(new ExponentialBackoffRetry(1500, 3));
		builder.sessionTimeoutMs(6000);
		builder.connectionTimeoutMs(6000);
		builder.authorization(AuthConstants.defaultAuthInfos);
		CuratorFramework client = builder.build();
		client.start();
		return client;
	}

	public String getZKHost() {
		return ZKHost;
	}

	public void setZKHost(String zKHost) {
		ZKHost = zKHost;
	}
	
}
