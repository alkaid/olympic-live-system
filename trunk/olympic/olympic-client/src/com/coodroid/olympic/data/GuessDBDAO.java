package com.coodroid.olympic.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.coodroid.olympic.common.Constants;
import com.coodroid.olympic.common.DBHelper;
import com.coodroid.olympic.model.Answer;
import com.coodroid.olympic.model.Live;
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
		cvQuestion.put("_answerId", question.getAnswerId());
		cvQuestion.put("_score", question.getScore());
		db.insert(questionTable, cvQuestion);
		//插入问题答案关联表数据
		List<Answer> answers = question.getAnswers();
		for(int i=0;i<answers.size();i++){
			Answer answer = answers.get(i);
			cvAnswer2Question.put("_answerId", answer.getId());
			cvAnswer2Question.put("_questionId",question.getId());
			db.insert(answer2questionTable, cvAnswer2Question);
			//插入答案数据
			List<String> answerMatchs = new ArrayList<String>();
			answerMatchs.add("_id="+"'"+answer.getId()+"'");
			if(db.query(answerTable, null, answerMatchs)==null){
				cvAnswer.put("_id", answer.getId());
				cvAnswer.put("_order",answer.getOrder());
				cvAnswer.put("_text", answer.getText());
//				cvAnswer.put("_isRight", answer.getIsRight());
				db.insert(answerTable, cvAnswer);
			}
		}		
	}
	
	/**
	 * 更新一条数据到lo_question表里和lo_answer表里
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
			updates.add("_date="+"'"+db.formatSQL(question.getDate())+"'");
		}
		if(question.getText()!=null){
			updates.add("_text="+"'"+db.formatSQL(question.getText())+"'");
		}
		if(question.getAnswerId()!=null){
			updates.add("_answerId="+"'"+db.formatSQL(question.getAnswerId())+"'");
		}
		if(question.getScore()!=-1){
			updates.add("_score="+question.getScore());
		}
		List<Answer> answers = question.getAnswers();
		if(answers!=null){
			for(int i=0;i<answers.size();i++){
				Answer answer = answers.get(i);
				List<String> answerUpdates = new ArrayList<String>();
				List<String> answerMatchs = new ArrayList<String>();
				answerMatchs.add("_id="+answer.getId());
				List<String> answerQueryMatchs = new ArrayList<String>();
				answerQueryMatchs.add("_id="+"'"+answer.getId()+"'");
				if(db.query(answerTable, null, answerQueryMatchs)!=null){
					if(answer.getId()!=-1){
						answerUpdates.add("_id="+answer.getId());
					}
					if(answer.getOrder()!=-1){
						answerUpdates.add("_order="+answer.getOrder());
					}
//					if(answer.getQuestionId()!=-1){
//						answerUpdates.add("_questionId="+answer.getQuestionId());
//					}
					if(answer.getText()!=null){
						answerUpdates.add("_text="+"'"+db.formatSQL(answer.getText())+"'");
					}
						db.update(answerTable, answerUpdates, answerMatchs);
				}
			}
		}
		db.update(questionTable, updates, matchs);			
	}
	
	/**
	 * 数据表里有记录采用更新，无记录采用插入操作
	 * @param live
	 */
	public void addOrUpdate(Question question){
		List<String> matchs = new ArrayList<String>();
		matchs.add("_id="+"'"+question.getId()+"'");
		if(db.query(questionTable, null, matchs)!=null){
			questionUpdate(question);
//			List<Answer> answers = question.getAnswers();
//			for(int i=0;i<answers.size();i++){
//				List<String> answerMatchs = new ArrayList<String>();
//				answerMatchs.add("_id="+"'"+answers.get(i).getId()+"'");
//				if(db.query(answerTable, null, answerMatchs)!=null){
//					
//				}
//			}
		}else{
			add(question);
		}
	}
	
	
//	/**
//	 * 更新一条数据到lo_answer表里
//	 * @param question 一条问题
//	 */
//	public void answerUpdate(Question question){
//		List<String> updates = new ArrayList<String>();
//		List<String> matchs = new ArrayList<String>();
//		matchs.add("_id="+question.getId());
//		updates.add("_id="+question.getId());
//		if(question.getOrder()!=-1){
//			updates.add("_order="+question.getOrder());
//		}
//		if(question.getText()!=null){
//			updates.add("_text="+question.getText());
//		}
//		db.update(answerTable, updates, matchs);		
//	}
//	
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
	 * 根据日期查询出当天的竞猜题目和答案选项,按问题序号，答案序号排列
	 * @param date
	 * @return
	 */
	public Cursor query(String date){
		if(date!=null){
			String sql = "SELECT q._id,q._order,q._score,q._date,q._text,a._id,a._order,a._text,q._answerId FROM "+ 
					questionTable+" q JOIN "+ answer2questionTable+ " aq ON q._id = aq._questionId"+
					" JOIN lo_answer a ON aq._answerId = a._id WHERE q._date="+
					"'"+db.formatSQL(date)+"'"+" ORDER BY q._order,a._order";
			return db.query(sql);
		}else{
			return null;
		}
	}
	
	/**
	 * 用于关闭数据库操作
	 */
	public void close(){
		db.close();
	}
}
