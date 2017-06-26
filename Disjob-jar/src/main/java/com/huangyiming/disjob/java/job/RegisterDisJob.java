package com.huangyiming.disjob.java.job;

import java.nio.charset.Charset;
import java.util.Date;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import com.huangyiming.disjob.java.CuratorClientBuilder;
import com.huangyiming.disjob.java.ProviderClassName;
import com.huangyiming.disjob.java.bean.JobInfo;
import com.huangyiming.disjob.java.service.DisJobConfigService;
import com.huangyiming.disjob.java.service.LocalHostService;
import com.huangyiming.disjob.java.utils.Log;

public class RegisterDisJob implements ProviderClassName{
	private JobInfo jobInfo;
	public RegisterDisJob(JobInfo jobInfo) {
		this.jobInfo = jobInfo;
	}
	
	public boolean createJob(){
		boolean flag = true ;
		try {
			this.createJob(jobInfo);
		} catch (Exception e) {
			Log.error(getClassName(),e);
			flag = false ;
		}
		return flag ;
	}
	
	private void createJob(JobInfo jobInfo) throws Exception{
		CuratorFramework client = CuratorClientBuilder.getInstance().getCuratorFramework() ;
		//1、添加一个新的Job
		String tmpPrefix = DisJobConfigService.getClusterName().length() <=0 ? "" : DisJobConfigService.getClusterName()+"_";
		String tmpGroup = tmpPrefix + jobInfo.getGroupName();
		String path = "/rpc/" + tmpGroup + "/" + jobInfo.getJobName() ;
		if(null == client.checkExists().forPath(path)){
			Log.debug("create the new job is :"+path);
			client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path,("a new job:"+new Date().toString()).getBytes(Charset.forName("utf-8")));
		}else{
			Log.debug(getClassName()+"已存在这个job, path is："+path);
		}
		
		String ip = LocalHostService.getIp();
		int port = DisJobConfigService.getServerPort();
		//2、创建 /disJob-dev/rpc/global/job/providers/ip:9501 这个数据节点，并写入需要调度的基本信息
		String ipNode = path +"/providers/"+ip+":"+port;
		if(null == client.checkExists().forPath(ipNode)){
			//3、disJob://10.40.6.89:9501/test?serverGroup=oms&phpFilePath=/usr/local/php-test/TestService.php&className=TestService&methodName=test&version=1
			StringBuffer sb = new StringBuffer();
			sb.append("disJob://");
			sb.append(ip+":"+DisJobConfigService.getServerPort()+"/");
			sb.append(jobInfo.getJobName()+"?");
			sb.append("serverGroup="+tmpGroup);
			sb.append("&");
			sb.append("phpFilePath=''");
			sb.append("&");
			sb.append("className="+jobInfo.getClassName());
			sb.append("&");
			sb.append("methodName=execute");
			sb.append("&");
			sb.append("cron="+jobInfo.getCron());
			sb.append("&");
			sb.append("fireNow="+String.valueOf(jobInfo.isfireNow()));
			sb.append("&");
			sb.append("version=1");
			String data = sb.toString();
			Log.debug("create the providers of the job [ "+jobInfo.getJobName()+" ] is:"+ipNode);
			client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(ipNode,data.getBytes(Charset.forName("utf-8")));
		}else{
			Log.debug(getClassName()+"已经存在该 providers 节点："+ipNode);
		}
	}

	public String getClassName() {
		return this.getClass().getName();
	}

	public JobInfo getJobInfo() {
		return jobInfo;
	}

	public void setJobInfo(JobInfo jobInfo) {
		this.jobInfo = jobInfo;
	}
}
