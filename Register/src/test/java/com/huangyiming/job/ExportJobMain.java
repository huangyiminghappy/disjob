package com.huangyiming.job;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;

import com.huangyiming.disjob.common.Constants;
import com.huangyiming.disjob.register.auth.AuthConstants;
import com.huangyiming.disjob.register.repository.ZnodeApi;
import com.huangyiming.disjob.register.repository.ZnodeApiCuratorImpl;
import com.google.common.collect.Lists;


/**
 * 升级旧的job为数据绑定的方式整合
 * 前置条件是, setting.ini中的任务组需要有绑定的会话, 否则会导致任务的数据节点被删除, 导致无法调度
 * 总体策略是删除provider下所有的ip节点, 
 * 然后根据任务组绑定的会话 所关联的ip地址, 在provider下创建新的ip节点, 节点数据保留原数据信息, 替换其中的ip地址
 * 这种替换provider下ip信息的修改算是最小的
 * 修改zk的rpc中provider节点为永久节点, 仅针对只有一个provider的情况
 * @author chengangxiong
 *
 */
public class ExportJobMain {

	private static final String startTitle = "[";
	private static final String endTitle = "]";
	private static final String connectString = "=";
	
	private static final String _group = "group";
	private static final String _service = "service";
	private static final String _path = "path";
	private static final String _clazz = "class";
	private static final String _method = "method";
	private static final String _cron = "cron";
	private static final String _version = "version";
	
	private CuratorFramework client;
	
	public static void main(String[] args){
		
		String zkConnectString = "localhost";
//		String zkConnectString = "10.4.4.10:2181";
		ExportJobMain main = new ExportJobMain(zkConnectString);
		main.exportJob();
		/*try {
			main.export(file_path);
		} catch (Exception e) {
			System.err.println(" 发布任务时出现异常 : " + e);
		}*/
	}

	public ExportJobMain(String zkConnectString){
		Builder builder = CuratorFrameworkFactory.builder();
		builder.connectString(zkConnectString).retryPolicy(new ExponentialBackoffRetry(1500, 3));
		builder.authorization(Lists.newArrayList(AuthConstants.defaultAdminAuthInfo));
		client = builder.build();
		client.start();
	}
	
	private void export(String file_path) {
		/*File file = new File(file_path);
		if(file.exists()){
			if(file.isDirectory()){
				throw new RuntimeException("不支持读文件夹:" + file_path);
			}else{
			}
		}else {
			throw new RuntimeException("文件路径不存在:" + file_path);
		}*/
		readFile(null);
		
	}

	private void readFile(File file) {
		/*FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
			String line;
			while((line = reader.readLine()) != null){
			}
		} catch (IOException e) {
			throw new RuntimeException("I/O异常");
		}*/
		readLine("");
	}

	String group = null;
	String service = null;
	String path = null;
	String clazz = null;
	String method = null;
	String version = null;
	String cron = null;
	private void readLine(String line) {
		/*if(line.startsWith(startTitle) && line.endsWith(endTitle)){
			return;
		}
		if (line.contains(connectString)) {
			String[] property = line.split(connectString);
			if(property.length != 2){
				System.err.println("行数据[" + line + "]不合法");
			}
			String propertyKey = property[0].trim();
			String propertyValue = property[1].trim();
			
			switch (propertyKey) {
			case _group:
				group = propertyValue;
				break;
			case _service:
				service = propertyValue;
				break;
			case _path:
				path = propertyValue;
				break;
			case _clazz:
				clazz = propertyValue;
				break;
			case _method:
				method = propertyValue;
				break;
			case _version:
				version = propertyValue;
				break;
			case _cron:
				cron = propertyValue;
				break;
			default:
				System.err.println("无法识别的属性 : " + line);
				break;
			}
		}
		if(version != null){
			group=null;service=null;path=null;clazz=null;method=null;cron=null;version=null;
		}*/
		exportJob();
	}

	public static final String providerPathFormat = ZKPaths.makePath(Constants.ROOT, Constants.DISJOB_RPC_NODE_ROOT, "%s", "%s", Constants.DISJOB_PROVIDERS); 
	public static final String dataFormatCommon = "disJob://%s/%s?serverGroup=%s&phpFilePath=%s&className=%s&methodName=%s&version=1";
//	public static final String dataFormat = "disJob://%s/%s?serverGroup=%s&phpFilePath=%s&className=%s&methodName=%s&cron=%s&version=1";
	private void exportJob() {
		
		String groupName0 = "pms";
		List<String> jobNames;
		try {
			jobNames = client.getChildren().forPath("/disJob/rpc/" + groupName0);
			for(String jobName0 : jobNames){
				ZnodeApi znodeApi = new ZnodeApiCuratorImpl();
				String groupName = groupName0;
				String jobName = jobName0;
				String providerPath = String.format(providerPathFormat, groupName, jobName);
				List<String> ips = znodeApi.getChildren(client, providerPath);
				System.err.println(ips.toString());
				if(!ips.isEmpty()){
					String ip = ips.get(0);
					String ipPath = ZKPaths.makePath(providerPath, ip);
					String rpcData = znodeApi.getData(client, ipPath);
					System.err.println("path " + ZKPaths.makePath(providerPath, ip) + " | " + "rpcData " + rpcData);
					znodeApi.deleteByZnode(client, ZKPaths.makePath(providerPath, ip));
					String newIpPath = ZKPaths.makePath(providerPath, ip);
					znodeApi.createPersistent(client, newIpPath, rpcData);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//disJob://10.40.6.89:9501/atest11?serverGroup=oms-1&phpFilePath=/usr/local/php-test/TestService.php&className=TestService&cron=0/5 * * * * ?&methodName=test1&version=1
	private List<String> getHostByGroupName(CuratorFramework client, String groupName) {
		List<String> list = Lists.newArrayList();
		try {
			List<String> sessionNames = client.getChildren().forPath("/disJob/publish");
			for(String sessionName : sessionNames){
				List<String> jobGroupNames = client.getChildren().forPath(ZKPaths.makePath("/disJob/publish", sessionName));
				for(String jobGroupName : jobGroupNames){
					if(groupName.equals(jobGroupName)){
						list.addAll(client.getChildren().forPath(ZKPaths.makePath("/disJob/session/", sessionName)));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}
