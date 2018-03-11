package com.cn.my.model;

import java.sql.Timestamp;

public class Variant_word {

	// 变异词实体类
	private int id;
	private String srcword;
	private String varword;
	private int fromid;
	private Timestamp createdtime;

	public Variant_word() {
		super();
	}

	public Variant_word(int id, String srcword, String varword, int fromid, Timestamp createdtime) {
		super();
		this.id = id;
		this.srcword = srcword;
		this.varword = varword;
		this.fromid = fromid;
		this.createdtime = createdtime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSrcword() {
		return srcword;
	}

	public void setSrcword(String srcword) {
		this.srcword = srcword;
	}

	public String getVarword() {
		return varword;
	}

	public void setVarword(String varword) {
		this.varword = varword;
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
