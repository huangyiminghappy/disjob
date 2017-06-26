package com.huangyiming.disjob.console.cron;

import com.huangyiming.disjob.console.cron.factors.TimeFactor;

public class YearFactor extends TimeFactor {

	public YearFactor setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}
}
 