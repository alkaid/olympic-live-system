package com.coodroid.olympic.view;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import com.coodroid.olympic.R;
import com.coodroid.olympic.common.Constants;
import com.coodroid.olympic.common.DBHelper;
import com.coodroid.olympic.common.HttpUtils;
import com.coodroid.olympic.common.LogUtil;
import com.coodroid.olympic.common.SystemUtil;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class OlympicClientActivity extends ActivityGroup {
	
	private LinearLayout container = null;
	private RelativeLayout medalBtn = null;
	private RelativeLayout matchBtn = null;
	private RelativeLayout liveBtn = null;
	private RelativeLayout guessBtn = null;
	private RelativeLayout moreBtn = null;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置视图
        getData();
        setContentView(R.layout.main);
        init();
    }
    
    /**
     * 初始化操作，包括初始化view，默认奖牌榜，数据库更新,监听activity切换
     */
    private void init(){
    	findViewByIds();
    	//初始化DB
    	DBHelper db = DBHelper.getInstance(this, Constants.OlympicDB);
    	db.open();
    	//监听用户操作5个模块
    	//默认显示奖牌榜
    	showDefaultActivity();
    	changeActivity();
    }
    

    /**
     *默认奖牌榜作为初始化显示 
     */
    private void showDefaultActivity(){
		container.addView(OlympicClientActivity.this.
				getLocalActivityManager().startActivity("MedalActivity", 
						new Intent(OlympicClientActivity.this,MedalActivity.class))
						.getDecorView());
    } 
    
    /**
     * findViewByIds
     */
    private void findViewByIds(){
    	  container = (LinearLayout) findViewById(R.id.containerBody);
    	  medalBtn = (RelativeLayout) findViewById(R.id.btnMedal);
    	  matchBtn = (RelativeLayout) findViewById(R.id.btnMatch);
    	  liveBtn = (RelativeLayout) findViewById(R.id.btnLive);
    	  guessBtn = (RelativeLayout) findViewById(R.id.btnGuess);
    	  moreBtn = (RelativeLayout) findViewById(R.id.btnMore);
    }
    
    /**
     * 这个方法提供了不同的点击跳转到不同Activity
     */
    private void changeActivity(){
    	//跳转到奖牌榜的界面
    	medalBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				container.removeAllViews();
				container.addView(OlympicClientActivity.this.
						getLocalActivityManager().startActivity("MedalActivity", 
								new Intent(OlympicClientActivity.this,MedalActivity.class).
								addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))								
								.getDecorView());
			}
		});
    	
    	//跳转到赛程表的界面
    	matchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				container.removeAllViews();
				LogUtil.i("Match is onclick");
				container.addView(OlympicClientActivity.this.
						getLocalActivityManager().startActivity("MatchActivity", 
								new Intent(OlympicClientActivity.this,MatchActivity.class)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
								.getDecorView());
			}
		});
    	
    	//跳转到直播吧的界面
    	liveBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				container.removeAllViews();
				container.addView(OlympicClientActivity.this.
						getLocalActivityManager().startActivity("LiveActivity", 
								new Intent(OlympicClientActivity.this,LiveActivity.class))
								.getDecorView());
			}
		});
    	
    	//跳转到藏宝阁的界面
    	guessBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				container.removeAllViews();
				container.addView(OlympicClientActivity.this.
						getLocalActivityManager().startActivity("GuessActivity", 
								new Intent(OlympicClientActivity.this,GuessActivity.class))
								.getDecorView());
			}
		});
    	
    	//跳转到更多的界面
    	moreBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				container.removeAllViews();
				container.addView(OlympicClientActivity.this.
						getLocalActivityManager().startActivity("MoreActivity", 
								new Intent(OlympicClientActivity.this,MoreActivity.class))
								.getDecorView());
			}
		});
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    }
    
    
    
    public void getData(){
    	try {
			String url = "http://coodroid.com/ocdemo/index.php?route=olympic/match"; 
			HttpUtils.setConnectionTimeout(3000);
			HttpUtils.setRetryCount(0);
			Map<String, String> params = new HashMap<String, String>();
//			params.put("p1", "0");
//			params.put("l", "10");
//			params.put("v","0");
			String data = HttpUtils.getContent(url, "GET", params, "utf-8");
			LogUtil.i(data);
		} catch (MalformedURLException e) {
			LogUtil.e(e);
		} catch (IOException e) {
			LogUtil.e(e);
		}
    }
}