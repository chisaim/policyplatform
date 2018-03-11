package com.cn.my.model;

import java.sql.Timestamp;

//角色表对应实体类
public class Role {
	
	private int id;
	private String name;
	private boolean status;
	private String description;
	private String role_level_code;
	private Timestamp create_time;
	private int create_by;
	
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
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getRole_level_code() {
		return role_level_code;
	}
	public void setRole_level_code(String role_level_code) {
		this.role_level_code = role_level_code;
	}
	public Timestamp getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Timestamp create_time) {
		this.create_time = create_time;
	}
	public int getCreate_by() {
		return create_by;
	}
	public void setCreate_by(int create_by) {
		this.create_by = create_by;
	}
	
	
}
