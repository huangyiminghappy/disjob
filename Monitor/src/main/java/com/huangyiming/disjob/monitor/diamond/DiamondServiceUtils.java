package com.huangyiming.disjob.monitor.diamond;
/*package com.huangyiming.disjob.monitor.diamond;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;

import com.huangyiming.disjob.monitor.db.util.DBCommonUtil;
import com.huangyiming.disjob.monitor.db.util.NamedThreadFactory;
import com.taobao.diamond.manager.impl.DefaultDiamondManager;
import com.taobao.diamond.manager.impl.PropertiesListener;

*//**
 * <pre>
 * 
 *  File: DiamondServiceUtils.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  实现diamond的监听初始化、更新的属性合并写文件
 * 
 *  Revision History
 *
 *  Date：		2016年8月16日
 *  Author：		Disjob
 *
 * </pre>
 *//*
public class DiamondServiceUtils {
	存储监听的diamond组/dataId对应的属性信息
	private static Map<String,Properties> propers;
	private static ExecutorService pool;//线程池执行
	private static String proPath;//路径
	private static String suffix = ".properties";//配置文件后缀
	private static String comma = ",";//dataId列表的分隔符

	static{
		propers = new ConcurrentHashMap<>();
		pool = Executors.newSingleThreadExecutor(new NamedThreadFactory("Diamond-listener"));
	}
	*//**初始化diamond的监听
	 * @param filePath  写入更新的配置文件路径
	 * @param groups  监听组名称
	 * @param dataIds 监听的diamond中组对应的dataId的列表，以“,”逗号进行分割
	 *//*
	public static void init(String filePath,String groups,String dataIds){
		if(StringUtils.isEmpty(filePath) || StringUtils.isEmpty(groups) || StringUtils.isEmpty(dataIds)){
			DBCommonUtil.logError(PropertyRWUtils.class, new Exception("diamond init error: filePath or groups or dataIds is null!"));
			return;
		}
		proPath = filePath+"/";
		for(String dataId : dataIds.split(comma)){
			new DefaultDiamondManager(groups, dataId, new CustomPropertiesListener(dataId));
			//manager.getPropertiesConfigureInfomation(10);//强制读取
		}
	}
	*//**
	 * <pre>
	 * 
	 *  File: DiamondServiceUtils.java
	 * 
	 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
	 * 
	 *  Description:
	 *  自定义监听器类：
	 *  	1，存储监听的dataID
	 * 		2，对更新后的配置属性和当前保存的属性进行合并
	 * 		3，启动线程进行配置文件更新操作
	 * 
	 *  Revision History
	 *
	 *  Date：		2016年8月16日
	 *  Author：		Disjob
	 *
	 * </pre>
	 *//*
	static class CustomPropertiesListener extends PropertiesListener implements Runnable{
		private String DataId;
		public CustomPropertiesListener(String dataId){
			DataId = dataId;
		}
		@Override
		public void innerReceive(Properties arg0) {
			DBCommonUtil.logInfo("[diamond property updating ] params[ "+proPath+DataId+suffix+", Update coming soon! ]");
			if(!propers.containsKey(DataId)){//没有存在则添加
				propers.put(DataId, new Properties());
			}
			propers.get(DataId).putAll(arg0);//属性合并
			pool.execute(this);
		}
		@Override
		public void run() {
			try{
				analyse(DataId);
				DBCommonUtil.logInfo("[diamond property updating ] params[ "+proPath+DataId+suffix+", Update complete! ]");
			}catch(Throwable e){}
		}
	}
	共享分析属性的方法，写入到相应的配置文件中
	static void analyse(String dataId){
		Properties pro = propers.get(dataId);
		for(Object key:pro.keySet()){
			//System.out.println(key+"="+pro.getProperty((String) key));
			PropertyRWUtils.writeOrUpdateKey(proPath+dataId+suffix, (String)key, pro.getProperty((String) key));
		}
	}
	//创建文件，如果目录不存在则创建，文件不存在则创建，异常则失败
	static boolean createFile(String filePath){
    	if(filePath == null)
    		return false;
    	File file = new File(filePath);
        if(!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        if(file.exists())
        	return true;
        try {
			return file.createNewFile();
		} catch (IOException e) {
			DBCommonUtil.logError(PropertyRWUtils.class, e);
			return false;
		}
    }

	public static void main(String[] args) {
		DiamondServiceUtils.init("d:/test/", "disJob", "jdbc,monitor-conf,quartz-conf,zoopkeeper-conf");
	}
}*/