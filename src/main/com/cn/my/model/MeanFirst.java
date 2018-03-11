package com.cn.my.model;

import java.util.List;

//一级菜单
public class MeanFirst {
	
	private String menuid;
	private String menuname;
	private List<MeanSecond> menus;
	public String getMenuid() {
		return menuid;
	}
	public void setMenuid(String menuid) {
		this.menuid = menuid;
	}
	public String getMenuname() {
		return menuname;
	}
	public void setMenuname(String menuname) {
		this.menuname = menuname;
	}
	public List<MeanSecond> getMenus() {
		return menus;
	}
	public void setMenus(List<MeanSecond> menus) {
		this.menus = menus;
	}
	
	
}
