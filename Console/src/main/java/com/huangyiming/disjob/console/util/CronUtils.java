package com.huangyiming.disjob.console.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.huangyiming.disjob.common.exception.DisJobCronException;
import com.huangyiming.disjob.common.exception.DisJobFrameWorkException;
import com.huangyiming.disjob.register.domain.CronInfo;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;

public class CronUtils {
 
	public static final String ALL = "*";
	
	public static final String COMMA = ",";
	
	public static final String QUESTION_MARK = "?";
	
	public static final String SPACE = " ";
	
	public static final String defaultSpecial = CronInfo.defaultSpecial;
	
	private static final BiMap<String, String> specialCronMap = HashBiMap.create(5);
	static{
		specialCronMap.put("hourly", "0 0 * * * ?");
		specialCronMap.put("daily", "0 0 0 * * ?");
		specialCronMap.put("weekly", "0 0 0 ? * SUN");
		specialCronMap.put("monthly", "0 0 0 1 * ?");
		specialCronMap.put("yearly", "0 0 0 1 1 ?");
	}
	
	private static final String[] dateFactor = new String[] { "seconds", "mins", "hours", "days", "months",
			"weekdays" };
	
	public static final String transferToCronExpression(CronInfo cronInfo) {
		String minute,second,hour,day,month,weekday;
		if(cronInfo.isChooseSpecial()){
			return specialCronMap.get(cronInfo.getSpecial());
		}else{
			
			if(cronInfo.isAllMins()){
				minute = ALL;
			}else{
				minute = transferToString(cronInfo.getMins());
			}
			
			if(cronInfo.isAllSeconds()){
				second = ALL;
			}else{
				second = transferToString(cronInfo.getSeconds());
			}
			
			if(cronInfo.isAllHours()){
				hour = ALL;
			}else{
				hour = transferToString(cronInfo.getHours());
			}
			
			if(cronInfo.isAllDays()){
				day = ALL;
			}else{
				day = transferToString(cronInfo.getDays());
			}
			
			if(cronInfo.isAllMonths()){
				month = ALL;
			}else{
				month = transferToString(cronInfo.getMonths());
			}
			
			if(cronInfo.isAllWeekdays()){
				if(ALL.equals(day)){
					weekday = QUESTION_MARK;
				}else{
					if(day.equals(QUESTION_MARK)){
						weekday = ALL;					
					}else{
						weekday = QUESTION_MARK;
					}
				}
			}else{
				weekday = transferToString(cronInfo.getWeekdays());
				day = QUESTION_MARK;
			}
		}
		
		String cronExpression = buidCron(second,minute,hour,day,month,weekday);
		if(CronExpression.isValidExpression(cronExpression)){
			return cronExpression;
		}else{
			throw new DisJobFrameWorkException("CronUtils.transferToCronExpression transfer CronInfo to StringCronExpression , but [" + cronExpression + "] isUnValidExpression");
		}
	}

	private static String buidCron(String...str) {
		return StringUtils.join(str, SPACE);
	}

	private static String transferToString(String[] iArray){
		
		return StringUtils.join(iArray, COMMA);
	}
	
	public static final CronInfo transferFromCronExpression(String cronExpression) {
		if(StringUtils.isEmpty(cronExpression)){
			return CronInfo.defaultValue();
		}
		cronExpression = cronExpression.trim();
		CronInfo cronInfo = new CronInfo();
		if(specialCronMap.containsValue(cronExpression)){
			cronInfo.setChooseSpecial(true);
			cronInfo.setSpecial(specialCronMap.inverse().get(cronExpression));
			return cronInfo;
		}
		StringTokenizer exprsTok = new StringTokenizer(cronExpression, " \t", false);
		int i = 0;
		while (exprsTok.hasMoreTokens() && i < dateFactor.length){
			String expr = exprsTok.nextToken().trim(); // 0 * * * * ? ...
			
			if(!QUESTION_MARK.equals(expr) && !ALL.equals(expr)){
				
			}
			
			String factorName = dateFactor[i]; // seconds...
			
			Class<CronInfo> clazz = CronInfo.class;
			try {
				Method methodSetAll = clazz.getDeclaredMethod("setAll" + captureName(factorName), boolean.class);
				methodSetAll.setAccessible(true);
				if(ALL.equals(expr) || QUESTION_MARK.equals(expr)){
					methodSetAll.invoke(cronInfo, true);
				}else{
					Method method = clazz.getDeclaredMethod("set" + captureName(factorName), String[].class);
					method.setAccessible(true);
					method.invoke(cronInfo, new Object[]{transferArrayToString(expr)});		
					methodSetAll.invoke(cronInfo, false);
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new DisJobCronException(e);
			}
			i++;
		}
		return cronInfo;
	}
	
	private static String[] transferArrayToString(String str){
		List<String> list = Lists.newArrayList();
		for(String string : str.split(COMMA)){
			String number = string.trim();
			if(!NumberUtils.isNumber(number)){
				throw new DisJobCronException();
			}
			list.add(number);
		}
		String[] is = new String[0];
		return list.toArray(is);
	}
	
	public static String captureName(String name) {
		char[] cs = name.toCharArray();
		cs[0] -= 32;
		return String.valueOf(cs);

	}
}
