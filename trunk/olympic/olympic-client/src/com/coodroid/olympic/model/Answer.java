package com.coodroid.olympic.model;

/**
 * 某题答案相关实体类
 * @author Cater
 *
 */
public class Answer {
	/** 一条答案的唯一id*/
	private int id;
	/** 答案所处的序列 */
	private int order;
	/** 答案的具体内容 */
	private String text;
	
	public Answer(int id) {
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
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	
}
