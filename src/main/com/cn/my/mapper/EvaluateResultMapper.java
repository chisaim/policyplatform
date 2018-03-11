package com.cn.my.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.my.model.EvaluateResult;
import com.cn.my.model.PreEvaluateResult;

public interface EvaluateResultMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(EvaluateResult record);

    int insertSelective(EvaluateResult record);

    EvaluateResult selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(EvaluateResult record);

    int updateByPrimaryKey(EvaluateResult record);

	int countEvaluateResult()throws Exception;

	List<EvaluateResult> queryEvaluateResult(@Param("limit")int size,@Param("page")int i)throws Exception;

	void insertPreEvaluateTaskInfo(PreEvaluateResult per)throws Exception;

}