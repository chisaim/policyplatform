package com.cn.my.model;

import java.sql.Timestamp;

//权限实体类
public class Action {
	
	private int id;
	private String action;
	private String action_name;
	private int action_column_id;
	private String action_path;
	private Timestamp create_time;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getAction_name() {
		return action_name;
	}
	public void setAction_name(String action_name) {
		this.action_name = action_name;
	}
	public int getAction_column_id() {
		return action_column_id;
	}
	public void setAction_column_id(int action_column_id) {
		this.action_column_id = action_column_id;
	}
	public String getAction_path() {
		return action_path;
	}
	public void setAction_path(String action_path) {
		this.action_path = action_path;
	}
	public Timestamp getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Timestamp create_time) {
		this.create_time = create_time;
	}
	
	
}
