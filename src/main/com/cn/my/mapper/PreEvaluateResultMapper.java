package com.cn.my.mapper;

import com.cn.my.model.PreEvaluateResult;

public interface PreEvaluateResultMapper {
    int deleteByPrimaryKey(Integer taskid);

    int insert(PreEvaluateResult record);

    int insertSelective(PreEvaluateResult record);

    PreEvaluateResult selectByPrimaryKey(Integer taskid);

    int updateByPrimaryKeySelective(PreEvaluateResult record);

    int updateByPrimaryKey(PreEvaluateResult record);
}