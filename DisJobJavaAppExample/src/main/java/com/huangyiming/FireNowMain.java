package com.huangyiming;

import com.huangyiming.disjob.java.DisJobBootstrap;
import com.huangyiming.disjob.java.DisJobConstants;

public class FireNowMain {

	public static void main(String[] args) {
		String path = "E:/__work_space_middleware/EJob1.0Fixed/DisJobJavaApp/src/main/resources/META-INF/disJob.properties";
		new DisJobBootstrap().startUpDisJob(DisJobConstants.StartUpType.JAVA_APPLICATION, path);
	}
}
