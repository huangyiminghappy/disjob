package com.grobalegrow.disJob.produce.job;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StartUpDisJobMain {

	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"disJob.xml"});
		context.start();
	}

}
     