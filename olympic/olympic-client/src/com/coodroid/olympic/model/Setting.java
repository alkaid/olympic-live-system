package com.coodroid.olympic.model;

import android.content.Context;
import android.content.SharedPreferences;

public class Setting {
	private static final String SP_NAME="setting";
	private static final String FIELD_AUTOLOGIN="autoLogin";
	
	public static boolean isAutoLogin(Context context){
		SharedPreferences sp=context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		return sp.getBoolean(FIELD_AUTOLOGIN, true);
	}
}
