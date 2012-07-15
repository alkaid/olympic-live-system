package com.coodroid.olympic.view;

import java.util.ArrayList;

import com.coodroid.olympic.R;
import com.coodroid.olympic.common.LogUtil;
import com.coodroid.olympic.common.SystemUtil;
import com.coodroid.olympic.data.MatchDBDAO;
import com.coodroid.olympic.model.Match;

import android.app.Activity;
import android.os.Bundle;
/**
 * 直播吧的Activity，这个Activity显示的是文字直播和视频直播的列表
 * @author Cater
 *
 */
public class LiveActivity extends Activity{
	
	private static String MATCH_FIRST_DATE = "2012-07-26"; 
	private MatchDBDAO db;
	private MatchActivity matchActivity;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.live);
		db = new MatchDBDAO(this);
		matchActivity = new MatchActivity();
		LogUtil.v("DAXIAMAO",SystemUtil.getCurrentDate());
		LogUtil.v("DAXIAMAO",SystemUtil.getCurrentTime());
		
	}
	
	/**
	 * 加载数据
	 */
	private void loadData(){
		if(SystemUtil.checkNet(this)){
//			if(SystemUtil.getCurrentDate()<MATCH_FIRST_DATE){
			ArrayList<Match> matchs = (ArrayList<Match>) matchActivity.analyze(matchActivity.getServerData("20120726"));
			}
		}
	}
//}
