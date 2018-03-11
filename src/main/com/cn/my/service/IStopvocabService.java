package com.cn.my.service;

import java.util.List;
import java.util.Map;

import com.cn.my.model.Stopvocab;
/**
 * @ClassName: User
 * @Description: 停用词库管理接口
 * @author caixiaoyu
 * @date 2017年11月15日
 */
public interface IStopvocabService {

	//用于查询停用词接口
	//List<Stopvocab> getStopvocab();
	
	//用于添加停用词的接口
    boolean addstopvocab(Stopvocab stopvocab);
 
     //用于停用词的删除
	boolean removestopvocab(int stopvocab);
 
	//用于停用词的修改
	boolean editstopvocab(Stopvocab stopvocab);
	
	//用于按词名称来查询词库的接口
	List<Stopvocab> foundStopvocab(String stopvocab_stopword);
	//分页查询
	Map<String, Object> getStopvocab(int pageSize, int pageNumber);
	//将数据读入数据库表中
	boolean readFile2Database(List<Stopvocab> wordlist, int fromid)throws Exception;

	List<Stopvocab> getAll();
  }
