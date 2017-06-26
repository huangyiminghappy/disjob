package com.huangyiming.disjob.register.job;

import java.util.List;
import java.util.Map;

import org.apache.curator.framework.CuratorFramework;

import com.huangyiming.disjob.common.exception.ZKNodeException;
import com.huangyiming.disjob.register.domain.DisJobServerInfo;
import com.huangyiming.disjob.register.domain.Job;
import com.huangyiming.disjob.rpc.client.HURL;

/**
 * disJob为后台提供接口服务类
 * @author Disjob
 *
 */
public interface DisJobServerService {
	/**
	 * 将所有job均分给slave机，暂时没有考虑机器动态添加
	 * 
	 * @param client
	 * @return
	 * @throws ZKNodeException
	 */
	boolean distributeJob(CuratorFramework client) throws ZKNodeException;
	
    List<String> getAllJobNameByGroup(CuratorFramework client, String paht) throws ZKNodeException;
    
    List<DisJobServerInfo> getServerInfos() throws ZKNodeException;
    
    Map<String, List<String>> getServerJob(DisJobServerInfo server);
    
    /**
     * 根据job和对应IP得到RPC对应的HURL对象
     * @param group
     * @param jobName
     * @param Ip
     * @return
     */
    HURL getHurlByJobAndIp(String group,String jobName,String Ip);
    
    
     /**
      * 根据组名和job名得到/disJob/job/groupname/jobname/config对应的job对象
      * @param group
      * @param jobName
      * @return
      */
    Job getJobByGroupNameAndJobName(String group,String jobName);
    
    List<String> getSessionsList();
}
