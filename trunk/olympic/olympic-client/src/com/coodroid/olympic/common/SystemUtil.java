package com.coodroid.olympic.common;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;

public class SystemUtil {

	/**
	 * 检查联网状态
	 * @param context
	 * @return
	 */
	public static boolean checkNet(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService("connectivity");
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (null == netInfo || !netInfo.isConnected())
			return false;
		return netInfo.getState() == NetworkInfo.State.CONNECTED;
	}
	/**
	 * 获得wifi物理地址
	 * @param context
	 * @return
	 */
	public static String getLocalMacAddress(Context context) {
		return ((WifiManager) context.getSystemService("wifi"))
				.getConnectionInfo().getMacAddress();
	}
	/** 获得手机imei地址*/
	public static String getImei(Context context){
		return ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
	}
	/**
	 * Reports the type of network (currently mobile or Wi-Fi) to which the info in this object pertains.
	 * @param context
	 * @return
	 */
	public static int getNetType(Context context){
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService("connectivity");
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (null == netInfo || !netInfo.isConnected())
			return -1;
		else if(netInfo.getState() != NetworkInfo.State.CONNECTED)
			return -1;
		return netInfo.getType();
	}
	/**
	 * 获得手机信息  包括手机型号 SDK版本号
	 * @return
	 */
	public static String getMobilePhoneInfo(){
		StringBuilder sb=new StringBuilder();
		//手机型号
		sb.append(Build.MODEL).append("  ").append(Build.VERSION.RELEASE);//SDK
		return sb.toString();
	}
	
	/**
	 * 获取当前手机的系统年月日
	 * 
	 */
	
	public static String getCurrentDate(){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}
	
	/**
	 * 获取当前手机的系统时分秒
	 */
	public static String getCurrentTime(){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return sdf.format(date);
	}

	/**
	 * 获得DisplayMetrics
	 * @param context
	 * @return
	 */
	public static DisplayMetrics getDm(Activity context){
		Display display = context.getWindowManager().getDefaultDisplay();
		DisplayMetrics dm=new DisplayMetrics();
		display.getMetrics(dm);
		return dm;
	}
}
