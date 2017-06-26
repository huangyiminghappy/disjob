package com.huangyiming.disjob.console.cron;

import com.huangyiming.disjob.console.cron.factors.DayOfMonthFactor;
import com.huangyiming.disjob.console.cron.factors.DayOfWeekFactor;
import com.huangyiming.disjob.console.cron.factors.MonthFactor;
import com.huangyiming.disjob.console.cron.factors.TimeFactor;
import com.huangyiming.disjob.console.util.CronExpression;

public class DisJobCronExpression {
 
	private TimeFactor secondFactor;

	private TimeFactor minuteFactor;

	private TimeFactor hourFactor;

	private DayOfMonthFactor dayOfMonthFactor;

	private MonthFactor monthFactor;

	private DayOfWeekFactor weekOfMonthFactor;
	
	private YearFactor yearFactor;

	private String secondCron;
	
	private String minuteCron;
	
	private String hourCron;
	
	private String dayOfMonthCron;
	
	private String monthCron;
	
	private String weekOfMonthCron;
	
	private String yearCron;
	
	private static final String BLANK = " ";
	private static final String ASTERISK = "*";
	private static final String Q_MARK = "?";

	public DisJobCronExpression(TimeFactor sf, TimeFactor mf, TimeFactor hf, DayOfMonthFactor dom, MonthFactor mf1,
			DayOfWeekFactor weekOfMonthFactor) {
		this.secondFactor = sf;
		this.minuteFactor = mf;
		this.hourFactor = hf;
		this.dayOfMonthFactor = dom;
		this.monthFactor = mf1;
		this.weekOfMonthFactor = weekOfMonthFactor;
	}
	public DisJobCronExpression(TimeFactor sf, TimeFactor mf, TimeFactor hf, DayOfMonthFactor dom, MonthFactor mf1,
			DayOfWeekFactor weekOfMonthFactor, YearFactor yearFactor) {
		this.secondFactor = sf;
		this.minuteFactor = mf;
		this.hourFactor = hf;
		this.dayOfMonthFactor = dom;
		this.monthFactor = mf1;
		this.weekOfMonthFactor = weekOfMonthFactor;
		this.yearFactor = yearFactor;
	}
	
	public String buid(){
		
		buildSecondFactor();
		buildMinuteFactor();
		buildHourFactor();
		buildDayOfMonthFactor();
		buildMonthFactor();
		buildWeekOfMonthFactor();
		buildYearFactor();
		
		return buidQuartzCronExpression();
		
	}

	private void buildYearFactor() {
		if(yearFactor == null){
			yearCron = null;
		}else{
			yearCron = yearFactor.build();
		}
	}

	private void buildWeekOfMonthFactor() {
		if(weekOfMonthFactor == null){
			weekOfMonthCron = "*";
		}else
			weekOfMonthCron = weekOfMonthFactor.build();
	}

	private void buildMonthFactor() {
		if(monthFactor == null){
			monthCron = "*";
		}else
			monthCron = monthFactor.build();
	}

	private void buildDayOfMonthFactor() {
		if(dayOfMonthFactor == null){
			dayOfMonthCron = "*";
		}else
			dayOfMonthCron = dayOfMonthFactor.build();
	}

	private void buildHourFactor() {
		if(hourFactor == null){
			hourCron = "*";
		}else
			hourCron = hourFactor.build();
	}

	private void buildMinuteFactor() {
		if(minuteFactor == null){
			minuteCron = "*";
		}else
			minuteCron = minuteFactor.build();
	}
	
	private void buildSecondFactor() {
		if(secondFactor == null){
			secondCron = "*";
		}else
			secondCron = secondFactor.build();
	}

	private String buidQuartzCronExpression() {
		if(dayOfMonthCron.equals(ASTERISK) && weekOfMonthCron.equals(ASTERISK)){
			weekOfMonthCron = Q_MARK;
		}
		if(!dayOfMonthCron.equals(Q_MARK) && !weekOfMonthCron.equals(Q_MARK)){
			if(dayOfMonthCron.equals(ASTERISK)){
				dayOfMonthCron = Q_MARK;
			}
			if(weekOfMonthCron.equals(ASTERISK)){
				weekOfMonthCron = Q_MARK; 
			}
		}
		StringBuffer stringbuffer = new StringBuffer()
			.append(secondCron).append(BLANK)
			.append(minuteCron).append(BLANK)
			.append(hourCron).append(BLANK)
			.append(dayOfMonthCron).append(BLANK)
			.append(monthCron).append(BLANK)
			.append(weekOfMonthCron);
		String cronExpression;
		if(yearCron != null){
			cronExpression = stringbuffer.append(BLANK).append(yearCron).toString();
		}else{
			cronExpression = stringbuffer.toString();
		}
		if(CronExpression.isValidExpression(cronExpression)){
			return cronExpression;
		}else{
			return "generated cronExpression isnot valid : " + cronExpression;
		}
	}

}
