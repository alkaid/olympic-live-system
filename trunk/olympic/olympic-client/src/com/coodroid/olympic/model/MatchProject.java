package com.coodroid.olympic.model;
/**
 * 标识比赛所属项目的实体类
 * @author Cater
 *
 */
public class MatchProject {
	/** 项目的id*/
	private int id;
	/** 项目的名称*/
	private String name;
	
	public MatchProject(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
