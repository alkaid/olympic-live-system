/**
 * 
 */
package com.coodroid.olympic.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
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
import android.widget.Toast;

import com.coodroid.olympic.R;
import com.coodroid.olympic.common.Constants;
import com.coodroid.olympic.common.HttpUtils;
import com.coodroid.olympic.common.IOUtil;
import com.coodroid.olympic.common.LogUtil;
import com.coodroid.olympic.common.SessionManager;
import com.coodroid.olympic.ui.FormListView;
import com.coodroid.olympic.ui.FormListView.FormListAdapter.Item;
import com.coodroid.olympic.ui.FormListView.FormListAdapter.Validater;

/**
 * 用户注册
 * @author Alkaid
 *
 */
public class RegisterActivity extends Activity {
	Context context;
	FormListView formListView;
	Item email=new Item("email", "请输入邮箱");
	Item unick=new Item("unick", "请输入昵称(1-32位字符)");
	Item password=new Item("password", "密码(6-12位字符)");
	/** 注册失败 */
	private static final int status_failed=-1;
	private static final String msg_failed="注册失败";
	/** 已经是登录状态。不用注册*/
	private static final int status_logged=0;
	private static final String msg_logged="您已经是登录状态";
	/** 注册成功*/
	private static final int status_success=1;
	private static final String msg_success="注册成功,现已以会员身份登录";
	/** 网络错误*/
	private static final int status_net_error=-2;
	private static final String msg_net_error="网络异常,请检查您的网络设置稍后重试";
	
	private ProgressDialog pd;
	private String sessionId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		context=this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		initForm();
		
		//init Button
		Button btnRegister=(Button) findViewById(R.id.btnRegister);
		btnRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(formListView.validate()){
					pd=ProgressDialog.show(context, null, "正在注册,请稍后...", true, false);
					new RegisterTask().execute();
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
		email.validater=new Validater() {
			@Override
			public boolean validate(String text) {
				this.warn="请输入正确的邮箱格式";
				if(text.length()<1||text.length()>96)
					return false;
				String reglex="^[^\\@]+@.*\\.[a-z]{2,6}$";
				Pattern p=Pattern.compile(reglex,Pattern.CASE_INSENSITIVE);
				if(p.matcher(text).matches()){
					return true;
				}
				return false;
			}
		};
		unick.validater=new Validater() {
			@Override
			public boolean validate(String text) {
				this.warn="昵称长度应为1-32个字符";
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
		data.add(email);
		data.add(unick);
		data.add(password);
		formListView=new FormListView(context, data);
		layForm.addView(formListView,new LayoutParams(LayoutParams.FILL_PARENT,formListView.getViewHeight()));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//test
		/*new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				formListView.setWarnInfo(email.tag, "请输入正确的邮箱格式");
			}
		}, 1000);*/
	}
	
	private class RegisterTask extends AsyncTask<Void, Void, String>{
		@Override
		protected String doInBackground(Void... params) {
			HttpUtils.setDefaultConfig();
			Map<String, String> reqParams=formListView.getFormBean();
			HttpResponse response=null;
			try {
				response=HttpUtils.request(Constants.url.user.register, HttpUtils.METHOD_POST, reqParams);
			} catch (ClientProtocolException e) {
				LogUtil.e(e);
			} catch (IOException e) {
				LogUtil.e(e);
			}
			if(null==response)
				return null;
			String json=null;
			try {
				json = IOUtil.readInputStrem2Str(response.getEntity().getContent(),"utf-8");
			} catch (IllegalStateException e) {
				LogUtil.e(e);
			} catch (IOException e) {
				LogUtil.e(e);
			}
			if(!TextUtils.isEmpty(json)){
				sessionId=SessionManager.getRemoteSessionId(response);
			}
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
		JSONObject matchJSON = null;
		if(!TextUtils.isEmpty(jsonStr)){
			try {
				matchJSON = new JSONObject(jsonStr);
				status=Integer.valueOf(matchJSON.getString("status"));
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
			Intent intent=new Intent(context, OlympicClientActivity.class);
			startActivity(intent);
			finish();
			break;
		case status_failed:
			Toast.makeText(context, msg_failed, Toast.LENGTH_LONG).show();
			JSONObject errorJson;
			try {
				errorJson = matchJSON.getJSONObject("error");
				Iterator<String> iterator = errorJson.keys();
				while(iterator.hasNext()){
					String tag=iterator.next();
					String warn=errorJson.getString(tag);
					formListView.setWarnInfo(tag, warn);
				}
				formListView.requestFocusOnError();
			//服务端内部错误时可能返回出错信息，此时不是Json格式，解析会异常
			} catch (JSONException e) {
				LogUtil.e(e);
				Toast.makeText(context, msg_net_error, Toast.LENGTH_LONG).show();
			}
			break;
		case status_success:
			SessionManager.setLocalSessionId(context, sessionId);
			Toast.makeText(context, msg_success, Toast.LENGTH_LONG).show();
			intent=new Intent(context, OlympicClientActivity.class);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}
	}
	
}
