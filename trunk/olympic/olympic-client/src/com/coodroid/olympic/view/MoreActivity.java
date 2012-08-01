package com.coodroid.olympic.view;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.coodroid.olympic.R;
import com.coodroid.olympic.view.account.AccountActivity;

/**
 * 更多模块的Activity
 * @author Cater
 *
 */
public class MoreActivity extends BaseActivity{
	
	private LinearLayout container;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context=this;
		setContentView(R.layout.more);
		container = (LinearLayout)((ActivityGroup)getParent()).getWindow().findViewById(R.id.containerBody);
		Button btnAccount=(Button) findViewById(R.id.btnAccount);
		Button btnNews = (Button) findViewById(R.id.btnNews);
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
	}
}
