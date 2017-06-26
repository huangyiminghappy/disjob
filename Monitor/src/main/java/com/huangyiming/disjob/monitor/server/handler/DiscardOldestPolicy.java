package com.huangyiming.disjob.monitor.server.handler;

import io.netty.util.CharsetUtil;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import com.huangyiming.disjob.common.util.LoggerUtil;

/**
 * 如果队列满了,则会丢弃队列尾部最老的数据
 * A handler for rejected tasks that discards the oldest unhandled
 * request and then retries {@code execute}, unless the executor
 * is shut down, in which case the task is discarded.
 */
public class DiscardOldestPolicy implements RejectedExecutionHandler {
    /**
     * Creates a {@code DiscardOldestPolicy} for the given executor.
     */
    public DiscardOldestPolicy() { }

    /**
     * Obtains and ignores the next task that the executor
     * would otherwise execute, if one is immediately available,
     * and then retries execution of task r, unless the executor
     * is shut down, in which case task r is instead discarded.
     *
     * @param r the runnable task requested to be executed
     * @param e the executor attempting to execute this task
     */
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        if (!e.isShutdown()) {
        	Object obj = e.getQueue().poll();
        	if(obj instanceof UDPTaskHandler){
        		UDPTaskHandler hander = (UDPTaskHandler)obj;
            	if(hander !=null){
             		String msg = hander.getMsg().content().toString(CharsetUtil.UTF_8);
            		LoggerUtil.warn("Discard message:"+msg);
            	}
        	}
        	
            e.execute(r);
        }
    }
}
