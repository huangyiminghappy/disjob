package com.huangyiming.disjob.monitor.db.dbpool;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
 
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:META-INF/spring-test-monitor.xml")
public class dbpoolTest {
  
	@Autowired
	private com.jolbox.bonecp.BoneCPDataSource dataSourceTest;
	@Test
	public void test() throws SQLException{
		Connection  conn1 = dataSourceTest.getConnection();
		Connection  conn2 = dataSourceTest.getConnection();
		new Thread(new test1(conn2)).start();
		Connection  conn3 = dataSourceTest.getConnection();
		System.out.println(conn2 == conn3);
		System.out.println(conn3);

	}
	
	     
	
	   
}

class test1 implements Runnable{
	Connection  conn;
	public test1(Connection  conn){
		this.conn = conn;
	}

	@Override
	public void run() {
		 try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
