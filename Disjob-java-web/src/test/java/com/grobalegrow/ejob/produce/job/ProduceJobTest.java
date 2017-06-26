package com.grobalegrow.disJob.produce.job;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;


public class ProduceJobTest {

	public static void main(String[] args) {

//		produceConfig();
		produceFireNowJob();
		produceFireNowServletJob();
	}
  
	private static void produceConfig() {
		String groupStart = "<disJob:group id=\"springJob_%s\" name=\"springJob%s\">";
		String jobName =	 "<disJob:job name=\"%s\" classname=\"com.globalgrow.disJob.spring.%s\" method=\"execute\" />" ;
	 	String groupEnd =   "</disJob:group>";
	 	StringBuffer sb = new StringBuffer();
	 	for(int i=1;i<=100;i++){
	 		if(i%5 == 0){
	 			System.out.println(String.format(groupStart, "batchAdd_"+i,"batchAdd_"+i));
	 			System.out.println(sb.toString());
	 			System.out.println(groupEnd);
	 			sb.setLength(0);
	 		}
	 		sb.append(String.format(jobName,"SpringJob_"+(i-1), "SpringJob_"+(i-1))+"\r\n");
	 	}
	} 

	private static void produceFireNowJob() {
		String path = "E:/__work_space_middleware/EJob1.1Dev/DisJob-java-web/src/main/java/com/globalgrow/disJob/spring/firenow/FireNowJobTemp.java"; 
		String bean = "<bean class=\"com.globalgrow.disJob.spring.%s\" />";
		try {
			for(int i=0;i<100;i++){
				BufferedReader reader = new BufferedReader(new FileReader(path));
				BufferedWriter writer = new BufferedWriter(new FileWriter("E:/__work_space_middleware/EJob1.1Dev/DisJob-java-web/src/main/java/com/globalgrow/disJob/spring/firenow/SpringFireNowJob_"+i+".java"));
				String line = "";
				while((line=reader.readLine()) !=null){
					if(line.indexOf("FireNowJobTemp")>0){
						line = line.replace("FireNowJobTemp", "SpringFireNowJob_"+i);//处理类型
					}
					if(line.indexOf("JobDec") >0 ){
						line = line.replace("springCronJob", "springCronJob_"+(i/20 + 1));
						line = line.replace("fireNowJobTemp", "fireNowJob_"+i);
					}
					writer.write(line);
					writer.newLine();
				}
				writer.flush();
				writer.close();
				System.out.println(String.format(bean, "SpringJob_"+i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void produceFireNowServletJob() {
		String path = "E:/__work_space_middleware/EJob1.1Dev/DisJob-java-web/src/main/java/com/globalgrow/disJob/servlet/FireNowJobServletTemp.java"; 
		String bean = "<bean class=\"com.globalgrow.disJob.spring.%s\" />";
		try {
			for(int i=0;i<100;i++){
				BufferedReader reader = new BufferedReader(new FileReader(path));
				BufferedWriter writer = new BufferedWriter(new FileWriter("E:/__work_space_middleware/EJob1.1Dev/DisJob-java-web/src/main/java/com/globalgrow/disJob/servlet/ServletFireNowJob_"+i+".java"));
				String line = "";
				while((line=reader.readLine()) !=null){
					if(line.indexOf("FireNowJobServletTemp")>0){
						line = line.replace("FireNowJobServletTemp", "ServletFireNowJob_"+i);//处理类型
					}
					if(line.indexOf("JobDec") >0 ){
						line = line.replace("servletCronJob", "servletCronJob_"+(i/20 + 1));//处理组名
						line = line.replace("fireNowJobServletTemp", "fireNowJobServlet_"+i);//处理job name
					}
					writer.write(line);
					writer.newLine();
				}
				writer.flush();
				writer.close();
				System.out.println(String.format(bean, "SpringJob_"+i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
