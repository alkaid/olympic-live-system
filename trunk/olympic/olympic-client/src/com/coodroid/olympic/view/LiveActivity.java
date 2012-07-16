package com.coodroid.olympic.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.coodroid.olympic.R;
import com.coodroid.olympic.common.LogUtil;
import com.coodroid.olympic.common.SystemUtil;
import com.coodroid.olympic.data.MatchDBDAO;
import com.coodroid.olympic.model.Match;
/**
 * 直播吧的Activity，这个Activity显示的是文字直播和视频直播的列表
 * @author Cater
 *
 */
public class LiveActivity extends Activity{
	
	/**用于标记比赛日第一天*/
	private static String MATCH_FIRST_DATE = "2012-07-26";
	/**用于判断比赛是否结束的比赛时间间隔*/
	private static long MATCH_END_TIME_INTERVAL = 10*60*60*1000;
	/**用于判断比赛是否开始的时间间隔*/
	private static long MATCH_START_TIME_INTERVAL = 2*60*60*1000;
		
	/**做match表的操作类*/
	private MatchDBDAO db;
	/**初始化MatchActivity，用于获取服务端比赛信息*/
	private MatchActivity matchActivity;
	/**用于保存需要查看所对应的日期*/
	private String OPERATED_DATE;
	/**这个List用于保存直播中的比赛*/
	private List<Match> livingMatchs;
	/**这个List用于保存即将直播的比赛*/
	private List<Match> willLiveMatchs;
	/**这个List用于保存可以查看直播实录的比赛*/
	private List<Match> liveEndMatchs;
	
	private LayoutInflater mInflater;
	
	/**相关显示的view*/
	private ListView livingList;
	private ListView willLiveList;
	private ListView liveEndList;

	public LiveActivity() {
		OPERATED_DATE=SystemUtil.getCurrentDate();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.live);
		db = new MatchDBDAO(this);
		matchActivity = new MatchActivity();
		livingMatchs = new ArrayList<Match>();
		willLiveMatchs = new ArrayList<Match>();
		liveEndMatchs = new ArrayList<Match>();
//		LogUtil.v("DAXIAMAO",SystemUtil.getCurrentDate());
//		LogUtil.v("DAXIAMAO",SystemUtil.getCurrentTime());
//		test();
		init();
		
	}
	
	private void init(){
		livingList = (ListView) findViewById(R.id.living_list);
		willLiveList =(ListView) findViewById(R.id.will_live_list);
		liveEndList = (ListView) findViewById(R.id.live_end_list);
		
		mInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		
		new matchMsgAsyncTask().execute(null);
	}
	
	
	
	/**
	 * 加载数据，分别设置需要的比赛日的即将比赛，正在直播的比赛，已经比赛完的比赛信息
	 */
	private void loadData(){
		if(SystemUtil.checkNet(this)){
			ArrayList<Match> matchs = null;
			if(OPERATED_DATE.compareTo(MATCH_FIRST_DATE)<0){
				matchs = (ArrayList<Match>) matchActivity.analyze(matchActivity.getServerData(MATCH_FIRST_DATE));
				OPERATED_DATE = MATCH_FIRST_DATE;
			}else{
				matchs = (ArrayList<Match>) matchActivity.analyze(matchActivity.getServerData(OPERATED_DATE));
			}
			if(matchs!=null){
				for(Match match:matchs){
					db.addOrUpdate(match);
				}
			}
		}
		
		Cursor c = db.query(OPERATED_DATE);
		if(c.moveToFirst()){
			do{					
				Match m = new Match(c.getInt(0));
				m.setBjDate(c.getString(1));
				m.setBjTime(c.getString(2));
//				m.setLondonDate(c.getString(3));
//				m.setLondonTime(c.getString(4));
//				m.setPartOfProject(new MatchProject(c.getInt(5)));
				m.setName(c.getString(6));
				m.setHasVideoLive(c.getInt(7));
				m.setHasTextLive(c.getInt(8));
				LogUtil.v("MAOXIA", m.getBjDate()+"   "+m.getBjTime()+"   "+m.getName());
				String matchDatetime = c.getString(1)+" "+c.getString(2);
				String curDatetime = SystemUtil.getCurrentDate()+" "+SystemUtil.getCurrentTime();
				long timeInterval = compareDatetime(matchDatetime, curDatetime);
				if(c.getInt(8)==1){
					if(timeInterval>(0-MATCH_START_TIME_INTERVAL)&&timeInterval<MATCH_END_TIME_INTERVAL){
						livingMatchs.add(m);
					}else if(timeInterval<(0-MATCH_START_TIME_INTERVAL)){
						willLiveMatchs.add(m);
					}else{
						liveEndMatchs.add(m);
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
	
	
	private class matchMsgAsyncTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			loadData();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			livingList.setAdapter(new matchMsgAdapter(livingMatchs));
			willLiveList.setAdapter(new matchMsgAdapter(willLiveMatchs));
			liveEndList.setAdapter(new matchMsgAdapter(liveEndMatchs));
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView==null){
				convertView = mInflater.inflate(R.layout.live_msg, null);
				holder = new ViewHolder();
				holder.liveTimeTv = (TextView) convertView.findViewById(R.id.live_time);
				holder.liveNameTv = (TextView) convertView.findViewById(R.id.live_name);
				holder.textLiveEntrance = (TextView) convertView.findViewById(R.id.textlive_entrance);
				holder.videoLiveEntrance = (TextView) convertView.findViewById(R.id.video_entrance);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			Match m = matchs.get(position);
			String mBjTime = m.getBjTime();
			holder.liveTimeTv.setText(mBjTime.substring(0,mBjTime.length()-3));
			holder.liveNameTv.setText("【"+m.getName()+"】");
			if(m.getHasTextLive()==0){
				holder.textLiveEntrance.setVisibility(View.GONE);
			}
			if(m.getHasVideoLive()==0){
				holder.videoLiveEntrance.setVisibility(View.GONE);
			}
			return convertView;
		}
		
	}

	static class ViewHolder{
		TextView liveTimeTv;
		TextView liveNameTv;
		TextView textLiveEntrance;
		TextView videoLiveEntrance;		
	}
//	private void test(){
//		loadData();
//		for(Match match:livingMatchs){
//			LogUtil.v("LIVING",match.getBjDate()+"   "+match.getBjTime()+"   "+match.getHasTextLive());
//		}
//		for(Match match:willLiveMatchs){
//			LogUtil.v("WILL",match.getBjDate()+"   "+match.getBjTime()+"   "+match.getHasTextLive());
//		}
//		for(Match match:liveEndMatchs){
//			LogUtil.v("END",match.getBjDate()+"   "+match.getBjTime()+"   "+match.getHasTextLive());
//		}
//	}
}
