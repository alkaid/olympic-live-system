package com.coodroid.olympic.model;

/**
 * 一个问题相关实体类
 * @author Cater
 *
 */

public class Question {
	/** 一个问题的唯一id */
	private int id;
	/** 问题的序号 */
	private int order;
	/** 问题存在于哪天 */
	private String date;
	/** 问题的具体内容 */
	private String text;
	/** 问题答案对应的答案 */
	private Answer answer;
	/** 问题所对应的分数 */
	private int score;
	
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

	public Answer getAnswer() {
		return answer;
	}

	public void setAnswer(Answer answer) {
		this.answer = answer;
	}

	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	
	
	

}
