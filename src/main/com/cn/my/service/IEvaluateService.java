package com.cn.my.service;

import java.util.List;
import java.util.Map;

import com.cn.my.model.PreMessage;

public interface IEvaluateService {

	Map<String, Object> queryEvaluateResult(int page, int size)throws Exception;

	List<Map<String, Object>> queryTargetMessage(String audited_ids)throws Exception;

	void executePreEvaluate(int preMessageFileid, int preKeywordFileid, int userid)throws Exception;

	Map<String, Object> queryPreResult(int page, int size)throws Exception;

	Map<String, Object> queryDataByTaskid(int taskid, int page, int size)throws Exception;

	List<PreMessage> queryPreMessages(String messageids)throws Exception;

}
