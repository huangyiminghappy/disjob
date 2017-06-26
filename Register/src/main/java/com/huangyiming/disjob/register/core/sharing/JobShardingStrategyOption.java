package com.huangyiming.disjob.register.core.sharing;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 作业分片策略选项.
 * 
 * @author Disjob
 */
 
public final class JobShardingStrategyOption implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 7480874642956269418L;

	/**
     * 作业名称.
     */
    private final String jobName;
    
 /**
     * 作业分片总数.
     */
    /*private final int shardingTotalCount;*/
    
    /**
     * 分片序列号和个性化参数对照表.
     */
    private final Map<String, String> shardingItemParameters;
    
    
    
    private List<String> paramList;
    
    
    
    

	public List<String> getParamList() {
		return paramList;
	}

	public void setParamList(List<String> paramList) {
		this.paramList = paramList;
	}

	public JobShardingStrategyOption(String jobName,Map<String, String> shardingItemParameters,List<String> paramList){
    	this.jobName = jobName;
    	this.shardingItemParameters = shardingItemParameters;
    	this.paramList = paramList;
    }

	public String getJobName() {
		return jobName;
	}

	public Map<String, String> getShardingItemParameters() {
		return shardingItemParameters;
	}
}
