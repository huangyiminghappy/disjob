package com.huangyiming.disjob.monitor.db.domain;

import java.util.SortedSet;

public class PageResultAndCategories extends PageResult{

	private SortedSet<String> categories;

	public SortedSet<String> getCategories() {
		return categories;
	}

	public void setCategories(SortedSet<String> categories) {
		this.categories = categories;
	}


}
