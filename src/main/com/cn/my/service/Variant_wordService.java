package com.cn.my.service;

import java.util.List;
import java.util.Map;

import com.cn.my.model.Variant_word;

public interface Variant_wordService {
	
	Map<String, Object> getVariant_words(int pageSize, int pageNumber);
	
	Variant_word getvariant_word(int id);
	
	boolean insertVariant_word(Variant_word variant_word);
	
	boolean deleteVariant_word(int id);
	
	boolean updateVariant_word(Variant_word variant_word);
	
	List<Variant_word> getAll();
	//上传
	boolean uploadWord(List<Variant_word> list,int fromid);
}
