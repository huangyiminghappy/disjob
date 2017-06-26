package com.huangyiming.disjob.register.repository.watch.listener;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.zookeeper.CreateMode;

import com.huangyiming.disjob.common.Constants;
import com.huangyiming.disjob.common.util.LocalHost;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.register.repository.ZnodeApiCuratorImpl;

/**
 * client会话状态监听
 * @author Disjob
 *
 */
public class ConnectionStateListenerImpl implements ConnectionStateListener {

	@Override
	public void stateChanged(CuratorFramework client, ConnectionState newState) {
		LocalHost localHost = new LocalHost();
		LoggerUtil.info("HostName:" + localHost.getHostName() + " IP:" + localHost.getIp() +" session status：" + newState.name());
		if(newState  == ConnectionState.LOST || newState  == ConnectionState.SUSPENDED || ConnectionState.RECONNECTED == newState){
			while (true) {
				try {
					if (client.getZookeeperClient().blockUntilConnectedOrTimedOut()) {
						LoggerUtil.info("ip : " + new LocalHost().getIp() + " reconnection");
						ZnodeApiCuratorImpl znodeApi = new ZnodeApiCuratorImpl();
						TimeUnit.SECONDS.sleep(5);
						if(!znodeApi.checkExists(client, Constants.ROOT+Constants.DISJOB_SERVER_NODE_ROOT+Constants.DISJOB_SERVER_NODE_SLAVE+Constants.PATH_SEPARATOR + localHost.getIp()+ Constants.DISJOB_SERVER_NODE_SLAVE_STATUS)){
							client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
							.forPath(Constants.ROOT+Constants.DISJOB_SERVER_NODE_ROOT+Constants.DISJOB_SERVER_NODE_SLAVE+Constants.PATH_SEPARATOR + localHost.getIp()+ Constants.DISJOB_SERVER_NODE_SLAVE_STATUS, Constants.READY.getBytes("UTF-8"));
						}
						break;
					}
				} catch (InterruptedException e) {
					LoggerUtil.error(
							"InterruptedException lient.getZookeeperClient().blockUntilConnectedOrTimedOut()",e);
					try {
						TimeUnit.SECONDS.sleep(3);
					} catch (InterruptedException e1) {
						 
					}
					//break;
				} catch (Exception e) {
					LoggerUtil.error("Exception lient.getZookeeperClient().blockUntilConnectedOrTimedOut()", e);
					try {
						TimeUnit.SECONDS.sleep(3);
					} catch (InterruptedException e1) {
						 
					}
					//break;
				}
			}
		}
	}
}
