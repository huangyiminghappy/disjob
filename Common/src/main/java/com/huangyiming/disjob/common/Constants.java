package com.huangyiming.disjob.common;

public interface Constants {
	public static final String APPLICATION = "DISJOB";
	
	public static final String READY = "ready";
	public static final String RUNNING = "running";
	public static final String PATH_SEPARATOR = "/";
	public static final String JOB_SEPARATOR = "|";
	public static final String TRANSFER_CHAR = "\\";
	public static final String ROOT = "/disjob";//测试环境  "/disjob-dev";
	public static final String SESSION_CONNECT_ROOT = ROOT; // php会话连接的根节点
	public static final String APP_JOB_NODE_ROOT = "/job";
	public static final String APP_JOB_NODE_CONFIG = "/config";
	public static final String APP_JOB_NODE_CRON = "/cron";
	public static final String APP_JOB_NODE_FAILOVER = "/failover";
	public static final String APP_JOB_NODE_JOBPATH = "/jobpath";
	public static final String APP_JOB_NODE_PARAMETERS = "/parameters";
	public static final String APP_JOB_NODE_SHARDINGCOUNT = "/shardingCount";
	public static final String APP_JOB_NODE_SHARDINGITEMPARAMETERS = "/shardingItemParameters";
	public static final String APP_JOB_NODE_MISFIRE = "/misfire";
	public static final String APP_JOB_NODE_DESCRIPTION = "/description";
	public static final String DISJOB_RPC_NODE_ROOT = "/rpc";
	public static final String DISJOB_SERVER_NODE_ROOT = "/scheduler";
	public static final String DISJOB_SERVER_NODE_MASTER = "/master";
	public static final String DISJOB_SERVER_NODE_SLAVE = "/slave";
//	public static final String DISJOB_SERVER_NODE_SLAVE_INFO ="/info";
	public static final String DISJOB_SERVER_NODE_SLAVE_STATUS ="/status";
	public static final String DISJOB_SERVER_NODE_SLAVE_HOSTNAME ="/hostName";
	public static final String DISJOB_SERVER_NODE_SLAVE_EXECUTION ="/execution";
	//MASTER IP
	public static final String DISJOB_SERVER_NODE_MASTER_IP = ROOT+"/scheduler/master/host";
	
	//会话节点
	public static final String DISJOB_SERVER_NODE_SESSION = SESSION_CONNECT_ROOT + "/session"; 

	//任务绑定节点
	
	public static final String DISJOB_SERVER_NODE_PROJECT = SESSION_CONNECT_ROOT + "/publish"; 
	public static final String DISJOB_PROVIDERS = "providers";
    public static final String DISJOB_CONSUMERS = "consumers";
    public static final String DISJOB_CONFIG = "/config";
    public static final String DISJOB_CONFIG_JOBSTATUS = "/jobStatus";
    
    
    //slave挂掉后master等待3秒后查看是否重连上
    public static final long WAIT_RECONNECT_TIME = 3000l;
    
    //disJob执行默认超时时间,十分钟,单位是s
    public static final long DISJOB_EXECUTE_TIMEOUT = 600;
     
    
    /*机器IP默认权重*/
    public static final int DISJOB_SLAVE_DEFAULT_WEIGHT = 1;
     
    public static final String DISJOB_SLAVE_STATUS = "ready";
    
	public static final boolean isDebug = false ;

	public static final boolean isCanConnPool = true ;

	public static final int RECON_COUNT_FAIL = 3;
	//超时时间倍数 报警提示
	public static final int TIMEOUT_TIMES = 2 ;
}
