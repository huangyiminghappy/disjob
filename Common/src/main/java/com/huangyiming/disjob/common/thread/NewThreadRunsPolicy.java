package com.huangyiming.disjob.common.thread;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import com.huangyiming.disjob.common.util.LoggerUtil;

public class NewThreadRunsPolicy implements RejectedExecutionHandler {
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        try {
            final Thread t = new Thread(r, "Temporary task executor");
            LoggerUtil.warn("threadpool is full,so create Thread to run job");
            t.start();
        } catch (Throwable e) {
            throw new RejectedExecutionException("Failed to ;start a new thread", e);
        }
    }
}
