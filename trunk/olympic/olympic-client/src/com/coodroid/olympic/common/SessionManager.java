/**
 * 
 */
package com.coodroid.olympic.common;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author Alkaid
 *
 */
public class SessionManager {
	public static final String ID_KEY="PHPSESSIONID";
	public static final String SP_NAME="session";
	/**
	 * 获得服务端返回的sessionId
	 * @param response
	 * @return
	 */
	public static String getRemoteSessionId(HttpResponse response){
		Header[] headers = response.getHeaders(HttpUtils.HEADER_SET_COOKIE);
		for(Header header:headers){
			String value=header.getValue();
			if(null!=value && value.contains(ID_KEY)){
				return value.split("=")[1].trim();
			}
		}
		return null;
	}
	/**
	 * 从本地SharedPreferences获得sessionId
	 * @param context
	 * @return
	 */
	public static String getLocalSessionId(Context context){
		SharedPreferences sp=context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		return sp.getString(ID_KEY, null);
	}
	/**
	 * 写入sessionId
	 * @param context
	 * @param sessionId
	 */
	public static void setLocalSessionId(Context context,String sessionId){
		SharedPreferences sp=context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		sp.edit().putString(ID_KEY, sessionId);
	}
}
