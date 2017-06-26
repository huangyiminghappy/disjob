package com.huangyiming.disjob.service;

import org.springframework.stereotype.Service;

import com.huangyiming.disjob.AlamerLogWriter;

@Service("springServiceTest")
public class SpringServiceTest {
	public SpringServiceTest() {
		System.out.println("-----");
	}
	public void writer(String info){
		AlamerLogWriter.writer.println(info);
		AlamerLogWriter.writer.flush();
	}
}  
