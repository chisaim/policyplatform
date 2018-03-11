package com.cn.my.model;

import java.sql.Timestamp;

import org.springframework.format.annotation.DateTimeFormat;
/**
 * @ClassName: User
 * @Description: 停用词库管理实体类
 * @author caixiaoyu
 * @date 2017年11月15日
 */

public class Stopvocab {

	private int id;
	private String stopword;
	private boolean status;
	private Integer wordtype;
	private int fromid;
	@DateTimeFormat(pattern="yyyy-MM-dd hh:mm:ss")
	private java.sql.Timestamp createdtime;
	public Stopvocab() {
		super();
		
	}
	public Stopvocab(int id, String stopword, boolean status, Integer wordtype, int fromid, Timestamp createdtime) {
		super();
		this.id = id;
		this.stopword = stopword;
		this.status = status;
		this.wordtype = wordtype;
		this.fromid = fromid;
		this.createdtime = createdtime;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getStopword() {
		return stopword;
	}
	public void setStopword(String stopword) {
		this.stopword = stopword;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public Integer getWordtype() {
		return wordtype;
	}
	public void setWordtype(Integer wordtype) {
		this.wordtype = wordtype;
	}
	public int getFromid() {
		return fromid;
	}
	public void setFromid(int fromid) {
		this.fromid = fromid;
	}
	public java.sql.Timestamp getCreatedtime() {
		return createdtime;
	}
	public void setCreatedtime(java.sql.Timestamp createdtime) {
		this.createdtime = createdtime;
	}
	@Override
	public String toString() {
		return "Stopvocab [id=" + id + ", stopword=" + stopword + ", status=" + status + ", wordtype=" + wordtype
				+ ", fromid=" + fromid + ", createdtime=" + createdtime + "]";
	}
	
}