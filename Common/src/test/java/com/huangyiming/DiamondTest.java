package com.huangyiming;
//package com.huangyiming;
//
//import java.util.Properties;
//
//import com.taobao.diamond.manager.DiamondManager;
//import com.taobao.diamond.manager.impl.DefaultDiamondManager;
//import com.taobao.diamond.manager.impl.PropertiesListener;
//import com.taobao.diamond.utils.TimeUtils;
//
//public class DiamondTest {
//	
//	public static void main(String[] args) {
//		DiamondManager manager = new DefaultDiamondManager("DEFAULT_GROUP", "test", new PropertiesListener() {
//			
//			@Override
//			public void innerReceive(Properties properties) {
//				System.out.println(TimeUtils.getCurrentTime());
//				System.out.println(properties);
//				System.out.println(properties.getProperty("kjasdf"));
//				System.out.println(properties.getProperty("kjasdf=kkk"));
//				System.out.println("========================");
//			}
//		});
//	}
//}