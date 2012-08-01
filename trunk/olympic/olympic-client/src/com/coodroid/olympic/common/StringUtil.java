package com.coodroid.olympic.common;

/**
 * 用于处理字符串的类
 * @author JiangYinzhi
 *
 */
public class StringUtil {
	
	public static String formatJSON(String str){
		if("null".equals(str)){
			return null;
		}
		return str;
	}
}
