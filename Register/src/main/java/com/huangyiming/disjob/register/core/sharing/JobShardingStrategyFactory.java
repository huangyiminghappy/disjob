package com.huangyiming.disjob.register.core.sharing;


/**
 * 作业分片策略工厂.
 * 
 * @author Disjob
 */
 
public final class JobShardingStrategyFactory {
    
    /**
     * 获取 作业分片策略实例.
     * 
     * @param jobShardingStrategyClassName 作业分片策略类名,目前默认平均分配
     * @return 作业分片策略实例
     */
    public static JobShardingStrategy getStrategy(final String jobShardingStrategyClassName) {
         return new AverageAllocationJobShardingStrategy();
    }
}
