package com.coodroid.olympic.model;

import java.io.UnsupportedEncodingException;

import com.coodroid.olympic.common.AES;
import com.coodroid.olympic.common.Constants;
import com.coodroid.olympic.common.Global;
import com.coodroid.olympic.common.LogUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class User extends Model {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5872913059470220638L;
	private static final String SP_NAME="user";
	private static final String FIELD_EMAIL="ileea";
	private static final String FIELD_UNICK="kcnw";
	private static final String FIELD_PASSWORD="dcne";
	
	private String email;
	private String unick;
	private String password;
	
	public User(){}
	
	public User(String email,String unick,String password){
		this.email=email;
		this.unick=unick;
		this.password=password;
	}
	
	public void save(Context context){
		Editor et=context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit();
		et.putString(FIELD_EMAIL, encrypt(email))
			.putString(FIELD_UNICK, encrypt(unick))
			.putString(FIELD_PASSWORD, encrypt(password))
			.commit();
	}
	
	public static User readUser(Context context){
		SharedPreferences sp=context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		String email=decode(sp.getString(FIELD_EMAIL, null));
		String unick=decode(sp.getString(FIELD_UNICK, null));
		String password=decode(sp.getString(FIELD_PASSWORD, null));
		if(null!=email){
			return new User(email, unick, password);
		}
		return null;
	}
	
	public static void clearUser(Context context){
		SharedPreferences sp=context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		sp.edit().clear().commit();
	}
	
	public static void onChange(User user,Context context){
		Global global=Global.getGlobal(context, false);
		global.user=user;
		if(Setting.isAutoLogin(context)){
			user.save(context);
		}
	}
	
	private static String encrypt(String target){
		try {
			return new String(AES.encrypt(target.getBytes("utf-8"), Constants.PWD),"utf-8");
		} catch (UnsupportedEncodingException e) {
			LogUtil.e(e);
			return null;
		}
	}
	private static String decode(String target){
		if(null==target)
			return null;
		try {
			return new String((AES.decode(target.getBytes("utf-8"), Constants.PWD).getBytes()),"utf-8");
		} catch (UnsupportedEncodingException e) {
			LogUtil.e(e);
			return null;
		}
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUnick() {
		return unick;
	}

	public void setUnick(String unick) {
		this.unick = unick;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
