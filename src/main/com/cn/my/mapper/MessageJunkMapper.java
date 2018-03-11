package com.cn.my.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.my.model.MessageAudited;
import com.cn.my.model.MessageJunk;

public interface MessageJunkMapper {
	
    int deleteByPrimaryKey(Integer junk_id)throws Exception;
    
    int insert(MessageJunk record)throws Exception;

    int insertSelective(MessageJunk record)throws Exception;

    MessageJunk selectByPrimaryKey(Integer junk_id)throws Exception;

    int updateByPrimaryKeySelective(MessageJunk record)throws Exception;

    int updateByPrimaryKey(MessageJunk record)throws Exception;

	List<MessageJunk> getMessageJunk(@Param("from")Date datefrom, @Param("to")Date dateto,
			@Param("inspect_level")Integer inspect_level, @Param("limit")Integer number)throws Exception;

	int updateStatus(List<Integer> junk_idList)throws Exception;

	List<Map<String, Object>> queryMessageJunkByAuditedMsgId(List<MessageAudited> auditedList)throws Exception;

	int batchDeleteData(List<MessageAudited> auditedList)throws Exception;

	int countNeedAuditNum()throws Exception;

	int countAllMessageJunk(@Param("from")Date from, @Param("to")Date to,
			@Param("inspect_level")Integer level)throws Exception;

	List<MessageJunk> queryMessageJunkByCondition(@Param("from")Date from, @Param("to")Date to,
			@Param("inspect_level")Integer level, @Param("page")int i, @Param("limit")int size)throws Exception;

	void resetJunkSms(@Param("from")Date from, @Param("to")Date to)throws Exception;

}