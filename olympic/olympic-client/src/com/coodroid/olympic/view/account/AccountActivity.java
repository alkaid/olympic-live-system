package com.coodroid.olympic.view.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.coodroid.olympic.R;
import com.coodroid.olympic.model.Setting;
import com.coodroid.olympic.model.User;
import com.coodroid.olympic.view.BaseActivity;

public class AccountActivity extends BaseActivity{
	
	private ViewGroup layLogged;
	private ViewGroup layUnlogged;
	private Button btnLogin;
	private Button btnRegister;
	private CheckBox chkAutoLogin;
	private TextView tvWelcome;
	private ViewGroup layPwd;
	private ViewGroup layLogout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account);
		initView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		switchLoginView();
	}

	private void initView() {
		layLogged=(ViewGroup) findViewById(R.id.layLogged);
		layUnlogged=(ViewGroup) findViewById(R.id.layUnlogged);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnRegister=(Button) findViewById(R.id.btnRegister);
		chkAutoLogin=(CheckBox) findViewById(R.id.chkAutoLogin);
		tvWelcome=(TextView) findViewById(R.id.tvWelcome);
		layPwd=(ViewGroup) findViewById(R.id.layPwd);
		layLogout=(ViewGroup) findViewById(R.id.layLogout);
		
		
		chkAutoLogin.setChecked(Setting.isAutoLogin(context));
		
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(context,LoginActivity.class));
			}
		});
		btnRegister.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(context,RegisterActivity.class));
			}
		});
		chkAutoLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Setting.setAutoLogin(context, chkAutoLogin.isChecked());
			}
		});
		layPwd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(context,PwdModifyActivity.class));
			}
		});
		layLogout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				User.logout(context);
				switchLoginView();
			}
		});
		
	}
	
	private void switchLoginView(){
		if(null!=global.user){
			layLogged.setVisibility(View.VISIBLE);
			layUnlogged.setVisibility(View.GONE);
			String welcome=getResources().getString(R.string.account_welcome_logged);
			welcome=String.format(welcome, global.user.getUnick());
			tvWelcome.setText(welcome);
		}else{
			layLogged.setVisibility(View.GONE);
			layUnlogged.setVisibility(View.VISIBLE);
			tvWelcome.setText(getResources().getString(R.string.account_welcome_unlogged));
		}
	}
	
}
