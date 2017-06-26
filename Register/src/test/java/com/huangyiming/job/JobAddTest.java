package com.huangyiming.job;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;

import com.huangyiming.disjob.register.auth.AuthConstants;
import com.google.common.collect.Lists;

public class JobAddTest {


		TestingServer server ;	
		String connectString;
		String data = "disJob://10.40.6.89:9501/%s?serverGroup=%s&phpFilePath=/usr/local/php-test/TestService.php&className=TestService&cron=%s&methodName=test1&version=1";
		String path = "/disJob/rpc/%s/%s/providers/10.40.6.89:9501";
		public static void main(String[] args){

			JobAddTest test = new JobAddTest();
			try {
				test.createServer();
				test.test();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private void test() throws Exception {
			Builder builder = CuratorFrameworkFactory.builder();//newClient(connectString, new ExponentialBackoffRetry(1500, 3));
			builder.connectString(connectString).retryPolicy(new ExponentialBackoffRetry(1500, 3));
			builder.authorization(Lists.newArrayList(AuthConstants.defaultAdminAuthInfo));
			CuratorFramework client = builder.build();
			client.start();
			
			
			
			String path0 = "";
			String data0 = "";
			
			String jobname = "atest11";
			String jobgroup = "oms-7";
			addJob(client, String.format(path, jobgroup, jobname), String.format(data, jobname, jobgroup, "0/5 * * * * ?"));
			jobname= "atest111";
			addJob(client, String.format(path, jobgroup, jobname), String.format(data, jobname, jobgroup, "0/5 * * * * ?"));
//			addJob(client, String.format(path, jobgroup, "job44"), String.format(data, "job44", jobgroup, "20 * * * * ?"));
			
			/*for(int i = 1; i < 10; i++){
				for(int j = 1 ; j < 10; j ++){
					path0 = String.format(path, "group_php5_" + i, "job_php5_" + j);
					data0 = String.format(data, "job_php5_" + j, "group_php5_" + i, "0 * * * * ?");
					addJob(client, path0, data0);
				}
			}*/
			System.in.read();
			
		}

		private void addJob(CuratorFramework client, String path, String data) throws Exception {
			String path0 = ZKPaths.getPathAndNode(path).getPath();
			
			client.create().creatingParentsIfNeeded().forPath(path0, new byte[0]);
			client.create().withMode(CreateMode.EPHEMERAL).forPath(path,data.getBytes());
		}
		
		private void createServer() throws Exception{
			server = new TestingServer();
			connectString = server.getConnectString();
			server.start();
			connectString = "localhost:2181,localhost:2182";
//			connectString = "10.40.6.100:2181,10.40.6.101:2181,10.40.6.102:2181";
			System.out.println(connectString);
		}

	
}
