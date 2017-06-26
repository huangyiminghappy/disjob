package com.huangyiming.disjob.rpc.client.proxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
 
public class ExecutorInfoTest {

	public static void main(String[] args) throws Exception {
 
		String logInfo = "F:/debug.log";
		BufferedReader reader = new BufferedReader(new FileReader(logInfo));
		System.err.println(reader);
		String line = "";
		File file = new File("D:/result.log");
		if(!file.exists()){
			file.createNewFile();
		}
		String key = "ff80808158867ded015886836f076b10";
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		while((line=reader.readLine())!=null){
			if(line.indexOf(key) > 0){
				System.out.println(line);
				writer.write(line);
				writer.newLine();
			}
		}
		writer.flush();
		writer.close();
	}

	private static void findByInfoLog() throws FileNotFoundException,IOException {
		String logInfo = "D:/EdisJob/log/info.log";
		BufferedReader reader = new BufferedReader(new FileReader(logInfo));
		System.err.println(reader);
		String line = "";
		while((line=reader.readLine())!=null){
			if(line.indexOf("8aa0817558577a7c015857a20b7d68cd") > 0){
				System.out.println(line);
			}
		}
	}
	
	
}
