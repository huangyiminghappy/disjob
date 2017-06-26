package com.huangyiming.disjob.rpc.client.proxy;
import java.util.UUID;

import org.junit.Test;

import com.huangyiming.disjob.common.exception.TransportException;
import com.huangyiming.disjob.rpc.client.HURL;
import com.huangyiming.disjob.rpc.client.proxy.RpcClient;
import com.huangyiming.disjob.rpc.codec.Header;
import com.huangyiming.disjob.rpc.codec.Response;
import com.huangyiming.disjob.rpc.codec.RpcRequest;
import com.huangyiming.disjob.rpc.codec.RpcRequestData;

/**
 * 
 * @author Disjob
 *
 */
public  class RpcClientTest{
	String urlStr = "disJob://10.40.6.100:9501/test?serverGroup=oms&phpFilePath=/usr/local/rpc-project/test.php&className=Test&methodName=start&version=0.1";

	@Test
	public void rpcClientTest(){
		for(int index=0;index<500;index++){
			System.out.println("test-fixed--------------xiaoluo-----------4");       
			HURL hurl = new HURL();
			hurl.setHost("10.32.1.245");
			hurl.setPort(9501);
			hurl.setPhpFilePath("/usr/local/php-test/TestService.php");
			hurl.setClassName("com.huangyiming.disJob.java.app.FireNowJob_0");
			hurl.setMethodName("execute");
			hurl.setVersion("0.1");
			hurl.addParameter("async", "true");

			Header header = new Header();
			header.setType((byte)1);
			header.setVersion(1);

			RpcClient client = new RpcClient(hurl);
			client.open();

			RpcRequest request1 = new RpcRequest();
			RpcRequestData data1 = new RpcRequestData();
			data1.setRequestId(UUID.randomUUID().toString());
			data1.setPath(hurl.getPhpFilePath());
			data1.setClassName(hurl.getClassName());
			data1.setMethodName(hurl.getMethodName());
			request1.setHeader(header);
			request1.setData(data1);

			//    	RpcRequest request2 = new RpcRequest();
			//    	RpcRequestData data2 = new RpcRequestData();
			//    	data2.setRequestId(UUID.randomUUID().toString());
			//    	data2.setPath(hurl.getPhpFilePath());
			//    	data2.setClassName(hurl.getClassName());
			//    	data2.setMethodName(hurl.getMethodName());
			//    	request2.setHeader(header);
			//    	request2.setData(data2);
			//    	
			//    	RpcRequest request3 = new RpcRequest();
			//    	RpcRequestData data3 = new RpcRequestData();
			//    	data3.setRequestId(UUID.randomUUID().toString());
			//    	data3.setPath(hurl.getPhpFilePath());
			//    	data3.setClassName(hurl.getClassName());
			//    	data3.setMethodName(hurl.getMethodName());
			//    	request3.setHeader(header);
			//    	request3.setData(data3);

			try {
				Response rep1 = client.request(request1);
				String reqId1 = rep1.getRequestId();
				System.out.println(reqId1);

				//    		Response rep2 = client.request(request2);
				//    		String reqId2 = rep2.getRequestId();
				//    		System.out.println(reqId2);
				//    			
				//    		Response rep3 = client.request(request3);
				//    		String reqId3 = rep3.getRequestId();
				//    		System.out.println(reqId3);

				//    		Thread.sleep(100000);
				//    			
				//    		Response rep4 = client.request(request3);
				//    		String reqId4 = rep4.getRequestId();
				//    		System.out.println(reqId4);
			} catch (TransportException e) {
				e.printStackTrace();
			} 
			//    	catch (InterruptedException e) {
			//			e.printStackTrace();
			//		}
		}
		try {
			Thread.sleep(10000000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}