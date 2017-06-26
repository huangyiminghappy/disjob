
package com.huangyiming.disjob.graph;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jboss.netty.util.internal.ConcurrentHashMap;

import com.huangyiming.disjob.event.AbstractEventObject;
import com.huangyiming.disjob.event.ObjectEvent;
import com.huangyiming.disjob.event.ObjectListener;
import com.huangyiming.disjob.java.job.DependDisJob;

public class Scheduler extends AbstractEventObject<Node<DependDisJob>> {
	
	@SuppressWarnings("rawtypes")
	private Graph graph ;
	private Map<Node<DependDisJob>, JobCondition> jobConditionsMap = null ;
	@SuppressWarnings("rawtypes")
	Scheduler(Graph graph){
		this.graph = graph ;
	}
	@SuppressWarnings("unchecked")
	@Override
	public void attachListener() {
		
		jobConditionsMap = new ConcurrentHashMap<Node<DependDisJob>, JobCondition>(graph.getVertexSet().size());
		Iterator<?> nodes = graph.getVertexSet().iterator();
		
		while(nodes.hasNext()){
			Node<DependDisJob> node = (Node<DependDisJob>) nodes.next();
			Set<Node<DependDisJob>> reverseNode = (Set<Node<DependDisJob>>) graph.getReverseAdjaNode().get(node);
			
			jobConditionsMap.put(node,new JobCondition(this,node,reverseNode));
		}
		
		this.addListener(new ObjectListener<Node<DependDisJob>>() {
			
			public void onEvent(ObjectEvent<Node<DependDisJob>> event) {
				Node<DependDisJob> disJob = event.getValue();
				//得到当前这个节点的前驱节点[可能有多个]
				Set<Node<DependDisJob>> dependsNodes = (Set<Node<DependDisJob>>) graph.getAdjaNode().get(disJob);
				if(dependsNodes!=null){
					//如果不为null,则向他的后继节点广播一个消息。后继节点都知道自己将收到多少个消息后便可以开始执行
					Iterator<?> iter = dependsNodes.iterator();
					for(;iter.hasNext();){
						Node<DependDisJob> temp = (Node<DependDisJob>) iter.next();
						JobCondition jobCondition = jobConditionsMap.get(temp) ;
						jobCondition.increMessageCount(disJob);//收到一个消息。进行加一操作。
						jobCondition.handler();
					}
				}else{
					jobConditionsMap.get(disJob).handler();//如果是最后一个节点每次执行完就直接看消息有没有收足够，收足够了，就执行，没有收足够，就直接丢弃
				}
			}
		}, 4);
	}
	
	public void notify(Node<DependDisJob> node){
		ObjectEvent<Node<DependDisJob>> objectEvent = new ObjectEvent<Node<DependDisJob>>(node, 4);
		this.notifyListeners(objectEvent);
	}
}
