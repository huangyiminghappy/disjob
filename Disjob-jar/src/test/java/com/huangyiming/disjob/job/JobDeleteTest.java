package com.huangyiming.disjob.job;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;

import com.huangyiming.disjob.java.CuratorClientBuilder;

public class JobDeleteTest {

	public static void main(String[] args) throws Exception {
		CuratorFramework client = CuratorClientBuilder.getInstance().getCuratorFramework() ;
		Builder builder = CuratorFrameworkFactory.builder();
		String path = "/rpc" ;
		List<String> childPaths = client.getChildren().forPath(path);
		for(String group:childPaths){
			if(group.startsWith("springJobbatchAdd")){
				String jobPath = path + "/"+group;
				List<String> jobSize = client.getChildren().forPath(jobPath);
				for(String jobName:jobSize){
					String providers = jobPath + "/" + jobName + "/" +"providers" ;
					if(client.checkExists().forPath(providers)!=null){
						List<String> providerIps = client.getChildren().forPath(providers);
						if(providerIps.size()>0){
							client.delete().forPath(providers + "/" + providerIps.get(0));
						}
						client.delete().forPath(providers);
					}
					String jobNamePath = jobPath + "/" + jobName;
//					client.delete().forPath(jobNamePath);
				}
				client.delete().forPath(jobPath);
				System.err.println(group+";"+jobSize.size());
			}
		}
		System.out.println(childPaths.size());
	}
}
