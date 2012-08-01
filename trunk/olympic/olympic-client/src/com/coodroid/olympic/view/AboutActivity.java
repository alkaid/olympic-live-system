package com.coodroid.olympic.view;

import java.io.IOException;
import java.io.InputStream;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.coodroid.olympic.R;
import com.coodroid.olympic.common.Constants;
import com.coodroid.olympic.common.IOUtil;
import com.coodroid.olympic.common.LogUtil;

public class AboutActivity extends BaseActivity{
	
	private Typeface fontType=Typeface.DEFAULT;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		TextView aboutContent = (TextView) findViewById(R.id.aboutContent);
		setTextProperty(aboutContent);
		aboutContent.setText(getContent());
	}
	
	
	private String getContent(){
		String url = Constants.url.file.about;
		String text = null;
		InputStream is;
		try {
			is = context.getAssets().open(url);
			text = IOUtil.readInputStrem2Str(is, null);
		} catch (IOException e) {
			LogUtil.e(e);
		}
		
		return text;
	}
	/**
	 * 默认的文本字体,格式设置
	 * @param tv
	 */
	private void setTextProperty(TextView tv){
//		tv.setGravity(Gravity.CENTER);
		tv.setTypeface(fontType);
		tv.setTextColor(context.getResources()
				.getColor(R.color.textcolor));
		tv.setTextSize(12*global.dm.scaledDensity);
	}
}
