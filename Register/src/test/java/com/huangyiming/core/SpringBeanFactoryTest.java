package com.huangyiming.core;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.CronExpression;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.huangyiming.disjob.common.model.JobInfo;
import com.huangyiming.disjob.register.core.jobs.StatefullJobFactory;
import com.huangyiming.disjob.register.core.service.GeneralSchedulerService;
import com.huangyiming.disjob.register.rpc.SubscribeService;
import com.huangyiming.disjob.rpc.client.HURL;

/**
 * <pre>
 * 
 *  File: SpringBeanFactoryTest.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  测试spring配置加载是否有效
 * 
 *  Revision History
 *
 *  Date：		2016年5月11日
 *  Author：		Disjob
 *
 * </pre>
 */
@RunWith(SpringJUnit4ClassRunner.class)//使用junit4进行测试  
@ContextConfiguration   ({"/META-INF/spring-register-test.xml"}) //加载配置文件  
public class SpringBeanFactoryTest {
	@Resource
	private GeneralSchedulerService serv;
	@Resource
	 SubscribeService subscribeService;
	
	@Test
	public void basicTest(){
		if(serv == null){
        	System.out.println("--------------------QuartzJobService is null--------------------------------");
        }
        else{
        	System.out.println("--------------------QuartzJobService here--------------------------------");
         	JobInfo job = new JobInfo();
        	job.setCronExpression("*/10 * * * * ?");
        	job.setGroupName("JobTest");
        	job.setJobName("test1");
        	//job.setScheduleMode(2);
        	job.setJobClass(StatefullJobFactory.class);
        	System.out.println("[add]");//创建job
        	/*List<HURL> lists = subscribeService.getHURListProvidesByService("oms", "test");*/
//        	JobInfo job1 = new JobInfo();
//        	job1.setCronExpression("0/10 * * * * ?");
//        	job1.setJobName("test2");
//        	job1.setScheduleMode(2);
//        	job1.setJobClass(StatefullJobFactory.class);
//        	System.out.println("[add]");//创建job
        	serv.create(job);
//        	serv.create(job1);
        	try {
    			TimeUnit.SECONDS.sleep(30);
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
        	job.setCronExpression("*/40 * * * * ?");
        	serv.update(job);
//        	job.setGroupName("DEFAULT");//更新
//        	job.setCronExpression("0/30 * * * * ?");
//        	serv.update(job);
        }
        try {
			TimeUnit.SECONDS.sleep(150);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
       
        for(int index= 0;index<20;index++){
        	for(JobInfo job : serv.findAllExecuting())//查找所有Job
            	System.out.println("[all-exceting]"+job);
        	try {
    			TimeUnit.SECONDS.sleep(1);
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
        }
        try {
			TimeUnit.SECONDS.sleep(1065);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        System.out.println( "---------------------测试 spring配置----------------------关闭-" );
	}
	
	@Test
	public void testCronExpression(){
		System.out.println(CronExpression.isValidExpression("0/10 * * * * ?"));
	}

}
