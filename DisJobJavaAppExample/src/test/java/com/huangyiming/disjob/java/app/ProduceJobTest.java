package com.huangyiming.disjob.java.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;
 
public class ProduceJobTest {
	private final static int[] SECONDS = {5,10,15,20,25,30,35,40,45,50,55};
	  
	private final static int[] MIN = {1,2,3,4,5,6,7,8,9,10};
	private final static String prefix = "3000";
	public static void main(String[] args) {
		String srcPath = "E:/workspace/disjob/DisJobJavaApp/src/main/java/com/huangyiming/one/hundred/FireNowJobTemp.java"; 
		String targetPath = "E:/workspace/disjob/DisJobJavaApp/src/main/java/com/huangyiming/one/hundred/FireNowJob_%s.java";
		produceJob(srcPath,targetPath);
	}
   
	private static void produceJob(String srcPath,String targetPath) {
		try {
			int jobCount = 1000 ;
			for(int i=1;i<=jobCount;i++){
				BufferedReader reader = new BufferedReader(new FileReader(srcPath));
				BufferedWriter writer = new BufferedWriter(new FileWriter(String.format(targetPath, i)));
				String line = "";
				while((line=reader.readLine()) !=null){
					if(line.indexOf("FireNowJobTemp")>0){
						line = line.replace("FireNowJobTemp", "FireNowJob_"+i);
					}
					if(line.indexOf("JobDec") >0 ){
						line = line.replace("10", String.valueOf(new Random().nextInt(59)+1));//设置表达�?
//						if(i%2 ==0){
//							line = line.replace("#", String.valueOf(SECONDS[new Random().nextInt(SECONDS.length)]));//设置表达�?
//							line = line.replace("0/$", String.valueOf("*"));//设置表达�?
//						}else if(i%3 == 0){
//							line = line.replace("$", String.valueOf(MIN[new Random().nextInt(MIN.length)]));//设置表达�?
//							line = line.replace("0/#", String.valueOf("0"));//设置表达�?
//						}else{
//							line = line.replace("#", String.valueOf(SECONDS[new Random().nextInt(SECONDS.length)]));//设置表达�?
//							line = line.replace("$", String.valueOf(MIN[new Random().nextInt(MIN.length)]));//设置表达�?
//						}
						line = line.replace("cronJob", "cronJob_"+((i-1)/20 + 1));//设置分页
						line = line.replace("fireNowJobTemp", prefix+"_fireNow_"+i);
					}
					writer.write(line);
					writer.newLine();
				}
				writer.flush();
				writer.close();
			}
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
