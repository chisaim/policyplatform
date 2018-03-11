package com.cn.my.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.my.model.Special_char;

public interface Special_charMapper {
	
	List<Special_char> getSpecial_chars(@Param("offset")int pageSize,@Param("limit") int pageNumber);
	//获取总分词条数
	int getCount();
	//获取详细信息
	Special_char getSpecial_char(int id);
	//删除
	boolean  deleteSpecial_char(int id);
	//修改
	boolean updateSpecial_char(Special_char special_char);
	//增加
	boolean insertSpecial_char(Special_char special_char);
	
	List<Special_char> getAll();
	
	//将excel数据上传到数据库
	boolean uploadSp(List<Special_char> list)throws Exception;

}
