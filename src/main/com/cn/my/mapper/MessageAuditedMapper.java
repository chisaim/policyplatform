package com.cn.my.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.my.model.EvaluateWorker;
import com.cn.my.model.MapModel;
import com.cn.my.model.MessageAudited;

public interface MessageAuditedMapper {
    int deleteByPrimaryKey(Integer audited_id);

    int insert(MessageAudited record);

    int insertSelective(MessageAudited record);

    MessageAudited selectByPrimaryKey(Integer audited_id);

    int updateByPrimaryKeySelective(MessageAudited record);

    int updateByPrimaryKey(MessageAudited record);

	int insertRecords(List<MessageAudited> list)throws Exception;

	int updateRepeatCountByMsgId(List<MessageAudited> listUpdateCount)throws Exception;

	int countPersonlAuditedNumToday(@Param("id")int user_id, @Param("date")Date date)throws Exception;

	int countTeamAuditedNumToday(Date date)throws Exception;

	int countAllMessageAudited(@Param("from")Date from, @Param("to")Date to,
			@Param("inspect_level")Integer level, @Param("auditedresult")Integer auditedresult)throws Exception;

	List<MessageAudited> queryMessageAuditedByCondition(@Param("from")Date from, @Param("to")Date to,
			@Param("inspect_level")Integer level, @Param("auditedresult")Integer auditedresult, @Param("page")int i, @Param("limit")int size)throws Exception;

	List<MapModel> calculateWorkResultPerDay(@Param("from")Date from,@Param("to")Date to)throws Exception;

	void insertWorkResultPerDay(@Param("list")List<MapModel> list,@Param("date")Date date,@Param("datetime")Date date2)throws Exception;

	List<Integer> getExamUserInfo(@Param("from")Date from,@Param("to")Date to)throws Exception;

	int countExamerData(@Param("from")Date from,@Param("to")Date to,@Param("userid")Integer userid)throws Exception;

	List<EvaluateWorker> queryExamerData(@Param("from")Date from,@Param("to")Date to,@Param("userid")Integer userid,
			@Param("size")int size,@Param("page")int page)throws Exception;

	List<MessageAudited> queryTargetMessage(List<Integer> list)throws Exception;
}