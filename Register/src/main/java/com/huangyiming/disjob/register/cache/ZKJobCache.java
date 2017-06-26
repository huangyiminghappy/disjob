package com.huangyiming.disjob.register.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.huangyiming.disjob.register.domain.DisJobServerInfo;
import com.huangyiming.disjob.register.domain.Job;
import com.huangyiming.disjob.register.rpc.ConcurrentHashSet;

/**
 * <pre>
 * 
 *  File: ZKJobCache.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  存放维护ZK上server分组和servername信息列表
 * 
 *  Revision History
 *  Date,					Who,					What;
 *  2016年5月27日				Disjob				Initial.
 *
 * </pre>
 */
public class ZKJobCache {
	/**
	 * 存放/disJob/rpc/下的组名列表
	 */
	//public static List<String> groupList = new ArrayList<String>();

	/**
	 * 存放/etc/rpc/下组对应的server信息列表,key是group名,value是server值
	 */
	//public static ConcurrentHashMap<String, List<String>> serverMap = new ConcurrentHashMap<String, List<String>>();

	/**
	 * 在/etc/rpc下监听同步到rpc下时候,维护group和job列表缓存
	 */
	//public static ConcurrentHashMap<String, Set<Job>> groupJobMap = new ConcurrentHashMap<String, Set<Job>>();

	
	public static ConcurrentHashSet<DisJobServerInfo> ipList = new ConcurrentHashSet<DisJobServerInfo>();

	/**
	 *  /disJob/session/会话名/ip:port中   会话名与ip:port映射关系  map<session,List<ip:port>>
	 */
	public static ConcurrentHashMap<String, List<String>> sessionHostMap = new ConcurrentHashMap<String, List<String>>();

	
	/**
	 * 会话与项目的绑定关系, map<project,List<session>>
	 *//*
	public static 	ConcurrentHashMap<String, List<String>> projectSessionMap = new ConcurrentHashMap<String, List<String>>();*/

 }
