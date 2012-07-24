package com.coodroid.olympic.model;

import java.util.Date;

/**
 * 具体比赛的实体类
 * @author Cater
 *
 */
public class Match extends Model{
	private static final long serialVersionUID = -8021714607471331788L;
	/** 比赛所对应的ID*/
	private int id;
	/** 比赛日期(北京时间)*/
	private String bjDate;
	/** 比赛时间(北京时间)*/
	private String bjTime;
	/** 比赛日期(伦敦时间)*/
	private String londonDate;
	/** 比赛时间(伦敦时间)*/
	private String londonTime;
	/** 比赛的详细名称*/
	private String name;
	/** 是否有文字直播*/
	private int hasTextLive;
	/** 是否有视频直播*/
	private int hasVideoLive;
	/** 视频直播频道*/
	private String videoChannel;
	/** 属于某个项目的一部分*/
	private MatchProject partOfProject;
	
	public Match(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public String getBjDate() {
		return bjDate;
	}

	public void setBjDate(String bjDate) {
		this.bjDate = bjDate;
	}

	public String getBjTime() {
		return bjTime;
	}

	public void setBjTime(String bjTime) {
		this.bjTime = bjTime;
	}

	public String getLondonDate() {
		return londonDate;
	}

	public void setLondonDate(String londonDate) {
		this.londonDate = londonDate;
	}

	public String getLondonTime() {
		return londonTime;
	}

	public void setLondonTime(String londonTime) {
		this.londonTime = londonTime;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public int getHasTextLive() {
		return hasTextLive;
	}

	public void setHasTextLive(int hasTextLive) {
		this.hasTextLive = hasTextLive;
	}

	public int getHasVideoLive() {
		return hasVideoLive;
	}

	public void setHasVideoLive(int hasVideoLive) {
		this.hasVideoLive = hasVideoLive;
	}

	public MatchProject getPartOfProject() {
		return partOfProject;
	}

	public void setPartOfProject(MatchProject partOfProject) {
		this.partOfProject = partOfProject;
	}

	public String getVideoChannel() {
		return videoChannel;
	}

	public void setVideoChannel(String videoChannel) {
		this.videoChannel = videoChannel;
	}
	
	
}
