package com.coodroid.olympic.view;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.coodroid.olympic.R;
import com.coodroid.olympic.common.HttpUtils;
import com.coodroid.olympic.common.LogUtil;
import com.coodroid.olympic.common.SystemUtil;
import com.coodroid.olympic.data.MatchDBDAO;
import com.coodroid.olympic.model.Match;
import com.coodroid.olympic.model.MatchProject;
import com.coodroid.olympic.model.Medal;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 赛程表的Activity
 * @author Cater
 *
 */
public class MatchActivity extends Activity{
	/** 请求的链接 */
	public static final String matchUrl = "http://coodroid.com/ocdemo/index.php?route=olympic/match";
	/** 赛程表拥有的赛程日 */
	private String[] dates = {"7-27","开幕式","7-29","7-30","7-31","8-01",
			"8-02","8-03","8-04","8-05","8-06","8-07","8-08","8-09","8-10","8-12","闭幕式"};
	/** 被选中的赛程日 */
	private int position ;
	/** 赛程表里左列的日期list */
	private ListView matchDateList;
	/** 赛程表里右列的赛程内容list */
	private ListView matchList;
	private MatchDBDAO db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.match);
		db = new MatchDBDAO(this);
		matchDateList = (ListView) findViewById(R.id.match_date);
		matchList = (ListView) findViewById(R.id.match_list);
		setMatchDateList();
		
	}
	
	private void updateMatchList(){
		if(SystemUtil.checkNet(this)){
			//服务器匹配10条数据，看有没有更新，有获得数据并更新到数据库中
			ArrayList<Match> matchs = (ArrayList<Match>) analyze(getServerData("20120728"));
			for(Match match:matchs){
				db.addOrUpdate(match);
			}			
		}
		Cursor cr = db.queryPaging(1, 10);
	}
	
	/**
	 * 设置比赛时间表
	 */
	private void setMatchDateList(){
		MatchDataAdapter adapter = new MatchDataAdapter();
		matchDateList.setAdapter(adapter);
		matchDateList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				position = arg2;
			}
			
		});
	}
	
	/**
	 * 发送请求 返回Medal更新的数据
	 * @param index 开始索引项，从第几条开始请求
	 * @param length 请求返回的长度
	 * @return JSON用于解析
	 */
	private String getServerData(String date){
		String matchServerData = null;
    	try {
			HttpUtils.setConnectionTimeout(3000);
			HttpUtils.setRetryCount(0);
			Map<String, String> params = new HashMap<String, String>();
			params.put("date", date);
			matchServerData = HttpUtils.getContent(matchUrl, HttpUtils.METHOD_GET, params, "utf-8");
			LogUtil.i(matchServerData);
		} catch (MalformedURLException e) {
			LogUtil.e(e);
		} catch (IOException e) {
			LogUtil.e(e);
		}
		return matchServerData;
		
	}
	
	/**
	 * 将服务端返回的数据进行解析获得
	 * @param matchServerData 服务端的Json
	 * @return 返回多条比赛记录
	 */
	private List<Match> analyze(String matchServerData){
		List<Match> matchs = new ArrayList<Match>();
		try {
			JSONObject matchJSON = new JSONObject(matchServerData);
			if(matchJSON.getString("staus").equals("2")){
				JSONArray matchArray = matchJSON.getJSONArray("data");
				for(int i=0;i<matchArray.length();i++){
					JSONObject matchObject = (JSONObject) matchArray.opt(i);
					Match m = new Match(Integer.parseInt(matchObject.getString("id")));
//					m.setDatetime(matchObject.getString("datetime"));
//					m.setLondonDatetime(matchObject.getString("londonTime"));
					m.setHasTextLive(Integer.parseInt(matchObject.getString("hasTextLive")));
					m.setHasVideoLive(Integer.parseInt(matchObject.getString("hasVideoLive")));
					m.setName(matchObject.getString("name"));
					m.setVideoChannel(matchObject.getString("videoChannel"));
					MatchProject partOfProject = new MatchProject(Integer.parseInt(matchObject.getString("projectId")));
					m.setPartOfProject(partOfProject);
					matchs.add(m);
				}
			}
		} catch (JSONException e) {
			LogUtil.e(e);
			LogUtil.e("JSON解析异常");
		}
		return matchs;
	}
	
	/**
	 *@param cur
	 */
	private void sortByProject(Cursor match){
		
	}
	

	
	/**
	 * 赛程内容的list的adapter
	 * @author Cater
	 *
	 */
	private class MatchContentAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return null;
		}
		
	}
	
	/**
	 * 
	 * @author 比赛时间列表的adapter
	 *
	 */
	private class MatchDataAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return dates.length;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View view = null;
			if(arg1!=null){
				view = arg1;
			}{
				LayoutInflater inflater = (LayoutInflater) MatchActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
				LinearLayout date = (LinearLayout) inflater.inflate(R.layout.match_date_list, null);
				TextView dateTxt = (TextView) date.findViewById(R.id.date_txt);
				dateTxt.setText(dates[arg0]);
				view = date;
			}
			return view;
		}
		
	}
}
