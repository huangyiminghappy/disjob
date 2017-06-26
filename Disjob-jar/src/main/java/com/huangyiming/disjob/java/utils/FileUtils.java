package com.huangyiming.disjob.java.utils;

public final class FileUtils {

	private FileUtils(){}
	
	public static boolean isXmlExtension(String path){
		if(checkIsEmpty(path)){
			return false ;
		}
		
		if(path.endsWith(".xml")){
			
			return true;
		}
		
		return false;
	}
	
	public static boolean isPropertiesExtension(String path){
		if(checkIsEmpty(path)){
			return false ;
		}
		
		if(path.endsWith(".properties")){
			
			return true;
		}
		
		return false;
	}

	public static boolean isJarFile(String path) {
		if(checkIsEmpty(path)){
			return false ;
		}
		
		return path.endsWith(".jar");
	}
	
	private static boolean checkIsEmpty(String src){
		if(StringUtils.isEmpty(src)){
			return true;
		}
		return false ;
	}
}
