package com.cn.my.model;

import java.sql.Timestamp;

//特殊字符实体类
public class Special_char {
	
	private int id;
	private String spchar;
	private int fromid;
	private Timestamp createdtime;
	
	public Special_char() {
		super();
	}	

	public Special_char(int id, String spchar, int fromid, Timestamp createdtime) {
		super();
		this.id = id;
		this.spchar = spchar;
		this.fromid = fromid;
		this.createdtime = createdtime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSpchar() {
		return spchar;
	}

	public void setSpchar(String spchar) {
		this.spchar = spchar;
	}

	public int getFromid() {
		return fromid;
	}

	public void setFromid(int fromid) {
		this.fromid = fromid;
	}

	public Timestamp getCreatedtime() {
		return createdtime;
	}

	public void setCreatedtime(Timestamp createdtime) {
		this.createdtime = createdtime;
	}

}
