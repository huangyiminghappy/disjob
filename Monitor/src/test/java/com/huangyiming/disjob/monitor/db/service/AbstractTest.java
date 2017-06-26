package com.huangyiming.disjob.monitor.db.service;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * <pre>
 * 
 *  File: AbstractTest.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  抽象测试类，指定单元测试执行类、配置文件
 * 
 *  Revision History
 *
 *  Date：		2016年6月24日
 *  Author：		Disjob
 *
 * </pre>
 */
@RunWith(SpringJUnit4ClassRunner.class) 
@ContextConfiguration(locations="classpath:META-INF/spring-test-monitor.xml")
public abstract class AbstractTest {
	
}