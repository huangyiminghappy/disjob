package com.huangyiming.disjob.register.rpc;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.rpc.client.HURL;
import com.huangyiming.disjob.rpc.client.HURLParamType;

 

 /**
  * 
  * <pre>
  * 
  *  File: FailbackRegistry.java
  * 
  *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
  * 
  *  Description:
  *  注册发现失败接口
  * 
  *  Revision History
  *  Date,					Who,					What;
  *  2016年5月16日				Disjob				Initial.
  *
  * </pre>
  */

public abstract class FailbackRegistry extends AbstractRegistry {
    private Set<HURL> failedRegistered = new ConcurrentHashSet<HURL>();
    private Set<HURL> failedUnregistered = new ConcurrentHashSet<HURL>();
    private ConcurrentHashMap<HURL, ConcurrentHashSet<NotifyListener>> failedSubscribed =
            new ConcurrentHashMap<HURL, ConcurrentHashSet<NotifyListener>>();
    private ConcurrentHashMap<HURL, ConcurrentHashSet<NotifyListener>> failedUnsubscribed =
            new ConcurrentHashMap<HURL, ConcurrentHashSet<NotifyListener>>();

    private static ScheduledExecutorService retryExecutor = Executors.newScheduledThreadPool(1);

    public FailbackRegistry(HURL url) {
        super();
        long retryPeriod = url.getIntParameter(HURLParamType.registryRetryPeriod.getName(), HURLParamType.registryRetryPeriod.getIntValue());
        retryExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    retry();
                } catch (Exception e) {
                   LoggerUtil.warn(String.format("[%s] False when retry in failback registry", registryClassName), e);
                }

            }
        }, retryPeriod, retryPeriod, TimeUnit.MILLISECONDS);
    }

    public void register(HURL url){
        
    }
    
    /**
     * 调用模板订阅处理,若失败会将失败的内容存入内存重试直到成功
     */
    @Override
    public void subscribe(HURL url, NotifyListener listener) {
        removeForFailedSubAndUnsub(url, listener);
         try {
            super.subscribe(url, listener);
        } catch (Exception e) {
             addToFailedMap(failedSubscribed, url, listener);
        }
    }

    /**
     * 取消订阅,暂未实现
     */
    @Override
    public void unsubscribe(HURL url, NotifyListener listener) {
        removeForFailedSubAndUnsub(url, listener);

        try {
            super.unsubscribe(url, listener);
        } catch (Exception e) {
            /*if (isCheckingUrls(getUrl(), url)) {
                throw new DisJobFrameWorkException(String.format("[%s] false to unsubscribe %s from %s", registryClassName, url, getUrl()),
                       e);
            }*/
            addToFailedMap(failedUnsubscribed, url, listener);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<HURL> discover(HURL url) {
        try {
            return super.discover(url);
        } catch (Exception e) {
            // 如果discover失败，返回一个empty list吧，毕竟是个下行动作，
            LoggerUtil.warn(String.format("Failed to discover url:%s ", url), e);
            return Collections.EMPTY_LIST;
        }
    }

    private boolean isCheckingUrls(HURL... urls) {
        for (HURL url : urls) {
            if (!Boolean.parseBoolean(url.getParameter(HURLParamType.check.getName(), HURLParamType.check.getValue()))) {
                return false;
            }
        }
        return true;
    }

    private void removeForFailedSubAndUnsub(HURL url, NotifyListener listener) {
        Set<NotifyListener> listeners = failedSubscribed.get(url);
        if (listeners != null) {
            listeners.remove(listener);
        }
        listeners = failedUnsubscribed.get(url);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    private void addToFailedMap(ConcurrentHashMap<HURL, ConcurrentHashSet<NotifyListener>> failedMap, HURL url, NotifyListener listener) {
        Set<NotifyListener> listeners = failedMap.get(url);
        if (listeners == null) {
            failedMap.putIfAbsent(url, new ConcurrentHashSet<NotifyListener>());
            listeners = failedMap.get(url);
        }
        listeners.add(listener);
    }

    private void retry() {
        if (!failedRegistered.isEmpty()) {
            Set<HURL> failed = new HashSet<HURL>(failedRegistered);
            LoggerUtil.info("[{}] Retry register {}", registryClassName, failed);
            try {
                for (HURL url : failed) {
                    super.register(url);
                    failedRegistered.remove(url);
                }
            } catch (Exception e) {
                LoggerUtil.warn(String.format("[%s] Failed to retry register, retry later, failedRegistered.size=%s, cause=%s",
                        registryClassName, failedRegistered.size(), e.getMessage()), e);
            }

        }
        if (!failedUnregistered.isEmpty()) {
            Set<HURL> failed = new HashSet<HURL>(failedUnregistered);
            LoggerUtil.info("[{}] Retry unregister {}", registryClassName, failed);
            try {
                for (HURL url : failed) {
                    super.unregister(url);
                    failedUnregistered.remove(url);
                }
            } catch (Exception e) {
                LoggerUtil.warn(String.format("[%s] Failed to retry unregister, retry later, failedUnregistered.size=%s, cause=%s",
                        registryClassName, failedUnregistered.size(), e.getMessage()), e);
            }

        }
        if (!failedSubscribed.isEmpty()) {
            Map<HURL, Set<NotifyListener>> failed = new HashMap<HURL, Set<NotifyListener>>(failedSubscribed);
            for (Map.Entry<HURL, Set<NotifyListener>> entry : new HashMap<HURL, Set<NotifyListener>>(failed).entrySet()) {
                if (entry.getValue() == null || entry.getValue().size() == 0) {
                    failed.remove(entry.getKey());
                }
            }
            if (failed.size() > 0) {
                LoggerUtil.info("[{}] Retry subscribe {}", registryClassName, failed);
                try {
                    for (Map.Entry<HURL, Set<NotifyListener>> entry : failed.entrySet()) {
                        HURL url = entry.getKey();
                        Set<NotifyListener> listeners = entry.getValue();
                        for (NotifyListener listener : listeners) {
                            super.subscribe(url, listener);
                            listeners.remove(listener);
                        }
                    }
                } catch (Exception e) {
                    LoggerUtil.warn(String.format("[%s] Failed to retry subscribe, retry later, failedSubscribed.size=%s, cause=%s",
                            registryClassName, failedSubscribed.size(), e.getMessage()), e);
                }
            }
        }
        if (!failedUnsubscribed.isEmpty()) {
            Map<HURL, Set<NotifyListener>> failed = new HashMap<HURL, Set<NotifyListener>>(failedUnsubscribed);
            for (Map.Entry<HURL, Set<NotifyListener>> entry : new HashMap<HURL, Set<NotifyListener>>(failed).entrySet()) {
                if (entry.getValue() == null || entry.getValue().size() == 0) {
                    failed.remove(entry.getKey());
                }
            }
            if (failed.size() > 0) {
                LoggerUtil.info("[{}] Retry unsubscribe {}", registryClassName, failed);
                try {
                    for (Map.Entry<HURL, Set<NotifyListener>> entry : failed.entrySet()) {
                        HURL url = entry.getKey();
                        Set<NotifyListener> listeners = entry.getValue();
                        for (NotifyListener listener : listeners) {
                            super.unsubscribe(url, listener);
                            listeners.remove(listener);
                        }
                    }
                } catch (Exception e) {
                    LoggerUtil.warn(String.format("[%s] Failed to retry unsubscribe, retry later, failedUnsubscribed.size=%s, cause=%s",
                            registryClassName, failedUnsubscribed.size(), e.getMessage()), e);
                }
            }
        }

    }

}

