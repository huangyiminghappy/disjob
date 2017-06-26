package com.huangyiming.disjob.register.center.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.AuthInfo;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.register.auth.AuthConstants;
import com.huangyiming.disjob.register.auth.node.UserAuthNode;
import com.huangyiming.disjob.register.repository.watch.listener.ConnectionStateListenerImpl;

/**
 * @author Disjob
 */
@Service("consoleZKRegistry")
public final class ConsoleZKRegistry {
    
	@Value("${zk.host}")
	private String ZKHost;
	
	public ConsoleZKRegistry() {
	}
	 	 
	public ConsoleCuratorClient init(){
		return init(null);
	}
    public ConsoleCuratorClient init(String username){
	    LoggerUtil.debug("DisJob server client init, ConsoleZKRegistry ZK server list is:"+ ZKHost);
           
        Builder builder = CuratorFrameworkFactory.builder().connectString(ZKHost)
				.retryPolicy(new ExponentialBackoffRetry(1500, 3));
		builder.sessionTimeoutMs(6000);
		builder.connectionTimeoutMs(6000);
		
		List<AuthInfo> authInfos = new ArrayList<>();
		if(!StringUtils.isEmpty(username)){
			authInfos.addAll(new UserAuthNode(ZKHost, username).getAuthInfos());
		}
		authInfos.add(AuthConstants.defaultAuthInfo);
		builder.authorization(authInfos);
		CuratorFramework client = builder.build();
        client.getConnectionStateListenable().addListener(new ConnectionStateListenerImpl());
        client.start();
		try {
			boolean connected = client.blockUntilConnected(1, TimeUnit.SECONDS);
			ConsoleCuratorClient curatlrClient = new ConsoleCuratorClient(client, connected);
			return curatlrClient;
		} catch (InterruptedException e) {
			LoggerUtil.warn("zk client establish failed！");  
			return null;
		}
    } 
}
