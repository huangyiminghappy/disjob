package com.huangyiming.disjob.register.center;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.huangyiming.disjob.common.Constants;
import com.huangyiming.disjob.common.util.LocalHost;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.register.auth.node.GlobalAuthNode;
import com.huangyiming.disjob.register.repository.ZnodeApi;
import com.huangyiming.disjob.register.repository.watch.listener.ConnectionStateListenerImpl;
import com.huangyiming.disjob.register.rpc.DisJobConstants;
import com.huangyiming.disjob.register.rpc.SubscribeService;
import com.huangyiming.disjob.register.rpc.ZookeeperRegistry;
import com.huangyiming.disjob.register.utils.ZooKeeperRegistryUtils;
import com.huangyiming.disjob.rpc.client.HURL;

/**
 * RpcZKRegistry初始化，获取到ZooKeeper的client连接
 * 
 * @author Disjob
 * @data 创建时间：2016-5-19
 */
@Service("rpcZKRegistry")
public class RpcZKRegistry extends AbstractZKRegistryCenter
{
    @Resource
    private ZnodeApi znodeApi;

    private CuratorFramework client;

    @Resource
    private ZookeeperRegistry zookeeperRegistry;

    @Resource
    private SubscribeService subscribeService;
    

    @Value("${zk.host}")
    private String ZKHost;

    private String rpcRootUrl = Constants.ROOT+"/rpc";

    public RpcZKRegistry()
    {
    }

    public CuratorFramework getClient()
    {
        return client;
    }

    @PostConstruct
    public void init()
    {
        LoggerUtil.debug("DisJob:DisJob server client init,RpcZKRegistry ZK server list is:"+ ZKHost);
 
        Builder builder = CuratorFrameworkFactory.builder().connectString(ZKHost)
				.retryPolicy(new ExponentialBackoffRetry(1500, 3));
		builder.sessionTimeoutMs(6000);
		builder.connectionTimeoutMs(6000);
		builder.authorization(new GlobalAuthNode(ZKHost).getAuthInfos());
        client = builder.build();
        client.getConnectionStateListenable().addListener(new ConnectionStateListenerImpl());
        client.start();
        try{
            client.blockUntilConnected(3, TimeUnit.SECONDS);
            initRootNode();
        }catch (final Exception ex){
            RegistryExceptionHandler.handleException(ex);
        }
        ZooKeeperRegistryUtils.zookeeperRegistry = zookeeperRegistry;
        
        zookeeperRegistry.setZkClient(client);
        
        subscribeServer();
       /* //应该所有节点都能监控到
        RegisterUtils.watchRpc2Job(true, client, zookeeperRegistry);*/
       
    }

    @Override
    protected boolean initRootNode()
    {
        return false;
    }

    @PreDestroy
    public void dostory()
    {
        CloseableUtils.closeQuietly(client);
    }

    /**
     * 订阅服务
     * @param clientUrl
     */
    public void subscribeServer(HURL clientUrl)
    {

        subscribeService.DoSubscribe(clientUrl);
    }

    /**
     * 订阅服务,将服务信息放入本地缓存
     */
     protected void subscribeServer()
    {
        List<String> groupList = new ArrayList<String>();
        try
        {
        	if(!znodeApi.checkExists(client, rpcRootUrl)){
        		znodeApi.createPersistent(client, rpcRootUrl, null);
        	}
            groupList = client.getChildren().forPath(rpcRootUrl);
            if(CollectionUtils.isNotEmpty(groupList)){
                String ip = new LocalHost().getIp();
                for(String group : groupList){
                    String groupNode = rpcRootUrl + DisJobConstants.PATH_SEPARATOR + group;
                    List<String> serverNameList = client.getChildren().forPath(groupNode);
                    if(CollectionUtils.isNotEmpty(serverNameList)){
                       for(String serverName: serverNameList){
                             HURL hurl = new HURL(group , ip, serverName);
                             subscribeServer(hurl);
                            //这句只为初始化的时候存数据到本地缓存
                            subscribeService.doDiscover(hurl);
                       }
                    }
                 }
            }
        }
        catch (Exception e)
        {
            LoggerUtil.error("get group by  " + rpcRootUrl + " error ", e);
        }
        
    }
 
    public SubscribeService getSubscribeService()
    {
        return subscribeService;
    }
}
