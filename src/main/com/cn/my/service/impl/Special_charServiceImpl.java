package com.cn.my.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.my.mapper.Special_charMapper;
import com.cn.my.model.Special_char;
import com.cn.my.service.Special_charService;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service("Special_charService")
@Transactional
public class Special_charServiceImpl implements Special_charService {
	
	@Autowired
	private Special_charMapper Special_charMapper;
	
	@Autowired
	private JedisPool jedisPool;
	
	private final static Log logger = LogFactory.getLog(ParticipleServiceImpl.class);

	public Map<String, Object> getSpecial_chars(int pageSize, int pageNumber) {
		// TODO Auto-generated method stub
		Map<String, Object> map=new HashMap<String, Object>();
		List<Special_char> list=Special_charMapper.getSpecial_chars(pageSize, (pageNumber-1)*pageSize);
		
		int total=Special_charMapper.getCount();
		map.put("data", list);
		map.put("total",total );
		return map;
	}

	public Special_char getSpecial_char(int id) {
		// TODO Auto-generated method stub
		return Special_charMapper.getSpecial_char(id);
	}

	public boolean deleteSpecial_char(int id) {
		boolean deleteSpecial_char = Special_charMapper.deleteSpecial_char(id);
		String format = String.format("%04d", id);
		String key = "special_char" + ":" + format + ":" + "spchar";
		Jedis jedis = jedisPool.getResource();
		jedis.del(key);
		
		return deleteSpecial_char;
	}

	public boolean updateSpecial_char(Special_char special_char){
		boolean updateSpecial_char = Special_charMapper.updateSpecial_char(special_char);
		String id=String.format("%04d", special_char.getId());
		String spchar=special_char.getSpchar();
		String key = "special_char" + ":" + id + ":" + "spchar";
		String value = "\\" + spchar;
		Jedis jedis = jedisPool.getResource();
		jedis.set(key, value);
		return updateSpecial_char;
	}

	public boolean insertSpecial_char(Special_char special_char){
		boolean insertSpecial_char = Special_charMapper.insertSpecial_char(special_char);
		String id=String.format("%04d", special_char.getId());
		String spchar=special_char.getSpchar();
		String key = "special_char" + ":" + id + ":" + "spchar";
		String value = "\\" + spchar;
		Jedis jedis = jedisPool.getResource();
		jedis.set(key, value);
		return insertSpecial_char;
	}

	@Override
	public List<Special_char> getAll() {
		// TODO Auto-generated method stub
		return Special_charMapper.getAll();
	}
	
	@Override
	public boolean uploadSp(List<Special_char> list, int fromid) {
		try {
			List<Special_char> list2=new ArrayList<>();
			List<Special_char> nameisnull = Nameisnull(list);
			for (Special_char special_char : nameisnull) {
				special_char.setFromid(fromid);
				special_char.setCreatedtime(new Timestamp((new Date()).getTime()));
				list2.add(special_char);
			}
			boolean uploadSp = Special_charMapper.uploadSp(list2);
			for (Special_char special_char : list2) {
				String id=String.format("%04d", special_char.getId());
				String spchar=special_char.getSpchar();
				String key = "special_char" + ":" + id + ":" + "spchar";
				String value = "\\" + spchar;
				Jedis jedis = jedisPool.getResource();
				jedis.set(key, value);
			}
			return uploadSp;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("批量插入数据失败！"+e.getMessage());
			return false;
		}
	}
	
	private List<Special_char> Nameisnull(List<Special_char> list){
		List<Special_char> list1=new ArrayList<>();
		for (Special_char special_char : list) {
			if(special_char.getSpchar()!=null&&!"".equals(special_char.getSpchar())){
				list1.add(special_char);
			}else{
				logger.toString();
			}
		}
		return list1;
	}
	
}
