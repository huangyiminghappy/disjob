package com.huangyiming.disjob.graph;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import com.huangyiming.disjob.java.job.DependDisJob;

/**
 * 
 * @author Disjob
 *
 */
public class JobCondition extends AbstractJobCondition {
	
	public JobCondition(Scheduler schedule,Node<DependDisJob> observiable, Set<Node<DependDisJob>> v) {
		super(schedule,observiable, v);
	}

	@Override
	public boolean isFinished() {
		//统计 收到 大于  1 的 node 的数量
		int tmpNum = 0 ;
		for(AtomicInteger value : messageCountMap.values()){
			if(value.get()>=1){
				tmpNum++ ;
			}
		}
		return tmpNum >= messageTotal;
	}

	/**
	 * 收到 job node 完成的消息数
	 * @param targetJobNode
	 */
	public void increMessageCount(Node<DependDisJob> targetJobNode){
		
		messageCountMap.get(targetJobNode).getAndIncrement();
	}
}
