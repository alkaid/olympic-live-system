package com.coodroid.olympic.view;

import com.coodroid.olympic.R;
import com.coodroid.olympic.ui.Turntable;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

public class LotteryActivity extends Activity {
    /** Called when the activity is first created. */
	
	private void findViewAndButton(){
		
		//自定义的View
		final Turntable panView=(Turntable) this.findViewById(R.id.zhuanpanView);
		
		//开始旋转的按钮
		Button startButton=(Button) this.findViewById(R.id.startButton);
		startButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				
				panView.startRotate();
				
			}
			
		});
		//停止旋转的按钮
		Button stopButton=(Button) this.findViewById(R.id.stopButton);
		stopButton.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				
				panView.stopRotate();
				
			}
			
		});
		
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Context context=this;
    	super.onCreate(savedInstanceState);
    	WebView mWebView=new WebView(context);
//        setContentView(R.layout.lottery);
    	setContentView(mWebView);
    	WebSettings webSettings=mWebView.getSettings();
    	webSettings.setJavaScriptEnabled(true);
    	mWebView.loadUrl("http://www.classyuan.com/demo/lotterydraw/lotterydraw.html");
//        findViewAndButton();
        
    }
}