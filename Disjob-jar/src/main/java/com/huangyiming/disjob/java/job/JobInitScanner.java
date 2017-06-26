package com.huangyiming.disjob.java.job;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.huangyiming.disjob.java.ProviderClassName;
import com.huangyiming.disjob.java.service.DisJobConfigService;
import com.huangyiming.disjob.java.service.JobService;
import com.huangyiming.disjob.java.utils.ClasspathPackageScanner;
import com.huangyiming.disjob.java.utils.Log;
import com.huangyiming.disjob.java.utils.TimeUtils;

/**
 * 如果不是spring 引用。那么注册job 的方式以注解的形式来注册
 * @author Disjob
 *
 */
public class JobInitScanner implements ProviderClassName,Runnable{

	public static final String nameSpace = "disJob" ;
	
	public JobInitScanner() {
		Log.info("开启zk 客户端:at time:"+TimeUtils.getFormatNow());
		Log.debug(getClassName()+" starting scanner all of the job.");
	}
	private void init(){
		LinkedList<String> classNames = scan(DisJobConfigService.getJobPackages()) ;
		if(JobService.initJob(classNames)){
			Log.debug(getClassName()+" init job success");
		}else{
			Log.error(getClassName()+" ; init disJob fail");
			throw new RuntimeException("init disJob fail.");
		}
	}
	
	public LinkedList<String> scan(String... packages) {
		Log.debug("start to scaner these packages:"+packages);
		if (packages == null || packages.length <= 0) {
			return new LinkedList<String>();
		}

		LinkedList<String> classNames = new LinkedList<String>();

		try {
			for (String pack : packages) {
				List<String> cns = new ClasspathPackageScanner(pack).getClassNameList(); 
				if(cns == null||cns.isEmpty()){
					continue;
				}
				classNames.addAll(cns);
				Log.debug("package:"+pack+" has command size:"+cns.size());
			}
		} catch (IOException e) {
			Log.error(getClassName(), e);
		}

		return classNames;
	}
	
	public void run() {
		this.init();
	}

	public String getClassName() {
		return this.getClass().getName();
	}
}
