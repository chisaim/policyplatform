package com.cn.my.service.impl;

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

import com.cn.my.mapper.ParticipleMapper;
import com.cn.my.model.Participle;
import com.cn.my.service.ParticipleService;
@Service("ParticipleService")
@Transactional
public class ParticipleServiceImpl implements ParticipleService {
	
	@Autowired
	private ParticipleMapper ParticipleMapper;
	
	private final static Log logger = LogFactory.getLog(ParticipleServiceImpl.class);

	public Participle getParticiple(int id) {
		// TODO Auto-generated method stub
		return ParticipleMapper.getParticiple(id);
	}

	public boolean insertParticiple(Participle participle) {
		// TODO Auto-generated method stub
		return ParticipleMapper.insertParticiple(participle);
	}

	public boolean updateParticiple(Participle participle) {
		// TODO Auto-generated method stub
		return ParticipleMapper.updateParticiple(participle);
	}

	public boolean deleteParticiple(int id) {
		// TODO Auto-generated method stub
		return ParticipleMapper.deleteParticiple(id);
	}

	public Map<String, Object> pageInfo(int pageSize, int pageNumber) {
		Map<String, Object> map=new HashMap<String, Object>();
		int count=ParticipleMapper.getCount();
		map.put("total", count);
		
		List<Participle> plist=ParticipleMapper.pageInfo(pageSize, (pageNumber-1)*pageSize);
				
		map.put("data", plist);
		return map;
	}

	@Override
	public boolean uploadData(List<Participle> wordlist, String operator) {
		// TODO Auto-generated method stub
		try {
			Date date = new Date();
			List<Participle> nameisnull = Nameisnull(wordlist);
			ParticipleMapper.uploadData(nameisnull, operator, date);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("批量插入数据失败！"+e.getMessage());
			return false;
		}
	}
	
	private List<Participle> Nameisnull(List<Participle> wordlist){
		List<Participle> list=new ArrayList<>();
		for (Participle participle : wordlist) {
			if(participle.getName()!=null&&!"".equals(participle.getName())){
				list.add(participle);
			}else{
				logger.toString();
			}
		}
		return list;
	}

	@Override
	public void exportData() {
       
	}

	@Override
	public List<Participle> getAll() {
		// TODO Auto-generated method stub
		return ParticipleMapper.getAll();
	}
		
}
