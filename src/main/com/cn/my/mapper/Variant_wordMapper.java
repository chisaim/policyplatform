package com.cn.my.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.my.model.Variant_word;

public interface Variant_wordMapper {
	
	List<Variant_word> getVariant_words(@Param("offset")int pageSize,@Param("limit") int pageNumber);
	//获取总分词条数
	int getCount();
	
	Variant_word getvariant_word(int id);
	
	int insertVariant_word(Variant_word variant_word);
	
	int deleteVariant_word(int id);
	
	int updateVariant_word(Variant_word variant_word);
	
	List<Variant_word> getAll();
	
	//将excel数据上传到数据库
	boolean uploadWord(List<Variant_word> wordlist)throws Exception;
}
