package com.cn.my.model;

public class MapModel {
	private Integer id;
	private Integer number;
	private Integer count;
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	@Override
	public String toString() {
		return "MapModel [id=" + id + ", number=" + number + ", count=" + count
				+ ", name=" + name + "]";
	}
}
