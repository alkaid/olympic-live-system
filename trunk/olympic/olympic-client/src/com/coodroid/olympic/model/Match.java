package com.coodroid.olympic.model;
/**
 * 具体比赛的实体类
 * @author Cater
 *
 */
public class Match {
	/** 比赛所对应的ID*/
	private int id;
	/** 什么时候开始比赛*/
	private String datetime;
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
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
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
