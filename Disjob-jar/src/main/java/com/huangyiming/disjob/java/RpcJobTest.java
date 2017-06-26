package com.huangyiming.disjob.java;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import com.huangyiming.disjob.java.service.LocalHostService;

public class RpcJobTest {

	public static final String nameSpace = "disJob" ;
	public static final CuratorFramework client = CuratorFrameworkFactory
			.builder().connectString("10.32.1.245:2181").namespace(nameSpace).sessionTimeoutMs(3000)
			.retryPolicy(new ExponentialBackoffRetry(3000, 10)).build();
	static{
		client.start();
	}
	public static void main(String[] args) throws Exception {
		for(int i=1;i<=20;i++){
			createJob("global", "job_"+i);
		}
		client.close();
	}
	
	public static void createJob(String group,String job) throws Exception{
		String path = "/rpc/" + group + "/" + job ;
		//1、添加一个新的Job
		client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path,("a new job:"+new Date().toString()).getBytes(Charset.forName("utf-8")));
		String ip = LocalHostService.getIp();
		//2、创建 /disJob-dev/rpc/global/job/providers/ip:9501 这个数据节点，并写入需要调度的基本信息
		String ipNode = path +"/providers/"+LocalHostService.getIp()+":9501";
		//3、disJob://10.40.6.89:9501/test?serverGroup=oms&phpFilePath=/usr/local/php-test/TestService.php&className=TestService&methodName=test&version=1
		StringBuffer sb = new StringBuffer();
		sb.append("disJob://");
		sb.append(ip+":9501/");
		sb.append(job+"?");
		sb.append("serverGroup="+group);
		sb.append("&");
		sb.append("phpFilePath=''");
		sb.append("&");
		sb.append("className=globalgrow.local.job.PrintTimeJob");
		sb.append("&");
		sb.append("methodName=execute");
		sb.append("&");
		sb.append("version=1");
		String data = sb.toString();
		client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(ipNode,data.getBytes(Charset.forName("utf-8")));
	}
	
	@SuppressWarnings("unused")
	private static void getAllChilds() throws Exception {
		List<String> childNames = client.getChildren().forPath("/");
		for(String name:childNames){
			System.err.println(name);
		}
	}
}
