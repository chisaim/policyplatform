package com.cn.my.service;

import java.util.List;
import java.util.Map;

import com.cn.my.model.Special_char;

public interface Special_charService {
	
	Map<String, Object> getSpecial_chars(int pageSize, int pageNumber);
	//获取详细信息
	Special_char getSpecial_char(int id);
	//删除
	boolean  deleteSpecial_char(int id);
	//修改
	boolean updateSpecial_char(Special_char special_char);
	//增加
	boolean insertSpecial_char(Special_char special_char);
	
	List<Special_char> getAll();
	//上传
	boolean uploadSp(List<Special_char> list,int fromid);
}
