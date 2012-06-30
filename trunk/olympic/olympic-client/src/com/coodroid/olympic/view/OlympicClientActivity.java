package com.coodroid.olympic.view;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import com.coodroid.olympic.R;
import com.coodroid.olympic.common.HttpUtils;
import com.coodroid.olympic.common.LogUtil;

import android.app.Activity;
import android.os.Bundle;

public class OlympicClientActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getData();
    }
    
    public void getData(){
    	try {
			String url = "http://127.0.0.1/ocdemo/index.php?route=olympic/medal"; 
			HttpUtils.setConnectionTimeout(3000);
			HttpUtils.setRetryCount(0);
			Map<String, String> params = new HashMap<String, String>();
			params.put("p1", "0");
			params.put("l", "10");
			params.put("v","0");
			String data = HttpUtils.getContent(url, "GET", params, "utf-8");
			LogUtil.d(data);
		} catch (MalformedURLException e) {
			LogUtil.e(e);
		} catch (IOException e) {
			LogUtil.e(e);
		}
    }
}