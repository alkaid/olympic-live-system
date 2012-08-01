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

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.coodroid.olympic.R;
import com.coodroid.olympic.common.Constants;
import com.coodroid.olympic.common.HttpRequest;
import com.coodroid.olympic.common.LogUtil;
import com.coodroid.olympic.common.SystemUtil;
import com.coodroid.olympic.data.MedalDBDAO;
import com.coodroid.olympic.model.Medal;
import com.coodroid.olympic.ui.PullListView;
import com.coodroid.olympic.ui.PullListView.OnRefreshListener;

/**
 * 奖牌榜的Activity
 * @author Cater
 *
 */
public class MedalActivity extends BaseActivity{
	/** 一页显示奖牌榜的最大值*/
	private int maxPerPage = 20;
	
	private final static int ALL_MEDALS = 1;
	private final static int PART_MEDALS = 0;
	
	private MedalDBDAO db = null;
	MedalSimpleCursorAdapter adapter;
	
	private PullListView medalList = null;
	private TextView moreMedal = null;
	private ImageView refreshBtn = null;
	private ProgressBar medalTitleProgressBar = null;
	private ProgressBar medalContentProgressBar = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.medal);	
		findview();
		init();

	}
	
	/**
	 * 初始化操作，包括DB对象的建立，所有控件的监听
	 */
	private void init(){
		db = new MedalDBDAO(this);
		updateMedalContent(0);
		medalList.setonRefreshListener(new OnRefreshListener(){
			@Override
			public void onRefresh(){
				updateMedalContent(0);
			}			
		});
		refreshBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				updateMedalContent(0);
			}
		});
		moreMedal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				updateMedalContent(1);
			}
		});
	}
	
	/**
	 * 初始化所有需要的view
	 */
	private void findview(){
		medalList = (PullListView) findViewById(R.id.medalTable);
		moreMedal = (TextView) findViewById(R.id.more_medal);
		refreshBtn = (ImageView) findViewById(R.id.refresh_btn);
		medalTitleProgressBar = (ProgressBar) findViewById(R.id.medalTitleProgressBar);
		medalContentProgressBar= (ProgressBar) findViewById(R.id.medalContentProgressBar);
	}
	
	
	/**
	 * AsyncTask用于异步获取数据操作
	 * @author Cater
	 *
	 */
	private class RefreshAsyncTask extends AsyncTask<Void, Void, Cursor>{
		/** tag用于标记后台所需要做的操作*/
		int tag;
		public RefreshAsyncTask(int tag) {
			this.tag=tag;
		}
		
		@Override
		protected Cursor doInBackground(Void... params) {
			if(tag==PART_MEDALS){
				return LoadData(1);
			}else if(tag==ALL_MEDALS){
				return LoadData(0);
			}else{
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(Cursor cr) {
			if(cr!=null){
				String[] columnNames = {cr.getColumnName(1),
							cr.getColumnName(2),cr.getColumnName(3),cr.getColumnName(4),cr.getColumnName(5),cr.getColumnName(6),cr.getColumnName(7)};
				adapter = new MedalSimpleCursorAdapter
						(MedalActivity.this, R.layout.medal_list, cr, columnNames, new int[]{R.id.ranking,R.id.picture,R.id.simpleName,
								R.id.gold,R.id.silver,R.id.copper,R.id.total});
				medalList.setAdapter(adapter);
			}
			adapter.notifyDataSetChanged();
			medalList.onRefreshComplete();
			refreshBtn.setVisibility(View.VISIBLE);
			medalTitleProgressBar.setVisibility(View.GONE);
//			medalList.setVisibility(View.VISIBLE);
			medalContentProgressBar.setVisibility(View.GONE);
		}
		
	}
	
	
	/**
	 * 用于读取数据,如果page为0表示请求所有数据，返回所有数据
	 * @param page 第几页开始读
	 * 
	 */
	private Cursor LoadData(int page){
		
			if(SystemUtil.checkNet(this)){
				if(page>0){
					//服务器匹配20条数据，看有没有更新，有获得数据并更新到数据库中
					ArrayList<Medal> medals = (ArrayList<Medal>) analyze(getServerData((page-1)*maxPerPage+"", maxPerPage+""));
					if(medals!=null){
						for(Medal medal:medals){
							db.addOrUpdate(medal);
						}
						return db.queryPaging(page, maxPerPage);
					}else{
						return null;
					}
					
				}else if(page==0){
					ArrayList<Medal> medals = (ArrayList<Medal>) analyze(getServerData(null,null));
					if(medals!=null){						
						for(Medal medal:medals){
							db.addOrUpdate(medal);
						}
						return db.queryAll();
					}else{
						return null;
					}
				}else{
					return null;
				}
			}else{
				return null;
			}
		

	}
	
	/**
	 * 发送请求 返回Medal更新的数据
	 * @param index 开始索引项，从第几条开始请求
	 * @param length 请求返回的长度
	 * @return JSON用于解析
	 */
	public String getServerData(String index,String length){
		String medalServerData = null;
    	try {
			Map<String, String> params = new HashMap<String, String>();
			if(length!=null&&Integer.parseInt(length)>0){
				params.put("l", length);
			}
			if(index!=null&&Integer.parseInt(index)>=0){
				params.put("p1", index);
			}
			HttpRequest request=new HttpRequest();
			medalServerData=request
					.setConnectionTimeout(3000)
					.setRetryCount(0)
					.setCookieStore(global.cookieStore)
					.setUrl(Constants.url.api.medal)
					.setMethod(HttpRequest.METHOD_GET)
					.setParams(params)
					.setCharset("utf-8")
					.getContent();
			LogUtil.i(medalServerData);
		} catch (MalformedURLException e) {
			LogUtil.e(e);
		} catch (IOException e) {
			LogUtil.e(e);
		}
		return medalServerData;
		
	}
	
	/**
	 * 将服务端返回的数据进行解析获得
	 * @param medalServerData 服务端的Json
	 * @return 返回多条奖牌的记录
	 */
	public List<Medal> analyze(String medalServerData){
		if(medalServerData!=null){
			List<Medal> medals = new ArrayList<Medal>();
			try {
				JSONObject medalJSON = new JSONObject(medalServerData);
				if(medalJSON.getString("status").equals("2")){
					JSONArray medalArray = medalJSON.getJSONArray("data");
					for(int i=0;i<medalArray.length();i++){
						JSONObject medalObject = (JSONObject) medalArray.opt(i);
						Medal m = new Medal(medalObject.getString("id"));
						m.setRanking(Integer.parseInt(medalObject.getString("ranking")));
						m.setSimpleName(medalObject.getString("simpleName"));
						m.setGold(Integer.parseInt(medalObject.getString("gold")));
						m.setSilver(Integer.parseInt(medalObject.getString("silver")));
						m.setCopper(Integer.parseInt(medalObject.getString("copper")));
						m.setTotal(Integer.parseInt(medalObject.getString("total")));
						medals.add(m);
					}
				}
			} catch (JSONException e) {
				LogUtil.e(e);
			}
			return medals;
		}
		return null;
	}
		
	/**
	 * 用于显示奖牌表ListView的适配器，其中getView里的方法是制作多彩表格的。
	 * @author Cater
	 *
	 */
	private class MedalSimpleCursorAdapter extends SimpleCursorAdapter{

		public MedalSimpleCursorAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to) {
			super(context, layout, c, from, to);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LogUtil.v("MAOXIA",position+"");
			 View view = null; 
			         if (convertView != null) { 
			             view = convertView; 
			             // 使用缓存的view,节约内存 
			             // 当listview的item过多时，拖动会遮住一部分item，被遮住的item的view就是convertView保存着。 
			             // 当滚动条回到之前被遮住的item时，直接使用convertView，而不必再去new view() 
			         } else { 
			             view = super.getView(position, convertView, parent); 
			         } 
			         int[] colors = { Color.WHITE, Color.rgb(219, 238, 244) };//RGB颜色 
			         view.setBackgroundColor(colors[position % 2]);// 每隔item之间颜色不同 
			         return super.getView(position, view, parent); 
		}
		
	}
	
	/**
	 * 用于刷新medal奖牌榜的
	 * @param i i=0表示刷新几条奖牌，i=1表示刷新所有奖牌榜
	 */
	private void updateMedalContent(int i){
		refreshBtn.setVisibility(View.GONE);
		medalTitleProgressBar.setVisibility(View.VISIBLE);
//		medalList.setVisibility(View.GONE);
		medalContentProgressBar.setVisibility(View.VISIBLE);
		new RefreshAsyncTask(i).execute(null);
	}
	
	
}
