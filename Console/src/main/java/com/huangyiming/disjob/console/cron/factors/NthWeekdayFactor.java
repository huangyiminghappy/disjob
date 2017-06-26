package com.huangyiming.disjob.console.cron.factors;

import com.huangyiming.disjob.console.cron.BuildInterface;

public class NthWeekdayFactor implements BuildInterface{

	int nthweek;

	int weekday;

	public NthWeekdayFactor() {
	}  

	public NthWeekdayFactor(int nthweek, int weekday) {
		this.nthweek = nthweek;
		this.weekday = weekday;
	}

	public int getNthweek() {
		return nthweek;
	}

	public void setNthweek(int nthweek) {
		this.nthweek = nthweek;
	}

	public int getWeekday() {
		return weekday;
	}

	public void setWeekday(int weekday) {
		this.weekday = weekday;
	}

	@Override
	public String build() {
		return weekday + "#" + nthweek;
	}

}
