package com.huangyiming.disjob.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

/**
 * <pre>
 * 
 *  File: DateUtil.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  日期工具类，实现UTC、local的转换/格式化
 * 
 *  Revision History
 *
 *  Date：		2016年8月23日
 *  Author：		Disjob
 *
 * </pre>
 */
public class DateUtil {
	public static String patten = "yyyy-MM-dd HH:mm:ss";

	public static String date2Str(Date date){
		SimpleDateFormat sdf=new SimpleDateFormat(patten);
		return sdf.format(date);
	}

	public static long getExcuteTime(String endTime, String startTime){
		if(StringUtils.isEmpty(endTime)|| StringUtils.isEmpty(startTime)){
			return 0;
		}
		if(endTime.equals(startTime)){
			return 0 ;
		}
		
		SimpleDateFormat sdf=new SimpleDateFormat(patten);
		long processTime = 0;
		if(!(StringUtils.isNotEmpty(endTime) && StringUtils.isNotEmpty(startTime))){
			return processTime;
		}
		try {
			java.util.Date end = sdf.parse(endTime);
			java.util.Date start = sdf.parse(startTime);
			processTime = (end.getTime() - start.getTime()) / 1000;
		} catch (ParseException e) {
			LoggerUtil.warn("rpc request occers exception", e);
		}
		if(processTime > 1400000000){
			processTime = 0 ;
		}
		return processTime;
	}

	public static long getInterval(Date endTime,Date startTime){
		
		return  (endTime.getTime() - startTime.getTime()) / 1000;
	}
	public static Date parse(String time){
		
		try {
			return new SimpleDateFormat(patten).parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}
	
	public static String getUtc() {
		SimpleDateFormat sdf=new SimpleDateFormat(patten);
		/*------------------------------UTC--------------------比北京时间慢8小时*/
		/*Calendar cal = Calendar.getInstance();
    	long value = cal.getTimeInMillis();       //your long value.
    	int zoneOffset = cal.get(Calendar.ZONE_OFFSET); 
    	int dstOffset = cal.get(Calendar.DST_OFFSET); 
    	cal.setTimeInMillis(value);
    	cal.add(Calendar.MILLISECOND, -(zoneOffset+dstOffset)); //it only takes int int
        return sdf.format(new Date(cal.getTimeInMillis()));*/
		return sdf.format(new Date());
	}
	public static String local2Utc(Date data) {
		if(null == data)
			return null;
		SimpleDateFormat sdf=new SimpleDateFormat(patten);
		/*------------------------------UTC--------------------比北京时间慢8小时*/
		/*Calendar cal = Calendar.getInstance();
    	long value = data.getTime();       //your long value.
    	int zoneOffset = cal.get(Calendar.ZONE_OFFSET); 
    	int dstOffset = cal.get(Calendar.DST_OFFSET); 
    	cal.setTimeInMillis(value);
    	cal.add(Calendar.MILLISECOND, -(zoneOffset+dstOffset)); //it only takes int int
        return sdf.format(new Date(cal.getTimeInMillis()));*/
		return sdf.format(data);
	}
	public static String utc2Local(String utcTime,String utcTimePatten) {
		if(null != utcTime && utcTime.length()>0){
			try{
				SimpleDateFormat utcFormater = new SimpleDateFormat(utcTimePatten);
				utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
				Date gpsUTCDate = null;
				try {
					gpsUTCDate = utcFormater.parse(utcTime);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				SimpleDateFormat localFormater = new SimpleDateFormat(patten);
				localFormater.setTimeZone(TimeZone.getDefault());
				String localTime = localFormater.format(gpsUTCDate.getTime());
				return localTime;
			}catch(Throwable e){
				return null;
			}
		}
		return null;
	}
	
	public final static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss" ;

	public static String getFormat(Date date,String format){
		SimpleDateFormat  sdf = new SimpleDateFormat(format);
		
		return sdf.format(date);
	}
	
	public static String getFormatNow(){
		
		return getFormat(new Date(), YYYY_MM_DD_HH_MM_SS);
	}
	
	public static void main(String[] args) {
		
		String time = DateUtil.utc2Local("2016-12-14 14:00:00",DateUtil.patten);
		System.out.println(time);
	} 
	
	public static Date now(){
		return new Date();
	}
}
