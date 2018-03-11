package com.cn.my.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.my.model.Keyword;

public interface KeyWordMapper {

	int deleteByPrimaryKey(Integer id)throws Exception;

	int insert(Keyword record)throws Exception;

	int insertSelective(Keyword record);

	Keyword selectByPrimaryKey(Integer id)throws Exception;

	int updateByPrimaryKeySelective(Keyword record);

	int updateByPrimaryKey(Keyword record);

	void readFile2Database(@Param("list")List<Keyword> wordlist,@Param("userid")int userid,@Param("date")Date date)throws Exception;

	int countKeyword()throws Exception;

	List<Keyword> queryKeywordByPage(@Param("offset")int offset, @Param("limit")int limit)throws Exception;

}
