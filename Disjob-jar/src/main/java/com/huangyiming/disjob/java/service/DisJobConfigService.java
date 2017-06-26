package com.huangyiming.disjob.java.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.huangyiming.disjob.java.DisJobConstants;
import com.huangyiming.disjob.java.spring.bean.DisJobThreadPoolCfg;
import com.huangyiming.disjob.java.utils.FileUtils;
import com.huangyiming.disjob.java.utils.Log;
import com.huangyiming.disjob.java.utils.StringUtils;

/**
 * 统一参数接口
 * @author Disjob
 *
 */
public class DisJobConfigService {

	public static Properties configProperties = new Properties();
	
	/**
	 * default input path is properties file extension
	 * @param disJobConfigPath
	 */
	public static void init(String disJobConfigPath){
		if(StringUtils.isEmpty(disJobConfigPath)){
			Log.error("the location of web.xml config the [ disJobConfig ] parameter is null.");
			throw new IllegalArgumentException("the location of web.xml config the [ disJobConfig ] parameter is null.");
		}
		try {
			if(FileUtils.isPropertiesExtension(disJobConfigPath)){
				DisJobConfigService.init(new FileInputStream(disJobConfigPath));
			}else if(FileUtils.isXmlExtension(disJobConfigPath)){
				DisJobConfigService.initByXml(disJobConfigPath);
			}
		} catch (FileNotFoundException e) {
			Log.error("the path which "+disJobConfigPath+" is not fount.",e);
		} catch (Exception e) {
			Log.error("DisJob config init error", e);
		}
	}
	
	/**
	 * default input path is properties file extension
	 * @param is
	 * @throws Exception
	 */
	public static void init(InputStream is) throws Exception{
		if(is == null){
			Log.error("disJob init config properties is null,please check the disJob.properties location");
			throw new IllegalAccessException("disJob init config properties is null,please check the disJob.properties location");
		}
		
		try {
			configProperties.load(is);
		} catch (IOException e) {
			Log.error("load the disJob.properties is error.",e);
		}
	}
	
	/**
	 * if the input path is xml file then implements this method and put the key and value to configProperties field
	 * @param xmlConfigPath
	 */
	public static void initByXml(String xmlConfigPath){
		
	};
	
	public static String getZkHost(){
		String zkHost = "";
		if(configProperties != null){
			zkHost = configProperties.getProperty(DisJobConstants.Config.ZK_HOST);
			zkHost = (zkHost == null?"":zkHost);
		}
		
		if(StringUtils.isEmpty(zkHost)){
			throw new RuntimeException("the zkHost must set the value.");
		}
		
		return zkHost;
	}
	
	public static Integer getServerPort(){
		String port = "";
		if(configProperties != null){
			port = configProperties.getProperty(DisJobConstants.Config.SERVER_PORT);
			port = (StringUtils.isEmpty(port) ? "9501" : port);//set the default port
			port = port.trim();
			if(StringUtils.isNumeric(port)&&Integer.parseInt(port)<0){
				Log.error("the port must is a postive integer. port:"+port);
				throw new IllegalArgumentException("the port must is a postive integer. port:"+port);
			}
		}
		
		return Integer.parseInt(port) ;
	}
	
	public static String[] getJobPackages(){
		String[] packages = new String[0];
		if(null != configProperties && null != configProperties.getProperty(DisJobConstants.Config.JOB_PACKAGES)){
			
			packages = configProperties.getProperty(DisJobConstants.Config.JOB_PACKAGES).split("[;]");; 
		}
		return packages;
	}
	
	public static String getDynamicDir(){
		
		return configProperties.getProperty(DisJobConstants.Config.DYNAMIC_DIR);
	}
	
	public static short getStartupType(){
		String type = configProperties.getProperty(DisJobConstants.StartUpType.START_UP_TYPE);
		
		return type ==null ? 0 : Short.valueOf(type.trim());
	}
	
	public static DisJobThreadPoolCfg getDisJobCfg(){
		return (DisJobThreadPoolCfg) configProperties.get(DisJobConstants.Config.DISJOB_THREADPOOL_CFG);
	}
	
	public static String getClusterName(){
		
		String prefix = configProperties.getProperty(DisJobConstants.Config.DISJOB_CLUSTER_NAME);
		
		return StringUtils.isEmpty(prefix) ? "" : prefix.trim() ;
	}
	
	public static String getZKRootNode(){
		
		String zkRootNode = configProperties.getProperty(DisJobConstants.Config.ZK_ROOT_NODE);
		
		return StringUtils.isEmpty(zkRootNode) ? "disJob" : zkRootNode.trim() ;
	}
	
	public static void destory(){
		
		configProperties.clear();
	}
	
	/**
	 * 
	 * @param times 处理器个数的倍数
	 * @return
	 */
	public static int getCorePoolSize(int times){
		String coreSizeStr = configProperties.getProperty(DisJobConstants.Config.THREAD_COREPOOL_SIZE);
		if(StringUtils.isEmpty(coreSizeStr)){
			int coreSize = times * Runtime.getRuntime().availableProcessors() + 1 ;
			return coreSize ;
		}
		
		if(!StringUtils.isNumeric(coreSizeStr.trim())){
			throw new RuntimeException(coreSizeStr+" must be a positive numeric .");
		}
		
		return Integer.parseInt(coreSizeStr.trim());
	}
	
	public static int getMaxPoolSize(int times){
		String maxPoolSizeStr = configProperties.getProperty(DisJobConstants.Config.THREAD_MAXPOOL_SIZE);
		if(StringUtils.isEmpty(maxPoolSizeStr)){
			times = times > 0 ? times : 2 ;
			int coreSize = times * Runtime.getRuntime().availableProcessors() + 1 ;
			return coreSize * ((times-1) / 2 + 1) ;
		}
		
		if(!StringUtils.isNumeric(maxPoolSizeStr.trim())){
			throw new RuntimeException(maxPoolSizeStr+" must be a positive numeric .");
		}
		
		return Integer.parseInt(maxPoolSizeStr.trim()) ;
	}
	
	/**
	 * @return 单位： 分钟(m)
	 */
	public static int getKeepAliveTime(){
		
		String keepAliveTimeStr = configProperties.getProperty(DisJobConstants.Config.THREAD_KEEPALIVE_TIME);
		if(StringUtils.isEmpty(keepAliveTimeStr)){
			return 2 ;
		}
		
		if(!StringUtils.isNumeric(keepAliveTimeStr)){
			throw new RuntimeException(keepAliveTimeStr+" must be a positive numeric .");
		}
		
		return Integer.parseInt(keepAliveTimeStr.trim()) ;
	}
}
