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
	public static final String userTable="lo_user";
	private DBHelper db = null;
	
	public UserDBDAO(Context context){
		db = DBHelper.getInstance(context, Constants.OlympicDB);
		db.open();
	}
	
	/**
	 * 插入lo_user数据的方法
	 * @param medal奖牌榜一行的数据
	 */
	public void add(User user){
		ContentValues cv = new ContentValues();
		cv.put("_id", user.getId());
		cv.put("_unick", user.getUnick());
		cv.put("_questionScore", user.getQuestionScore());
		db.insert(userTable, cv);
	}
	
	/**
	 * 更新一条数据到lo_medal表里
	 * @param medal Medal实体类 表示更新的数据
	 */
	public void update(Medal medal){
		List<String> updates = new ArrayList<String>();
		List<String> matchs = new ArrayList<String>();
		matchs.add("_id="+"'"+db.formatSQL(medal.getId())+"'");
		updates.add("_id="+"'"+db.formatSQL(medal.getId())+"'");
		if(medal.getRanking()!=-1){
			updates.add("_ranking="+medal.getRanking());
		}
		if(medal.getPicture()!=null){
			updates.add("_picture="+"'"+db.formatSQL(medal.getPicture())+"'");
		}
		if(medal.getSimpleName()!=null){
			updates.add("_simpleName="+"'"+db.formatSQL(medal.getSimpleName())+"'");
		}
		if(medal.getGold()!=-1){
			updates.add("_gold="+medal.getGold());
		}
		if(medal.getSilver()!=-1){
			updates.add("_silver="+medal.getSilver());
		}
		if(medal.getCopper()!=-1){
			updates.add("_copper="+medal.getSilver());
		}
		if(medal.getTotal()!=-1){
			updates.add("_total="+medal.getTotal());
		}
//		db.update(medalTable,updates,matchs);			
	}
	
	/**
	 * 数据表里有记录采用更新，无记录采用插入操作
	 * @param medal
	 */
	public void addOrUpdate(Medal medal){
//		List<String> matchs = new ArrayList<String>();
//		matchs.add("_id="+"'"+db.formatSQL(medal.getId())+"'");
//		if(db.query(medalTable, null, matchs)!=null){
//			update(medal);
//		}else{
//			add(medal);
//		}
//		
	}
	
	/**
	 * 查询第几页的记录,其中查询的列包括ranking,picture,simpleName,gold,silver,copper,total
	 * @param page查询第几页
	 * @param maxResult每页多少记录
	 */
//	public Cursor queryPaging(int page,int maxResult){
//		List<String> columns = new ArrayList<String>();
//		columns.add("_ranking");
//		columns.add("_picture");
//		columns.add("_simpleName");
//		columns.add("_gold");
//		columns.add("_silver");
//		columns.add("_copper");
//		columns.add("_total");
//		int firstResult = (page-1)*maxResult;
//		return db.queryPaging(medalTable, null,firstResult, maxResult);
//	}
	
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
