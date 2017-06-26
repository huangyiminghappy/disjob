package com.huangyiming.disjob.event;

import java.util.EventListener;

/**
 * 定义事件处理接口。由用户真正的实现
 * @author Disjob
 *
 * @param <V>
 */
public interface ObjectListener<V> extends EventListener {
	
	public void onEvent(ObjectEvent<V> event);

}
