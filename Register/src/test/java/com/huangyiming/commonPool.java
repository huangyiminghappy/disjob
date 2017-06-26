package com.huangyiming;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Before;
import org.junit.Test;

import com.huangyiming.disjob.common.exception.TransportException;
import com.huangyiming.disjob.common.util.LocalHost;
import com.huangyiming.disjob.common.util.UUIDHexGenerator;
import com.huangyiming.disjob.register.center.pool.ConsoleCuratorClient;
import com.huangyiming.disjob.register.center.pool.ThreadLocalClient;
import com.huangyiming.disjob.register.job.DisJobServerServiceImpl;
import com.huangyiming.disjob.rpc.client.HURL;
import com.huangyiming.disjob.rpc.client.proxy.RpcClient;
import com.huangyiming.disjob.rpc.codec.Header;
import com.huangyiming.disjob.rpc.codec.Response;
import com.huangyiming.disjob.rpc.codec.RpcRequest;
import com.huangyiming.disjob.rpc.codec.RpcRequestData;
import com.huangyiming.disjob.rpc.codec.RpcResponse;

public class commonPool extends BaseJunitTest{
	
	@Resource
	private ThreadLocalClient threadLocalClient;
	
	@Resource
	public   DisJobServerServiceImpl initServerExecuteJobService;
	
     
 
 CuratorFramework client = null;
	    @Before
    public void init(){
         // zkUrl = new HURL("zookeeper", "10.40.6.100", 2181, "service4");
         // clientUrl = new HURL(EJobConstants.PROTOCOL_MOTAN, "10.40.6.100", 2181, "2");
        LocalHost localHost = new LocalHost();
        //clientUrl = new HURL("oms",EJobConstants.PROTOCOL_MOTAN, localHost.getIp(), 1, "test");
	        client = CuratorFrameworkFactory.builder()
                  .connectString("10.40.6.100:2181,10.40.6.101:2181,10.40.6.102:2181")
                  .sessionTimeoutMs(5000)
                  .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                  .build();
          client.start();
          
          threadLocalClient.setCuratorClient(); 
	    	ConsoleCuratorClient curatorClient = threadLocalClient.getCuratorClient();
	        if (null == curatorClient || !curatorClient.isConnected()) {
	        	System.out.println("get ZK client failed！");
	            return ;
	        }
    }
	@Test
	public void testBorrow() throws TransportException{
		
		  HURL hurl = new  HURL("oms4", "disJob", "10.40.6.100", 9501, "ctestjob1");
	        final RpcClient client = com.huangyiming.disjob.rpc.client.proxy.RpcClientCache.get(hurl);
	    	String parameters ="123";
	    	   //根据JobDataMap提供的数据和HURL对象组装调用数据
	    	final RpcRequest request = new RpcRequest();
	    	Header header = new Header();
	    	header.setType((byte)1);
	    	header.setVersion(1);
	    	
	    	RpcRequestData data = new RpcRequestData();
	    	data.setRequestId(UUIDHexGenerator.generate());
	    	data.setPath(hurl.getPhpFilePath());
	    	data.setClassName(hurl.getClassName());
	    	data.setMethodName(hurl.getMethodName());
	    	data.setParameters(parameters);
	    	
	    	request.setHeader(header);
	    	request.setData(data);
	    	
	    	
	    	ExecutorService executorService = Executors.newFixedThreadPool(10);  
	    	for(int i =0;i<1;i++){
				/*try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				
				  
				executorService.execute(new Runnable() {  
				    public void run() {  

						Response response = new RpcResponse();
						try {
							response = client.request(request);
						} catch (TransportException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

			    		System.out.println(response);
						
					
				    }  
				});  
				 
				 
				
			}
	    	try {
				Thread.sleep(50505050);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	executorService.shutdown();
	    	
 	}
	

}
