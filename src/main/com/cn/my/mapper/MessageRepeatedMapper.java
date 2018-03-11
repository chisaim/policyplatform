package com.cn.my.mapper;

import java.util.List;
import java.util.Map;

import com.cn.my.model.MessageRepeated;

public interface MessageRepeatedMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MessageRepeated record);

    int insertSelective(MessageRepeated record);

    MessageRepeated selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MessageRepeated record);

    int updateByPrimaryKey(MessageRepeated record);

    List<Map<String, Integer>> countRepeat(List<Integer> junk_idList) throws Exception;
}