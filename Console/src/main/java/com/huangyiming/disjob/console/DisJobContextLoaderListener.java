package com.huangyiming.disjob.console;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextLoaderListener;

import com.huangyiming.disjob.common.util.LoggerUtil;


public class DisJobContextLoaderListener extends ContextLoaderListener {
	  
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			if (driver.getClass().getClassLoader() == cl) {
				try {
					LoggerUtil.info("Deregistering JDBC driver {}", driver);
					DriverManager.deregisterDriver(driver);
				} catch (SQLException ex) {
					LoggerUtil.error("Error deregistering JDBC driver {}",driver, ex);
				}
			} else {
				
				LoggerUtil.error("Not deregistering JDBC driver {} as it does not belong to this webapp's ClassLoader",driver);
			}
		}
	}

}
