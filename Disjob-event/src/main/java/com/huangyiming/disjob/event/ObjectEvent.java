package com.huangyiming.disjob.event;


public class ObjectEvent<V> {

	private V value;
	private int eventType;

	/**
	 * @param source 系统默认参数
	 * @param objData 自定义参数
	 * @param eventType 事件健值
	 */
	public ObjectEvent( V value, int eventType) {
		this.value = value;
		this.eventType = eventType;
	}

	public int getEventType() {
		return eventType;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}
	
}
