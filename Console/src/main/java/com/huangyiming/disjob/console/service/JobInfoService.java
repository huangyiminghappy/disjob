package com.huangyiming.disjob.console.service;

import java.util.List;

/**
 * <pre>
 * 
 *  File: ScheduleJobService.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  调度job的服务操作，提供添加、删除、修改、暂停、恢复、查找
 * 
 *  Revision History
 *
 *  Date：		2016年5月19日
 *  Author：		Disjob
 *
 * </pre> 
 */
public interface JobInfoService {
	/**
	 * 根据requestId干掉某次任务
	 * @param requestId
	 */
	List<String> KillTaskByRequestId(String requestId);
	
	
    List<String> restartJob(String groupName,String jobName);
	
}
