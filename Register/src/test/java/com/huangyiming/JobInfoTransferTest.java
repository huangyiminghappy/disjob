package com.huangyiming;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Before;
import org.junit.Test;

import com.huangyiming.disjob.common.model.JobInfo;
import com.huangyiming.disjob.register.domain.Job;
import com.huangyiming.disjob.register.repository.ZnodeApi;
import com.huangyiming.disjob.register.repository.ZnodeApiCuratorImpl;
import com.google.gson.Gson;

public class JobInfoTransferTest {
	
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
	public void getJobFromZKTest(){
		ZnodeApi znodeApi = new ZnodeApiCuratorImpl();
		String jobPath="/disJob/job/oms14/test14/config";
		Job job =  new Gson().fromJson(znodeApi.getData(client, jobPath), Job.class);
		System.out.println(znodeApi.getData(client, jobPath));
		System.out.println(job.toString());
	}
 
}
