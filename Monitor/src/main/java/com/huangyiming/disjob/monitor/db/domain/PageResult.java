package com.huangyiming.disjob.monitor.db.domain;

import java.util.List;

/**
 * <pre>
 * 
 *  File: PageResult.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  存储分页结果
 * 
 *  Revision History
 *
 *  Date：		2016年7月13日
 *  Author：		Disjob
 *
 * </pre>
 */
public class PageResult {
	private boolean successful = true;
	private long total;//总记录数
	private List<?> rows;//当前查询分页包含的行数据
	public long getTotal() {
		return total;
	}
	public PageResult setTotal(long total) {
		this.total = total;
		return this;
	}
	public List<?> getRows() {
		return rows;
	}
	public PageResult setRows(List<?> rows) {
		this.rows = rows;
		return this;
	}
	public boolean isSuccessful() {
		return successful;
	}
	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}
	@Override 
	public String toString() {
		return "PageResult [total=" + total + ", rows=" + rows + "]";
	}
}
