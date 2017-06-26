package com.huangyiming;

import java.io.IOException;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Before;
import org.junit.Test;

import com.huangyiming.disjob.register.repository.watch.WatchApiCuratorImpl;

public class WatchApiTest {

    CuratorFramework client = null;
    @Before
    public void init(){
        client = CuratorFrameworkFactory.builder()
                .connectString("10.40.6.100:2181")
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        client.start();
    }
     
    
    @Test
    public void createPathInvokeListener() {
        try{
        final WatchApiCuratorImpl watcher = new WatchApiCuratorImpl();
        watcher.pathChildrenWatch(client, "/grobalegrow/demo/1/server", true, new PathChildrenCacheListener(){

            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception
            {
                switch (event.getType()) {
                    case CHILD_ADDED:
                        System.out.println("CHILD_ADDED: " + event.getData().getPath());
                        break;
                    case CHILD_REMOVED:
                        System.out.println("CHILD_REMOVED: " + event.getData().getPath());
                        break;
                    case CHILD_UPDATED:
                        System.out.println("CHILD_UPDATED: " + event.getData().getPath());
                        break;
                    default:
                        break;
                        
               
                
               }
                List<ChildData> list = watcher.getPathChildrenCache().getCurrentData();
                for(ChildData data: list){
                    System.out.println("list"+data.getPath() + ",value:"+new String(data.getData()));
                }
               System.out.println("==="+event.getData().getPath() + ",value:"+new String(event.getData().getData()));  
            }
        });
        client.create().forPath("/grobalegrow/demo/1/server/192.168.35:2199", "disJob://192.168.110:2189/abc.efg?className=classya&methodName=woqu&version=1&test=00".getBytes());
       Thread.sleep(Integer.MAX_VALUE);
        }catch(Throwable e){
            e.printStackTrace();
        }
    }
    
    @Test
    public void treeWatchTest1() throws IOException{
    	String znode = "/disJob/scheduler/slave/10.37.1.214/execution";
    	final WatchApiCuratorImpl watcher = new WatchApiCuratorImpl();
    	watcher.pathChildrenWatch(client, znode, true, new PathChildrenCacheListener(){
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception
            {
                switch (event.getType()) {
                    case CHILD_ADDED:
                        System.out.println("CHILD_ADDED: " + event.getData().getPath() + " data: " + new String( event.getData().getData() ));
                        break;
                    case CHILD_REMOVED:
                        System.out.println("CHILD_REMOVED: " + event.getData().getPath());
                        break;
                    case CHILD_UPDATED:
                        System.out.println("CHILD_UPDATED: " + event.getData().getPath() + " data: " + new String( event.getData().getData() ));
                        break;
                    default:
                        break;
               }
            }
        });
    	PathChildrenCache cache = watcher.getPathChildrenCache();
    	try {
    		String groupPath = znode + "/omsTest";
    		//client.delete().deletingChildrenIfNeeded().forPath(groupPath);
    		//client.create().creatingParentsIfNeeded().forPath(groupPath);
            client.setData().forPath(groupPath, "jobTest1|jobTest2|jobTest3|jobTest4|test5".getBytes());	
            Thread.sleep(1000000);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
		    cache.close();
		}
    	
    }
}
