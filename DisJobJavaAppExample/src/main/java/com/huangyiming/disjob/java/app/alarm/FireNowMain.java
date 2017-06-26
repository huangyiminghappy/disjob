package com.huangyiming.disjob.java.app.alarm;

import com.huangyiming.disjob.java.DisJobBootstrap;
import com.huangyiming.disjob.java.DisJobConstants;

public class FireNowMain {

	public static void main(String[] args) {
		String path = "F:/project/DisJobJavaApp/DisJobJavaApp/src/main/resources/META-INF/disJob.properties";
		new DisJobBootstrap().startUpDisJob(DisJobConstants.StartUpType.JAVA_APPLICATION, path);
	}
}
