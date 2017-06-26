package com.huangyiming.disjob.event;

import java.util.EventListener;

/**
 * 所有的事件处理放在一个队列里面有序执行.后面的事件处理在前面的事件正确执行下才能够往下执行.这种适用于前后有依赖关系的 event listener
 * @author Disjob
 *
 * @param <V>
 */
public interface ObjectListenerQueue<V> extends EventListener{

	/**
	 * 
	 * @param event
	 * @return true is can execute after event then stop event and the after event canno't be execute
	 */
	public boolean onEvent(ObjectEvent<V> event);
}
