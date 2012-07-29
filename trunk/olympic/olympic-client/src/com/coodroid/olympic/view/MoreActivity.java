package com.coodroid.olympic.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.coodroid.olympic.R;
import com.coodroid.olympic.view.account.AccountActivity;

/**
 * 更多模块的Activity
 * @author Cater
 *
 */
public class MoreActivity extends BaseActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context=this;
		setContentView(R.layout.more);
		Button btnAccount=(Button) findViewById(R.id.btnAccount);
		btnAccount.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(context, AccountActivity.class));
			}
		});
	}
}
