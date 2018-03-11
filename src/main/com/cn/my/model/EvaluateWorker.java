package com.cn.my.model;

import java.util.Date;

public class EvaluateWorker {
	private Integer id;
	private Integer userid;
	private Date date;
	private Integer number;
	private Date createtime;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getUserid() {
		return userid;
	}
	public void setUserid(Integer userid) {
		this.userid = userid;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	@Override
	public String toString() {
		return "EvaluateWorker [id=" + id + ", userid=" + userid + ", date="
				+ date + ", number=" + number + ", createtime=" + createtime
				+ "]";
	}

}
