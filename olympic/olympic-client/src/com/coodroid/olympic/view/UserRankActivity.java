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

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.coodroid.olympic.R;
import com.coodroid.olympic.common.Constants;
import com.coodroid.olympic.common.Global;
import com.coodroid.olympic.common.HttpRequest;
import com.coodroid.olympic.common.LogUtil;
import com.coodroid.olympic.common.SystemUtil;
import com.coodroid.olympic.data.UserDBDAO;
import com.coodroid.olympic.model.User;

public class UserRankActivity  extends BaseActivity{

	private UserDBDAO db;
	private LayoutInflater mInflater;
	private List<User> users;
	private User localUser;
	private RankAdapter adapter;
	private boolean isInRank = false;
	//初始化view
	private ListView userRankList;
	private LinearLayout localUserRankLayout;
	private TextView backBtn;
	private ImageView refresh;
	private ProgressBar refreshBar;
	private LinearLayout container;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_rank);
		init();
		
	}
	
	private void init(){
		//初始化view
		userRankList =(ListView) findViewById(R.id.user_rank_list);
		localUserRankLayout = (LinearLayout) findViewById(R.id.local_user_rank);
		backBtn = (TextView) findViewById(R.id.user_rank_back);
		refresh = (ImageView) findViewById(R.id.refresh_btn);
		refreshBar = (ProgressBar) findViewById(R.id.userAnswersProcessBar);
		container = (LinearLayout)((ActivityGroup)getParent()).getWindow().findViewById(R.id.containerBody);
		//初始化操作
		db = new UserDBDAO(this);
		mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		users = new ArrayList<User>();
		adapter = new RankAdapter();
		refresh();
		
		//监听操作
		refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				refresh();
			}
		});
		backBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserRankActivity.this,GuessActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				container.removeAllViews();
				container.addView(((ActivityGroup)UserRankActivity.this.getParent()).getLocalActivityManager()
							.startActivity("RankActivity", 
								intent)
								.getDecorView());
			}
		});
		
	}
	
	private void refresh(){
		refresh.setVisibility(View.GONE);
		refreshBar.setVisibility(View.VISIBLE);
		new UserRankAsyncTask().execute(null);
	}

	private void loadData(){
		if(SystemUtil.checkNet(this)){
			List<User> users = analyzeUsersRank(getServerData(0, 10));
			localUser = analyzeUserPoint(getLocalUserServerData());

			if(users!=null){
				for(User user:users){
					db.addOrUpdate(user);
				}
			}
		}
		users.clear();
		Cursor c = db.queryPaging(1, 10);

		if(c.moveToFirst()){
			do{					
				User u = new User(c.getString(0));
				u.setRank(c.getInt(1));
				u.setQuestionScore(c.getInt(2));
//				LogUtil.v("MAOXIA", u.getUnick()+"   "+u.getRank()+"   "+u.getQuestionScore());
				users.add(u);
			}while(c.moveToNext());
		}
	}

	
	/**
	 * 发送请求 用于获得当前用户的积分
	 * @param url
	 * @return
	 */
	private String getLocalUserServerData(){
			String rankServerData = null;
		try {
			HttpRequest request=new HttpRequest();
			rankServerData=request
					.setConnectionTimeout(3000)
					.setRetryCount(0)
					.setCookieStore(global.cookieStore)
					.setUrl(Constants.url.user.userPoint)
					.setMethod(HttpRequest.METHOD_GET)
					.setCharset("utf-8")
					.getContent();
			LogUtil.i(rankServerData);
		} catch (MalformedURLException e) {
			LogUtil.e(e);
		} catch (IOException e) {
			LogUtil.e(e);
		}
		return rankServerData;
	}
	
	/**
	 * 发送请求 user排名的数据
	 * @return JSON用于解析
	 */
	
	private String getServerData(int startNum,int perPageIndex){
		String rankServerData = null;
    	try {
    		Map<String, String> params = new HashMap<String, String>();
			if(startNum>=0){
				params.put("p1", startNum+"");
			}
			if(perPageIndex>=0){
				params.put("l", perPageIndex+"");
			}
			HttpRequest request=new HttpRequest();
			rankServerData=request
					.setConnectionTimeout(3000)
					.setRetryCount(0)
					.setCookieStore(global.cookieStore)
					.setUrl(Constants.url.api.points)
					.setMethod(HttpRequest.METHOD_GET)
					.setParams(params)
					.setCharset("utf-8")
					.getContent();
			LogUtil.i(rankServerData);
		} catch (MalformedURLException e) {
			LogUtil.e(e);
		} catch (IOException e) {
			LogUtil.e(e);
		}
		return rankServerData;
		
	}
	
	/**
	 * 将服务端返回的数据进行解析获得
	 * @param rankServerData 服务端的Json
	 * @return 返回多条user记录
	 */
	public List<User> analyzeUsersRank(String rankServerData){
		if(rankServerData!=null){
			List<User> users = new ArrayList<User>();
			try {
				JSONObject matchJSON = new JSONObject(rankServerData);
				if(matchJSON.getString("status").equals("2")){
					JSONArray userArray = matchJSON.getJSONArray("data");
					for(int i=0;i<userArray.length();i++){
						JSONObject userObject = (JSONObject) userArray.opt(i);
						User m = new User(userObject.getString("unick"));
						m.setRank(userObject.getInt("rank"));
						m.setQuestionScore(userObject.getInt("questionScore"));
						users.add(m);
					}
				}
			} catch (JSONException e) {
				LogUtil.e(e);
				LogUtil.e("JSON解析异常");
			}
			return users;
		}else{
			return null;
		}
	}
	
	/**
	 * 将服务端返回的数据进行解析获得
	 * @param rankServerData 服务端的Json
	 * @return 返回多条user记录
	 */
	public User analyzeUserPoint(String rankServerData){
		User m = null;
		if(rankServerData!=null){			
			try {
				JSONObject userJSON = new JSONObject(rankServerData);
				if(userJSON.getString("status").equals("2")){
					JSONObject userObject = userJSON.getJSONObject("data"); 
						m = new User(userObject.getString("unick"));
						m.setRank(userObject.getInt("rank"));
						m.setQuestionScore(userObject.getInt("questionScore"));
				}
			} catch (JSONException e) {
				LogUtil.e(e);
				LogUtil.e("JSON解析异常");
			}
			return m;
		}else{
			return null;
		}
	}
	
	private class RankAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return users.size();
		}

		@Override
		public Object getItem(int position) {
			return users.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView==null){
				convertView = mInflater.inflate(R.layout.user_rank_item,null); 
				holder = new ViewHolder();
				holder.unick = (TextView) convertView.findViewById(R.id.user_rank_unick);
				holder.questionScore = (TextView) convertView.findViewById(R.id.user_rank_questionScore);
				holder.rank = (TextView) convertView.findViewById(R.id.user_rank_rank);
				holder.userRankItem = (LinearLayout) convertView.findViewById(R.id.user_rank_item);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();				
			}
			final User u = users.get(position);
			holder.unick.setText(u.getUnick());
			holder.questionScore.setText(u.getQuestionScore()+"分");
			holder.rank.setText(u.getRank()+"");
			if(u.getUnick()!=null&&localUser!=null&&u.getUnick().equals(localUser.getUnick())){
//				holder.unick.setTextColor(R.color.live_select_color);
//				holder.questionScore.setTextColor(R.color.live_select_color);
//				holder.rank.setTextColor(R.color.live_select_color);
				holder.userRankItem.setBackgroundColor(Color.WHITE);
				isInRank = true;
			};
			return convertView;
		}
		
	}
	
	static class ViewHolder{
		TextView unick;
		TextView rank;
		TextView questionScore;
		LinearLayout userRankItem;
	}
	
	private class UserRankAsyncTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			loadData();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			userRankList.setAdapter(adapter);
			if(isInRank==false&&localUser!=null){
				localUserRankLayout.setVisibility(View.VISIBLE);
				LinearLayout userRankItem = (LinearLayout) mInflater.inflate(R.layout.user_rank_item, null);
				TextView localUserUnick = (TextView) userRankItem.findViewById(R.id.user_rank_unick);
				TextView localUserQuestionScore = (TextView) userRankItem.findViewById(R.id.user_rank_questionScore);
				TextView localUserRank = (TextView) userRankItem.findViewById(R.id.user_rank_rank);
				localUserUnick.setText(localUser.getUnick()+"");
				localUserQuestionScore.setText(localUser.getQuestionScore()+"");
				localUserRank.setText(localUser.getRank()+"");
				userRankItem.setBackgroundColor(Color.WHITE);
				localUserRankLayout.addView(userRankItem);	
			}else{
				localUserRankLayout.setVisibility(View.GONE);
			}
			refresh.setVisibility(View.VISIBLE);
			refreshBar.setVisibility(View.GONE);
		}
		
	}
	
}
