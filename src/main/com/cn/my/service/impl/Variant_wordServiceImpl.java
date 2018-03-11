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

import com.cn.my.mapper.Variant_wordMapper;
import com.cn.my.model.Variant_word;
import com.cn.my.service.Variant_wordService;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service("Variant_wordService")
@Transactional
public class Variant_wordServiceImpl implements Variant_wordService {

	@Autowired
	private Variant_wordMapper Variant_wordMapper;
	
	@Autowired
	private JedisPool jedisPool;
	
	private final static Log logger = LogFactory.getLog(ParticipleServiceImpl.class);
	
	@Override
	public Map<String, Object> getVariant_words(int pageSize, int pageNumber) {
		List<Variant_word> variant_words = Variant_wordMapper.getVariant_words(pageSize, (pageNumber-1)*pageSize);
		int count = Variant_wordMapper.getCount();
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("data", variant_words);
		map.put("total", count);
		return map;
	}

	@Override
	public Variant_word getvariant_word(int id) {
		return Variant_wordMapper.getvariant_word(id);
	}

	@Override
	public boolean insertVariant_word(Variant_word variant_word) {
		int insertVariant_word = Variant_wordMapper.insertVariant_word(variant_word);
		String id=String.format("%04d", variant_word.getId());
		String srcword=variant_word.getSrcword();
		String varword=variant_word.getVarword();
		String key = "variant_word" + ":" + id + ":" + "srcword";
		String key2 = "variant_word" + ":" + id + ":" + "varword";
		String value = "" + srcword;
		String value2 = "" + varword;
		Jedis jedis = jedisPool.getResource();
		jedis.set(key, value);
		jedis.set(key2, value2);
		return insertVariant_word==1;
	}

	@Override
	public boolean deleteVariant_word(int id) {
		int deleteVariant_word = Variant_wordMapper.deleteVariant_word(id);
		String format = String.format("%04d", id);
		String key="variant_word" + ":" + format + ":" + "srcword";;
		String key2="variant_word" + ":" + format + ":" + "varword";;
		Jedis jedis = jedisPool.getResource();
		jedis.del(key);
		jedis.del(key2);
		return deleteVariant_word==1;
	}

	@Override
	public boolean updateVariant_word(Variant_word variant_word) {
		int updateVariant_word = Variant_wordMapper.updateVariant_word(variant_word);
		String id=String.format("%04d", variant_word.getId());
		String srcword=variant_word.getSrcword();
		String varword=variant_word.getVarword();
		String key = "variant_word" + ":" + id + ":" + "srcword";
		String key2 = "variant_word" + ":" + id + ":" + "varword";
		String value = "" + srcword;
		String value2 = "" + varword;
		Jedis jedis = jedisPool.getResource();
		jedis.set(key, value);
		jedis.set(key2, value2);
		return updateVariant_word==1;
	}

	@Override
	public List<Variant_word> getAll() {
		return Variant_wordMapper.getAll();
	}

	@Override
	public boolean uploadWord(List<Variant_word> list, int fromid) {
		try {
			List<Variant_word> list2=new ArrayList<>();
			List<Variant_word> nameisnull = Nameisnull(list);
			for (Variant_word variant_word : nameisnull) {
				variant_word.setFromid(fromid);
				variant_word.setCreatedtime(new Timestamp((new Date()).getTime()));
				list2.add(variant_word);
			}
			boolean uploadWord = Variant_wordMapper.uploadWord(list2);
				for (Variant_word variant_word : list2) {
					String id=String.format("%04d", variant_word.getId());
					String srcword=variant_word.getSrcword();
					String varword=variant_word.getVarword();
					String key = "variant_word" + ":" + id + ":" + "srcword";
					String key2 = "variant_word" + ":" + id + ":" + "varword";
					String value = "" + srcword;
					String value2 = "" + varword;
					Jedis jedis = jedisPool.getResource();
					jedis.set(key, value);
					jedis.set(key2, value2);
				}
			return uploadWord;
		} catch (Exception e) {
			logger.error("批量插入数据失败！"+e.getMessage());
			return false;
		}
	}
	
	private List<Variant_word> Nameisnull(List<Variant_word> list){
		List<Variant_word> list1=new ArrayList<>();
		for (Variant_word variant_word : list) {
			if(variant_word.getSrcword()!=null&&!"".equals(variant_word.getSrcword())&&variant_word.getVarword()!=null&&!"".equals(variant_word.getVarword())){
				list1.add(variant_word);
			}else{
				logger.toString();
			}
		}
		return list1;
	}

}
