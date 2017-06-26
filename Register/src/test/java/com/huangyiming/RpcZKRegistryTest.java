package com.huangyiming;

import java.util.List;

import javax.annotation.Resource;

import org.apache.curator.framework.CuratorFramework;
import org.junit.Test;

import com.huangyiming.disjob.register.center.RpcZKRegistry;
import com.huangyiming.disjob.register.repository.ZnodeApiCuratorImpl;
import com.huangyiming.disjob.register.rpc.ZookeeperRegistry;
import com.huangyiming.disjob.rpc.client.HURL;

public class RpcZKRegistryTest extends BaseJunitTest{
    @Resource
    private RpcZKRegistry rpcZKRegistry;
    
     @Test
     public void initClientTest() throws Exception{
         CuratorFramework client = rpcZKRegistry.getClient();
         System.out.println(client.getState());
         ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
        
         znode.createEphemeral(client, "/disJob/rpc/oms/test/providers/192.168.99.ccc:15", "disJob://10.40.6.74:74/test1?phpFilePath=/usr/local/rpc-project/test.php&className=Test&methodName=start&version=0.1");
        
          
         Thread.sleep(Integer.MAX_VALUE);
     }
     
     
     
     
     public void printHURL(HURL hurl){
         List<HURL> map =  ZookeeperRegistry.subscribedCategoryResponses.get(hurl);
          if(map !=null){
               
                  for(HURL h : map){
                      System.out.println("rpc url is "+h.toAllString());
                  }
              
          }
         
     }
     public static void main(String[] args)
    {
        String str ="/disJob/rpc/oms/test";
        System.out.println(str.split("/")[2]);  
        System.out.println(str.split("/")[3]);

    }

}
