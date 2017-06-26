package com.huangyiming.disjob.register.center;

import org.apache.zookeeper.KeeperException.ConnectionLossException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;

import com.huangyiming.disjob.common.exception.ZKNodeException;
import com.huangyiming.disjob.common.util.LoggerUtil;

/**
 * 抛出RegException的异常处理类.
 * @author Disjob
 */
public final class RegistryExceptionHandler {
    
    /**
     * 处理掉中断和连接失效异常并继续抛出RegException.
     * @param cause 待处理的异常.
     */
    public static void handleException(final Exception cause) {
        if (isIgnoredException(cause) || isIgnoredException(cause.getCause())) {
            LoggerUtil.warn("disJob: ignored exception for: {}", cause.getMessage());
        } else if (cause instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        } else {
            throw new ZKNodeException(cause);
        }
    }
    
    private static boolean isIgnoredException(final Throwable cause) {
        return null != cause && (cause instanceof ConnectionLossException || cause instanceof NoNodeException || cause instanceof NodeExistsException);
    }
}
