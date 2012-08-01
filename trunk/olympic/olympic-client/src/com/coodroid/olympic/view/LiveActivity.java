package com.coodroid.olympic.view;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityGroup;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.coodroid.olympic.R;
import com.coodroid.olympic.common.Constants;
import com.coodroid.olympic.common.HttpRequest;
import com.coodroid.olympic.common.LogUtil;
import com.coodroid.olympic.common.SystemUtil;
import com.coodroid.olympic.data.MatchDBDAO;
import com.coodroid.olympic.model.Match;
import com.coodroid.olympic.model.MatchProject;
/**
 * 直播吧的Activity，这个Activity显示的是文字直播和视频直播的列表
 * @author Cater
 *
 */
public class LiveActivity extends BaseActivity{
	
	/**用于标记比赛日第一天*/
	private static String MATCH_FIRST_DATE = "2012-07-26";
	/**用于判断比赛是否结束的比赛时间间隔*/
	private static long MATCH_END_TIME_INTERVAL = 10*60*60*1000;
	/**用于判断比赛是否开始的时间间隔*/
	private static long MATCH_START_TIME_INTERVAL = 2*60*60*1000;
	/**监听显示的是文字直播*/
	private static int TEXT_LIVE_VIEW = 0;
	/**监听显示的是视频直播*/
	private static int VIDEO_LIVE_VIEW = 1;
	/**直播选择的是哪一种直播*/
	private int liveTypeSelect = VIDEO_LIVE_VIEW;
	/**正在直播标记*/
	private int LIVING_SORT_SELECT = 0;
	/**即将直播标记*/
	private int WILL_LIVE_SORT_SELECT = 1;
	/**直播实录标记*/
	private int LIVE_END_SORT_SELECT = 2;
	/**屏幕上显示直播的选择,包括正在直播，即将直播，直播实录*/
	private int liveSortSelect = LIVING_SORT_SELECT;
			
		
	/**做match表的操作类*/
	private MatchDBDAO db;
	/**用于保存需要查看所对应的日期*/
	private String operatorDate;
	/**这个List用于保存直播中有文字直播的比赛*/
	private List<Match> livingHasTextLiveMatchs;
	/**这个List用于保存即将直播有文字直播的比赛*/
	private List<Match> willLiveHasTextLiveMatchs;
	/**这个List用于保存可以查看直播有文字直播实录的比赛*/
	private List<Match> liveEndHasTextLiveMatchs;
	/**这个List用于保存正在直播的有视频直播的比赛*/
	private List<Match> livingHasVideoLiveMatchs;
	/**表示显示的比赛*/
	private List<Match> showMatchs;
	/**文字直播头的标签*/
	private Button textLiveTagBtn;
	/**视频直播头的标签*/
	private Button videoLiveTagBtn;
	
	private LayoutInflater mInflater;
	
	ClickEvent clickEvent;
	
	/**相关显示的view*/
//	private LinearLayout liveContainer;
	/** 父view */
	private LinearLayout container;
	private ListView liveSortList;
	private TextView liveDate;
	private Button lastDateBtn;
	private Button nextDateBtn;
	private TextView liveSortTitle;
	private Button liveSort;
	private ImageView refreshImg;
	private ProgressBar liveProgressBar;

	public LiveActivity() {
		operatorDate=SystemUtil.getCurrentDate();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.live);
		init();
		refresh();
		tagListen();
		
	}
	private void init(){
		container=(LinearLayout)((ActivityGroup)getParent()).getWindow().findViewById(R.id.containerBody);//注意这里，还是获取group的view
		liveSortList = (ListView) findViewById(R.id.living_list);
		liveDate = (TextView) findViewById(R.id.live_date);
		lastDateBtn = (Button) findViewById(R.id.live_last_date);
		nextDateBtn = (Button) findViewById(R.id.live_next_date);
		textLiveTagBtn = (Button) findViewById(R.id.tagOfText);
		videoLiveTagBtn = (Button) findViewById(R.id.tagOfVideo);
		liveSortTitle = (TextView) findViewById(R.id.live_sort_title);
		liveSort = (Button) findViewById(R.id.live_sort);
		refreshImg = (ImageView) findViewById(R.id.refresh_btn);
		liveProgressBar = (ProgressBar) findViewById(R.id.liveProcessBar);
		
		db = new MatchDBDAO(this);
		livingHasTextLiveMatchs = new ArrayList<Match>();
		willLiveHasTextLiveMatchs = new ArrayList<Match>();
		liveEndHasTextLiveMatchs = new ArrayList<Match>();
		livingHasVideoLiveMatchs = new ArrayList<Match>();
		
		mInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		clickEvent = new ClickEvent();
		liveSortList.setVisibility(View.GONE);
		liveSortTitle.setVisibility(View.VISIBLE);
	}
	
	private void tagListen(){
		textLiveTagBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(liveTypeSelect == VIDEO_LIVE_VIEW){
					textLiveTagBtn.setBackgroundResource(R.color.live_noselect_color);
					videoLiveTagBtn.setBackgroundResource(R.color.live_select_color);
					liveTypeSelect = TEXT_LIVE_VIEW;
					refresh();
				}
			}
		});
		videoLiveTagBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(liveTypeSelect == TEXT_LIVE_VIEW){
					textLiveTagBtn.setBackgroundResource(R.color.live_select_color);
					videoLiveTagBtn.setBackgroundResource(R.color.live_noselect_color);
					liveTypeSelect = VIDEO_LIVE_VIEW;
					refresh();
				}
			}
		});
		liveSort.setOnClickListener(clickEvent);
		refreshImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {			
				refresh();
			}
		});
		liveSortList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(LiveActivity.this,TextLiveActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				global.putData(Constants.bundleKey.matchOfLive, showMatchs.get(arg2));
				container.removeAllViews();
				container.addView(((ActivityGroup)LiveActivity.this.getParent()).getLocalActivityManager()
							.startActivity("TextLiveActivity", 
								intent)
								.getDecorView());
			}
			
		});
		lastDateBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				if(operatorDate!=null&&operatorDate.compareTo(SystemUtil.getCurrentDate())>0){
				try {	
						 SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");					
						 Date date = fmt.parse(operatorDate);
						 Calendar calendar = Calendar.getInstance();     
						 calendar.setTime(date);  
						 calendar.set(Calendar.DAY_OF_YEAR,calendar.get(Calendar.DAY_OF_YEAR) - 1);
						 operatorDate = fmt.format(calendar.getTime());
						 refresh();
					} catch (ParseException e) {
						LogUtil.e(e);
					}
				}
//			}
		});
		nextDateBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(operatorDate!=null){
				try {
						 SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");					
						 Date date = fmt.parse(operatorDate);
						 Calendar calendar = Calendar.getInstance();     
						 calendar.setTime(date);  
						 calendar.set(Calendar.DAY_OF_YEAR,calendar.get(Calendar.DAY_OF_YEAR) + 1);
						 operatorDate = fmt.format(calendar.getTime());
						 refresh();
					} catch (ParseException e) {
						LogUtil.e(e);
					}
				}
			}
		});
	}
	
	private void refresh(){
		refreshImg.setVisibility(View.GONE);
		liveProgressBar.setVisibility(View.VISIBLE);
//		liveSortList.setVisibility(View.GONE);
//		liveSortTitle.setVisibility(View.GONE);
//		liveDate.setVisibility(View.GONE);
//		lastDateBtn.setVisibility(View.GONE);
//		nextDateBtn.setVisibility(View.GONE);
		new matchMsgAsyncTask().execute(null);
	}
	
	/**
	 * 加载数据，分别设置需要的比赛日的即将比赛，正在直播的比赛，已经比赛完的比赛信息
	 */
	private void loadData(){
		if(SystemUtil.checkNet(this)){
			ArrayList<Match> matchs = null;
			if(operatorDate.compareTo(MATCH_FIRST_DATE)<0){
				matchs = (ArrayList<Match>)analyze(getServerData(MATCH_FIRST_DATE));
				operatorDate = MATCH_FIRST_DATE;
			}else{
				matchs = (ArrayList<Match>)analyze(getServerData(operatorDate));
			}
			if(matchs!=null){
				for(Match match:matchs){
					db.addOrUpdate(match);
				}
			}
		}
		livingHasTextLiveMatchs.clear();
		willLiveHasTextLiveMatchs.clear();
		liveEndHasTextLiveMatchs.clear();
		livingHasVideoLiveMatchs.clear();
		Cursor c = db.query(operatorDate);
		if(c.moveToFirst()){
			do{					
				Match m = new Match(c.getInt(0));
				m.setBjDate(c.getString(1));
				m.setBjTime(c.getString(2));
//				m.setLondonDate(c.getString(3));
//				m.setLondonTime(c.getString(4));
//				m.setPartOfProject(new MatchProject(c.getInt(5)));
				m.setName(c.getString(6));
				m.setHasVideoLive(c.getInt(9));
				m.setHasTextLive(c.getInt(8));
//				LogUtil.v("MAOXIA", m.getBjDate()+"   "+m.getBjTime()+"   "+m.getName());
				String matchDatetime = c.getString(1)+" "+c.getString(2);
				String curDatetime = SystemUtil.getCurrentDate()+" "+SystemUtil.getCurrentTime();
				long timeInterval = compareDatetime(matchDatetime, curDatetime);
				if(c.getInt(8)==1){
					if(timeInterval>(0-MATCH_START_TIME_INTERVAL)&&timeInterval<MATCH_END_TIME_INTERVAL){
						if(c.getInt(8)==1){
							livingHasTextLiveMatchs.add(m);
						}
						if(c.getInt(9)==1){
							livingHasVideoLiveMatchs.add(m);
						}
					}else if(timeInterval<(0-MATCH_START_TIME_INTERVAL)){
						willLiveHasTextLiveMatchs.add(m);
					}else{
						liveEndHasTextLiveMatchs.add(m);
					}
				}
			}while(c.moveToNext());
		}
	}
	
	
	@SuppressWarnings("finally")
	private long compareDatetime(String date1,String date2){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long timeInterval=0;
		try {
			Date t1 = sdf.parse(date1);
			Date t2 = sdf.parse(date2);
			timeInterval = t2.getTime()-t1.getTime();
		} catch (ParseException e) {
			LogUtil.e(e);
			LogUtil.e("时间格式解析有误");
		}finally{
			return timeInterval;
		}
	}
	
	/**
	 * 发送请求 返回Match更新的数据
	 * @param date 请求的参数 表示比赛日的时间
	 * @return JSON用于解析
	 */
	
	public String getServerData(String date){
		String matchServerData = null;
    	try {
			Map<String, String> params = new HashMap<String, String>();
			if(date!=null){
				params.put("date", date);
			}
			HttpRequest request=new HttpRequest();
			matchServerData=request
					.setConnectionTimeout(3000)
					.setRetryCount(0)
					.setCookieStore(global.cookieStore)
					.setUrl(Constants.url.api.match)
					.setMethod(HttpRequest.METHOD_GET)
					.setParams(params)
					.setCharset("utf-8")
					.getContent();
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
	public List<Match> analyze(String matchServerData){
		if(matchServerData!=null){
			List<Match> matchs = new ArrayList<Match>();
			try {
				JSONObject matchJSON = new JSONObject(matchServerData);
				if(matchJSON.getString("status").equals("2")){
					JSONArray matchArray = matchJSON.getJSONArray("data");
					for(int i=0;i<matchArray.length();i++){
						JSONObject matchObject = (JSONObject) matchArray.opt(i);
						Match m = new Match(Integer.parseInt(matchObject.getString("id")));
						//解析伦敦时间日期
						String londonTime = matchObject.getString("londonTime");
						String[] londonTimes = londonTime.split("\\s+");
						m.setLondonDate(londonTimes[0]);
						m.setLondonTime(londonTimes[1]);
						//解析北京时间日期
						String bjDatetime =matchObject.getString("datetime");
						String[] bjTimes = bjDatetime.split("\\s+");
						m.setBjDate(bjTimes[0]);
						m.setBjTime(bjTimes[1]);					
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
		}else{
			return null;
		}
	}
	
	
	private class matchMsgAsyncTask extends AsyncTask<Void, Void, Void>{
		
		
		public matchMsgAsyncTask() {

		}
		
		@Override
		protected Void doInBackground(Void... params) {
			loadData();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
//			liveDate.setVisibility(View.VISIBLE);
			liveDate.setText("比赛日:"+operatorDate);
			updateLiveContent(liveTypeSelect,liveSortSelect);
			liveSortTitle.setBackgroundResource(R.color.live_select_color);
//			liveSortTitle.setVisibility(View.VISIBLE);
//			liveSortList.setVisibility(View.VISIBLE);
			liveProgressBar.setVisibility(View.GONE);
			refreshImg.setVisibility(View.VISIBLE);
			lastDateBtn.setVisibility(View.VISIBLE);
			nextDateBtn.setVisibility(View.VISIBLE);
		}
	} 
	
	/**
	 * 用于显示比赛直播信息的adapter
	 * @author cater
	 *
	 */
	private class matchMsgAdapter extends BaseAdapter{
		
		List<Match> matchs;
		
		public matchMsgAdapter(List<Match> matchs) {
			this.matchs=matchs;
			showMatchs = matchs;
		}

		@Override
		public int getCount() {
			return matchs.size();
		}

		@Override
		public Object getItem(int arg0) {
			return matchs.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
//		@Override
//		public boolean isEnabled(int position) {
//			return false;
//		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView==null){
				convertView = mInflater.inflate(R.layout.live_msg, null);
				holder = new ViewHolder();
				holder.liveTimeTv = (TextView) convertView.findViewById(R.id.live_time);
				holder.liveNameTv = (TextView) convertView.findViewById(R.id.live_name);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			Match m = matchs.get(position);
			String mBjTime = m.getBjTime();
			holder.liveTimeTv.setText(mBjTime.substring(0,mBjTime.length()-3));
			holder.liveNameTv.setText("【"+m.getName()+"】");
			return convertView;
		}
		
	}
	
	/**
	 * 根据不同的直播分类显示不同的内容
	 * @param liveSortSelect
	 */
	private void updateLiveContent(int liveTypeSelect,int liveSortSelect){
		if(liveTypeSelect==TEXT_LIVE_VIEW){
			if(liveSortSelect==LIVING_SORT_SELECT){
				liveSortList.setAdapter(new matchMsgAdapter(livingHasTextLiveMatchs));
				liveSortTitle.setText("正在直播");
			}else if(liveSortSelect==WILL_LIVE_SORT_SELECT){
				liveSortList.setAdapter(new matchMsgAdapter(willLiveHasTextLiveMatchs));
				liveSortTitle.setText("即将直播");
			}else if(liveSortSelect==LIVE_END_SORT_SELECT){
				liveSortList.setAdapter(new matchMsgAdapter(liveEndHasTextLiveMatchs));
				liveSortTitle.setText("直播实录");
			}
		}else if(liveTypeSelect==VIDEO_LIVE_VIEW){
			liveSortList.setAdapter(new matchMsgAdapter(livingHasVideoLiveMatchs));
			liveSortTitle.setText("正在直播");
		}
	}

	static class ViewHolder{
		TextView liveTimeTv;
		TextView liveNameTv;	
	}
	/**
	 * 用于监听生成Title PopupWindow
	 * @author Cater
	 *
	 */
	private class ClickEvent implements OnClickListener{
		private PopupWindow popupWindow;
		private int screenWidth;
		private int dialogWidth;
		
		@Override
		public void onClick(View v) {
			if(popupWindow!=null&&popupWindow.isShowing()){
				popupWindow.dismiss();
			}else{
				getPopuWindow().showAsDropDown(v,screenWidth-dialogWidth,0);
			}
		}
		
		private PopupWindow getPopuWindow(){
			
			View popupWindow_view = getLayoutInflater().inflate(R.layout.live_sort_list, null,false);
			if(liveTypeSelect==TEXT_LIVE_VIEW){
					popupWindow = new PopupWindow(popupWindow_view, 120, 150, false);
			}else if(liveTypeSelect==VIDEO_LIVE_VIEW){
				popupWindow = new PopupWindow(popupWindow_view, 120, 50, false);
			}			
			TextView livingSortSelectTxt = (TextView) popupWindow_view.findViewById(R.id.living_sort_select);
			TextView willLiveSortSelectTxt = (TextView) popupWindow_view.findViewById(R.id.will_live_sort_select);
			TextView liveEndSortSelect = (TextView) popupWindow_view.findViewById(R.id.live_end__sort_select);
			if(liveTypeSelect==TEXT_LIVE_VIEW){
				willLiveSortSelectTxt.setVisibility(View.VISIBLE);
				willLiveSortSelectTxt.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						liveSortSelect = WILL_LIVE_SORT_SELECT;
						refresh();
						popupWindow.dismiss();
					}
				});
			}else if(liveTypeSelect==VIDEO_LIVE_VIEW){
				willLiveSortSelectTxt.setVisibility(View.GONE);
			}
			if(liveTypeSelect==TEXT_LIVE_VIEW){
				liveEndSortSelect.setVisibility(View.VISIBLE);
				liveEndSortSelect.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						liveSortSelect = LIVE_END_SORT_SELECT;
						refresh();
						popupWindow.dismiss();
					}
				});
			}else if(liveTypeSelect==VIDEO_LIVE_VIEW){
				liveEndSortSelect.setVisibility(View.GONE);
			}
			livingSortSelectTxt.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					liveSortSelect = LIVING_SORT_SELECT;
					refresh();
					popupWindow.dismiss();
					
				}
			});
		
			screenWidth = LiveActivity.this.getWindowManager().getDefaultDisplay().getWidth();
			dialogWidth = popupWindow.getWidth();
			
			
			return popupWindow;
		}
		
	}
	

}
