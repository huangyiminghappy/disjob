package com.huangyiming.disjob.console.cron.factors;

import com.huangyiming.disjob.console.cron.BuildInterface;

public class MonthFactor extends TimeFactor {
 
	@Override
	public void setSpecifyFactor(final SpecifyFactor specifyFactor) {
		specifyFactor.setBuild(new BuildInterface() {
			@Override
			public String build() {
				StringBuffer buffer = new StringBuffer();
				for (int i : specifyFactor.getSpecify()) {
					if (buffer.length() != 0) {
						buffer.append(",");
					}
					buffer.append(++i);
				}
				return buffer.toString();
			}
		}); 
		super.setSpecifyFactor(specifyFactor);
	}
	
	public MonthFactor setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue; 
		return this; 
	}
}
