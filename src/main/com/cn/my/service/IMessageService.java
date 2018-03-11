package com.cn.my.service;

import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.cn.my.model.MessageJunk;

public interface IMessageService {

	//审核人员获取待审核的数据
	List<MessageJunk> getMessageJunk(Date datefrom, Date dateto,
			Integer inspect_level, Integer number)throws Exception;

	//审核人员提交审核结果
	boolean submitMessageAudited(int user_id, JSONArray array)throws Exception;

	Map<String, Integer> queryAuditedNum(int user_id)throws Exception;

	int queryJUNKNum()throws Exception;

	Map<String, Object> queryMessageAudited(Date from, Date to, Integer level,
			Integer auditedresult, int size, int number)throws Exception;

	void exportdata(Date from, Date to, Integer level, Integer auditedresult,
			OutputStream out)throws Exception;

	Map<String, Object> queryMessageJunk(Date from, Date to, Integer level,
			int size, int number)throws Exception;

	void exportJunkdata(Date from, Date to, Integer level, OutputStream out)throws Exception;

	List<Map<String, Object>> getExamUserInfo(Date from, Date to)throws Exception;

	Map<String, Object> queryExamerData(Date from, Date to, Integer userid,
			int size, int page)throws Exception;

	void exportExamerReport(Date from, Date to, Integer valueOf,
			OutputStream out)throws Exception;

}
