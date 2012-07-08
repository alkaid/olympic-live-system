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

import com.coodroid.olympic.R;
import com.coodroid.olympic.common.HttpUtils;
import com.coodroid.olympic.common.LogUtil;
import com.coodroid.olympic.common.SystemUtil;
import com.coodroid.olympic.data.MedalDBDAO;
import com.coodroid.olympic.model.Medal;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * 奖牌榜的Activity
 * @author Cater
 *
 */
public class MedalActivity extends Activity{
	
	public static final String medalUrl = "http://coodroid.com/ocdemo/index.php?route=olympic/medal";
	private MedalDBDAO db = null;
	private ListView medalList = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.medal);
		db = new MedalDBDAO(this);
		medalList = (ListView) findViewById(R.id.medalTable);
		updateListView();
	}
	
	/**
	 * 用于更新listview
	 */
	private void updateListView(){
		if(SystemUtil.checkNet(this)){
			//服务器匹配10条数据，看有没有更新，有获得数据并更新到数据库中
			ArrayList<Medal> medals = (ArrayList<Medal>) analyze(getServerData(0+"", 10+""));
			for(Medal medal:medals){
				db.addOrUpdate(medal);
			}			
		}
		Cursor cr = db.queryPaging(1, 10);
//		if(cr!=null){
//				cr.getColumnNames();
//				LogUtil.i(Integer.toString(cr.getColumnCount()));
//		}
		if(cr!=null){
			String[] columnNames = {cr.getColumnName(1),
						cr.getColumnName(2),cr.getColumnName(3),cr.getColumnName(4),cr.getColumnName(5),cr.getColumnName(6),cr.getColumnName(7)};
			MedalSimpleCursorAdapter adapter = new MedalSimpleCursorAdapter
					(this, R.layout.medal_list, cr, columnNames, new int[]{R.id.ranking,R.id.picture,R.id.simpleName,
							R.id.gold,R.id.silver,R.id.copper,R.id.total});
			medalList.setAdapter(adapter);
		}
	}
	
	/**
	 * 发送请求 返回Medal更新的数据
	 * @param index 开始索引项，从第几条开始请求
	 * @param length 请求返回的长度
	 * @return JSON用于解析
	 */
	private String getServerData(String index,String length){
		String medalServerData = null;
    	try {
			HttpUtils.setConnectionTimeout(3000);
			HttpUtils.setRetryCount(0);
			Map<String, String> params = new HashMap<String, String>();
			params.put("l", length);
			params.put("p1", index);
			medalServerData = HttpUtils.getContent(medalUrl, HttpUtils.METHOD_GET, params, "utf-8");
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
	private List<Medal> analyze(String medalServerData){
		List<Medal> medals = new ArrayList<Medal>();
		try {
			JSONObject medalJSON = new JSONObject(medalServerData);
			if(medalJSON.getString("staus").equals("2")){
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
	
//	/**
//	 * 解析请求返回的数据，并相应的更新数据库
//	 * @param url
//	 */
//	private void analyzeData(String url){
//    	try {
//			HttpUtils.setConnectionTimeout(3000);
//			HttpUtils.setRetryCount(0);
//			Map<String, String> params = new HashMap<String, String>();
//			params.put("p1", "0");
//			params.put("l", "10");
//			params.put("v","0");
//			String data = HttpUtils.getContent(url, "GET", params, "utf-8");
//			LogUtil.i(data);
//		} catch (MalformedURLException e) {
//			LogUtil.e(e);
//		} catch (IOException e) {
//			LogUtil.e(e);
//		}
//
//	}
//	
	
	
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
	
	
}
