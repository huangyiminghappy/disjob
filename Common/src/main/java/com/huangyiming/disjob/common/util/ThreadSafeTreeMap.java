package com.huangyiming.disjob.common.util;

import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafeTreeMap<K, V> extends TreeMap<K, V>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ReentrantLock lock = new ReentrantLock();
	
	@Override
	public V put(K key, V value) {
		lock.lock();
		try{
			return super.put(key, value);
		}finally{
			lock.unlock();
		}
	}
	
	@Override
	public V get(Object key) {
		lock.lock();
		try{
			return super.get(key);
		}finally{
			lock.unlock();
		}
	}
	
	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		lock.lock();
		try{
			return super.entrySet();
		}finally{
			lock.unlock();
		}
	}
	
	@Override
	public Collection<V> values() {
		lock.lock();
		try{
			return super.values();
		}finally{
			lock.unlock();
		}
	}
	
	@Override
	public Set<K> keySet() {
		lock.lock();
		try{
			return super.keySet();
		}finally{
			lock.unlock();
		}
	}
	
	@Override
	public V remove(Object key) {
		lock.lock();
		try{
			return super.remove(key);
		}finally{
			lock.unlock();
		}
	}
}
