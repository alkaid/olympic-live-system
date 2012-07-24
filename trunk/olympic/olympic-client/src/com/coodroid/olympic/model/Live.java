package com.coodroid.olympic.model;
/**
 * 一条直播相关实体类
 * @author Cater
 *
 */
public class Live extends Model{
	private static final long serialVersionUID = -7390634935428378909L;
	/** 直播数据的Id*/
	private int id;
	/** 比赛的Id*/
	private int matchId;
	/** 服务端的时间*/
	private String servetTime;
	/** 直播的比分*/
	private String score;
	/** 阶段或者时间，篮球比赛就是阶段第4节*/
	private String textTime;
	/** 直播具体内容*/
	private String text;
	
	public Live(int id) {
		this.id=id;
	}
	
	public int getId() {
		return id;
	}

	public int getMatchId() {
		return matchId;
	}

	public void setMatchId(int matchId) {
		this.matchId = matchId;
	}

	public String getServetTime() {
		return servetTime;
	}
	public void setServetTime(String servetTime) {
		this.servetTime = servetTime;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getTextTime() {
		return textTime;
	}
	public void setTextTime(String textTime) {
		this.textTime = textTime;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	
}
