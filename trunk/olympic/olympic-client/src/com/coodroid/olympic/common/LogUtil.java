package com.coodroid.olympic.common;

import android.util.Log;

/**
 * 用法与android.util.Log相同,是Log的包装类，仅是为了方便调试输出<br/>
 * 若不传入tag参数 则默认tag为"take"
 * @author lincong
 *
 */
public class LogUtil {
	private static final String TAG="alkaid";
	/** 是否允许输出debug日志 包括Log.d() Log.v() Log.i() Log.w()*/
	private static final boolean D=false;
	/** 是否允许输出error日志 包括Log.e()*/
	private static final boolean E=true;
	public static int v(String msg) {
        return D? Log.v(TAG, msg) : -1;
    }
	public int v(String msg,Throwable e){
		return D? Log.v(TAG,msg,e) : -1;
	}
	public static int v(String TAG,String msg) {
        return D? Log.v(TAG, msg) : -1;
    }
	public int v(String TAG,String msg,Throwable e){
		return D? Log.v(TAG,msg,e) : -1;
	}
	
	public static int d(String msg) {
		return D? Log.d(TAG, msg) : -1;
	}
	public static int d(String msg,Throwable e){
		return D? Log.d(TAG, msg,e) : -1;
	}
	public static int d(String TAG,String msg) {
		return D? Log.d(TAG, msg) : -1;
	}
	public static int d(String TAG,String msg,Throwable e){
		return D? Log.d(TAG, msg,e) : -1;
	}
	
	public static int i(String msg){
		return D? Log.i(TAG, msg) : -1;
	}
	public static int i(String msg,Throwable e){
		return D? Log.i(TAG, msg,e) : -1;
	}
	public static int i(String TAG,String msg){
		return D? Log.i(TAG, msg) : -1;
	}
	public static int i(String TAG,String msg,Throwable e){
		return D? Log.i(TAG, msg,e) : -1;
	}
	
	public static int w(String msg){
		return D? Log.w(TAG, msg) : -1;
	}
	public static int w(String msg,Throwable e){
		return D? Log.w(TAG, msg,e) : -1;
	}
	public static int w(String TAG,String msg){
		return D? Log.w(TAG, msg) : -1;
	}
	public static int w(String TAG,String msg,Throwable e){
		return D? Log.w(TAG, msg,e) : -1;
	}
	
	
	public static int e(String msg){
		return E? Log.e(TAG, msg) : -1;
	}
	public static int e(String msg,Throwable e){
		return E? Log.e(TAG, msg,e) : -1;
	}
	public static int e(String TAG,String msg){
		return E? Log.e(TAG, msg) : -1;
	}
	public static int e(String TAG,String msg,Throwable e){
		return E? Log.e(TAG, msg,e) : -1;
	}
	public static int e(Throwable e){
		return E? Log.e(TAG, e.getMessage(),e) : -1;
	}
	
	public static boolean isLoggable(int level){
		return Log.isLoggable(TAG, level);
	}
	public static String getStackTraceString(Throwable e){
		return Log.getStackTraceString(e);
	}
	public static int println(int priority, String tag, String msg){
		return Log.println(priority, tag, msg);
	}
}
