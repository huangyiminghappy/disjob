package com.huangyiming.test;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
 
public class TestCacheBuilder {
   
	public static void main(String[] args){
		Cache<String, Map<String, Boolean>> cache = 
				CacheBuilder.newBuilder()
				.expireAfterWrite(10, TimeUnit.SECONDS)
				.maximumSize(1000)
				.build();
		ConcurrentMap<String, Map<String, Boolean>> authCacheMap = cache.asMap();
		
		authCacheMap.get("a").put("gg", Boolean.FALSE);
	}
}
     