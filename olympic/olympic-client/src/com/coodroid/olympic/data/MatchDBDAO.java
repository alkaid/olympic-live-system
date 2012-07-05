package com.coodroid.olympic.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.coodroid.olympic.common.Constants;
import com.coodroid.olympic.common.DBHelper;
import com.coodroid.olympic.model.Match;

/**
 * 这个类包含了一系列赛程表模块的数据库的操作
 * @author Cater
 *
 */
public class MatchDBDAO{
	
	public static final String matchTable="lo_match";
	public static final String projectTable="lo_project";
	private DBHelper db = null;
	
	public MatchDBDAO(Context context) {
		db = DBHelper.getInstance(context, Constants.OlympicDB);
		db.open();
	}
	
	/**
	 * 增加一场比赛到数据库中，涉及到lo_match和;o_project表
	 * @param match一场比赛一行的数据
	 */
	public void add(Match match){
		//做遍历查询是否数据库存在次比赛所对应的项目,不存在做插入操作，存在跳过
		Cursor cProject = db.query(projectTable,null,null);
		int projectIdInServer = match.getPartOfProject().getId();
		if(cProject.moveToFirst()){
			do{
				int columnIndex = cProject.getColumnIndex("_id");
				if(projectIdInServer==cProject.getInt(columnIndex)){
					ContentValues cvProject = new ContentValues();
					cvProject.put("_id", projectIdInServer);
					cvProject.put("_name", match.getPartOfProject().getName());
					db.insert(projectTable, cvProject);
					break;
				}
			}while(cProject.moveToNext());
		}
		//做比赛数据的插入操作
		ContentValues cvMatch = new ContentValues();
		cvMatch.put("_id", match.getId());
		cvMatch.put("_datetime",match.getDatetime());
		cvMatch.put("_name", match.getName());
		cvMatch.put("_hasTextLive", match.getHasTextLive());
		cvMatch.put("_hasVideoLive", match.getHasVideoLive());
		cvMatch.put("_videoChannel", match.getVideoChannel());
		db.insert(matchTable, cvMatch);
	}
	
	/**
	 * 更新一条数据到lo_match表里
	 * @param medal Medal实体类 表示更新的数据
	 */
	public void update(Match match){
		//创建list用于增加
		List<String> updates = new ArrayList<String>();
		List<String> matchs = new ArrayList<String>();
		matchs.add("_id="+match.getId());
		updates.add("_id="+match.getId());
		if(match.getDatetime()!=null){
			updates.add("_datetime="+match.getDatetime());
		}
		if(match.getName()!=null){
			updates.add("_name="+match.getName());
		}
		if(match.getHasTextLive()!=-1){
			updates.add("_hasTextLive="+match.getHasTextLive());
		}
		if(match.getHasVideoLive()!=-1){
			updates.add("_hasVideoLive="+match.getHasVideoLive());
		}
		if(match.getVideoChannel()!=null){
			updates.add("_videoChannel="+match.getVideoChannel());
		}
		db.update(matchTable, updates, matchs);			
	}
	
	/**
	 * 查询第几页的记录
	 * @param page第几页
	 * @param resultMax每页多少记录
	 */
	public Cursor queryPaging(int page,int maxResult){
		int firstResult = (page-1)*maxResult;
		return db.queryPaging(matchTable,null, firstResult, maxResult);
	}
	
	/**
	 * 用于关闭数据库操作
	 */
	public void close(){
		db.close();
	}
}
