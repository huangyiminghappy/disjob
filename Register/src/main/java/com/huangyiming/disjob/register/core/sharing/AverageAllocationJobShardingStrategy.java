package com.huangyiming.disjob.register.core.sharing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.collections4.CollectionUtils;

import com.huangyiming.disjob.common.util.DeepCopy;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.rpc.client.HURL;

/**
 * 基于平均分配算法的分片策略.
 * 
 * <p>
 * 如果分片不能整除, 则不能整除的多余分片将依次追加到序号小的服务器.
 * </p>
 * 
 * @author Disjob
 */
public final class AverageAllocationJobShardingStrategy implements JobShardingStrategy {
 	@Override
	public Map<HURL, List<String>> sharding(List<HURL> serversList,JobShardingStrategyOption option) {

		if (serversList.isEmpty()) {
			return Collections.emptyMap();
		}
		JobShardingStrategyOption oldOption = option;
		try {
			oldOption = (JobShardingStrategyOption) DeepCopy.copy(option);
		} catch (Exception e) {
			LoggerUtil.error("deep copy JobShardingStrategyOption object error");
		}

		Map<HURL, List<String>> tmpResult = shardingAliquot(serversList, option);
		Map<HURL, List<String>> result = new HashMap<HURL, List<String>>();
		if (tmpResult != null && tmpResult.size() > 0) {
			for (Map.Entry<HURL, List<String>> entry : tmpResult.entrySet()) {
				HURL key = entry.getKey();
				List<String> value = new ArrayList<String>();
				List<String> tmpvalue = entry.getValue();
				if (CollectionUtils.isNotEmpty(tmpvalue)) {
					for (String str : tmpvalue) {
						value.add(oldOption.getShardingItemParameters().get(str));
					}
				}
				result.put(key, value);
			}
		}
		if(option.getParamList().size() >0){
			int i = 0;
			List<String> paramList = option.getParamList();
			int size = serversList.size();
			
			// 循环平均匹配
			for (String param : paramList) {
				//如果只有一个参数但是有多个hurl的时候则随机取一个值得到hurl
				if(paramList.size() ==1){
 					int index = new Random().nextInt(size);
					HURL hurl = serversList.get(index);
					buildResultByHurl(result, param, hurl);
				}else{
					i++;
					int tmp = i % size;
					HURL hurl = serversList.get(tmp);
					buildResultByHurl(result, param, hurl);
				 
			 }
 				//buildResult(result, entry.getKey(), serversList.get(tmp));
			}
		}
		
		return result;
	}

	private void buildResultByHurl(Map<HURL, List<String>> result, String param, HURL hurl) {
		List<String> list = result.get(hurl);
		if(result.containsKey(hurl)){
			if(!list.contains(param)){
				list.add(param);
			}
		}else{
			if(list == null){
				list = new ArrayList<String>();
			}
			list.add(param);
			result.put(hurl, list);
		}
	}

	private Map<HURL, List<String>> shardingAliquot(final List<HURL> serversList, JobShardingStrategyOption option) {
		Map<HURL, List<String>> result = new LinkedHashMap<HURL, List<String>>(serversList.size());
		Map<String, String> shardingItemParameters = option.getShardingItemParameters();
		Map<String, HURL> hurlMap = hurlList2Map(serversList);//host -> hurl
		// IP匹配,result-><hurl,ipList>
		//ip->paramlist 转化为hurl->paramlist
		mapperIP(result, shardingItemParameters, hurlMap);
		int i = 0;
		int size = serversList.size();
		// 循环平均匹配
		for (Map.Entry<String, String> entry : shardingItemParameters.entrySet()) {
			i++;
			int tmp = i % size;
			buildResult(result, entry.getKey(), serversList.get(tmp));
		}
		
		return result;
	}

	/**
	 * 如果分片项中存在ip地址且与可用机器ip匹配则该分片对应的参数应该分给该机器
	 * 
	 * @param result
	 * @param shardingItemParameters   
	 * @param hurlMap   
	 */
	private void mapperIP(Map<HURL, List<String>> result,Map<String, String> shardingItemParameters,Map<String, HURL> hurlMap) {
		if (shardingItemParameters != null && shardingItemParameters.size() > 0) {
			Iterator<String> item = shardingItemParameters.keySet().iterator();
			while (item.hasNext()) {
				String key = item.next();
				if (hurlMap.containsKey(key)) {
					HURL url = hurlMap.get(key);
					buildResult(result, key, url);
					// 匹配过的项移除
					item.remove();
				}
			}
		}
	}

	/**
	 * 
	 * @param result
	 * @param key
	 * @param url
	 */
	private void buildResult(Map<HURL, List<String>> result, String key,HURL url) {
		if (!result.containsKey(url)) {
			List<String> list = new ArrayList<String>();
			if(!list.contains(key)){
 				list.add(key);
			}
			result.put(url, list);
		} else {
			List<String> list = result.get(url);
			list.add(key);
		}
	}

	/**
	 * urlKey与HURL对应关系map
	 * 
	 * @param serversList
	 * @return
	 */
	private Map<String, HURL> hurlList2Map(List<HURL> serversList) {
		Map<String, HURL> result = new HashMap<String, HURL>();
		for (HURL hurl : serversList) {
			result.put(hurl.getHurlKey(), hurl);
		}
		return result;
	}

	public static void main(String[] args) {
		 
		String jobName = "test";
		Map<String, String> shardingItemParameters = new HashMap<String, String>();
		shardingItemParameters.put("192.168.1.1","name=111&ip=192.168.1.1&age=13");
		shardingItemParameters.put("123", "name=222&ip=192.168.2.1&age=13");
		shardingItemParameters.put("456", "name=333&ip=192.168.2.1&age=13");
		shardingItemParameters.put("79796416", "name=444&ip=192.168.3.1&age=13");
		shardingItemParameters.put("192.168.1.1", "name=999&ip=192.168.2.1&age=13");
		shardingItemParameters.put("456", "name=888&ip=192.168.2.1&age=13");

		//JobShardingStrategyOption option = new JobShardingStrategyOption(jobName, shardingItemParameters,new ArrayList<String>());
		List<String> list1 = new ArrayList<String>();
		list1.add("name=111&ip=192.168.1.1&age=13");
		list1.add("name=222&ip=192.168.1.1&age=13");
		list1.add("name=333&ip=192.168.1.1&age=13");
		list1.add("name=444&ip=192.168.1.1&age=13");
		JobShardingStrategyOption option = new JobShardingStrategyOption(jobName, shardingItemParameters,list1);
		AverageAllocationJobShardingStrategy strategy = new AverageAllocationJobShardingStrategy();

		List<HURL> serversList = new ArrayList<HURL>();
		serversList.add(new HURL("", "", "192.168.1.1", 8080, ""));
		serversList.add(new HURL("", "", "192.168.1.2", 8080, ""));
		serversList.add(new HURL("", "", "192.168.1.3", 8080, ""));

		Map<HURL, List<String>> map = strategy.sharding(serversList, option);
		for (Map.Entry<HURL, List<String>> entry : map.entrySet()) {
			System.out.println("====");
			System.out.println(entry.getKey());
			List<String> list = entry.getValue();
			for (String str : list) {
				System.out.println("str:" + str);
			}
		}
	}

}
