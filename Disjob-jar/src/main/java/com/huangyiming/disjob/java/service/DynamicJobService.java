package com.huangyiming.disjob.java.service;

import java.util.concurrent.ConcurrentHashMap;

import com.huangyiming.disjob.java.job.DisJob;

public final class DynamicJobService {

	public static ConcurrentHashMap<String, Class<? extends DisJob>> DYNAMIC_JOBS = new ConcurrentHashMap<String, Class<? extends DisJob>>();
	
	public static ConcurrentHashMap<String, DisJob> DISJOB_OBJECT_MAP = new ConcurrentHashMap<String, DisJob>();
	
	private DynamicJobService() {
	}
}
