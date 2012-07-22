package com.coodroid.olympic.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.CookieStore;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;

import com.coodroid.olympic.model.User;

/**
 * @author Alkaid
 *
 */

public class Global extends Application{
	/** 全局数据 类似session*/
	private Map<String, Object> session=new HashMap<String, Object>();
	/** 屏幕相关参数*/
	public DisplayMetrics dm ;
	private static boolean inited=false;
	public CookieStore cookieStore=null;
	public User user=null;
	
	/**
	 * 获得全局单例  若没有初始化过Global则默认会初始化
	 * @param context
	 * @return
	 */
	public static Global getGlobal(Context context){
		Global global=(Global)context.getApplicationContext();
		if(!inited){
			initApp((Activity) context);
		}
		return global;
	}
	/**
	 * 获得全局单例  根据needInitApp判断在没有初始化过Global的情况下是否要初始化
	 * @param context
	 * @param needInitApp 是否需要初始化Global
	 * @return
	 */
	public static Global getGlobal(Context context,boolean needInitApp){
		Global global=(Global)context.getApplicationContext();
		if(needInitApp&&!inited)
			initApp((Activity) context);
		return global;
	}

	/** 初始化应用*/
	public static void initApp(Activity context){
		inited=true;
		Global global=(Global)context.getApplicationContext();
		Display display = context.getWindowManager().getDefaultDisplay();
		DisplayMetrics dm=new DisplayMetrics();
		display.getMetrics(dm);
		global.setDm(dm);
//		global.setWidthRate(global.getWidth()/320.0f);
//		global.setHeightRate(global.getHeight()/480.0f);
	}
	
	public void putData(String key,Object data){
		session.put(key, data);
	}
	
	public Object getData(String key){
		Object m=session.get(key);
		session.remove(key);
		return m;
	}
	private void setDm(DisplayMetrics dm) {
		this.dm = dm;
	}
	
	
}
