package com.coodroid.olympic.view;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.coodroid.olympic.R;
import com.coodroid.olympic.common.Constants;
import com.coodroid.olympic.common.HttpRequest;
import com.coodroid.olympic.common.LogUtil;
import com.coodroid.olympic.common.SystemUtil;
import com.coodroid.olympic.data.LiveDBDAO;
import com.coodroid.olympic.model.Live;
import com.coodroid.olympic.model.Match;

public class TextLiveActivity extends BaseActivity{
	
	/**显示的每一页几行*/
	private static final int INDEX_PER_PAGE = 10;
	/**自动刷新*/
	private static final int AUTO_REFRESH = 0;
	/**手动刷新*/
	private static final int MANUAL_REFRESH =1;
	/**自动刷新间隔时间*/
	private static final long REFRESH_TIME_INTERVAL = 10000;
	/**用于标识load数据完成*/
	private static final int LOAD_DATA_FINISH = 1;
	/**用于标识load数据开始*/
	private static final int LOAD_DATA_START =2;
	/**用于标识上一页标志可点击*/
	private static final int LAST_PAGE_ISONCLICK = 3;
	/**用于标识下一页标志可点击*/
	private static final int NEXT_PAGE_ISONCLICK = 4;
	
	/**显示第几页*/
	private int pageNum = 1;
	/**标记是否是最后一页*/
	private boolean isFinalPage =false;
	/**存放需要显示的文字直播记录*/
	private List<Live> textLives;
	/**刷新标识*/
	private int refreshState = 0;
	
	/**view初始化*/
	private ListView textLiveList;
	private Button refreshTab;
	private ImageView refreshBtn;
	private ProgressBar textliveProgressBar;
	private ImageView lastPageMark;
	private ImageView nextPageMark;
	
	/**用于操作直播的db*/
	private LiveDBDAO db;
	private LayoutInflater mInflater =null;
	private TextLiveAdapter tlAdapter = null;
	/**直播的比赛id*/
	private int matchId=-1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.text_live);
		init();
	}
	
	/**
	 * 初始化操作
	 */
	private void init(){
		//veiw初始化
		textLiveList = (ListView) findViewById(R.id.text_live_list);
		refreshTab = (Button) findViewById(R.id.refresh_tab);
		refreshBtn = (ImageView) findViewById(R.id.refresh_btn);
		textliveProgressBar = (ProgressBar) findViewById(R.id.liveProcessBar);
		lastPageMark = (ImageView) findViewById(R.id.text_live_last_page);
		nextPageMark = (ImageView) findViewById(R.id.text_live_next_page);
		//其他初始化
		db = new LiveDBDAO(this);
		mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		textLives = new ArrayList<Live>();
		tlAdapter = new TextLiveAdapter();
		//获取intent的操作
		Match match = (Match) global.getData(Constants.bundleKey.matchOfLive);
		matchId = match.getId();
		//控件的监听
		refreshTab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(refreshState==AUTO_REFRESH){
					refreshState=MANUAL_REFRESH;
					refreshTab.setText("手动刷新");
				}else if(refreshState==MANUAL_REFRESH){
					refreshState=AUTO_REFRESH;
					new AutoRefreshThread().start();
					refreshTab.setText("自动刷新");
				}
			}
		});
		refreshBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(refreshState==MANUAL_REFRESH){
					refreshBtn.setVisibility(View.GONE);
					textliveProgressBar.setVisibility(View.VISIBLE);
					new RefreshAsyncTask().execute(null);
				}
			}
		});
		new AutoRefreshThread().start();
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			new ISFinalPageAsyncTask().execute(null);
			//判断上一页逻辑
			if(pageNum==1){
				lastPageMark.setVisibility(View.GONE);
			}else{
				lastPageMark.setVisibility(View.VISIBLE);
				new Thread(){
						public void run() {
							lastPageMark.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									pageNum = pageNum -1;
									new RefreshAsyncTask().execute(null); 
								}
							});
							try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								LogUtil.e("下一页图标出现，休眠时间被打断");
							}
						};
				}.start();				
			}
			//判断下一页逻辑
			if(isFinalPage){
				nextPageMark.setVisibility(View.GONE);
			}else{
				nextPageMark.setVisibility(View.VISIBLE);
				new Thread(){
					public void run() {
						nextPageMark.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								pageNum = pageNum +1;
								new RefreshAsyncTask().execute(null); 
							}
						});
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							LogUtil.e("下一页图标出现，休眠时间被打断");
						}
					};
				}.start();				
				
			}
		}
		return super.onTouchEvent(event);
	}
	
	private class ISFinalPageAsyncTask extends AsyncTask<Void, Void, Boolean>{
				
		@Override
		protected Boolean doInBackground(Void... params) {
			Cursor c = db.query(matchId, pageNum+1, INDEX_PER_PAGE);
			if(c.getCount()==0){
				isFinalPage = true;
			}
			return isFinalPage;
		}
		
		private boolean isFinalPage(){
			return isFinalPage;
		}
	}
	
	/**
	 * AsyncTask用于异步获取数据操作
	 * @author Cater
	 *
	 */
	private class RefreshAsyncTask extends AsyncTask<Void, Void, Void>{
		
		@Override
		protected Void doInBackground(Void... params) {
			loadData(matchId+"");
			return null;
		}
		
		@Override
		protected void onPostExecute(Void v) {
			refreshBtn.setVisibility(View.VISIBLE);
			textliveProgressBar.setVisibility(View.GONE);
			textLiveList.setAdapter(tlAdapter);
		}
		
	}
	
	/**
	 * 加载数据，包括获取JSON，分析JSON，插入或者更新数据库，根据点击的date
	 * 值查询相应的数据增加到缓存中供显示到matchlist中
	 * @param groupByWhat以什么方式进行排列
	 */
	private void loadData(String matchId){
		//根据matchId取
		ArrayList<Live> lives = null;
		if(SystemUtil.checkNet(this)){
			Cursor maxTextLive = db.queryTextLiveId(Integer.parseInt(matchId));
			int maxTextLiveValue = 0; 
			if(maxTextLive.moveToFirst()){
				do{
					int columnIndex = maxTextLive.getColumnIndex("_id");
					maxTextLive.getInt(columnIndex);
				}while((maxTextLive.moveToNext()));
			}
			lives = (ArrayList<Live>) analyze(getServerData(matchId, maxTextLiveValue+""));
		}
		if(lives!=null){
			for(Live live:lives){
				db.addOrUpdate(live);
			}
		}
		textLives.clear();
		Cursor c = db.query(Integer.parseInt(matchId), pageNum, INDEX_PER_PAGE);
		if(c.moveToFirst()){
			do{
				Live l = new Live(c.getInt(0));
				l.setMatchId(c.getInt(1));
				l.setServetTime(c.getString(2));
				l.setScore(c.getString(3));
				l.setTextTime(c.getString(4));
				l.setText(c.getString(5));
				textLives.add(l);
			}while(c.moveToNext());
		}
	}

	
	
	/**
	 * 发送请求 返回textLive更新的数据
	 * @param matchId 请求的参数 表示直播比赛的Id
	 * @param textLiveId 表示最大直播id数
	 * @return JSON用于解析
	 */	
	public String getServerData(String matchId,String textLiveId){
		String matchServerData = null;
    	try {
			Map<String, String> params = new HashMap<String, String>();
			if(matchId!=null){
				params.put("matchId", matchId);
				params.put("id",textLiveId );
			}
			HttpRequest request=new HttpRequest();
			matchServerData=request
					.setConnectionTimeout(3000)
					.setRetryCount(0)
					.setCookieStore(global.cookieStore)
					.setUrl(Constants.url.api.textlive)
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
	public List<Live> analyze(String matchServerData){
		if(matchServerData!=null){
			List<Live> lives = new ArrayList<Live>();
			try {
				JSONObject matchJSON = new JSONObject(matchServerData);
				if(matchJSON.getString("status").equals("2")){
					JSONArray textLiveArray = matchJSON.getJSONArray("data");
					for(int i=0;i<textLiveArray.length();i++){
						JSONObject textLiveObject = (JSONObject) textLiveArray.opt(i);
						Live l = new Live(Integer.parseInt(textLiveObject.getString("id")));
						l.setMatchId(Integer.parseInt(textLiveObject.getString("matchId")));
						l.setServetTime(textLiveObject.getString("serverTime"));
						l.setScore(textLiveObject.getString("score"));
						l.setTextTime(textLiveObject.getString("textTime"));
						l.setText(textLiveObject.getString("text"));
						lives.add(l);
					}
				}
			} catch (JSONException e) {
				LogUtil.e(e);
				LogUtil.e("JSON解析异常");
			}
			return lives;
		}else{
			return null;
		}
	}
	
	private class TextLiveAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return textLives.size();
		}

		@Override
		public Object getItem(int position) {
			return textLives.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView==null){
				convertView = mInflater.inflate(R.layout.text_live_content_list,null); 
				holder = new ViewHolder();
				holder.text = (TextView) convertView.findViewById(R.id.text_live_text);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();				
			}
			Live l = textLives.get(position);
			holder.text.setText(l.getText());
			convertView.setBackgroundColor(Color.WHITE);
			return convertView;
		}
		
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what){
			case LOAD_DATA_FINISH:
				refreshBtn.setVisibility(View.VISIBLE);
				textliveProgressBar.setVisibility(View.GONE);
				break;
			case LOAD_DATA_START:
				refreshBtn.setVisibility(View.GONE);
				textliveProgressBar.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
			textLiveList.setAdapter(new TextLiveAdapter());
		};
	};
	
	private class AutoRefreshThread extends Thread{
		
		@Override
		public void run() {
			while(refreshState == AUTO_REFRESH){
				handler.sendEmptyMessage(LOAD_DATA_START);
				loadData(matchId+"");
				handler.sendEmptyMessage(LOAD_DATA_FINISH);
				try {
					Thread.sleep(REFRESH_TIME_INTERVAL);
				} catch (InterruptedException e) {
					LogUtil.e(e);
					LogUtil.e("自动刷新休眠时间被打断");
				}
			}
		}
	}
	
	static class ViewHolder{
		TextView text;
	} 
}
