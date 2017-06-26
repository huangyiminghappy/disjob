package com.huangyiming.disjob.java.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public final class TimeUtils {

	public final static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss" ;

	private TimeUtils(){}
	
	public static String getFormat(Date date,String format){
		SimpleDateFormat  sdf = new SimpleDateFormat(format);
		
		return sdf.format(date);
	}
	
	public static String getFormatNow(){
		return getFormat(new Date(), YYYY_MM_DD_HH_MM_SS);
	}
	
	public static String local2Utc(Date data) {
		if(null == data)
			return null;
		SimpleDateFormat sdf=new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
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
				SimpleDateFormat localFormater = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
				localFormater.setTimeZone(TimeZone.getDefault());
				String localTime = localFormater.format(gpsUTCDate.getTime());
				return localTime;
			}catch(Throwable e){
				return null;
			}
		}
		return null;
	}
	
}
