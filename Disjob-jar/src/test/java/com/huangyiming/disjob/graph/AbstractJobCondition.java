package com.huangyiming.disjob.graph;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.util.internal.ConcurrentHashMap;

import com.huangyiming.disjob.event.BaseCondition;
import com.huangyiming.disjob.java.ExecutorBuilder;
import com.huangyiming.disjob.java.job.DependDisJob;

public abstract class AbstractJobCondition extends BaseCondition<Node<DependDisJob>, Set<Node<DependDisJob>>> {

	protected Scheduler scheduler ;
	protected ConcurrentHashMap<Node<DependDisJob>,AtomicInteger> messageCountMap = null;
	protected int messageTotal = 0;
	
	public AbstractJobCondition(Scheduler schedule,Node<DependDisJob> observiable,Set<Node<DependDisJob>> v) {
		super(observiable, v);
		this.scheduler = schedule ;
		if(v!=null){
			this.messageCountMap = new ConcurrentHashMap<Node<DependDisJob>, AtomicInteger>();
			this.messageTotal = v.size();
			Iterator<Node<DependDisJob>> iter = getValue().iterator();
			while(iter.hasNext()){
				Node<DependDisJob> tmp = iter.next();
				messageCountMap.put(tmp, new AtomicInteger(0));
			}
		}
	}

	@Override
	public abstract boolean isFinished() ;

	public abstract void increMessageCount(Node<DependDisJob> targetJobNode);
	
	@Override
	public void handler() {
		if(this.isFinished()){
			//一次消息消费成功，对每一个前驱节点的消息数减一。
			for(AtomicInteger value : messageCountMap.values()){
				if(value.get()>=1){
					value.decrementAndGet();
				}
			}
			
			ExecutorBuilder.getExecutor().execute(new JobAction(observiable, scheduler));
		}else{
			System.out.println(getObserviable().getVal().getKey()+"还没有达到条件:");
		}
	}
}
