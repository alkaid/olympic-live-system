package com.coodroid.olympic.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.coodroid.olympic.common.Constants;
import com.coodroid.olympic.common.DBHelper;
import com.coodroid.olympic.model.Medal;
import com.coodroid.olympic.model.User;

public class UserDBDAO {
	public static final String rankTable="lo_user_rank";
	private DBHelper db = null;
	
	public UserDBDAO(Context context){
		db = DBHelper.getInstance(context, Constants.OlympicDB);
		db.open();
	}
	
	/**
	 * 插入lo_user_rank数据的方法
	 * @param user用户一行的数据
	 */
	public void add(User user){
		ContentValues cv = new ContentValues();
		cv.put("_unick", user.getUnick());
		cv.put("_rank", user.getRank());
		cv.put("_questionScore", user.getQuestionScore());
		db.insert(rankTable, cv);
	}
	
	/**
	 * 更新一条数据到lo_user_rank表里
	 * @param medal Medal实体类 表示更新的数据
	 */
	public void update(User user){
		List<String> updates = new ArrayList<String>();
		List<String> matchs = new ArrayList<String>();
		matchs.add("_unick="+"'"+db.formatSQL(user.getUnick())+"'");
		updates.add("_unick="+"'"+db.formatSQL(user.getUnick())+"'");
		if(user.getRank()!=-1){
			updates.add("_rank="+user.getRank());
		}
		if(user.getQuestionScore()!=-1){
			updates.add("_questionScore="+user.getQuestionScore());
		}
		db.update(rankTable,updates,matchs);			
	}
	
	/**
	 * 数据表里有记录采用更新，无记录采用插入操作
	 * @param user
	 */
	public void addOrUpdate(User user){
		List<String> matchs = new ArrayList<String>();
		matchs.add("_unick="+"'"+db.formatSQL(user.getUnick())+"'");
		if(db.query(rankTable, null, matchs)!=null){
			update(user);
		}else{
			add(user);
		}
		
	}
	
	/**
	 * 查询第几页的记录,
	 * @param page查询第几页
	 * @param maxResult每页多少记录
	 */
	public Cursor queryPaging(int page,int maxResult){
		int firstResult = (page-1)*maxResult;
		String sql = "SELECT * FROM(SELECT * FROM "+rankTable+" order by _rank) LIMIT "+maxResult+" OFFSET "+firstResult;
		return  db.query(sql);
	}
	
//	/**
//	 * 用于查询所有的
//	 * @return
//	 */
//	public Cursor queryAll(){
////		return db.query(medalTable, null, null);
//	}
	/**
	 * 用于关闭数据库操作
	 */
	public void close(){
		db.close();
	}
}
