/**
 * 
 */
package com.coodroid.olympic.view.account;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.coodroid.olympic.R;
import com.coodroid.olympic.common.Constants;
import com.coodroid.olympic.common.Global;
import com.coodroid.olympic.common.HttpRequest;
import com.coodroid.olympic.common.LogUtil;
import com.coodroid.olympic.model.User;
import com.coodroid.olympic.ui.FormListView;
import com.coodroid.olympic.ui.FormListView.FormListAdapter.Item;
import com.coodroid.olympic.ui.FormListView.FormListAdapter.Validater;
import com.coodroid.olympic.view.OlympicClientActivity;

/**
 * 用户注册
 * @author Alkaid
 *
 */
public class LoginActivity extends Activity {
	Context context;
	FormListView formListView;
	Item uid=new Item("uid", "输入邮箱或昵称");
	Item password=new Item("password", "输入密码");
	/** 登录失败 */
	private static final int status_failed=-1;
	private static final String msg_failed="登录失败";
	/** 已经是登录状态。登录*/
	private static final int status_logged=0;
	private static final String msg_logged="您已经是登录状态,要切换账号请先注销。";
	/** 注册成功*/
	private static final int status_success=1;
	private static final String msg_success="登录成功";
	/** 网络错误*/
	private static final int status_net_error=-2;
	private static final String msg_net_error="网络异常,请检查您的网络设置稍后重试";
	
	private ProgressDialog pd;
	private CookieStore cookieStore;
	private Global global;
	private User user;
	private String from;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		context=this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		global=Global.getGlobal(this);
		from=getIntent().getStringExtra("from");
		initForm();
		
		//init Button
		TextView btnBack=(TextView) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		Button btnLogin=(Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(formListView.validate()){
					pd=ProgressDialog.show(context, null, "正在登录,请稍后...", true, false);
					new LoginTask().execute();
				}
			}
		});
	}

	/**
	 * 初始化表单
	 */
	private void initForm() {
		ViewGroup layForm=(ViewGroup) findViewById(R.id.layForm);
		List<Item> data=new ArrayList<Item>();
		uid.validater=new Validater() {
			@Override
			public boolean validate(String text) {
				this.warn="长度超过限制(1-32位字符)";
				if(text.length()<1||text.length()>32)
					return false;
				return true;
			}
		};
		password.validater=new Validater() {
			@Override
			public boolean validate(String text) {
				this.warn="密码长度应为6-12个字符";
				if(text.length()<6||text.length()>12)
					return false;
				return true;
			}
		};
		data.add(uid);
		data.add(password);
		formListView=new FormListView(context, data);
		layForm.addView(formListView,new LayoutParams(LayoutParams.FILL_PARENT,formListView.getViewHeight()));
	}
	
	private class LoginTask extends AsyncTask<Void, Void, String>{
		@Override
		protected String doInBackground(Void... params) {
			HttpRequest req=new HttpRequest();
			Map<String, String> reqParams=formListView.getFormBean();
			user=new User();
			user.setPassword( reqParams.get(password.tag));
			String json =null;
			try {
				json = req.setUrl(Constants.url.user.login)
					.setMethod(HttpRequest.METHOD_POST)
					.setParams(reqParams)
					.setCookieStore(global.cookieStore)
					.getContent();
			} catch (ClientProtocolException e) {
				LogUtil.e(e);
			} catch (IOException e) {
				LogUtil.e(e);
			}
			if(null!=json)
				cookieStore=req.getClient().getCookieStore();
			req.destroy();
			LogUtil.i(json);
			return json;
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(null!=pd) pd.dismiss();
			parseJson(result);
		}
	}
	
	private void parseJson(String jsonStr){
		int status=status_net_error;
		JSONObject loginJson = null;
		if(!TextUtils.isEmpty(jsonStr)){
			try {
				loginJson = new JSONObject(jsonStr);
				status=Integer.valueOf(loginJson.getString("status"));
			//服务端内部错误时可能返回出错信息，此时不是Json格式，解析会异常
			} catch (JSONException e) {
				LogUtil.e(e);
			}
		}
		switch (status) {
		case status_net_error:
			Toast.makeText(context, msg_net_error, Toast.LENGTH_LONG).show();
			break;
		case status_logged:
			Toast.makeText(context, msg_logged, Toast.LENGTH_LONG).show();
//			Intent intent=new Intent(context, OlympicClientActivity.class);
			Intent intent=null!=from?new Intent(from):new Intent(context, OlympicClientActivity.class);;
			startActivity(intent);
			finish();
			break;
		case status_failed:
			Toast.makeText(context, msg_failed, Toast.LENGTH_LONG).show();
			JSONObject errorJson;
			try {
				errorJson = loginJson.getJSONObject("error");
				Iterator<String> iterator = errorJson.keys();
				while(iterator.hasNext()){
					String tag=iterator.next();
					String warn=errorJson.getString(tag);
					formListView.setWarnInfo(tag, warn);
				}
				formListView.requestFocusOnError();
				global.cookieStore=null;
			//服务端内部错误时可能返回出错信息，此时不是Json格式，解析会异常
			} catch (JSONException e) {
				LogUtil.e(e);
				Toast.makeText(context, msg_net_error, Toast.LENGTH_LONG).show();
			}
			break;
		case status_success:
			Toast.makeText(context, msg_success, Toast.LENGTH_LONG).show();
			JSONObject userJson;
			try {
				userJson=loginJson.getJSONObject("user");
				user.setEmail(userJson.getString("email"));
				user.setUnick(userJson.getString("unick"));
				global.cookieStore=cookieStore;
				//user对象发生改变 保存
				User.onChange(user, context);
				intent=new Intent(context, OlympicClientActivity.class);
				startActivity(intent);
	//			finish();
			} catch (JSONException e) {
				LogUtil.e(e);
				Toast.makeText(context, msg_net_error, Toast.LENGTH_LONG).show();
			}
			break;
		default:
			break;
		}
	}
	
}
