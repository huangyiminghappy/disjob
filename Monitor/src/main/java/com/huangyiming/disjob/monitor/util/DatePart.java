package com.huangyiming.disjob.monitor.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * <pre>
 * 
 *  File: DatePart2.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  获取日期段，格式化话后输出方便进行统计使用
 * 
 *  Revision History
 *
 *  Date：		2016年8月11日
 *  Author：		Disjob
 *
 * </pre>
 */
public class DatePart {
	private static String CONCAT = "0";//拼接
	private static String FORMAT_HOUR = "yyyy-MM-dd";//以小时作为格式化
	private static String FORMAT_HOUR_SEG = " ";//分段，连接符
	private static String FORMAT_DAY = "yyyy-MM";//以天作为格式化
	private static String FORMAT_DAY_SEG = "-";//分段，连接符
	
	//根据日期，限制大小，连接符，格式进行日期格式化
	public static List<String> formating(Date date,int limit,String seg,String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String basics = sdf.format(date);//格式化基础字符串
		
		List<String> fdates = new LinkedList<String>();
		for(int index = 0; index <= limit; index++){
			fdates.add(basics+seg+(index>9?index:CONCAT+index));//为了格式统一，解决10和0-9之间位数不一致问题
		}
		return fdates;
	}
	//今天，按照小时为单位
	public static List<String> today(){
		Calendar cal = Calendar.getInstance();
		return formating(cal.getTime(),cal.getActualMaximum(Calendar.HOUR_OF_DAY),FORMAT_HOUR_SEG,FORMAT_HOUR);//取小时数
	}
	//昨天，按照小时为单位
	public static List<String> yesterday(){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);//负数为前，正为后
		return formating(cal.getTime(),cal.getActualMaximum(Calendar.HOUR_OF_DAY),FORMAT_HOUR_SEG,FORMAT_HOUR);//取小时数
	}
	//这个月，按照天为单位
	public static List<String> thisMonth(){
		Calendar cal = Calendar.getInstance();
		return formating(cal.getTime(),cal.getActualMaximum(Calendar.DATE),FORMAT_DAY_SEG,FORMAT_DAY);//取天数
	}
	//上个月，按照天为单位
	public static List<String> lastMonth(){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);//负数为前，正为后
		return formating(cal.getTime(),cal.getActualMaximum(Calendar.DATE),FORMAT_DAY_SEG,FORMAT_DAY);//取天数
	}
	//今年
	public static List<String> thisYear(){
		List<String> fdates = new LinkedList<String>();
		for(int index = 1;index < 13;index++){
			fdates.add(Calendar.getInstance().get(Calendar.YEAR)+FORMAT_DAY_SEG+(index>9?index:CONCAT+index));
		}
		return fdates;
	}
    
    //近7天，包括今天。按照天为单位
    public static List<String> last7days() {
    	List<String> fdates = new LinkedList<String>();
    	
        SimpleDateFormat fomater = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        for (int i = 6; i >= 0; i--) {
            calendar.set(Calendar.DAY_OF_YEAR, dayOfYear - i);
            fdates.add(fomater.format(calendar.getTime()));
        }
        return fdates;
    }
}
