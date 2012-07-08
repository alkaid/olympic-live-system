package com.coodroid.olympic.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.coodroid.olympic.common.Constants;
import com.coodroid.olympic.common.DBHelper;
import com.coodroid.olympic.model.Match;
import com.coodroid.olympic.model.MatchProject;
import com.coodroid.olympic.model.Medal;

/**
 * 这个类包含了一系列赛程表模块的数据库的操作
 * @author Cater
 *
 */
public class MatchDBDAO{
	
	public static final String matchTable="lo_match";
	public static final String projectTable="lo_project";
	private DBHelper db = null;
	private SimpleDateFormat format;
	public MatchDBDAO(Context context) {
		db = DBHelper.getInstance(context, Constants.OlympicDB);
		db.open();
		format = new SimpleDateFormat();
	}
	
	/**
	 * 增加一场比赛到数据库中，涉及到lo_match和lo_project表
	 * @param match一场比赛一行的数据
	 */
	public void add(Match match){
		//做遍历查询是否数据库存在次比赛所对应的项目,不存在做插入操作，存在跳过
		Cursor cProject = db.query(projectTable,null,null);
		int projectIdInServer = match.getPartOfProject().getId();
		boolean isExistProjectID = false;
		if(cProject.moveToFirst()){
			do{
				int columnIndex = cProject.getColumnIndex("_id");
				if(projectIdInServer==cProject.getInt(columnIndex)){
					isExistProjectID = true;
					break;
				}
			}while(cProject.moveToNext());
		}
		if(!isExistProjectID){
			ContentValues cvProject = new ContentValues();
			cvProject.put("_id", projectIdInServer);
			cvProject.put("_name", match.getPartOfProject().getName());
			db.insert(projectTable, cvProject);
		}
		//做比赛数据的插入操作
		ContentValues cvMatch = new ContentValues();
		cvMatch.put("_id", match.getId());
		cvMatch.put("_bjDate",match.getBjDate());
		cvMatch.put("_bjTime",format.format(match.getBjTime()));
		cvMatch.put("_name", match.getName());
		cvMatch.put("_hasTextLive", match.getHasTextLive());
		cvMatch.put("_hasVideoLive", match.getHasVideoLive());
		cvMatch.put("_videoChannel", match.getVideoChannel());
		cvMatch.put("_londonDate", match.getLondonDate());
		cvMatch.put("_londonTime", match.getLondonDate());
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
		if(match.getBjDate()!=null){
			updates.add("_bjDate="+match.getBjDate());
		}
		if(match.getBjTime()!=null){
			updates.add("_bjTime="+match.getBjDate());
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
		if(match.getLondonDate()!=null){
			updates.add("_londonDatetime="+match.getLondonDate());
		}
		if(match.getLondonTime()!=null){
			updates.add("_londonDatetime="+match.getLondonTime());
		}
		db.update(matchTable, updates, matchs);			
	}
	
	/**
	 * 数据表里有记录采用更新，无记录采用插入操作
	 * @param medal
	 */
	public void addOrUpdate(Match match){
		List<String> matchs = new ArrayList<String>();
		matchs.add("_id="+"'"+match.getId()+"'");
		if(db.query(matchTable, null, matchs)!=null){
			update(match);
		}else{
			add(match);
		}
		
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
	 * 
	 * @param date日期(北京时间)根据日期查找当天的赛事,按之间顺序排列
	 * @return返回查询出的数据
	 */
//	public Cursor query(String date){
//		if(date!=null){
////			String sql = 
////			return db.query(matchTable, null, matchs);
//		}else{
//			return null;
//		}
//	}
	
	/**
	 * 
	 * @param allMatch 需要分组的Cursor
	 * @param comluns group by后的列名
	 * @return 分组后一个project 对应一些列值
	 */
/*	public Map<MatchProject, List<Match>> group(String date){
		
	}*/
	/**
	 * 用于关闭数据库操作
	 */
	public void close(){
		db.close();
	}
}
