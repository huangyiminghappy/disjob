package com.huangyiming.disjob.rpc.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.huangyiming.disjob.rpc.utils.RpcConstants;

/**
 * <pre>
 * 
 *  File: HURL.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  存放订阅注册的URL对象
 * 
 *  Revision History
 *  Date,					Who,					What;
 *  2016年5月13日				Disjob				Initial.
 *
 * </pre>
 */
public class HURL implements Serializable{
    private String serverGroup;
    private String protocol;
    private String host;
    private int port;
    private String serverName;
    // interfaceName
    private String phpFilePath;
    private String className;
    private String methodName;
    private String version;
    private String clientUrl;
    private String async;
    private String cron ;
    private boolean fireNow ;
    private Map<String, String> parameters = new HashMap<String, String>();

    public HURL(String serverGroup, String protocol, String host, int port, String serverName){
        super();
        this.serverGroup = serverGroup;
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.serverName = serverName;
        parameters.put(HURLParamType.group.getName(), serverGroup);
        async = "true";
        parameters.put("async", "true");
    }


	public HURL(String serverGroup, String host, String serverName) {
		super();
		this.serverGroup = serverGroup;
		this.host = host;
		this.protocol = RpcConstants.PROTOCOL_EJOB;
		this.serverName = serverName;
		parameters.put(HURLParamType.group.getName(), serverGroup);
		async = "true";
		parameters.put("async", "true");
	}
    
    public HURL(String serverGroup, String protocol, String host, int port, String serverName, String phpFilePath,
            String className, String methodName, String version, String clientUrl, String async,Map<String, String> parameters)
    {
        super();
        this.serverGroup = serverGroup;
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.serverName = serverName;
        this.phpFilePath = phpFilePath;
        this.className = className;
        this.methodName = methodName;
        this.version = version;
        this.clientUrl = clientUrl;
        this.async = async;
        this.parameters = parameters;
    }

    
    public HURL(){
        
    }

    public HURL(String serverGroup, String protocol, String host, int port,String serverName,String phpFilePath, String className,
            String methodName, String version, Map<String, String> parameters)
    {
        super();
        this.serverGroup = serverGroup;
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.phpFilePath = phpFilePath;
        this.className = className;
        this.methodName = methodName;
        this.version = version;
        this.serverName = serverName;        
        this.parameters = parameters;
    }

    public HURL(String serverGroup, String protocol, String host, int port,Map<String, String> parameters){
        super();
        this.serverGroup = serverGroup;
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.parameters = parameters;
    }

    /**
     * 
     * RPC的url转化为HURL对象
     *
     * @param url:类似于：disJob://192.168.196.1:9501/vipJobAppFour?serverGroup=vip&phpFilePath=''&className=com.huangyiming.disJob.java.app.VipJobAppFour&methodName=execute&version=1
     * @return
     */
    public static HURL valueOf(String url) {
        if (StringUtils.isBlank(url)) {
           // throw new MotanServiceException("url is null");
            throw new RuntimeException("url is null");
        }
        String protocol = null;
        String host = null;
        int port = 0;
        String serverName = null;
        Map<String, String> parameters = new HashMap<String, String>();;
        int i = url.indexOf("?"); // seperator between body and parameters
        if (i >= 0) {
            String[] parts = url.substring(i + 1).split("\\&");
            for (String part : parts) {
                part = part.trim();
                if (part.length() > 0) {
                    int j = part.indexOf('=');
                    if (j >= 0) {
                        parameters.put(part.substring(0, j), part.substring(j + 1));
                    } else {
                        parameters.put(part, part);
                    }
                }
            }
            url = url.substring(0, i);
        }
        i = url.indexOf("://");//仅处理后，url=disJob://192.168.196.1:9501/vipJobAppFour
        if (i >= 0) {
            if (i == 0) throw new IllegalStateException("url missing protocol: \"" + url + "\"");
            protocol = url.substring(0, i);
            url = url.substring(i + 3);
        } else {
            i = url.indexOf(":/");
            if (i >= 0) {
                if (i == 0) throw new IllegalStateException("url missing protocol: \"" + url + "\"");
                protocol = url.substring(0, i);
                url = url.substring(i + 1);
            }
        }

        i = url.indexOf("/");
        if (i >= 0) {
            serverName = url.substring(i + 1);
            url = url.substring(0, i);
        }

        i = url.indexOf(":");
        if (i >= 0 && i < url.length() - 1) {
            port = Integer.parseInt(url.substring(i + 1));
            url = url.substring(0, i);
        }
        if (url.length() > 0) host = url;
       
        HURL hurl =  new HURL(HURLParamType.group.getValue(), protocol, host, port,parameters);
        hurl.setServerName(serverName);
        hurl.setClientUrl(url);
         if(parameters !=null && parameters.size() >0){
        	if(parameters.get("serverGroup") !=null) hurl.setServerGroup((parameters.get("serverGroup")) );
            if(parameters.get("phpFilePath") !=null) hurl.setPhpFilePath(parameters.get("phpFilePath") );
            if(parameters.get("className") !=null) hurl.setClassName(parameters.get("className") );
            if(parameters.get("methodName") !=null) hurl.setMethodName(parameters.get("methodName") );
            if(parameters.get("version") !=null) hurl.setVersion(parameters.get("version") );
            if(parameters.get("cron") != null) hurl.setCron(parameters.get("cron"));
            if(parameters.get("fireNow") !=null) hurl.setFireNow(Boolean.valueOf(parameters.get("fireNow")));
            if(parameters.get("async") !=null) {
                hurl.setAsync((parameters.get("async")) );
            }else{
                hurl.setAsync("true");
            }
        }
        return hurl;
       //return new HURL(HURLParamType.group, protocol, host, port);
    }

    private static String buildHostPortStr(String host, int defaultPort) {
        if (defaultPort <= 0) {
            return host;
        }

        int idx = host.indexOf(":");
        if (idx < 0) {
            return host + ":" + defaultPort;
        }

        int port = Integer.parseInt(host.substring(idx + 1));
        if (port <= 0) {
            return host.substring(0, idx + 1) + defaultPort;
        }
        return host;
    }

	public String getAsync() {
		return async;
	}

	public void setAsync(String async) {
		this.async = async;
	}

	public String getClientUrl() {
		return clientUrl;
	}

	public void setClientUrl(String clientUrl) {
		this.clientUrl = clientUrl;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public boolean isFireNow() {
		return fireNow;
	}

	public void setFireNow(boolean fireNow) {
		this.fireNow = fireNow;
	}

	public String getVersion() {
		return getParameter(HURLParamType.version.getName(),
				HURLParamType.version.getValue());
	}

	public String getGroup() {
		return getParameter(HURLParamType.group.getName(),HURLParamType.group.getValue());
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public String getParameter(String name) {
		return parameters.get(name);
	}

	public String getParameter(String name, String defaultValue) {
		String value = getParameter(name);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

    public String getMethodParameter(String methodName, String paramDesc, String name) {
        String value = getParameter(RpcConstants.METHOD_CONFIG_PREFIX + methodName + "(" + paramDesc + ")." + name);
        if (value == null || value.length() == 0) {
            return getParameter(name);
        }
        return value;
    }

    public String getMethodParameter(String methodName, String paramDesc, String name, String defaultValue) {
        String value = getMethodParameter(methodName, paramDesc, name);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        return value;
    }

    public void addParameter(String name, String value) {
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(value)) {
            return;
        }
        parameters.put(name, value);
    }

    public void removeParameter(String name) {
        if (name != null) {
            parameters.remove(name);
        }
    }

    public void addParameters(Map<String, String> params) {
        parameters.putAll(params);
    }

    public void addParameterIfAbsent(String name, String value) {
        if (hasParameter(name)) {
            return;
        }
        parameters.put(name, value);
    }

    public Boolean getBooleanParameter(String name, boolean defaultValue) {
        String value = getParameter(name);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }

        return Boolean.parseBoolean(value);
    }

    public Boolean getMethodParameter(String methodName, String paramDesc, String name, boolean defaultValue) {
        String value = getMethodParameter(methodName, paramDesc, name);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    public Integer getIntParameter(String name, int defaultValue) {
        Number n = getNumbers().get(name);
        if (n != null) {
            return n.intValue();
        }
        String value = parameters.get(name);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        int i = Integer.parseInt(value);
        getNumbers().put(name, i);
        return i;
    }

    public Integer getMethodParameter(String methodName, String paramDesc, String name, int defaultValue) {
        String key = methodName + "(" + paramDesc + ")." + name;
        Number n = getNumbers().get(key);
        if (n != null) {
            return n.intValue();
        }
        String value = getMethodParameter(methodName, paramDesc, name);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        int i = Integer.parseInt(value);
        getNumbers().put(key, i);
        return i;
    }

    public Long getLongParameter(String name, long defaultValue) {
        Number n = getNumbers().get(name);
        if (n != null) {
            return n.longValue();
        }
        String value = parameters.get(name);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        long l = Long.parseLong(value);
        getNumbers().put(name, l);
        return l;
    }

    public Long getMethodParameter(String methodName, String paramDesc, String name, long defaultValue) {
        String key = methodName + "(" + paramDesc + ")." + name;
        Number n = getNumbers().get(key);
        if (n != null) {
            return n.longValue();
        }
        String value = getMethodParameter(methodName, paramDesc, name);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        long l = Long.parseLong(value);
        getNumbers().put(key, l);
        return l;
    }

    public Float getFloatParameter(String name, float defaultValue) {
        Number n = getNumbers().get(name);
        if (n != null) {
            return n.floatValue();
        }
        String value = parameters.get(name);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        float f = Float.parseFloat(value);
        getNumbers().put(name, f);
        return f;
    }

    public Float getMethodParameter(String methodName, String paramDesc, String name, float defaultValue) {
        String key = methodName + "(" + paramDesc + ")." + name;
        Number n = getNumbers().get(key);
        if (n != null) {
            return n.floatValue();
        }
        String value = getMethodParameter(methodName, paramDesc, name);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        float f = Float.parseFloat(value);
        getNumbers().put(key, f);
        return f;
    }
    public HURL createCopy() {
        Map<String, String> params = new HashMap<String, String>();
        if (this.parameters != null) {
            params.putAll(this.parameters);
        }

        return new HURL(serverGroup, protocol, host, port, serverName, phpFilePath, className, methodName, version, clientUrl, async,params);
    }
    public Boolean getBooleanParameter(String name) {
        String value = parameters.get(name);
        if (value == null) {
            return null;
        }
        return Boolean.parseBoolean(value);
    }

    public String getUri() {
        return protocol + RpcConstants.PROTOCOL_SEPARATOR + host + ":" + port +
                RpcConstants.PATH_SEPARATOR + serverName;
    }

    /**
     * 返回一个service or referer的identity,如果两个url的identity相同，则表示相同的一个service或者referer
     *
     * @return
     */
    public String getIdentity() {
        return protocol + RpcConstants.PROTOCOL_SEPARATOR + host + ":" + port +
                "/" + getParameter(HURLParamType.group.getName(), HURLParamType.group.getValue()) + "/" +
                serverName + "/" + getParameter(HURLParamType.version.getName(), HURLParamType.version.getValue()) +
                "/" + getParameter(HURLParamType.nodeType.getName(), HURLParamType.nodeType.getValue());
    }

    /**
     * check if this url can serve the refUrl.
     *
     * @param refUrl
     * @return
     */
    public boolean canServe(HURL refUrl) {/*
        if (refUrl == null || !this.getPath().equals(refUrl.getPath())) {
            return false;
        }

        if (!ObjectUtils.equals(protocol, refUrl.protocol)) {
            return false;
        }

        if (!StringUtils.equals(this.getParameter(URLParamType.nodeType.getName()), EJobConstants.NODE_TYPE_SERVICE)) {
            return false;
        }

        String version = getParameter(URLParamType.version.getName(), URLParamType.version.getValue());
        String refVersion = refUrl.getParameter(URLParamType.version.getName(), URLParamType.version.getValue());
        if (!version.equals(refVersion)) {
            return false;
        }

        // 由于需要提供跨group访问rpc的能力，所以不再验证group是否一致。
        return true;
    */
    return false;    
    }

    public String toFullStr() {
        StringBuilder builder = new StringBuilder();
        builder.append(getUri()).append("?");

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();

            builder.append(name).append("=").append(value).append("&");
        }
        
        return builder.toString();
    }
    
    @Override
    public String toString()
    {
        return "HURL [serverGroup=" + serverGroup + ", host=" + host + ",port=" + port + ", serverName=" + serverName + "]";
    }

    //@Override
    public String toAllString()
    {
        return "HURL [serverGroup=" + serverGroup + ", protocol=" + protocol + ", host=" + host + ", port=" + port
                + ", serverName=" + serverName + ", phpFilePath=" + phpFilePath + ", className=" + className
                + ", methodName=" + methodName + ", version=" + version + ", async=" + async +", clientUrl=" + clientUrl + ", parameters="
                + parameters + "]";
    }

    // 包含协议、host、port、path、group
    public String toSimpleString() {
        return getUri() + "?group=" + getGroup();
    }

    public boolean hasParameter(String key) {
        return StringUtils.isNotBlank(getParameter(key));
    }

    /**
     * comma separated host:port pairs, e.g. "127.0.0.1:3000"
     *
     * @return
     */
    public String getServerPortStr() {
        return buildHostPortStr(host, port);

    }

    public String getServerGroup()
    {
        return serverGroup;
    }

    public void setServerGroup(String serverGroup)
    {
        this.serverGroup = serverGroup;
    }

    public String getServerName()
    {
        return serverName;
    }

    public void setServerName(String serverName)
    {
        this.serverName = serverName;
    }
    
    public String getPhpFilePath()
    {
        return phpFilePath;
    }



    public void setPhpFilePath(String phpFilePath)
    {
        this.phpFilePath = phpFilePath;
    }



    public String getClassName()
    {
        return className;
    }



    public void setClassName(String className)
    {
        this.className = className;
    }



    public String getMethodName()
    {
        return methodName;
    }



    public void setMethodName(String methodName)
    {
        this.methodName = methodName;
    }



    public void setVersion(String version)
    {
        this.version = version;
    }



    public void setParameters(Map<String, String> parameters)
    {
        this.parameters = parameters;
    }



    public void setNumbers(Map<String, Number> numbers)
    {
        this.numbers = numbers;
    }



    

    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + port;
        result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
        result = prime * result + ((serverGroup == null) ? 0 : serverGroup.hashCode());
        result = prime * result + ((serverName == null) ? 0 : serverName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HURL other = (HURL) obj;
        if (host == null)
        {
            if (other.host != null)
                return false;
        }
        else if (!host.equals(other.host))
            return false;
        if (port != other.port)
            return false;
        if (protocol == null)
        {
            if (other.protocol != null)
                return false;
        }
        else if (!protocol.equals(other.protocol))
            return false;
        if (serverGroup == null)
        {
            if (other.serverGroup != null)
                return false;
        }
        else if (!serverGroup.equals(other.serverGroup))
            return false;
        if (serverName == null)
        {
            if (other.serverName != null)
                return false;
        }
        else if (!serverName.equals(other.serverName))
            return false;
        return true;
    }
    
    /**
     * 用在分片时候hurl区分机器，为目前没有按ip:port来唯一区分一个应用,只是按照ip来区分机器
     * @return
     */
    public String getHurlKey(){
    	return host;
    }
    
    
    private volatile transient Map<String, Number> numbers;

    private Map<String, Number> getNumbers() {
        if (numbers == null) { // 允许并发重复创建
            numbers = new ConcurrentHashMap<String, Number>();
        }
        return numbers;
    }
   public static void main(String[] args)
	{
	   HURL url =  HURL.valueOf("disJob://10.40.6.100:9502/test?phpFilePath=/usr/local/rpc-project/test.php&className=Test&methodName=start&version=0.1");
	   url.toAllString();
	   
	   String path ="phpFilePath=/usr/local/rpc-project/test.php&className=Test&methodName=start&version=0.1";
	   
	   System.out.println(path.substring(("phpFilePath=").length(),path.indexOf("&")));
	}

}

