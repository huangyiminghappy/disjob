package com.huangyiming.disjob.java.listener;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;

import com.huangyiming.disjob.java.CuratorClientBuilder;
import com.huangyiming.disjob.java.service.LocalHostService;
import com.huangyiming.disjob.quence.Log;

/**
 * 使用 curator 连接 zookeeper 状态的监听器。因为有可能长时间连接的情况下，会发生连接断开或者 挂起等情况。因此需要
 * 在此做连接状态监听。
 * @author DisJob
 *
 */
public class CuratorConnectStateListener implements ConnectionStateListener{

	public void stateChanged(CuratorFramework client,ConnectionState newState) {
		Log.info("HostName:" + LocalHostService.getHostName() + " IP:" + LocalHostService.getIp() +" session status：" + newState.name());
		if(newState  == ConnectionState.LOST || newState  == ConnectionState.SUSPENDED || ConnectionState.RECONNECTED == newState){
			CuratorClientBuilder.isCanUse = false ;
			while (true) {
				try {
					try {
						TimeUnit.SECONDS.sleep(5);
					} catch (InterruptedException e1) {
						 
					}
					if (client.getZookeeperClient().blockUntilConnectedOrTimedOut()) {
						Log.info("ip : " + LocalHostService.getIp() + " reconnection");
						//重新连接后，检测 /scheduler/slave/ip/status 下面的节点状态值
						
						CuratorClientBuilder.isCanUse = true ;
						break;
					}
				} catch (InterruptedException e) {
					Log.error("InterruptedException lient.getZookeeperClient().blockUntilConnectedOrTimedOut()",e);
					try {
						TimeUnit.SECONDS.sleep(3);
					} catch (InterruptedException e1) {
 					}
				} catch (Exception e) {
					Log.error("Exception lient.getZookeeperClient().blockUntilConnectedOrTimedOut()", e);
					try {
						TimeUnit.SECONDS.sleep(3);
					} catch (InterruptedException e1) {
 					}				}
			}
		}
	}

}
