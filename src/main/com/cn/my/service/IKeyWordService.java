package com.cn.my.service;

import java.util.List;
import java.util.Map;

import com.cn.my.model.Keyword;

public interface IKeyWordService {

	//将数据读入数据库表中
	boolean readFile2Database(List<Keyword> wordlist, int userid)throws Exception;

	Map<String, Object> queryKeyword(int page, int size)throws Exception;

	boolean delKeyword(int keywordid)throws Exception;

	boolean addNewKeyword(Keyword keyword)throws Exception;

	Keyword querySingleKeyword(int keywordid)throws Exception;

	boolean editKeyword(Keyword keyword)throws Exception;

}
