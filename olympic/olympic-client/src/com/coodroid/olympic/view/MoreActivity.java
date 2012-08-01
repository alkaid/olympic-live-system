package com.coodroid.olympic.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.coodroid.olympic.R;
import com.coodroid.olympic.view.account.AccountActivity;
import com.umeng.fb.UMFeedbackService;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

/**
 * 更多模块的Activity
 * @author Cater
 *
 */
public class MoreActivity extends BaseActivity{

	private Button btnAccount;
	private Button btnNews;
	private Button btnUpdate;
	private Button btnFeedBack;
	private Button btnAbout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context=this;
		setContentView(R.layout.more);
		findView();
		allListener();

	}
	
	/**
	 * 初始化view
	 */
	private void findView(){
		btnAccount=(Button) findViewById(R.id.btnAccount);
		btnNews = (Button) findViewById(R.id.btnNews);
		btnUpdate = (Button) findViewById(R.id.btnUpdate);
		btnFeedBack = (Button) findViewById(R.id.btnFeedback);
		btnAbout = (Button) findViewById(R.id.btnAbout);
	}
	
	/**
	 * 所有监听
	 */
	private void allListener(){
		btnAccount.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(context, AccountActivity.class));
			}
		});
		btnNews.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(context, NewsActivity.class));
			}
		});
		btnUpdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UmengUpdateAgent.update(MoreActivity.this);
				UmengUpdateAgent.setUpdateAutoPopup(false);
				UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
				        @Override
				        public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
				            switch (updateStatus) {
				            case 0: // has update
				                UmengUpdateAgent.showUpdateDialog(MoreActivity.this, updateInfo);
				                break;
				            case 1: // has no update
				                Toast.makeText(MoreActivity.this, "没有更新", Toast.LENGTH_SHORT)
				                        .show();
				                break;
				            case 2: // none wifi
				                Toast.makeText(MoreActivity.this, "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT)
				                        .show();
				                break;
				            case 3: // time out
				                Toast.makeText(MoreActivity.this, "超时", Toast.LENGTH_SHORT)
				                        .show();
				                break;
				            }
				        }
				});
			}
		});
		btnFeedBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UMFeedbackService.openUmengFeedbackSDK(MoreActivity.this);
			}
		});
		btnAbout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(context, AboutActivity.class));
			}
		});
	}
}
