package com.huangyiming.disjob.slaver.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.huangyiming.disjob.common.Constants;
import com.huangyiming.disjob.register.core.sharing.JobShardingStrategyOption;
import com.huangyiming.disjob.rpc.client.HURL;

/**
 *分片参数转化为StrategyOption对象
 * @author Disjob
 *
 */
public class SharingItem2StrategyOption {

	/**
	 * 
	 * @param jobName job名字
	 * @param param
	 * @return
	 */
	public static JobShardingStrategyOption convert2StrategyOption(String jobName,String param,List<HURL> urlLst){
		if(!StringUtils.isNotEmpty(param)){
			return new JobShardingStrategyOption(jobName, new HashMap<String, String>(),new ArrayList<String>());
		}
		Map<String, String> shardingItemParameters = new HashMap<String, String>();
		List<String> paramList = new ArrayList<String>();
				
       // String param = "192.168.1.1:name=123&ip=192.168.1.1&age=13,123:name=123&ip=192.168.2.1&age=13,456:name=456&ip=192.168.2.1&age=13,79796416:name=456&ip=192.168.3.1&age=13";
        String[] array = param.split(",");
        if(array !=null && array.length >0){
        	int index = 0;
        	  for(String str:array){
              	 String[] array1 = str.split(":");
              	 if(array1 !=null && array1.length ==2){
               		 shardingItemParameters.put(array1[0], array1[1]);
              	 }else{
              		paramList.add(str);
              		  //2   4
              		/* if(urlLst.size() <=array.length && index< urlLst.size() ){
              			 
              			int temp = index % urlLst.size();
              			  
               			 shardingItemParameters.put(urlLst.get(temp).getHurlKey(), str);
              			 index++;
              			 
              		 }else if(urlLst.size() > array.length )
                     {
               			 shardingItemParameters.put(urlLst.get(index).getHurlKey(), str);
               			 index++;
              		 }*/
              		//urllist<str   2   4
              		//urllist>str   4   2
              		 
              	 }
              	 
               }
        }
       return new JobShardingStrategyOption(jobName, shardingItemParameters,paramList);
	}
}
