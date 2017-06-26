package com.huangyiming.disjob.graph;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import com.huangyiming.disjob.java.job.DependDisJob;

public class Graph<V extends DependDisJob> {

	 // 图中节点的集合  
	private Set<Node<V>> vertexSet = new HashSet<Node<V>>();  
    // 相邻的节点，纪录边   start -> end
    private Map<Node<V>, Set<Node<V>>> adjaNode = new ConcurrentHashMap<Node<V>, Set<Node<V>>>();
    // 记录： end -> start
    private Map<Node<V>, Set<Node<V>>> reverseAdjaNode = new ConcurrentHashMap<Node<V>, Set<Node<V>>>();
    
    private ReentrantLock lock = new ReentrantLock();
    // 将节点加入图中  
    public boolean addNode(Node<V> start, Node<V> end) {  
    	//1、save all of vertex
    	lock.lock();
    	try{
	    	if (!vertexSet.contains(start)) {  
	            vertexSet.add(start);  
	        }  
	        if (!vertexSet.contains(end)) {  
	            vertexSet.add(end);  
	        } 
    	}finally{
    		lock.unlock();
    	}
        
    	//2、save the relation of  start -> end relation
        if (adjaNode.containsKey(start)  
                && adjaNode.get(start).contains(end)) {  
            return false;  
        } 
        if (adjaNode.containsKey(start)) {  
            adjaNode.get(start).add(end);  
        } else {  
            Set<Node<V>> temp = new HashSet<Node<V>>();  
            temp.add(end);  
            adjaNode.put(start, temp);  
        } 
        
        //3、save the relation of  end -> start relation
        if(reverseAdjaNode.containsKey(end)&&reverseAdjaNode.get(end).contains(start)){
        	return false;
        }
        if(reverseAdjaNode.containsKey(end)){
        	reverseAdjaNode.get(end).add(start);
        }else{
        	Set<Node<V>> temp = new HashSet<Node<V>>();
        	temp.add(start);
        	reverseAdjaNode.put(end, temp);
        }
        
        end.setPathIn(end.getPathIn()+1);
        return true;  
    }  
    
    /**
     * 得到所有的节点
     * @return
     */
    public Set<Node<V>> getVertexSet() {
		return Collections.unmodifiableSet(vertexSet);
	}
    /**
     * 得到每一个节点的下一个节点 结合
     * @return
     */
	public Map<Node<V>, Set<Node<V>>> getAdjaNode() {
		return Collections.unmodifiableMap(adjaNode);
	}

	public Map<Node<V>, Set<Node<V>>> getReverseAdjaNode() {
		return Collections.unmodifiableMap(reverseAdjaNode);
	}

}
