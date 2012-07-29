package com.coodroid.olympic.view;

import java.util.List;

import org.apache.http.cookie.Cookie;

import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.coodroid.olympic.common.Constants;

public class LotteryActivity extends BaseActivity {
    private WebView mWebView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	mWebView=new WebView(context);
//        setContentView(R.layout.lottery);
    	setContentView(mWebView);
    	
//        findViewAndButton();
    	WebSettings webSettings=mWebView.getSettings();
    	webSettings.setJavaScriptEnabled(true);
//    	webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
    	mWebView.addJavascriptInterface(new JavaScriptInterface(), "jsni");
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	CookieSyncManager.createInstance(this);
    	CookieManager cookieManager = CookieManager.getInstance();
    	List<Cookie> cookies=global.cookieStore.getCookies();
    	String cookieString=null;
    	for (int i = 0; i < cookies.size(); i++) {
		    Cookie cookie = cookies.get(i);
		    cookieString=cookie.getName()+"="+cookie.getValue()+"; domain="+cookie.getDomain();
		    if (null!=cookieString) {
//		    	cookieManager.removeSessionCookie();
		    	cookieManager.setCookie(Constants.url.user.lottery, cookieString);
		    }   
		    CookieSyncManager.getInstance().sync();
		}
    	mWebView.loadUrl(Constants.url.user.lottery);
    }
    
    private static class JavaScriptInterface{
    	public void requestLogin(){
    		
    	}
    }
}