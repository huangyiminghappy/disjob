package com.huangyiming.disjob.register.rpc;

import java.util.regex.Pattern;

import com.huangyiming.disjob.common.Constants;

/**
 * <pre>
 * 
 *  File: MotanConstants.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  DISJOB注册订阅常量类
 * 
 *  Revision History
 *  Date,					Who,					What;
 *  2016年5月12日				Disjob				Initial.
 *
 * </pre>
 */
public class DisJobConstants {

    public static final String SEPERATOR_ACCESS_LOG = "|";
    public static final String COMMA_SEPARATOR = ",";
    public static final Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");
    public static final String PROTOCOL_SEPARATOR = "://";
    public static final String PATH_SEPARATOR = "/";
    public static final String REGISTRY_SEPARATOR = "|";
    public static final Pattern REGISTRY_SPLIT_PATTERN = Pattern.compile("\\s*[|;]+\\s*");
    public static final String SEMICOLON_SEPARATOR = ";";
    public static final Pattern SEMICOLON_SPLIT_PATTERN = Pattern.compile("\\s*[;]+\\s*");
    public static final String QUERY_PARAM_SEPARATOR = "&";
    public static final Pattern QUERY_PARAM_PATTERN = Pattern.compile("\\s*[&]+\\s*");
    public static final String EQUAL_SIGN_SEPERATOR = "=";
    public static final Pattern EQUAL_SIGN_PATTERN = Pattern.compile("\\s*[=]\\s*");
    public static final String NODE_TYPE_SERVICE = "server";
    public static final String SCOPE_NONE = "none";
    public static final String SCOPE_LOCAL = "local";
    public static final String REGISTRY_PROTOCOL_LOCAL = "local";
    public static final String REGISTRY_PROTOCOL_ZOOKEEPER = "zookeeper";
    public static final String PROTOCOL_INJVM = "injvm";
    public static final String PROTOCOL_DISJOB = "disJob";
   // public static final String PROTOCOL_MOTAN = "motan";
    public static final String PROXY_JDK = "jdk";
    public static final String FRAMEWORK_NAME = "disJob";
    public static final String PROTOCOL_SWITCHER_PREFIX = "protocol:";
    public static final String METHOD_CONFIG_PREFIX = "methodconfig.";
    public static final int MILLS = 1;
    public static final int SECOND_MILLS = 1000;
    public static final int MINUTE_MILLS = 60 * SECOND_MILLS;
    public static final String DEFAULT_VALUE = "default";
    public static final String DEFAULT_VERSION = "1.0";
    public static final String DEFAULT_CHARACTER = "utf-8";
    public static final int SLOW_COST = 50; // 50ms
    public static final int STATISTIC_PEROID = 30; // 30 seconds
 
   
 
    /**
     * heartbeat constants end
     */
    public static final String ZOOKEEPER_REGISTRY_NAMESPACE = com.huangyiming.disjob.common.Constants.ROOT+Constants.DISJOB_RPC_NODE_ROOT;

 

    private DisJobConstants() {
    }

}

