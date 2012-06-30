package com.coodroid.olympic.model;
/**
 * 奖牌榜模块的实体类
 * @author cater
 *
 */
public class Medal extends Model{

	private static final long serialVersionUID = 346004374322062967L;
	private int id;
	private int ranking;
	private String picture;
	private String simpleName;
	private int gold;
	private int silver;
	private int copper;
	private int total;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
