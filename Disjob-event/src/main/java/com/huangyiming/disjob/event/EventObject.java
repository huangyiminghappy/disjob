package com.huangyiming.disjob.event;

public interface EventObject<V> {

	public void attachListener();
	
	public void addListener(ObjectListener<V> objectListener, int eventType);
	
	public void removeListener(ObjectListener<V> objectListener, int eventType);
	
	public void removeListener(int eventType);
	
	public void notifyListeners(ObjectEvent<V> event);
	
	public void clearListener();
	
	public void publish(V v,int eventType);
}
