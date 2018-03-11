package com.cn.my.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.my.model.EvaluateFileInfo;
import com.cn.my.model.PolicyPreEvaluation;
import com.cn.my.model.PreEvaluateResult;
import com.cn.my.model.PreKeyword;
import com.cn.my.model.PreMessage;

public interface PreEvaluateMapper {

	int insertfileinfo(EvaluateFileInfo efi)throws Exception;

	int insertPreMessage(@Param("list")List<PreMessage> messagelist,@Param("fileid")int fileid,@Param("date")Date date)throws Exception;

	int countMessageFileByCondition(@Param("from")Date from, @Param("to")Date to, @Param("filename")String filename,@Param("filetype")Integer fileTypeKey)throws Exception;

	List<EvaluateFileInfo> queryPreFileInfo(@Param("from")Date from, @Param("to")Date to, @Param("filename")String filename,@Param("page")int i,@Param("limit")Integer size, @Param("filetype")Integer fileTypeKey)throws Exception;

	int countPreMessageNumByCondition(@Param("fileid")Integer fileid, @Param("servicetype")Integer servicetype,
			@Param("content")String content)throws Exception;

	List<PreMessage> queryPreMessage(@Param("fileid")Integer fileid, @Param("servicetype")Integer servicetype,
			@Param("content")String content, @Param("page")int i, @Param("limit")Integer size);

	void addPreEvaluateSms(PreMessage preMessage)throws Exception;

	void recountDataNum(Integer fileid)throws Exception;

	void editPreEvaluateSms(PreMessage preMessage)throws Exception;

	void delPreEvaluateSms(int id)throws Exception;

	PreMessage getMessageById(int id)throws Exception;
	
	int insertPreKeyword(@Param("list")List<PreKeyword> keywordlist, @Param("fileid")Integer fileid,
			@Param("date")Date date)throws Exception;

	int countPreKeywordNumByCondition(@Param("fileid")Integer fileid, @Param("servicetype")Integer servicetype,
			@Param("kwclass")Integer kwclass, @Param("keyword")String keyword)throws Exception;

	List<PreKeyword> queryPreKeyword(@Param("fileid")Integer fileid, @Param("servicetype")Integer servicetype,
			@Param("kwclass")Integer kwclass, @Param("keyword")String keyword, @Param("page")int i, @Param("limit")Integer size)throws Exception;

	void addPreEvaluateKeyword(PreKeyword preKeyword)throws Exception;

	void recountDataNumPreKey(Integer fileid)throws Exception;

	void editPreEvaluateKeyword(PreKeyword preKeyword)throws Exception;

	PreKeyword getKeywordById(int id)throws Exception;

	void delPreEvaluateKeyword(int id)throws Exception;

	EvaluateFileInfo getFileinfoByfileid(int preMessageFileid)throws Exception;

	String getPreTaskAppid(Integer taskid)throws Exception;

	void insertSparkExcuatedStatus(@Param("spark_status")int spark_status,@Param("task_id")Integer integer)throws Exception;

	int countPreEvaluateResult()throws Exception;

	List<PreEvaluateResult> queryPreResult(@Param("page")int i,@Param("limit")int size)throws Exception;

	int countPreKeyWordBytaskId(int taskid)throws Exception;

	List<PolicyPreEvaluation> queryDataByTaskid(@Param("taskid")int taskid,@Param("page")int i,@Param("limit")int size)throws Exception;

	List<PreMessage> queryPreMessagesByID(List<Integer> list)throws Exception;

	int countPreMessageLaji(List<Integer> premessages)throws Exception;

	void updatePreEvaluateResult(PreEvaluateResult per)throws Exception;
	
}
