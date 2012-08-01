package com.coodroid.olympic.view;

import com.coodroid.olympic.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
public class NewsActivity extends Activity{
	
	private WebView news;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news);
		news = (WebView) findViewById(R.id.newsWebView);
		news.loadUrl("http://3g.163.com/sports/special/000508U2/olympicgame.html");
		news.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if((keyCode == KeyEvent.KEYCODE_BACK)&&news.canGoBack()){
			news.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
