package com.coodroid.olympic.view.account;

import com.coodroid.olympic.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AccountActivity extends Activity{
	Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		context=this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account);
		Button btnAutoLogin=(Button) findViewById(R.id.btnAutoLogin);
		btnAutoLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(context, PwdModifyActivity.class);
				startActivity(intent);
			}
		});
	}
	
	
}
