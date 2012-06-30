package com.coodroid.olympic.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

import com.coodroid.olympic.common.Constants;
import com.coodroid.olympic.model.Medal;

/**
 * 
 * @author Cater
 * 这个类包含了一系列奖牌榜模块的数据库操作
 *
 */
public class MedalDBUtil extends DBUtil{
	
	public static final String medalTable="lo_medal";
	
	public MedalDBUtil(){
		super(Constants.OlympicDB);
	}
	
	/**
	 * 将一条数据插入lo_medal的方法
	 */
	public void add(Medal medal){
		ContentValues cv = new ContentValues();
		cv.put("_id", medal.getId());
		cv.put("_ranking", medal.getRanking());
		cv.put("_picture", medal.getPicture());
		cv.put("_simpleName", medal.getSimpleName());
		cv.put("_gold", medal.getGold());
		cv.put("_silver", medal.getSilver());
		cv.put("_copper", medal.getCopper());
		cv.put("_total", medal.getTotal());
		insert(medalTable, cv);
	}
	
	/**
	 * 更新一条数据到lo_medal表里
	 * @param medal Medal实体类 表示更新的数据
	 */
	public void update(Medal medal){
		ContentValues cv = new ContentValues();
		List<String> columns = new ArrayList<String>();
		List<String> m = new ArrayList<String>();
		
//		Cursor c = query(medalTable, "_id",medal.getId());
		cv.put("_id", medal.getId());
		cv.put("_ranking", medal.getRanking());
		cv.put("_picture", medal.getPicture());
		cv.put("_simpleName", medal.getSimpleName());
		cv.put("_gold", medal.getGold());
		cv.put("_silver", medal.getSilver());
		cv.put("_copper", medal.getCopper());
		cv.put("_total", medal.getTotal());
	}

}
