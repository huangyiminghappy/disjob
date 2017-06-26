package com.huangyiming.disjob.console.service;

import java.sql.DriverManager;

import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextLoaderListener;

public class DisJobContextLoaderListener extends ContextLoaderListener {
	
	public void contextDestroyed(ServletContextEvent event) {
		super.contextDestroyed(event);
		  try{
	            DriverManager.deregisterDriver(DriverManager.getDrivers().nextElement());
	        }catch(Exception e){
	            e.printStackTrace();
	        }  
	}

}
