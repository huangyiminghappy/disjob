package com.huangyiming.disjob.monitor.diamond;
/*package com.huangyiming.disjob.monitor.diamond;
import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;

import com.huangyiming.disjob.monitor.db.util.DBCommonUtil;

*//**
 * <pre>
 * 
 *  File: DiamondServer.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  获取diamond配置信息，启动diamond监听服务
 * 
 *  Revision History
 *
 *  Date：		2016年8月16日
 *  Author：		Disjob
 *
 * </pre>
 *//*
@Service("diamondServer")
public class DiamondServer   {
 
 	private String filePath;
	
 	private String groups;
	
 	private String dataIds;
 	
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getGroups() {
		return groups;
	}
	public void setGroups(String groups) {
		this.groups = groups;
	}
	public String getDataIds() {
		return dataIds;
	}
	public void setDataIds(String dataIds) {
		this.dataIds = dataIds;
	}
*//**
    *随着spring容器启动UDP服务 
    *//*
 	@PostConstruct
	public void diamondStart(){
 		//System.out.println("---------diamondStart-----------------"+filePath+"|"+groups+"|"+dataIds);
 		if(StringUtils.isEmpty(filePath) || StringUtils.isEmpty(groups) || StringUtils.isEmpty(dataIds))
 			DBCommonUtil.logError(DiamondServer.class, new Exception("[diamond] 配置信息的 filePath || groups || dataIds 异常！"));
 		else{
 			DBCommonUtil.logInfo("[diamond service Start] params[ { "+filePath+" },{ "+groups+" },{ "+dataIds+" }]");
 	 		new Thread(new Runnable(){
 				@Override
 				public void run() {
 					DiamondServiceUtils.init(filePath, groups, dataIds);
 				}
 			}).start();
 		}
	}
 	public void t(){
 		System.out.println("---------t-----------------"+filePath+"|"+groups+"|"+dataIds);
 	}
 }*/