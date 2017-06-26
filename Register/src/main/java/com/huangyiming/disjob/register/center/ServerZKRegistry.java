package com.huangyiming.disjob.register.center;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.transaction.CuratorTransactionFinal;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.utils.ZKPaths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.huangyiming.disjob.common.Constants;
import com.huangyiming.disjob.common.model.JobInfo;
import com.huangyiming.disjob.common.util.LocalHost;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.monitor.rms.SelfTestRMSMonitor;
import com.huangyiming.disjob.register.auth.AuthZKRegistry;
import com.huangyiming.disjob.register.auth.node.GlobalAuthNode;
import com.huangyiming.disjob.register.core.jobs.StatelessJobFactory;
import com.huangyiming.disjob.register.core.service.GeneralSchedulerService;
import com.huangyiming.disjob.register.repository.ZnodeApi;
import com.huangyiming.disjob.register.repository.election.LeaderElectionApi;
import com.huangyiming.disjob.register.repository.election.LeaderElectionApiImpl;
import com.huangyiming.disjob.register.repository.watch.WatchApi;
import com.huangyiming.disjob.register.repository.watch.listener.ConnectionStateListenerImpl;
import com.huangyiming.disjob.register.repository.watch.listener.SchedulerJobInitListener;
import com.huangyiming.disjob.register.repository.watch.listener.SessionNodeListener;
import com.huangyiming.disjob.register.rpc.SubscribeService;
import com.huangyiming.disjob.register.rpc.ZookeeperRegistry;
import com.huangyiming.disjob.register.utils.RegisterUtils;
import com.huangyiming.disjob.slaver.utils.SlaveUtils;

/**
 * DisJob Server注册类： 1、初始化disJob、rpc、scheduler三大节点 2、job初始化 3、job分配初始化
 * 4、job监听及动态分配到slave机器 5、slave宕机，进行slave所属机的任务分配 6、disJob动态扩容
 * 
 * @author Disjob
 * @date 创建时间：2016-5-19
 */
@Service("serverZKRegistry")
public class ServerZKRegistry extends AbstractZKRegistryCenter {
 
	private CuratorFramework client;

	private LeaderLatch leaderLatch;

	private final static LocalHost localHost = new LocalHost();

	@Resource
	private ZnodeApi znodeApi;

	@Resource
	private WatchApi watchApi;

	@Resource
	private ZookeeperRegistry zookeeperRegistry;
	
	@Resource
	private   SubscribeService subscribeService;

	@Resource
	private GeneralSchedulerService generalSchedulerService;

	@Value("${zk.host}")
	private String ZKHost;

	public ServerZKRegistry() {
	}

	public CuratorFramework getClient() {
		return client;
	}

	@PostConstruct
	public void init() {
		LoggerUtil.debug("DisJob server client init, ZK server list is:"+ ZKHost);
		Builder builder = CuratorFrameworkFactory.builder().connectString(ZKHost)
				.retryPolicy(new ExponentialBackoffRetry(1500, 3));
		builder.sessionTimeoutMs(6000);
		builder.connectionTimeoutMs(6000);
		builder.authorization(new GlobalAuthNode(ZKHost).getAuthInfos());
		client = builder.build();
		client.getConnectionStateListenable().addListener(new ConnectionStateListenerImpl());
		client.start();
		try {
			client.blockUntilConnected(1, TimeUnit.SECONDS);
			// 初始化rpc、job、scheduler等节点
			initRootNode();

			selectLeader();
			com.huangyiming.disjob.slaver.utils.SlaveUtils.leaderLatch = leaderLatch;

			// 实时获得disJob server信息
			//getDisJobServerInfos();

			// 初始化本机slave节点job到scheduler
			initSchedulerJob();

			//构造session会话与项目绑定相关的缓存
			buildSessionBindCache();
			// 监听slave节点
			watchApi.pathChildrenWatch(client, slavePathExecution, false,
					new SchedulerJobInitListener(client, generalSchedulerService, znodeApi));
			watchApi.pathChildrenWatch(client, Constants.DISJOB_SERVER_NODE_SESSION, false, new SessionNodeListener());
		} catch (final Exception ex) {
			ex.printStackTrace();
			RegistryExceptionHandler.handleException(ex);
		}
	}

	@Override
	protected boolean initRootNode() {
		znodeApi.makeDirs(client, rpcRootNode);
		znodeApi.makeDirs(client, jobRootNode);
		znodeApi.makeDirs(client, masterPath);
		znodeApi.makeDirs(client, slavePath);
		znodeApi.makeDirs(client, slaveServerPath);
		znodeApi.makeDirs(client, slavePathExecution);
		if(!znodeApi.checkExists(client, slavePathHostName)){
			znodeApi.createPersistent(client, slavePathHostName, localHost.getHostName());
		}
 		if(!znodeApi.checkExists(client, slavePathStatus)){
 			znodeApi.createEphemeral(client, slavePathStatus, Constants.READY);
		}
 		if(!znodeApi.checkExists(client, Constants.DISJOB_SERVER_NODE_SESSION)){
 			znodeApi.createPersistent(client, Constants.DISJOB_SERVER_NODE_SESSION,null);
		}
 		if(!znodeApi.checkExists(client, Constants.DISJOB_SERVER_NODE_PROJECT)){
 			znodeApi.createPersistent(client, Constants.DISJOB_SERVER_NODE_PROJECT,null);
		}
		LoggerUtil.info("node rpc and job and scheduler node init success!");
		new AuthZKRegistry(client).init();
		return true;
	}

	private void selectLeader() {
		String masterPath = znodeApi.makePath(Constants.ROOT, Constants.DISJOB_SERVER_NODE_ROOT,
				Constants.DISJOB_SERVER_NODE_MASTER);
		LeaderElectionApi leaderElectionApi = new LeaderElectionApiImpl();
		leaderLatch = leaderElectionApi.useLeaderLatch(client, masterPath, localHost.getIp());
		try {
			leaderLatch.addListener(new LeaderLatchListener() {
				// goes from hasLeadership = false to hasLeadership = true
				@Override
				public void isLeader() {
 				//	setMasterIp();
 
					try {
						SelfTestRMSMonitor.start();						
					} catch (Exception e) {
						LoggerUtil.warn("[RMS Monitor] self test start get an exception : " + e);
					}
					// 成为leader前处理的job分配处理
					try {
						LoggerUtil.info("begin to be leader,and to distribute job in  shutdown slave");
						preLeaderPerform();
					} catch (Exception e) {
						LoggerUtil.error("preLeaderPerform error", e);
					}
					// 成为leader的时候执行
					LoggerUtil.info("DisJob:HostName:" + localHost.getHostName() + " IP:" + localHost.getIp() + "成功成为leader");
					
				}

				private void preLeaderPerform() throws Exception {
					List<String> ips = SlaveUtils.getAllOfflineIpsToDistribute(client);
					CuratorTransaction transaction = znodeApi.startTransaction(client);
					if (CollectionUtils.isNotEmpty(ips)) {
						for (String ip : ips) {
 							SlaveUtils.distributeSlave(ip, client, transaction);
							// 清空该节点上job
							SlaveUtils.clearSlaveJobByTransaction(ip, client, transaction);
						}
					}
					CuratorTransactionFinal finalTransaction = (CuratorTransactionFinal) transaction;
					finalTransaction.commit();
				}

				// goes from hasLeadership = true to hasLeadership = false
				@Override
				public void notLeader() {
					try {
						SelfTestRMSMonitor.shutdown();						
					} catch (Exception e) {
						LoggerUtil.warn("[RMS Monitor] self test shutdown get an exception : " + e);
					}
					LoggerUtil.info("begin to be slaver,and to shutdown rms self test");
				}
			});
			
			LoggerUtil.info("HostName:" + localHost.getHostName() + " IP:" + localHost.getIp() + " leader electing！");
			leaderLatch.start();
			leaderLatch.await(1, TimeUnit.SECONDS);
			if (leaderLatch.hasLeadership()) {
				// 启动后进行选举成为leader的时候执行
				LoggerUtil.info("HostName:" + localHost.getHostName() + " IP:" + localHost.getIp() + " has bean the leader!");
				RegisterUtils.watchRpc2Job(client, zookeeperRegistry);
				RegisterUtils.watchSlaveNodeStatus(client);
			} else {
				RegisterUtils.watchRpc2Job(client, zookeeperRegistry);
				RegisterUtils.watchSlaveNodeStatus(client);
				// 启动后进行选举成为slave的时候执行
				LoggerUtil.info("HostName:" + localHost.getHostName() + " IP:" + localHost.getIp() + " has bean the slave!");
			}

		} catch (Exception e) {
			LoggerUtil.info("HostName:" + localHost.getHostName() + " IP:" + localHost.getIp() + " leader elected failure！", e);
		}
	}

	/**
	 * 设置/disJob/scheduler/master/host->masterIp
	 */
	private void setMasterIp() {

		String masterIp = znodeApi.getData(client, Constants.DISJOB_SERVER_NODE_MASTER_IP);
 		if(StringUtils.isNotEmpty(masterIp)){
			znodeApi.update(client, Constants.DISJOB_SERVER_NODE_MASTER_IP, localHost.getIp());      
		}
		 else{
			 //znodeApi.createPersistent(client, Constants.DISJOB_SERVER_NODE_MASTER_IP, localHost.getIp()); 
			 znodeApi.createPersistent(client, Constants.DISJOB_SERVER_NODE_MASTER_IP, localHost.getIp());
        }
		LoggerUtil.debug("after update masterip :"+localHost.getIp() );

	}
	private void initSchedulerJob() {
		List<String> groupLst = znodeApi.getChildren(client, slavePathExecution);
		for (String groupName : groupLst) {
			String groupPath = ZKPaths.makePath(slavePathExecution, Constants.PATH_SEPARATOR + groupName);
			String jobNameStr = znodeApi.getData(client, groupPath);
			if (StringUtils.isNoneBlank(jobNameStr) && jobNameStr.length() > 0) {
				String[] jobNameArray = jobNameStr.split(Constants.TRANSFER_CHAR + Constants.JOB_SEPARATOR);
				for (String jobName : jobNameArray) {
					String jobRootPath = ZKPaths.makePath(Constants.ROOT, Constants.APP_JOB_NODE_ROOT);
					String jobPath = ZKPaths.makePath(jobRootPath, Constants.PATH_SEPARATOR + groupName,Constants.PATH_SEPARATOR + jobName, Constants.APP_JOB_NODE_CONFIG);
					JobInfo job = new Gson().fromJson(znodeApi.getData(client, jobPath), JobInfo.class);
					if (job != null) {
 						job.setJobClass(StatelessJobFactory.class); 
						generalSchedulerService.create(job);
						LoggerUtil.debug("Init job to shceduler, jobGroup:" + groupName + " jobName:" + jobName);
					} else {
						LoggerUtil.warn("Can not find job, groupName:" + groupName + " jobName:" + jobName + " on /disJob/job node");
					}
				}
			}
		}
		LoggerUtil.info("HostName:" + localHost.getHostName() + " IP:" + localHost.getIp() + " init job completed!");
	}

	 
	public void  buildSessionBindCache(){
		com.huangyiming.disjob.slaver.utils.SlaveUtils.buildSessionHostMap(client);
		com.huangyiming.disjob.slaver.utils.SlaveUtils.buildProjectSessionMap(client);
	}

	@PreDestroy
	public void distory() {
		/*for(PathChildrenCache cache : WatchApiCuratorImpl.childrenCacheLst){
			CloseableUtils.closeQuietly(cache);
		}*/
		CloseableUtils.closeQuietly(leaderLatch);
		CloseableUtils.closeQuietly(client);
	}

	public static String jobRootNode = ZKPaths.makePath(Constants.ROOT, Constants.APP_JOB_NODE_ROOT);
	public static String rpcRootNode = ZKPaths.makePath(Constants.ROOT, Constants.DISJOB_RPC_NODE_ROOT);
	public static String masterPath = ZKPaths.makePath(Constants.ROOT, Constants.DISJOB_SERVER_NODE_ROOT, Constants.DISJOB_SERVER_NODE_MASTER);
	public static String slavePath = ZKPaths.makePath(Constants.ROOT, Constants.DISJOB_SERVER_NODE_ROOT, Constants.DISJOB_SERVER_NODE_SLAVE);
	public static String slaveServerPath = ZKPaths.makePath(slavePath, Constants.PATH_SEPARATOR + localHost.getIp());
	public static String slavePathStatus = ZKPaths.makePath(slaveServerPath, Constants.DISJOB_SERVER_NODE_SLAVE_STATUS);
	public static String slavePathHostName = ZKPaths.makePath(slaveServerPath, Constants.DISJOB_SERVER_NODE_SLAVE_HOSTNAME);
	public static String slavePathExecution = ZKPaths.makePath(slaveServerPath, Constants.DISJOB_SERVER_NODE_SLAVE_EXECUTION);
}
