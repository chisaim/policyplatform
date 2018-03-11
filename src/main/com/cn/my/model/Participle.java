package com.cn.my.model;

import java.sql.Timestamp;

public class Participle {
	@Override
	public String toString() {
		return "Participle [id=" + id + ", name=" + name + ", participle_type=" + participle_type + ", Pcreate_time="
				+ Pcreate_time + ", operator=" + operator + ", remark=" + remark + "]";
	}

	/**
	 * participle实体类
	 * 
	 * @date 2017-12-8
	 */

	private int id;
	private String name;
	private String participle_type;
	private Timestamp Pcreate_time;
	private String operator;
	private String remark;

	public Participle() {
		super();
	}

	public Participle(int id, String name, String participle_type, Timestamp pcreate_time, String operator,
			String remark) {
		super();
		this.id = id;
		this.name = name;
		this.participle_type = participle_type;
		Pcreate_time = pcreate_time;
		this.operator = operator;
		this.remark = remark;
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

	public String getParticiple_type() {
		return participle_type;
	}

	public void setParticiple_type(String participle_type) {
		this.participle_type = participle_type;
	}

	public Timestamp getPcreate_time() {
		return Pcreate_time;
	}

	public void setPcreate_time(Timestamp pcreate_time) {
		Pcreate_time = pcreate_time;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
