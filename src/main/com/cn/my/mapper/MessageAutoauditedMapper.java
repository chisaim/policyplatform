package com.cn.my.mapper;

import com.cn.my.model.MessageAutoaudited;

public interface MessageAutoauditedMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MessageAutoaudited record);

    int insertSelective(MessageAutoaudited record);

    MessageAutoaudited selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MessageAutoaudited record);

    int updateByPrimaryKey(MessageAutoaudited record);
}