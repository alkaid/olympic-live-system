package com.coodroid.olympic.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.coodroid.olympic.common.AESString;
import com.coodroid.olympic.common.Constants;
import com.coodroid.olympic.common.Global;
import com.coodroid.olympic.common.HttpRequest;
import com.coodroid.olympic.common.LogUtil;

public class User extends Model {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5872913059470220638L;
	
	public static class login{
		public static final String POST_FIELD_UID="uid";
		public static final String POST_FIELD_PASSWORD="password";
		public static final String msg_failed="登录失败";
		public static final String msg_logged="您已经是登录状态,要切换账号请先注销。";
		public static final String msg_success="登录成功";
		public static final String msg_net_error="网络异常,请检查您的网络设置稍后重试";
	}
	/** 登录失败 */
	public static final int status_failed=-1;
	/** 已经是登录状态。登录*/
	public static final int status_logged=0;
	/** 注册成功*/
	public static final int status_success=1;
	/** 网络错误*/
	public static final int status_net_error=-2;
	
	private static final String SP_NAME="user";
	private static final String FIELD_EMAIL="ileea";
	private static final String FIELD_UNICK="kcnw";
	private static final String FIELD_PASSWORD="dcne";
	
	private int id;
	private String email;
	private String unick;
	private String password;
	private int questionScore;
	private int state;
	
	public User(){}
	
	public User(String email,String unick,String password){
		this.email=email;
		this.unick=unick;
		this.password=password;
	}
	
	public User(int id){
		this.id = id;
	}
	
	public void save(Context context){
		Editor et=context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit();
		User user=new User(encrypt(email), encrypt(unick), encrypt(password));
		if(null!=user.getEmail()&&null!=user.getPassword())
		et.putString(FIELD_EMAIL, user.getEmail())
			.putString(FIELD_UNICK, user.getUnick())
			.putString(FIELD_PASSWORD, user.getPassword())
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
	
	private static void clearUser(Context context){
		SharedPreferences sp=context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		sp.edit().clear().commit();
	}
	
	public static void logout(Context context){
		Global global=Global.getGlobal(context, false);
		global.user=null;
		global.cookieStore=null;
		clearUser(context);
	}
	/**
	 * 用户改变时调用该方法，若user为null则表示登录失败
	 * @param user
	 * @param context
	 */
	public static void onChange(User user,Context context){
		Global global=Global.getGlobal(context, false);
		global.user=user;
		if(null!=user&&Setting.isAutoLogin(context)){
			user.save(context);
		}
	}
	/**
	 * 用户登录成功时调用该方法，user不能为null
	 * @param user
	 * @param context
	 * @param cookieStore
	 */
	public static void onChange(User user,Context context,CookieStore cookieStore){
		Global global=Global.getGlobal(context, false);
		global.cookieStore=cookieStore;
		global.user=user;
		if(Setting.isAutoLogin(context)){
			user.save(context);
		}
	}
	
	private static String encrypt(String target){
			try {
				return AESString.encrypt(target, Constants.PWD);
			} catch (Exception e) {
				LogUtil.e(e);
				return null;
			}
	}
	private static String decode(String target){
		if(null==target)
			return null;
		try {
			return AESString.decrypt(target, Constants.PWD);
		} catch (Exception e) {
			LogUtil.e(e);
			return null;
		}
	}
	/**
	 * 自动登录
	 * @param context
	 */
	public static void autoLogin(Context context){
		if(!Setting.isAutoLogin(context)){
			return;
		}
		User user=readUser(context);
		if(null==user){
			return;
		}
		Map<String, String> reqParams=new HashMap<String, String>();
		reqParams.put(login.POST_FIELD_UID, user.email);
		reqParams.put(login.POST_FIELD_PASSWORD, user.password);
		HttpRequest req=new HttpRequest();
		String resultJson =null;
		CookieStore cookieStore=null;
		try {
			resultJson=req
					.setConnectionTimeout(3000)
					.setRetryCount(0)
					.setCharset("utf-8")
					.setUrl(Constants.url.user.login)
					.setMethod(HttpRequest.METHOD_POST)
					.setParams(reqParams)
					.getContent();
		} catch (ClientProtocolException e) {
			LogUtil.e(e);
		} catch (IOException e) {
			LogUtil.e(e);
		}
		if(null!=resultJson)
			cookieStore=req.getClient().getCookieStore();
		req.destroy();
		int status=User.status_net_error;
		JSONObject loginJson = null;
		if(!TextUtils.isEmpty(resultJson)){
			try {
				loginJson = new JSONObject(resultJson);
				status=Integer.valueOf(loginJson.getString("status"));
			//服务端内部错误时可能返回出错信息，此时不是Json格式，解析会异常
			} catch (JSONException e) {
				LogUtil.e(e);
			}
		}
		switch (status) {
		case User.status_net_error:
//			Toast.makeText(context, User.login.msg_net_error, Toast.LENGTH_LONG).show();
			break;
		case User.status_logged:
//			Toast.makeText(context, User.login.msg_logged, Toast.LENGTH_LONG).show();
			break;
		case User.status_failed:
//			Toast.makeText(context, User.login.msg_failed, Toast.LENGTH_LONG).show();
			break;
		case User.status_success:
//			Toast.makeText(context, User.login.msg_success, Toast.LENGTH_LONG).show();
			JSONObject userJson;
			try {
				userJson=loginJson.getJSONObject("user");
				user.setEmail(userJson.getString("email"));
				user.setUnick(userJson.getString("unick"));
				//user对象发生改变 保存
				User.onChange(user, context,cookieStore);
			} catch (JSONException e) {
				LogUtil.e(e);
//				Toast.makeText(context, User.login.msg_net_error, Toast.LENGTH_LONG).show();
			}
			break;
		default:
			break;
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

	public int getQuestionScore() {
		return questionScore;
	}

	public void setQuestionScore(int questionScore) {
		this.questionScore = questionScore;
	}

	public int getId() {
		return id;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
	
}
