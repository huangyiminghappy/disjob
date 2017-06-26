 package com.huangyiming.disjob.monitor.db.service;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * <pre>
 * 
 *  File: AllTests.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  测试所有
 * 
 *  Revision History
 *
 *  Date：		2016年6月24日
 *  Author：		Disjob
 *
 * </pre>
 */   
@RunWith(Suite.class)
@SuiteClasses({ DBJobExeProgressServiceTest.class, DBJobBasicInfoServiceTest.class, DBStatisticsServiceTest.class })
public class AllTests {

}