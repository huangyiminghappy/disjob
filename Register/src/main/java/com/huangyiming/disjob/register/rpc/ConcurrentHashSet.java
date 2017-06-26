package com.huangyiming.disjob.register.rpc;
/**
 * <pre>
 * 
 *  File: ConcurrentHashSet.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  用concurenthashset来去重和hash
 * 
 *  Revision History
 *  Date,					Who,					What;
 *  2016年5月12日				Disjob				Initial.
 *
 * </pre>
 */

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.CollectionUtils;

import com.huangyiming.disjob.register.domain.DisJobServerInfo;

public class ConcurrentHashSet<E> extends AbstractSet<E> implements Set<E>, java.io.Serializable {

    private static final long serialVersionUID = -8672117787651310382L;

    private static final Object PRESENT = new Object();

    private final ConcurrentHashMap<E, Object> map;

    public ConcurrentHashSet() {
        map = new ConcurrentHashMap<E, Object>();
    }

    public ConcurrentHashSet(int initialCapacity) {
        map = new ConcurrentHashMap<E, Object>(initialCapacity);
    }

    /**
     * Returns an iterator over the elements in this set. The elements are returned in no particular
     * order.
     * 
     * @return an Iterator over the elements in this set
     * @see ConcurrentModificationException
     */
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    /**
     * Returns the number of elements in this set (its cardinality).
     * 
     * @return the number of elements in this set (its cardinality)
     */
    public int size() {
        return map.size();
    }

    /**
     * Returns <tt>true</tt> if this set contains no elements.
     * 
     * @return <tt>true</tt> if this set contains no elements
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Returns <tt>true</tt> if this set contains the specified element. More formally, returns
     * <tt>true</tt> if and only if this set contains an element <tt>e</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
     * 
     * @param o element whose presence in this set is to be tested
     * @return <tt>true</tt> if this set contains the specified element
     */
    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    /**
     * Adds the specified element to this set if it is not already present. More formally, adds the
     * specified element <tt>e</tt> to this set if this set contains no element <tt>e2</tt> such
     * that <tt>(e==null&nbsp;?&nbsp;e2==null&nbsp;:&nbsp;e.equals(e2))</tt>. If this set already
     * contains the element, the call leaves the set unchanged and returns <tt>false</tt>.
     * 
     * @param e element to be added to this set
     * @return <tt>true</tt> if this set did not already contain the specified element
     */
    public boolean add(E e) {
        return map.put(e, PRESENT) == null;
    }

    /**
     * Removes the specified element from this set if it is present. More formally, removes an
     * element <tt>e</tt> such that <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>,
     * if this set contains such an element. Returns <tt>true</tt> if this set contained the element
     * (or equivalently, if this set changed as a result of the call). (This set will not contain
     * the element once the call returns.)
     * 
     * @param o object to be removed from this set, if present
     * @return <tt>true</tt> if the set contained the specified element
     */
    public boolean remove(Object o) {
        return map.remove(o) == PRESENT;
    }

    /**
     * Removes all of the elements from this set. The set will be empty after this call returns.
     */
    public void clear() {
        map.clear();
    }

	 public static void main(String[] args) {
		ConcurrentHashSet<DisJobServerInfo> set = new ConcurrentHashSet<DisJobServerInfo>();
		set.add(new DisJobServerInfo("10.38.6.99"));

		set.add(new DisJobServerInfo("192.168.1.0"));
		set.add(new DisJobServerInfo("192.168.1.1"));
		set.add(new DisJobServerInfo("192.168.1.2"));
		set.add(new DisJobServerInfo("192.168.1.3"));
		set.add(new DisJobServerInfo("10.37.6.56"));
		set.add(new DisJobServerInfo("10.38.6.57"));
		set.add(new DisJobServerInfo("10.38.6.59"));
		set.add(new DisJobServerInfo("10.38.6.54"));



		 TreeSet<DisJobServerInfo> treeSet = new TreeSet<DisJobServerInfo>(new Comparator<DisJobServerInfo>() {

			@Override
			public int compare(DisJobServerInfo o1, DisJobServerInfo o2) {
				 
				if(o1.getIp().hashCode() < o2.getIp().hashCode() ){
					return -1;
				}
				if(o1.getIp().hashCode() > o2.getIp().hashCode()){
					return 1;
				}
				return 0;
			}
		});

		 treeSet.addAll(set);
		// TreeSet<DisJobServerInfo> treeSet = new TreeSet<DisJobServerInfo>(set);
		Iterator<DisJobServerInfo>  item =  treeSet.iterator();
		while(item.hasNext()){
			DisJobServerInfo info = item.next();
			//System.out.println(info.getIp()+"====="+info.getIp().hashCode());
					
		}

		
		Iterator<DisJobServerInfo>  it =  set.iterator();
		List<DisJobServerInfo> list = new ArrayList<DisJobServerInfo>();
		while(it.hasNext()){
			list.add(it.next());
		}
		 Collections.sort(list); 
		 for(DisJobServerInfo info:list){
			// System.out.println(info.getIp() + "-----" +info.getIp().hashCode());
		 }
		
		
	}

}
