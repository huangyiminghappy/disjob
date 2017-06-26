package com.huangyiming.disjob.common.util;

/**
 * <pre>
 * 
 *  File: StringHandleUtil.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  删除字符串前后空格，合并字符中间多个空格为一个
 * 
 *  Revision History
 *
 *  Date：		2016年6月20日
 *  Author：		Disjob
 *
 * </pre>
 */
public class StringHandleUtil {
	/**
	 * @param str 需要处理的字符串
	 * @return （采用变量替换方式,不-能-删除tab）删除前后空格、合并多余空格后的字符串
	 */
	public static String deleteExtraSpaceBasic(String str){
		if(null == str){
			return null;
		}
		if(0 == str.length() || str.equals(" ")){
			return new String();
		}
		char[] oldStr=str.toCharArray();
		int len=str.length();
		char[] tmpStr=new char[len];
		boolean keepSpace=false;
		int j=0;//the index of new string
		for(int i=0;i<len;i++){
			char tmpChar=oldStr[i];
			if(oldStr[i]!=' '){
				tmpStr[j++]=tmpChar;
				keepSpace=true;
			}else if(keepSpace){
				tmpStr[j++]=tmpChar;
				keepSpace=false;
			}
		}
		
		int newLen=j;
		if(tmpStr[j-1]==' '){
			newLen--;
		}
		char[] newStr=new char[newLen];
		for(int i=0;i<newLen;i++){
			newStr[i]=tmpStr[i];
		}
		return new String(newStr);
	}
	/**
	 * @param str 需要处理的字符串
	 * @return （采用正则表达式,能-删除tab）删除前后空格、合并多余空格后的字符串
	 */
	public static String deleteExtraSpaceRegular(String str){
		if(null == str){
			return null;
		}
		if(0 == str.length() || str.equals(" ")){
			return new String();
		}
		return str.trim().replaceAll("\\s{1,}", " ");
	}
	/**
	 * @param str 需要处理的字符串
	 * @return 验证字符串不是null、空格填充的字符串、tab填充的字符串
	 */
	public static boolean isNoneEmpty(String str){
		if(null == str)
			return false;
		if(0 == str.length()){
			return false;
		}
		return str.trim().replaceAll("\\s{1,}", "").length() > 0;//	^\\s*$
	}
	
	public static void main(String[] args) {
		System.out.println(isNoneEmpty(""));
		System.out.println(isNoneEmpty(" "));
		System.out.println(isNoneEmpty("   "));
		System.out.println(isNoneEmpty("	"));
		System.out.println(isNoneEmpty("		"));
		System.out.println(isNoneEmpty(null));
		System.out.println(isNoneEmpty("a	b"));
		System.out.println(isNoneEmpty("a b"));
	}
}
