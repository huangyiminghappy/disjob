package com.huangyiming.disjob.register.job;

/**
 * <pre>
 * 
 *  File: WeightedRoundRobinScheduling.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  TODO
 * 
 *  Revision History
 *  Date,					Who,					What;
 *  2016年6月1日				Disjob				Initial.
 *
 * </pre>
 */
import java.util.Date;

import org.apache.curator.framework.CuratorFramework;

import com.huangyiming.disjob.register.cache.ZKJobCache;
import com.huangyiming.disjob.register.domain.DisJobServerInfo;
import com.huangyiming.disjob.register.rpc.ConcurrentHashSet;
 
/**
 * 
 * <pre>
 * 
 *  File: WeightedRoundRobinScheduling.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  权重轮询调度算法(WeightedRound-RobinScheduling),根据IP和权重处理,从IP和权重列表对象的列表中选出合适的机器
 * 
 *  Revision History
 *
 *  Date：		2016年6月3日
 *  Author：		Disjob
 *
 * </pre>
 */
public class WeightedRoundRobinScheduling{

	/**
	 * 
	 * @param client 列表中选出来的合适的机器
	 * @return
	 */
	public static DisJobServerInfo GetBestSlaveServer(CuratorFramework client){
        ConcurrentHashSet<DisJobServerInfo> list = ZKJobCache.ipList;
        DisJobServerInfo server = null;
        DisJobServerInfo best = null;
        int total = 0;
        for (DisJobServerInfo tmp:list){ 
            server = tmp;
            if(server.isDown()){
                continue;
            }
            int currentWeight = server.getCurrentWeight();
            int effectiveWeight = server.getEffectiveWeight();

            currentWeight += effectiveWeight;
            server.setCurrentWeight(currentWeight);
            server.setEffectiveWeight(effectiveWeight);

            total += effectiveWeight;
            server.setEffectiveWeight(effectiveWeight);
            effectiveWeight = server.getEffectiveWeight();
            if(effectiveWeight < server.getWeight()){
                effectiveWeight++;
            }
            server.setEffectiveWeight(effectiveWeight);
            server.setCurrentWeight(currentWeight);
            
            if(best == null || currentWeight>best.getCurrentWeight()){
                best = server;
            }
        }

        if (best == null) {
             return null;
        }
        int currentWeight;
        currentWeight = best.getCurrentWeight();
        currentWeight -= total;
        best.setCurrentWeight(currentWeight);
        best.setCheckedDate(new Date());
        return best;
    }
}
