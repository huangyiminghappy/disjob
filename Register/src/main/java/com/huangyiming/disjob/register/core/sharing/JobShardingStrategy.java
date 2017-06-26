
package com.huangyiming.disjob.register.core.sharing;

import java.util.List;
import java.util.Map;

import com.huangyiming.disjob.rpc.client.HURL;

/**
 * 作业分片策略.
 * 
 * @author Disjob
 */
public interface JobShardingStrategy {
    
    /**
     * 进行作业分片.
     * 
     * @param serversList 所有参与分片的服务器列表
     * @param option 作业分片策略选项
     * @return 分配分片的HURL(ip:port)和分片集合的映射
     */
    Map<HURL, List<String>> sharding(List<HURL> serversList, JobShardingStrategyOption option);
}
