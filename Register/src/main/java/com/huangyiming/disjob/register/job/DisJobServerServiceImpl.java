package com.huangyiming.disjob.register.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.ZKPaths;
import org.springframework.stereotype.Service;

import com.huangyiming.disjob.common.Constants;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.register.center.pool.ThreadLocalClient;
import com.huangyiming.disjob.register.domain.DisJobServerInfo;
import com.huangyiming.disjob.register.domain.Job;
import com.huangyiming.disjob.register.repository.ZnodeApi;
import com.huangyiming.disjob.rpc.client.HURL;
import com.huangyiming.disjob.slaver.utils.SlaveUtils;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

/**
 * 服务器集群操作服务类
 * @author Disjob
 *
 */
@Service("initServerExecuteJobService")
public class DisJobServerServiceImpl implements DisJobServerService {
	@Resource
	private ZnodeApi znodeApi;
	
	@Resource
	private ThreadLocalClient threadLocalClient;

	public boolean distributeJob(CuratorFramework client) {
		//是否全部机器都启动才触发job分配？？？
		//是否每次启动都重新分配job？？？

		// 获得所有的job节点
		String jobPath = ZKPaths.makePath(Constants.ROOT, Constants.APP_JOB_NODE_ROOT);
		List<String> jobGroupLst = znodeApi.getChildren(client, jobPath);
		Map<String, List<String>> groupJobMap = new HashMap<String, List<String>>();
		for (String groupStr : jobGroupLst) {
			String groupPath = ZKPaths.makePath(Constants.ROOT, Constants.APP_JOB_NODE_ROOT,
					Constants.PATH_SEPARATOR + groupStr);
			List<String> jobNameLst = znodeApi.getChildren(client, groupPath);
			LoggerUtil.debug("distribute job, group: " + groupStr + "jobListSize: " + jobNameLst.size());
			groupJobMap.put(groupStr, jobNameLst);
		}

		// 获得所有ready状态的slave机器的ip节点
		String slavePath = ZKPaths.makePath(Constants.ROOT, Constants.DISJOB_SERVER_NODE_ROOT,
				Constants.DISJOB_SERVER_NODE_SLAVE);
		List<String> slaveIpLst = znodeApi.getChildren(client, slavePath);
		List<String> readyIpLst = new ArrayList<String>();
		for(String ip : slaveIpLst){
			String infoPath = ZKPaths.makePath(slavePath, Constants.PATH_SEPARATOR + ip);
			if(znodeApi.checkExists(client, infoPath)){
				readyIpLst.add(ip);
			}
		}
		
		//向所有的slave机器分配任务
		int jobSize = groupJobMap.entrySet().size();
		int slaveSize = readyIpLst.size();
		int perSlaveJobSize = jobSize / slaveSize;
		int i = 0;
		for (Map.Entry<String, List<String>> entry : groupJobMap.entrySet()) {
			i++;
			String ip = null;
			// 大于零，均分到所有slave机器上;小于零，分到一台机器上
			if (perSlaveJobSize > 0) {
				ip = readyIpLst.get(i / perSlaveJobSize);
			} else {
				ip = readyIpLst.get(0);
			}
			String slaveJobPath = ZKPaths.makePath(slavePath, Constants.PATH_SEPARATOR + ip,
					Constants.DISJOB_SERVER_NODE_SLAVE_EXECUTION, Constants.PATH_SEPARATOR + entry.getKey(),
					Constants.PATH_SEPARATOR + entry.getValue());
			znodeApi.createPersistent(client, slaveJobPath, null);
		}

		return true;
	}

	@Override
	public List<String> getAllJobNameByGroup(CuratorFramework client, String path) {
		return znodeApi.getChildren(client, path);
	}
	
	@Override
	public List<DisJobServerInfo> getServerInfos(){
		List<DisJobServerInfo> serverLst = new ArrayList<DisJobServerInfo>();
		CuratorFramework client = threadLocalClient.getCuratorClient().getCuratorClient();
		String slavePath = ZKPaths.makePath(Constants.ROOT, Constants.DISJOB_SERVER_NODE_ROOT, Constants.DISJOB_SERVER_NODE_SLAVE);
        String masterHostPath = Constants.DISJOB_SERVER_NODE_MASTER_IP;
        String masterIp ="";
		try {
			masterIp = SlaveUtils.getLeaderLatch().getLeader().getId();
		} catch (Exception e) {
			LoggerUtil.error("get master by leaderLatch ERROR",e);
		}
       // String masterIp = znodeApi.getData(client, masterHostPath);
		List<String> slaveIpLst = znodeApi.getChildren(client, slavePath);
		for (String slaveIp : slaveIpLst) {
			DisJobServerInfo serverInfo = new DisJobServerInfo(slaveIp);
			String ipPath = ZKPaths.makePath(slavePath, Constants.PATH_SEPARATOR + slaveIp);
			String hostNamePath = ZKPaths.makePath(ipPath, Constants.DISJOB_SERVER_NODE_SLAVE_HOSTNAME);
			String statusPath = znodeApi.makePath(ipPath, Constants.DISJOB_SERVER_NODE_SLAVE_STATUS);
			
			serverInfo.setHostName(znodeApi.getData(client, hostNamePath));
			
			if (slaveIp.equals(masterIp)) {
				serverInfo.setMaster(true);
			} else {
				serverInfo.setMaster(false);
			}
			
			if(znodeApi.checkExists(client, statusPath)){
				serverInfo.setActive(true);
			} else {
				serverInfo.setActive(false);
			}
			
			serverLst.add(serverInfo);
		}
		
		return serverLst;
	}
	
	@Override
	public Map<String, List<String>> getServerJob(DisJobServerInfo server){
		Map<String, List<String>> groupJobMap = new HashMap<String, List<String>>();
		if(server == null){
			return groupJobMap;
		}
		CuratorFramework client = threadLocalClient.getCuratorClient().getCuratorClient();
		String slavePath = ZKPaths.makePath(Constants.ROOT, Constants.DISJOB_SERVER_NODE_ROOT, Constants.DISJOB_SERVER_NODE_SLAVE);
	    String slaveServerPath = ZKPaths.makePath(slavePath, Constants.PATH_SEPARATOR + server.getIp());
	    String slavePathExecution = ZKPaths.makePath(slaveServerPath, Constants.DISJOB_SERVER_NODE_SLAVE_EXECUTION);
	    List<String> groupLst = znodeApi.getChildren(client, slavePathExecution);
	    for(String groupName : groupLst){
	    	String slavePathJob = ZKPaths.makePath(slavePathExecution, Constants.PATH_SEPARATOR + groupName);
	    	String jobNameStr = znodeApi.getData(client, slavePathJob);
	    	if(StringUtils.isNoneBlank(jobNameStr)){
 	    		String [] jobNameArray = jobNameStr.split(Constants.TRANSFER_CHAR + Constants.JOB_SEPARATOR);
	    		List<String> jobNameLst = new ArrayList<String>();
	    		for(String jobName : jobNameArray){
	    			jobNameLst.add(jobName);
	    		}
	    		groupJobMap.put(groupName, jobNameLst);
	    	}
	    }
		return groupJobMap;
	}

	@Override
	public HURL getHurlByJobAndIp(String group, String jobName, final String ip) {
		CuratorFramework client = threadLocalClient.getCuratorClient().getCuratorClient();
		String slavePath = ZKPaths.makePath(Constants.ROOT, Constants.DISJOB_RPC_NODE_ROOT,  Constants.PATH_SEPARATOR+group,Constants.PATH_SEPARATOR+jobName,Constants.PATH_SEPARATOR+Constants.DISJOB_PROVIDERS);

 		List<String> list = znodeApi.getChildren(client, slavePath);
 		//String hostIp = "";
 		HURL result = null;
 		for(String ipPort:list){
 			if(ipPort.indexOf(ip)>-1){
 				//hostIp = ip;
 				String hurl = znodeApi.getData(client, slavePath+Constants.PATH_SEPARATOR+ipPort);
 				result = HURL.valueOf(hurl);
 				break;
 			}
 		}
		return result;
	}

	@Override
	public Job getJobByGroupNameAndJobName(String group, String jobName) {
		if(StringUtils.isEmpty(group) && StringUtils.isEmpty(jobName)){
			throw new RuntimeException("group name or job name is empty");
		}
		CuratorFramework client = threadLocalClient.getCuratorClient().getCuratorClient();
   
		String jobPath = znodeApi.makePath(Constants.ROOT, Constants.APP_JOB_NODE_ROOT,Constants.PATH_SEPARATOR+group,Constants.PATH_SEPARATOR+jobName, Constants.APP_JOB_NODE_CONFIG);
        String data =  znodeApi.getData(client, jobPath);
        Job job = new Job();
        if(StringUtils.isNoneBlank(data)){
         	job =  new Gson().fromJson(data, Job.class);
	        return job;
         }else{
        	 return null;
         }
	}

	@Override
	public List<String> getSessionsList() {
		List<String> sessionList = Lists.newArrayList();
		CuratorFramework client = threadLocalClient.getCuratorClient().getCuratorClient();
		String sessionRootPath = Constants.DISJOB_SERVER_NODE_SESSION;
		List<String> sessionNames = znodeApi.getChildren(client, sessionRootPath);
		for(String session : sessionNames){
			String sessionPath = znodeApi.makePath(Constants.DISJOB_SERVER_NODE_SESSION, session);
			if(!znodeApi.getChildren(client, sessionPath).isEmpty()){
				sessionList.add(session);
			}
		}
		return sessionList;
	}
}
