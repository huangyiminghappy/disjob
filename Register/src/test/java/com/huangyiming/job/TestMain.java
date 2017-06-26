package com.huangyiming.job;

import com.huangyiming.disjob.common.model.JobGroup;

public class TestMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		JobGroup jobGroup = new JobGroup("a");
		jobGroup.addBindSession("a1");
		jobGroup.addBindSession("a2");
		
		System.err.println(jobGroup.getBindSession());
	}

}
