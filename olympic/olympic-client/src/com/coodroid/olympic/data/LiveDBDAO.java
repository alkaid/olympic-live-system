package com.coodroid.olympic.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.coodroid.olympic.common.Constants;
import com.coodroid.olympic.common.DBHelper;
import com.coodroid.olympic.model.Live;
import com.coodroid.olympic.model.Match;
/**
 * 这个类包含了一系列直播模块的数据库操作
 * @author Cater
 *
 */
public class LiveDBDAO{
	
	public static final String liveTable="lo_textlive";
	private DBHelper db = null;
	
	public LiveDBDAO(Context context) {
		db = DBHelper.getInstance(context, Constants.OlympicDB);
		db.open();
	}
	
	/**
	 * 增加一条直播记录到数据库中，
	 * @param live一条直播记录
	 */
	public void add(Live live){
		//做比赛数据的插入操作
		ContentValues cvLive = new ContentValues();
		cvLive.put("_id", live.getId());
		cvLive.put("_matchId",live.getMatchId());
		cvLive.put("_serverTime", live.getServetTime());
		cvLive.put("_score", live.getScore());
		cvLive.put("_textTime", live.getTextTime());
		cvLive.put("_text", live.getText());
		db.insert(liveTable, cvLive);
	}
	
	/**
	 * 更新一条数据到lo_textLive表里
	 * @param medal live更新的直播记录
	 */
	public void update(Live live){
		
		//创建list用于增加
		List<String> updates = new ArrayList<String>();
		List<String> matchs = new ArrayList<String>();
		matchs.add("_id="+live.getId());
		updates.add("_id="+live.getId());
		if(live.getMatchId()!=-1){
			updates.add("_matchId="+live.getMatchId());
		}
		if(live.getServetTime()!=null){
			updates.add("_serverTime="+"'"+db.formatSQL(live.getServetTime())+"'");
		}
		if(live.getScore()!=null){
			updates.add("_score="+"'"+db.formatSQL(live.getScore())+"'");
		}
		if(live.getTextTime()!=null){
			updates.add("_textTime="+"'"+db.formatSQL(live.getTextTime())+"'");
		}
		if(live.getText()!=null){
			updates.add("_text="+"'"+db.formatSQL(live.getText())+"'");
		}
		db.update(liveTable, updates, matchs);			
	}
	/**
	 * 数据表里有记录采用更新，无记录采用插入操作
	 * @param live
	 */
	public void addOrUpdate(Live live){
		List<String> lives = new ArrayList<String>();
		lives.add("_id="+"'"+live.getId()+"'");
		if(db.query(liveTable, null, lives)!=null){
			update(live);
		}else{
			add(live);
		}
	}
	
	/**
	 * 根据lo_live匹配id的删除一条记录
	 * @param id 要删除的id的值
	 */
	public void delete(String id){
		List<String> deletes = new ArrayList<String>();
		deletes.add(id);
		db.delete(liveTable, deletes);
	}

	/**这个方法是用于查询直播表里某一场比赛某一页的记录，排列方式按倒序
	 * @param matchId要查询的比赛Id
	 * @param pageNum要查询第几页
	 * @param indexPerPage 每页几行
	 * @return
	 */
	public Cursor query(int matchId,int pageNum,int indexPerPage){
		return db.query("SELECT * FROM (SELECT * FROM("+"SELECT *  FROM "+liveTable+" WHERE _matchId="+matchId+
				" ORDER BY _id DESC"+")  LIMIT "+indexPerPage+" OFFSET "+indexPerPage*(pageNum-1)+") ORDER BY _id");
	}
	
	
	
	/**
	 * 根据比赛Id查询最大直播记录
	 * @param matchId
	 * @return
	 */
	public Cursor queryTextLiveId(int matchId){
		return db.query("SELECT MAX(_id) _id FROM "+liveTable+" WHERE _matchId="+matchId);
	}
	
	/**
	 * 用于关闭数据库操作
	 */
	public void close(){
		db.close();
	}
}
