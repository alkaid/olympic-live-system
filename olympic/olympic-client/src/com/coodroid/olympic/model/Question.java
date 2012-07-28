package com.coodroid.olympic.model;

import java.util.List;

/**
 * 一个问题相关实体类
 * @author Cater
 *
 */

public class Question extends Model{
	private static final long serialVersionUID = -4704346955675851460L;
	/** 一个问题的唯一id */
	private int id;
	/** 问题的序号 */
	private int order;
	/** 问题存在于哪天 */
	private String date;
	/** 问题的具体内容 */
	private String text;
	/**问题对应的正确答案id*/
	private String answerId;
	/** 问题对应的答案 */
	private List<Answer> answers;
	/** 问题所对应的分数 */
	private int score;
	/**用户所选择的答案，未选答案为null*/
	private Answer answer;
	public Question(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}

	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}

	public String getAnswerId() {
		return answerId;
	}

	public void setAnswerId(String answerId) {
		this.answerId = answerId;
	}

	public Answer getAnswer() {
		return answer;
	}

	public void setAnswer(Answer answer) {
		this.answer = answer;
	}
	
}
