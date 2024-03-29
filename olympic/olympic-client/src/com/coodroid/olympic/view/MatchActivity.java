package com.coodroid.olympic.view;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coodroid.olympic.R;
import com.coodroid.olympic.common.Constants;
import com.coodroid.olympic.common.HttpRequest;
import com.coodroid.olympic.common.LogUtil;
import com.coodroid.olympic.common.SystemUtil;
import com.coodroid.olympic.data.MatchDBDAO;
import com.coodroid.olympic.model.Match;
import com.coodroid.olympic.model.MatchProject;
import com.coodroid.olympic.ui.CategoryAdapter;

/**
 * 赛程表的Activity
 * @author Cater
 *
 */
public class MatchActivity extends BaseActivity{
	/** 赛程表UI显示的赛程日 */
	private String[] dates = {"7-26","7-27","开幕式","7-29","7-30","7-31","8-01",
			"8-02","8-03","8-04","8-05","8-06","8-07","8-08","8-09","8-10","8-12","闭幕式"};
	/**用于点击匹配数据库的日期*/
	private String[] matchDates ={"2012-07-26","2012-07-27","2012-07-28","2012-07-29","2012-07-30","2012-07-31","2012-08-01","2012-08-02",
			"2012-08-03","2012-08-04","2012-08-05","2012-08-06","2012-08-07","2012-08-09","2012-08-10","2012-08-11","2012-08-12","2012-08-13"};
	
	/** 被选中的赛程日 */
	private int position = -1 ;
	
	private final static int PROJECT_ORDER=1;
	private final static int TIME_ORDER=2;
	
	/** 赛程表里左列的日期list */
	private ListView matchDateList;
	/** 赛程表里右列的赛程内容list */
	private ListView matchList;
	/** 赛程标题下拉菜单*/
	private RelativeLayout matchTitle;
	/**赛程标题右边刷新控件*/
	private ImageView refreshBtn;
	/**缓冲的圆形滚动条*/
	private ProgressBar matchProgressBar;
	private ProgressBar matchContentProgressBar;
	
	/**用于存放用于显示的比赛（按时间排列的）*/
	private List<Match> matchsTimeOrder;
	/**用于存放用于显示的比赛（按项目排列的）,其中Integer用ProjectID做KEY*/
	private Map<Integer, List<Match>> matchsProOrder;
	/**用于保存项目的信息，用于项目排列*/
	private List<MatchProject> projects;
	
	private MatchDBDAO db;
	private LayoutInflater mInflater;
	
	private int orderShow = PROJECT_ORDER;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.match);
		init();
	}
	
	/**
	 * 初始化需要的对象
	 */
	private void init(){
		//初始化view
		mInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		matchDateList = (ListView) findViewById(R.id.match_date);
		matchList = (ListView) findViewById(R.id.match_list);
		matchTitle = (RelativeLayout) findViewById(R.id.match_title);
		refreshBtn = (ImageView) findViewById(R.id.refresh_btn);
		matchProgressBar = (ProgressBar) findViewById(R.id.matchTitleProcessBar);
		matchContentProgressBar =(ProgressBar) findViewById(R.id.matchContentProcessBar); 
		
		//初始化其他对象
		db = new MatchDBDAO(this);
		matchsTimeOrder = new ArrayList<Match>();
		projects = new ArrayList<MatchProject>();
		matchsProOrder = new HashMap<Integer, List<Match>>();
		ClickEvent clickEvent = new ClickEvent();
		//设计其他需要的操作
		setMatchDateList();
		//初始化已进入界面所取的数据
		position = 0;
		updateMatchContent();
		matchTitle.setOnClickListener(clickEvent);
		//监听刷新操作
		refreshBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				updateMatchContent();				
			}
		});
	}	


	
	/**
	 * 设置比赛时间表
	 */
	private void setMatchDateList(){
		final MatchDataAdapter adapter = new MatchDataAdapter();
		matchDateList.setAdapter(adapter);
		matchDateList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				position = arg2;
				adapter.setSelectItem(arg2);
				updateMatchContent();
				adapter.notifyDataSetInvalidated();
//				arg1.setBackgroundResource(R.color.press);
			}
			
		});
	}
	
	/**
	 * 设置比赛内容的list表
	 */
	private void setMatchContent(int groupByWhat){
		matchList.setDivider(null);
		if(groupByWhat==TIME_ORDER){
			//按时间排列进行的操作
			OrderByAdpter adapter = new OrderByAdpter(matchsTimeOrder);
			matchList.setAdapter(adapter);
		}else if(groupByWhat==PROJECT_ORDER){
			//按项目分类进行的操作
			CategoryAdapter cAdapter = new CategoryAdapter() {				
				@Override
				protected View getTitleView(String title, int index, View convertView,
						ViewGroup parent) {
		            TextView titleView;  		              
		            if (convertView == null) {  
		            	titleView  = (TextView)getLayoutInflater().inflate(R.layout.match_project_title, null);
		            } else {  
		                titleView = (TextView)convertView;  
		            }  		              
		            titleView.setText(title);
		            LogUtil.v("MAOXIA",title);
		            titleView.setBackgroundColor(Color.rgb(219, 238, 244));
		            return titleView;  
				}
			};
			
			for(MatchProject project:projects){
				LogUtil.v("MAOXIAXIA",project.getName());
				cAdapter.addCategory(project.getName(), new OrderByAdpter(matchsProOrder.get(project.getId())));
			}		
			matchList.setAdapter(cAdapter);
			
		}
	}
		

	/**
	 * 加载数据，包括获取JSON，分析JSON，插入或者更新数据库，根据点击的date
	 * 值查询相应的数据增加到缓存中供显示到matchlist中
	 * @param groupByWhat以什么方式进行排列
	 */
	private void loadData(int groupByWhat){
		if(SystemUtil.checkNet(this)){
			ArrayList<Match> matchs = null;
			//根据日期到服务器匹配数据
			if(position!=-1){
				matchs = (ArrayList<Match>) analyze(getServerData(matchDates[position]));
			}else{
				matchs = (ArrayList<Match>) analyze(getServerData(null));
			}
			if(matchs!=null){
				for(Match match:matchs){
					db.addOrUpdate(match);
				}
			}
		}
		
		if(groupByWhat==TIME_ORDER){
			Cursor c = db.query(matchDates[position]);
			if(c.moveToFirst()){
				do{	
					
					Match m = new Match(c.getInt(0));
					m.setBjDate(c.getString(1));
					m.setBjTime(c.getString(2));
//					m.setLondonDate(c.getString(3));
//					m.setLondonTime(c.getString(4));
//					m.setPartOfProject(new MatchProject(c.getInt(5)));
					m.setName(c.getString(6));
					m.setHasVideoLive(c.getInt(7));
					m.setHasTextLive(c.getInt(8));
					LogUtil.v("MAOXIA", m.getBjDate()+"   "+m.getBjTime()+"   "+m.getName());
					matchsTimeOrder.add(m);
				}while(c.moveToNext());
			}
		}else if(groupByWhat==PROJECT_ORDER){
			Cursor c = db.groupQuery(matchDates[position]);
			if(c.moveToFirst()){
					int lastProjectId = -1;
				do{	
					if(c.getInt(5)!=lastProjectId){
						lastProjectId = c.getInt(5);
						MatchProject project = new MatchProject(lastProjectId);
						project.setName(c.getString(12));
						projects.add(project);
						matchsProOrder.put(lastProjectId, new ArrayList<Match>());						
					}
					Match m = new Match(c.getInt(0));
					m.setBjDate(c.getString(1));
					m.setBjTime(c.getString(2));
//					m.setLondonDate(c.getString(3));
//					m.setLondonTime(c.getString(4));
					m.setPartOfProject(new MatchProject(c.getInt(5)));
					m.setName(c.getString(6));
//					m.setHasVideoLive(c.getInt(7));
//					m.setHasTextLive(c.getInt(8));
					LogUtil.v("MAOXIA", m.getBjDate()+"   "+m.getBjTime()+"   "+m.getName());
					matchsProOrder.get(lastProjectId).add(m);					
				}while(c.moveToNext());
			}
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
	
	private void updateMatchContent(){
		refreshBtn.setVisibility(View.GONE);
//		matchList.setVisibility(View.GONE);
		matchProgressBar.setVisibility(View.VISIBLE);
		matchContentProgressBar.setVisibility(View.VISIBLE);
		new MatchTask(orderShow).execute(null);
	}
	
	private class MatchTask extends AsyncTask<Void, Void, Void>{
		private int groupByWhat;
		public MatchTask(int groupByWhat) {
			this.groupByWhat = groupByWhat;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			matchsTimeOrder.clear();
			matchsProOrder.clear();
			projects.clear();
			loadData(groupByWhat);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void Void) {
			setMatchContent(groupByWhat);
			matchProgressBar.setVisibility(View.GONE);
			matchContentProgressBar.setVisibility(View.GONE);
//			matchList.setVisibility(View.VISIBLE);
			refreshBtn.setVisibility(View.VISIBLE);
		}
		
	}
	
	/**
	 * 按顺序排列的adapter，其中内容包括比赛时间，比赛名称
	 * @author Cater
	 *
	 */
	private class OrderByAdpter extends BaseAdapter{
		
		List<Match> matchs;
		
		public OrderByAdpter(List<Match> matchs) {
			this.matchs = matchs;
		}
		
		@Override
		public int getCount() {
			return matchs.size();
		}

		@Override
		public Object getItem(int position) {
			return matchs.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView==null){
				convertView = mInflater.inflate(R.layout.match_order,null); 
				holder = new ViewHolder();
				holder.bjTime = (TextView) convertView.findViewById(R.id.match_time);
				holder.name = (TextView) convertView.findViewById(R.id.match_name);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();				
			}
			Match m = matchs.get(position);
			holder.bjTime.setText(m.getBjTime().substring(0, m.getBjTime().length()-3));
			holder.name.setText(m.getName());

//			LogUtil.v("MAOXIA",position+""+"    "+m.getName()+"  "+m.getBjTime());
			
			convertView.setBackgroundColor(Color.WHITE);
//	         int[] colors = { Color.WHITE, Color.rgb(219, 238, 244) };//RGB颜色 
//	         view.setBackgroundColor(colors[position % 2]);// 每隔item之间颜色不同
			 
			return convertView;
		}
		
	}
	
//	private String formatDate(String datetime){
//		try {
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			Date date = sdf.parse(datetime);
//			sdf.
//		} catch (ParseException e) {
//			LogUtil.e(e);
//		}
//	}
	
	/**
	 * 
	 * @author 比赛时间列表的adapter
	 *
	 */
	public class MatchDataAdapter extends BaseAdapter{

		private int selectItem = -1;	
		
		@Override
		public int getCount() {
			return dates.length;
		}

		@Override
		public Object getItem(int arg0) {
			return dates[arg0];
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
//			LogUtil.v("MAOXIA",arg0+"");
			View view = null;
			if(arg1!=null){
				view = arg1;
			}{
				LayoutInflater inflater = (LayoutInflater) MatchActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
				RelativeLayout date = (RelativeLayout) inflater.inflate(R.layout.match_date_list, null);
				TextView dateTxt = (TextView) date.findViewById(R.id.date_txt);
				dateTxt.setText(dates[arg0]);
				view = date;
			}
			if(arg0==selectItem){
				view.setBackgroundColor(Color.BLUE);
			}else{
				view.setBackgroundColor(Color.TRANSPARENT);
			}
			return view;
		}
		
		public void setSelectItem(int selectItem){
			this.selectItem = selectItem;
		}
		
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
				getPopuWindow().showAsDropDown(v,(screenWidth-dialogWidth)/2,0);
			}
		}
		
		private PopupWindow getPopuWindow(){
			
			View popupWindow_view = getLayoutInflater().inflate(R.layout.match_show_list, null,false);
			popupWindow = new PopupWindow(popupWindow_view, 220, 150, false);
			TextView timeTxt = (TextView) popupWindow_view.findViewById(R.id.timeOrder_show);
			TextView proTxt = (TextView) popupWindow_view.findViewById(R.id.proOrder_show);
			timeTxt.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					orderShow = TIME_ORDER;
					updateMatchContent();
					popupWindow.dismiss();
				}
			});
			proTxt.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					orderShow = PROJECT_ORDER;
					updateMatchContent();
					popupWindow.dismiss();
				}
			});
			
			screenWidth = MatchActivity.this.getWindowManager().getDefaultDisplay().getWidth();
			dialogWidth = popupWindow.getWidth();
			
			
			return popupWindow;
		}
		
	}
	
	static class ViewHolder{
		TextView bjTime;
		TextView name;
	}
}
