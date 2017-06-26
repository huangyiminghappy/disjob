package com.huangyiming;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import com.huangyiming.disjob.common.Constants;
import com.huangyiming.disjob.common.util.LocalHost;
import com.huangyiming.disjob.register.repository.ZnodeApi;
import com.huangyiming.disjob.register.repository.ZnodeApiCuratorImpl;


public  class ZnodeApiTest{
    CuratorFramework client = null;
    @Before
    public void init(){
        client = CuratorFrameworkFactory.builder()
                .connectString("10.40.6.100:2181,10.40.6.101:2181,10.40.6.102:2181")
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        client.start();
    }
	 
	
    @Test
    public void createPersistent() throws Exception{
     	ZnodeApi znodeApi = new ZnodeApiCuratorImpl();
    	znodeApi.createPersistent(client, "/apiTest", null);
    }
    
    @Test
    public void getStat() throws Exception{
    	ZnodeApi znodeApi = new ZnodeApiCuratorImpl();
    	Stat stat = znodeApi.getStat(client, "/apiTest");
    	System.out.println(stat.getAversion());
    }
	
    @Test
    public void makePath() throws Exception{
    	LocalHost localHost = new LocalHost();
     	ZnodeApi znodeApi = new ZnodeApiCuratorImpl();
    	client.start();
    	String path = znodeApi.makePath(Constants.ROOT, Constants.DISJOB_SERVER_NODE_ROOT, Constants.DISJOB_SERVER_NODE_MASTER,localHost.getIp());
    	System.out.println(path);
    	znodeApi.makeDirs(client, path);
    }
    
    @Test
    public void deleteByRecursion() throws Exception{
    	ZnodeApi znodeApi = new ZnodeApiCuratorImpl();
    	znodeApi.deleteByRecursion(client, "/motan");
        //znodeApi.deleteByRecursion(client, "/disJob/scheduler/slave/10.37.1.214/info");
        
    	/*znodeApi.deleteByRecursion(client, "/disJob/rpc/test99");
        znodeApi.deleteByRecursion(client, "/disJob/rpc/test8");

        znodeApi.deleteByRecursion(client, "/disJob/rpc/test9");

        znodeApi.deleteByRecursion(client, "/disJob/rpc/test6");

        znodeApi.deleteByRecursion(client, "/disJob/rpc/test7");

        znodeApi.deleteByRecursion(client, "/disJob/rpc/huang");

        znodeApi.deleteByRecursion(client, "/disJob/rpc/test123");*/
    }
}