package com.huangyiming.disjob.java;

public final class DisJobConstants {
	public static final boolean isDebug = false ;
	
	public final class Config{
		public static final String DISJOB_CONFIG_PATH = "disJobConfig" ;//这个给不是spring 应用程序用的
		
		public static final String LOG4J_CONFIG = "log4jConfig" ;
		
		public static final String ZK_HOST = "zk.host" ;
		
		public static final String SERVER_PORT = "server.port";
		
		public static final String JOB_PACKAGES = "job.packages";
		
		public static final String DYNAMIC_DIR = "dynamic.dir" ;
		
		public static final String DISJOB_THREADPOOL_CFG = "disJobCfg" ;
		
		public static final String DISJOB_CLUSTER_NAME = "cluster.name";
		
		public static final String ZK_ROOT_NODE = "zk.root.node";
		
		public static final String THREAD_COREPOOL_SIZE = "core.pool.size" ;
		public static final String THREAD_MAXPOOL_SIZE = "max.pool.size";
		public static final String THREAD_KEEPALIVE_TIME = "keep.alive.time";
		
	}
	
	public final class StartUpType{
		public static final String START_UP_TYPE = "startup.type" ;
		
		public static final short SPRING_START_UP = 1 ;//spring 项目
		
		public static final short WEB_SERVLET_START_UP = 2 ;//web servlet 项目
		
		public static final short JAVA_APPLICATION = 3 ;//普通 的 java 进程 应用程序
	}
	
	public final class ZKNode{
		
		public static final String SLAVE_STATUS_READY = "ready" ;
		
		public static final String SCHEDULER_SLAVE = "/scheduler/slave";
		
		public static final String SCHEDULER_SLAVE_IP = "/scheduler/slave/%s";
		
		public static final String SCHEDULER_SLAVE_IP_STATUS = "/scheduler/slave/%s/status";
	}
	
	public final class Policy{
		public static final int ALARM_DELAY = 3 ;//报警延时策略。 默认为3 秒钟
	}
}
