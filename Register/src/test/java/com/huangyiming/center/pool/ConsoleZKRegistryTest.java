package com.huangyiming.center.pool;

import javax.annotation.Resource;

import org.junit.Test;

import com.huangyiming.disjob.register.center.pool.ConsoleZKRegistry;
import com.huangyiming.BaseJunitTest;

public class ConsoleZKRegistryTest extends BaseJunitTest {
	
	@Resource
	private ConsoleZKRegistry consoleZKRegistry;
     
	@Test
	public void initTest(){
		consoleZKRegistry.init();
	}
}
