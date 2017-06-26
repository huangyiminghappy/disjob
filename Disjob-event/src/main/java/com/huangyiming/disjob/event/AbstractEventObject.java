package com.huangyiming.disjob.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractEventObject<V> implements EventObject<V>{
	private ConcurrentHashMap<Integer, Collection<ObjectListener<V>>> listeners;
	private static boolean isDebug = false;
	private ReentrantLock lock = new ReentrantLock();
	public AbstractEventObject(){
		this.attachListener();
	}
	public abstract void attachListener();
	
	public void publish(V v, int eventType) {
		notifyListeners(new ObjectEvent<V>(v, eventType));
	}
	
	public void addListener(ObjectListener<V> objectListener, int eventType) {
		lock.lock();
		try{
			if (listeners == null) {
				listeners = new ConcurrentHashMap<Integer, Collection<ObjectListener<V>>>();
			}
			if (listeners.get(eventType) == null) {
				Collection<ObjectListener<V>> tempInfo = new ArrayList<ObjectListener<V>>();
				tempInfo.add(objectListener);
				listeners.put(eventType, tempInfo);
			} else {
				listeners.get(eventType).add(objectListener);
			}
		}finally{
			lock.unlock();
		}
		debugEventMsg("注册一个事件,类型为" + eventType);
	}

	public void removeListener(ObjectListener<V> objectListener, int eventType) {
		if (listeners == null)
			return;
		lock.lock();
		try{
			Collection<ObjectListener<V>> tempInfo = listeners.get(eventType);
			if(tempInfo == null){
				return ;
			}
			if(tempInfo.size()==1){
				tempInfo.clear();
				return ;
			}
			tempInfo.remove(objectListener);
		}finally{
			lock.unlock();
		}
		debugEventMsg("移除一个事件,类型为" + eventType);
	}
	
	public void removeListener(int eventType){
		lock.lock();
		try{
			listeners.remove(eventType);
		}finally{
			lock.unlock();
		}
		debugEventMsg("移除一个事件,类型为" + eventType);
	}
	
	public void notifyListeners(ObjectEvent<V> event) {
		List<ObjectListener<V>> tempList = null;
		if (listeners == null)
			return;
		//1、
		int eventType = event.getEventType();
		//2、
		lock.lock();
		try{
			if (listeners.get(eventType) != null) {
				Collection<ObjectListener<V>> tempInfo = listeners.get(eventType);
				tempList = new ArrayList<ObjectListener<V>>(tempInfo);
			}
		}finally{
			lock.unlock();
		}
		//3、触发,
		if (tempList != null) {
			for (ObjectListener<V> listener : tempList) {
				listener.onEvent(event);
			}
		}
	}

	public void clearListener() {
		lock.lock();
		try{
			if (listeners != null) {
				listeners = null;
			}
		}finally{
			lock.unlock();
		}
	}

	protected void debugEventMsg(String msg) {
		if (isDebug) {
			System.out.println(msg);;
		}
	}
}
