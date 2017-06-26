package com.huangyiming.disjob;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class AlamerLogWriter {

	public static PrintWriter writer = null ;
	
	static{
		try {
			writer = new PrintWriter("D:/exe_job.log");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
