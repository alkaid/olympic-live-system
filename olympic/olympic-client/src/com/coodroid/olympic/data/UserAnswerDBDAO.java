package com.coodroid.olympic.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.coodroid.olympic.common.Constants;
import com.coodroid.olympic.common.DBHelper;
import com.coodroid.olympic.model.Answer;
import com.coodroid.olympic.model.Question;

/**
 * 用做用户历史数据的操作
 * @author Joys
 *
 */
public class UserAnswerDBDAO {
	
	public static final String questionTable="lo_question";
	public static final String userAnswerTable="lo_user_answers";
	public static final String answer2questionTable="lo_answer2question";
	public static final String answerTable = "lo_answer";
	private DBHelper db = null;
	
	public UserAnswerDBDAO(Context context) {
		db = DBHelper.getInstance(context, Constants.OlympicDB);
		db.open();
	}
	
	/**
	 * 增加一条问题到相应的表中，包含的表有lo_question,lo_answer2question,lo_user_answer
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
				db.insert(answerTable, cvAnswer);
			}
		}
	
	}
	
	/**
	 * 更新一条数据到lo_question表里和lo_user_answer表里
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
	 * 用于更新用户答题
	 * @param question
	 */
	public void userAnswerUpdate(Question question){
		//更新用户答题记录
		Answer userAnswer = question.getAnswer();
		List<String> userAnswerUpdates = new ArrayList<String>();
		List<String> userAnswerMatchs = new ArrayList<String>();
		userAnswerMatchs.add("_questionId="+"'"+question.getId()+"'");
		List<String> userAnswerQueryMatchs = new ArrayList<String>();
		userAnswerQueryMatchs.add("_questionId="+"'"+question.getId()+"'");
		if(db.query(userAnswerTable, null, userAnswerQueryMatchs)!=null){
			if(userAnswer.getId()!=-1){
				userAnswerUpdates.add("_answerId="+userAnswer.getId());
			}
			if(userAnswer.getQuestionId()!=-1){
				userAnswerUpdates.add("_questionId="+userAnswer.getQuestionId());
			}
			if(userAnswer.getIsRight()!=-1){
				userAnswerUpdates.add("_isRight="+userAnswer.getIsRight());
			}
			db.update(userAnswerTable, userAnswerUpdates, userAnswerMatchs);
		}
	}
	
	public void userAnswerAdd(Question question){
		//插入用户答题记录
		ContentValues cvUserAnswer = new ContentValues();
		List<String> userAnswerMatchs = new ArrayList<String>();
		userAnswerMatchs.add("_questionId="+"'"+question.getId()+"'");
		if(db.query(userAnswerTable, null, userAnswerMatchs)==null){
			cvUserAnswer.put("_answerId", question.getAnswer().getId());
			cvUserAnswer.put("_questionId", question.getAnswer().getQuestionId());
			cvUserAnswer.put("_isRight", question.getAnswer().getIsRight());
			db.insert(userAnswerTable, cvUserAnswer);
		}
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
		}else{
			add(question);
		}
		List<String> userAnswerMatchs = new ArrayList<String>();
		userAnswerMatchs.add("_questionId="+"'"+question.getId()+"'");
		if(db.query(userAnswerTable, null, userAnswerMatchs)!=null){
			userAnswerUpdate(question);
		}else{
			userAnswerAdd(question);
		}
	}
	
	
	/**
	 * 根据日期查询出当天的竞猜题目和答案选项,按问题序号，答案序号排列
	 * @param date
	 * @return
	 */
	public Cursor queryAllUserAnswer(){
			String sql ="select lq._id,lq._date,lq._text,lq._score,lua._answerId," +
					"lua._isRight,la._id,la._order,la._text,lq._answerId from "+userAnswerTable+ " lua join " +
					questionTable+" lq on lua._questionId=lq._id join "+ answer2questionTable+
					" laq on lq._id=laq._questionId join "+answerTable+" la on laq._answerId=la._id order by lq._date desc,lq._id asc,la._order asc" ;			
			return db.query(sql);
	}
	
	public void deleteTable(){
		String sql = "delete * from "+userAnswerTable;
	}

	/**
	 * 用于关闭数据库操作
	 */
	public void close(){
		db.close();
	}
}
