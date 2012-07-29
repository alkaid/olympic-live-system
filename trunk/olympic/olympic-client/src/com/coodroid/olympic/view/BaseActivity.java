package com.coodroid.olympic.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.coodroid.olympic.common.Global;

/**
 * 所有Activity的基类 统一菜单栏
 * @author Alkaid
 *
 */
public abstract class BaseActivity extends Activity {
	protected Context context;
	protected Global global;
	/** 是否需要初始化global 是欢迎界面专用参数，因为欢迎界面的初始化放在线程里*/
	protected boolean needInitApp=true;
	/*private Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Constants.msgWhat.toast:
				String str=msg.getData().getString(Constants.bundleKey.toastMag);
				Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context=this;
		global=Global.getGlobal(context,needInitApp);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
//		MobclickAgent.onResume(context);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
//		MobclickAgent.onPause(context);
	}
	
}
