package com.huangyiming.disjob.monitor.diamond;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import com.huangyiming.disjob.monitor.util.DBCommonUtil;


/**
 * <pre>
 * 
 *  File: PropertyRWUtils.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  property文件读写工具类
 * 
 *  Revision History
 *
 *  Date：		2016年8月16日
 *  Author：		Disjob
 *
 * </pre>
 */
public class PropertyRWUtils { 
	
	private static Properties props = new Properties();
	
    //创建文件，如果目录不存在则创建，文件不存在则创建，异常则失败
    private static boolean createFile(String filePath){
    	if(filePath == null)
    		return false;
    	File file = new File(filePath);
        if(!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        if(file.exists())
        	return true;
        try {
			return file.createNewFile();
		} catch (IOException e) {
			DBCommonUtil.logError(PropertyRWUtils.class, e);
			return false;
		}
    }

    /** 
    * 读取属性文件中相应键的值 
    * @param key 主键 
    * @return String 主键对应的值
    */ 
    public static String readKey(String filePath,String key) { 
    	props.clear();
    	try {
			props.load(new FileInputStream(filePath));
		} catch (FileNotFoundException e) {
			DBCommonUtil.logError(PropertyRWUtils.class, e);
			return null;
		} catch (IOException e) {
			DBCommonUtil.logError(PropertyRWUtils.class, e);
			return null;
		}
        return props.getProperty(key); 
    } 
    
    /** 
    * 更新properties文件的键值对 
    * 如果该主键已经存在，更新该主键的值； 
    * 如果该主键不存在，则插件一对键值。 
    * @param keyname 键名 
    * @param keyvalue 键值 
    */ 
    public static boolean writeOrUpdateKey(String filePath,String keyname,String keyvalue) { 
    	if(createFile(filePath)){
    		props.clear();
    		try {
    			props.load(new FileInputStream(filePath));
    		} catch (FileNotFoundException e) {
    			DBCommonUtil.logError(PropertyRWUtils.class, e);
    			return false;
    		} catch (IOException e) {
    			DBCommonUtil.logError(PropertyRWUtils.class, e);
    			return false;
    		}
    		try { 
	    		// 调用 Hashtable 的方法 put，使用 getProperty 方法提供并行性。 
	            // 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。 
	            OutputStream fos = new FileOutputStream(filePath); 
	            props.setProperty(keyname, keyvalue); 
	            // 以适合使用 load 方法加载到 Properties 表中的格式， 
	            // 将此 Properties 表中的属性列表（键和元素对）写入输出流 
	            props.store(fos, "Update '" + keyname + "' value");
    		} catch (IOException e) { 
    			DBCommonUtil.logError(PropertyRWUtils.class, e);
                return false;
            } 
    	}
    	return true;
    } 
    //测试代码 
    public static void main(String[] args) { 
        System.out.println("1  :  "+PropertyRWUtils.readKey("D:/bmail.properties","test"));
        PropertyRWUtils.writeOrUpdateKey("D:/bmail.properties","test", "test------7");
        System.out.println("1  :  "+PropertyRWUtils.readKey("D:/amail.properties","test"));
        PropertyRWUtils.writeOrUpdateKey("D:/amail.properties","test", "test------9");
        System.out.println("1  :  "+PropertyRWUtils.readKey("D:/cmail.properties","test"));
        System.out.println("操作完成"); 
    } 
} 