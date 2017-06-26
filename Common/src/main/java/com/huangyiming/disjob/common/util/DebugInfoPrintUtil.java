package com.huangyiming.disjob.common.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/*
 * 调式信息 打印到指定的文件
 */
public class DebugInfoPrintUtil {

	public static void debug2(BufferedWriter writer,String info){
		try {
			writer.write(info);
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void debug1(String path,String info){
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
