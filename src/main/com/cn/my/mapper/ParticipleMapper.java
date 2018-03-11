package com.cn.my.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.my.model.Participle;

public interface ParticipleMapper {
	
	//返回participle详细信息
	Participle getParticiple(int id);
	//增加一个分词
	boolean insertParticiple(Participle participle);
	//修改分词
	boolean updateParticiple(Participle participle);
	//删除一个分词
	boolean deleteParticiple(int id);
	//分页查询
	List<Participle> pageInfo(@Param("offset")int pageSize,@Param("limit") int pageNumber);
	//获取总分词条数
	int getCount();
	//将excel数据上传到数据库
	void uploadData(@Param("list")List<Participle> wordlist,@Param("operator")String operator,@Param("Pcreate_time")Date date)throws Exception;
	
	List<Participle> getAll();
}
