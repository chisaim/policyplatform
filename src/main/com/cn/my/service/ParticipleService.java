package com.cn.my.service;

import java.util.List;
import java.util.Map;

import com.cn.my.model.Participle;

public interface ParticipleService {
	
	//返回participle详细信息
	Participle getParticiple(int id);
	//增加一个分词
	boolean insertParticiple(Participle participle);
	//修改分词
	boolean updateParticiple(Participle participle);
	//删除一个分词
	boolean deleteParticiple(int id);
	//分页查询
	Map<String, Object> pageInfo(int pageSize, int pageNumber);
	//上传
	boolean uploadData(List<Participle> wordlist,String operator)throws Exception;
	//导出
	void exportData();
	
	List<Participle> getAll();
}
