package com.coodroid.olympic.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;

import com.coodroid.olympic.common.Constants;
import com.coodroid.olympic.common.DBHelper;
import com.coodroid.olympic.model.Question;
/**
 * 这个类包含了竞猜模块一系列的数据库操作
 * @author Cater
 *
 */
public class GuessDBDAO{
	
	public static final String questionTable="lo_question";
	public static final String answerTable="lo_answer";
	public static final String answer2questionTable="lo_answer2question";
	private DBHelper db = null;
	
	public GuessDBDAO(Context context) {
		db = DBHelper.getInstance(context, Constants.OlympicDB);
		db.open();
	}
	
	/**
	 * 增加一条问题到相应的表中，包含的表有lo_question,lo_answer2question,lo_answer
	 * @param question 一条问题
	 */
	public void add(Question question){
		ContentValues cvQuestion = new ContentValues();
		ContentValues cvAnswer = new ContentValues();
		ContentValues cvAnswer2Question = new ContentValues();
		//插入问题数据
		cvQuestion.put("_id", question.getId());
		cvQuestion.put("_order",question.getOrder());
		cvQuestion.put("_date", question.getDate());
		cvQuestion.put("_text", question.getText());
		cvQuestion.put("_answerId", question.getAnswer().getId());
		cvQuestion.put("_score", question.getScore());
		//插入问题答案关联表数据
		cvAnswer2Question.put("_answerId", question.getId());
		cvAnswer2Question.put("_questionId",question.getAnswer().getId());
		//插入答案数据
		cvAnswer.put("_id", question.getAnswer().getId());
		cvAnswer.put("_order",question.getAnswer().getOrder());
		cvAnswer.put("_text", question.getAnswer().getText());
		db.insert(questionTable, cvQuestion);
		db.insert(answer2questionTable, cvAnswer2Question);
		db.insert(answerTable, cvAnswer);
	}
	
	/**
	 * 更新一条数据到lo_question表里
	 * @param question 一条问题
	 */
	public void questionUpdate(Question question){
		List<String> updates = new ArrayList<String>();
		List<String> matchs = new ArrayList<String>();
		matchs.add("_id="+question.getId());
		updates.add("_id="+question.getId());
		if(question.getOrder()!=-1){
			updates.add("_order="+question.getOrder());
		}
		if(question.getDate()!=null){
			updates.add("_date="+question.getDate());
		}
		if(question.getText()!=null){
			updates.add("_text="+question.getText());
		}
		if(question.getAnswer().getId()!=-1){
			updates.add("_answerId="+question.getAnswer().getId());
		}
		if(question.getScore()!=-1){
			updates.add("_score="+question.getScore());
		}
		db.update(questionTable, updates, matchs);			
	}
	
	/**
	 * 更新一条数据到lo_answer表里
	 * @param question 一条问题
	 */
	public void answerUpdate(Question question){
		List<String> updates = new ArrayList<String>();
		List<String> matchs = new ArrayList<String>();
		matchs.add("_id="+question.getId());
		updates.add("_id="+question.getId());
		if(question.getOrder()!=-1){
			updates.add("_order="+question.getOrder());
		}
		if(question.getText()!=null){
			updates.add("_text="+question.getText());
		}
		db.update(answerTable, updates, matchs);		
	}
	
	/**
	 * 查询逻辑模糊
	 */
//	/**
//	 * 查询第几页的记录
//	 * @param page第几页
//	 * @param resultMax每页多少记录
//	 */
//	public Cursor queryPaging(int page,int maxResult){
//		int firstResult = (page-1)*maxResult;
//		return db.queryPaging(liveTable, firstResult, maxResult);
//	}
	
	/**
	 * 用于关闭数据库操作
	 */
	public void close(){
		db.close();
	}
}
