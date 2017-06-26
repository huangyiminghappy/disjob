package com.huangyiming.job;

import javax.annotation.Resource;

import org.junit.Test;

import com.huangyiming.disjob.common.exception.ZKNodeException2;
import com.huangyiming.disjob.register.center.pool.ThreadLocalClient;
import com.huangyiming.disjob.register.domain.Job;
import com.huangyiming.disjob.register.job.JobOperationService;

public class JobOperationServiceTest extends com.huangyiming.BaseJunitTest {

	@Resource
	public JobOperationService jobOperationService;

	@Resource
	private ThreadLocalClient threadLocalClient;

	@Test
	public void testAddJob(){
		threadLocalClient.setCuratorClient();
//		System.err.println(jobOperationService.getAllJobGroupForPageList());
		Job job = new Job();
		job.setFilePath("/usr/local/php-test/TestService.php");
		job.setJobName("test111222");
		job.setGroupName("groupNew");
		job.setClassName("TestService");
		job.setMethodName("test1");
		job.setTimeOut(60000L);
		job.setCronExpression("0,30 * * * * ?");
		try {
			jobOperationService.addJob(job, "admin");
		} catch (ZKNodeException2 e) {
			e.printStackTrace();
		}
	}
}