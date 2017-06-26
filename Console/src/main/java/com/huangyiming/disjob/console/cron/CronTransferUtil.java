package com.huangyiming.disjob.console.cron;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.huangyiming.disjob.console.cron.factors.DayOfMonthFactor;
import com.huangyiming.disjob.console.cron.factors.DayOfWeekFactor;
import com.huangyiming.disjob.console.cron.factors.MonthFactor;
import com.huangyiming.disjob.console.cron.factors.TimeFactor;
import com.huangyiming.disjob.console.util.CronExpression;

public class CronTransferUtil {
 
	static final String star = "*";
	static final String question = "?";
	
	public static CronResult fromCrontabToQuartz(String crontab){
		CronResult cronResult = new CronResult();
		cronResult.setCrontab(crontab);
		
		switch (crontab) {
			case "yearly":
			case "annually":
				crontab = "0 0 1 1 *";
				break;
			case "monthly":
				crontab = "0 0 1 * *";
				break;
			case "weekly":
				crontab = "0 0 * * 0";
				break;
			case "daily":
			case "midnight":
				crontab = "0 * * * *";
				break;
			default:
				break;
		}
		if(crontab.equals("yearly") || crontab.equals("annually")){
			crontab = "0 0 1 1 *";
		}
		if(crontab.equals("monthly")){
			crontab = "0 0 1 * *";
		}
		if(crontab.equals("weekly")){
			crontab = "0 0 * * 0";
		}
		if(crontab.equals("daily") || crontab.equals("midnight")){
			crontab = "0 0 * * *";
		}
		if(crontab.equals("hourly")){
			crontab = "0 * * * *";
		}
		
		String quartzCronExpression = "0 " + crontab.trim();			
		StringTokenizer exprsTok = new StringTokenizer(quartzCronExpression, " \t", false);
		String[] crontabArray = new String[exprsTok.countTokens()];
		int k = 0;
		while(exprsTok.hasMoreTokens()){
			crontabArray[k++] = exprsTok.nextToken();
		}
		
		String second = crontabArray[0];
		String minute = crontabArray[1];
		String hour = crontabArray[2];
		String day = crontabArray[3];
		String month = crontabArray[4];
		String week = crontabArray[5];
		
		if(day.equals(star) && week.equals(star)){
			week = question;
		}
		if(!day.equals(question) && !week.equals(question)){
			if(week.equals(star)){
				week = question;
			}else if(day.equals(star)){
				day = question;
			}else{
				cronResult.setTransferSuccess(false);
				cronResult.setMessage("同时指定了 日 和 周, 无法解析!   " + crontab);
				return cronResult;
			}
		}
		
		//
		char[] weekchar = week.toCharArray();
		for(int i = 0; i < week.length(); i ++){
			char c = week.charAt(i);
			if(c >= 48 && c <= 57){
				c ++;
			}
			weekchar[i] = c;
		}
		week = new String(weekchar);
		
		String result0 = second + " " + minute + " " + hour + " " + day + " " + month + " " + week;
		cronResult.setQuartzCronExpression(result0);
		boolean result = CronExpression.isValidExpression(result0);
		cronResult.setTransferSuccess(result);
		if(!result){
			cronResult.setMessage("转换后的quartz表达式校验不通过");
		}
		
		return cronResult;
	}
	
	public static String generateCron(TimeFactor secondFactor, TimeFactor minuteFactor, TimeFactor hourFactor,
			DayOfMonthFactor dayOfMonthFactor, MonthFactor monthFactor, DayOfWeekFactor weekOfMonthFactor,
			YearFactor yearFactor) {
		return new DisJobCronExpression(secondFactor, minuteFactor, hourFactor,
				dayOfMonthFactor, monthFactor, weekOfMonthFactor, yearFactor).buid();
	}

	public static List<CronResult> fromCrontabToQuartz(String[] crontabs) {
		List<CronResult> result = new ArrayList<>(crontabs.length);
		for(String contab : crontabs){
			result.add(fromCrontabToQuartz(contab));
		}
		return result;
	}
}
