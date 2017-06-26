package com.huangyiming.disjob.quence;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 说明：这里的队列是严格的无阻塞队列。如果这个队列中某个业务室阻塞式的，那么将会导致一个严重的问题就是后面的action 就无法保证顺利执行。
 * 使用场景：无阻塞是行为队列
 * @author Disjob
 *
 */
public class BaseActionQueue implements ActionQueue {

	private Queue<Action> queue;
	private Executor executor;
	private ReentrantLock lock = new ReentrantLock();
	public BaseActionQueue(Executor executor) {
		this.executor = executor;
		queue = new LinkedList<Action>();
	}

	public BaseActionQueue(Executor executor, Queue<Action> queue) {
		this.executor = executor;
		this.queue = queue;
	}

	public ActionQueue getActionQueue() {
		return this;
	}

	public Queue<Action> getQueue() {
		return queue;
	}

	public void enqueue(Action action) {
		int queueSize = 0;
		if(action.getActionQueue() == null){
			action.setActionQueue(this);
		}
		lock.lock();
		try{
			queue.add(action);
			queueSize = queue.size();
		}finally{
			lock.unlock();
		}
		if (queueSize == 1) {
			executor.execute(action);
		}
		if (queueSize > 4 * Runtime.getRuntime().availableProcessors()) {
			 Log.warn(action.toString() + " queue size : " + queueSize);
		}
	}

	public void dequeue(Action action) {
		Action nextAction = null;
		int queueSize = 0;
		String tmpString = null;
		lock.lock();
		try{
			queueSize = queue.size();
			Action temp = queue.remove();
			if (temp != action) {
				tmpString = temp.toString();
			}
			if (queueSize != 0) {
				nextAction = queue.peek();
			}
		}finally{
			lock.unlock();
		}

		if (nextAction != null) {
			executor.execute(nextAction);
		}
		if (queueSize == 0) {
			 Log.debug("queue.size() is 0.");
		}
		if (tmpString != null) {
			 Log.debug("action queue error. temp " + tmpString + ", action : " + action.toString());
		}
	}

	public void clear() {
		lock.lock();
		try{
			queue.clear();
		}finally{
			lock.unlock();
		}
	}
}
