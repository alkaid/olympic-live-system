package com.coodroid.olympic.view;

import java.util.Date;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.coodroid.olympic.R;
import com.coodroid.olympic.common.Global;
import com.coodroid.olympic.common.LogUtil;
import com.coodroid.olympic.model.User;

public class WelcomeActivity extends BaseActivity{
	/** 欢迎界面显示时间 */
	final int SHOWTIME=1000;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//默认不初始化app 放在线程中执行
		needInitApp=false;
		super.onCreate(savedInstanceState);
		//友盟错误报告
//		MobclickAgent.onError(this);
		//友盟以通知栏为反馈的形式
//		UMFeedbackService.enableNewReplyNotification(this, NotificationType.NotificationBar);
//		//友盟自动更新
//		UmengUpdateAgent.update(this);
		//全屏
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
				WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		ImageView imgView=new ImageView(this);
		imgView.setBackgroundResource(R.drawable.welcome);
		setContentView(imgView);
		new InitTask().execute();
	}
	
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event) {   
        //在欢迎界面屏蔽BACK键   
        if(keyCode==KeyEvent.KEYCODE_BACK) {   
            return false;   
        }   
        return false;   
    }
	
	/** 初始化任务 初始化应用全局信息以及书本信息*/
	private class InitTask extends AsyncTask<Void, Integer, Integer>{
		Date begin;Date end;
		@Override
		protected Integer doInBackground(Void... params) {
			//后台初始化全局
			begin=new Date();
			Global.initApp(WelcomeActivity.this);
			User.autoLogin(context);
			return null;
		}
		@Override
		protected void onPostExecute(Integer result) {
			//初始化完成后计算用时，若用时未达到2秒钟，延迟至2秒更新ui，否则立即更新UI
			super.onPostExecute(result);
			end=new Date();
			long delgat=end.getTime()-begin.getTime();
			LogUtil.i("用时:"+delgat+"ms");
			if(delgat<SHOWTIME){
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						startActivity(new Intent(WelcomeActivity.this, OlympicClientActivity.class));
						finish();
					}
				}, SHOWTIME-delgat);
			}else{
				startActivity(new Intent(WelcomeActivity.this, OlympicClientActivity.class));
				finish();
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
	
}
