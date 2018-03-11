package com.cn.my.service;

import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cn.my.model.PreKeyword;
import com.cn.my.model.PreMessage;

public interface IPreEvaluateService {

	boolean readFile2Database(List<PreMessage> messagelist, int userid, String filename) throws Exception;

	Map<String, Object> queryPreMessageFileInfo(Date from, Date to,
			String filename, Integer number, Integer size)throws Exception;

	Map<String, Object> queryPreMessage(Integer fileid, Integer servicetype,
			String content, Integer page, Integer size)throws Exception;

	void addPreEvaluateSms(PreMessage preMessage)throws Exception;

	void editPreEvaluateSms(PreMessage preMessage)throws Exception;

	void delPreEvaluateSms(int id)throws Exception;

	void exportdata(Integer fileid, Integer servicetype, String content,
			OutputStream out)throws Exception;

	boolean readPreKeyword2DB(List<PreKeyword> keywordlist, int userid,
			String filename)throws Exception;

	Map<String, Object> queryPreKeywordFileInfo(Date from, Date to,
			String filename, Integer number, Integer size)throws Exception;

	Map<String, Object> queryPreKeyword(Integer fileid, Integer servicetype,
			Integer kwclass, String keyword, Integer page, Integer size)throws Exception;

	void addPreEvaluateKeyword(PreKeyword preKeyword)throws Exception;

	void editPreEvaluateKeyword(PreKeyword preKeyword)throws Exception;

	void delPreEvaluateKeyword(int id)throws Exception;

	void exportKeyword(Integer fileid, Integer servicetype, Integer kwclass,
			String keyword, OutputStream out)throws Exception;

}
