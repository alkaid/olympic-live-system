package com.coodroid.olympic.model;
/**
 * 奖牌榜模块的实体类
 * @author cater
 *
 */
public class Medal extends Model{

	private static final long serialVersionUID = 346004374322062967L;
	/** 每个国家奖牌榜所对应的id*/
	private String id;
	/** 奖牌所对应的排名*/
	private int ranking;
	/** 国家所对应的国旗图片的地址*/
	private String picture;
	/** 国家的名称*/
	private String simpleName;
	/** 国家的金牌数*/
	private int gold;
	/** 国家的银牌数*/
	private int silver;
	/** 国家的铜牌数*/
	private int copper;
	/** 国家的所有奖牌数*/
	private int total;
	
	public Medal(String id) {
		this.id = id;
	}	
	public String getId() {
		return id;
	}
	public int getRanking() {
		return ranking;
	}
	public void setRanking(int ranking) {
		this.ranking = ranking;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	public String getSimpleName() {
		return simpleName;
	}
	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}
	public int getGold() {
		return gold;
	}
	public void setGold(int gold) {
		this.gold = gold;
	}
	public int getSilver() {
		return silver;
	}
	public void setSilver(int silver) {
		this.silver = silver;
	}
	public int getCopper() {
		return copper;
	}
	public void setCopper(int copper) {
		this.copper = copper;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
		
}
