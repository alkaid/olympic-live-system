package com.coodroid.olympic.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Setting {
	private static final String SP_NAME="setting";
	private static final String FIELD_AUTOLOGIN="autoLogin";
	
	public static boolean isAutoLogin(Context context){
		SharedPreferences sp=context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		return sp.getBoolean(FIELD_AUTOLOGIN, true);
	}
	public static void setAutoLogin(Context context,boolean autoLogin){
		Editor e=context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit();
		e.putBoolean(FIELD_AUTOLOGIN, autoLogin).commit();
	}
}
