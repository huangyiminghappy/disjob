package com.huangyiming.job;

import org.apache.curator.test.TestingServer;

public class ZKTestingServer {

	public static void main(String[] args){
		TestingServer server;
		try {
			server = new TestingServer();
			String connectString = server.getConnectString();
			server.start();
			System.err.println(connectString);
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
