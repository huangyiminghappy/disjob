package com.huangyiming.disjob.java.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/*
 * 调式信息 打印到指定的文件
 */
public class DebugInfoPrintUtil {

	public static void debug(BufferedWriter writer,String info){
		try {
			writer.write(info);
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void debug(String path,String info){
		try {
			BufferedWriter writer  = new BufferedWriter(new FileWriter(path,true));
			writer.write(info);
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
