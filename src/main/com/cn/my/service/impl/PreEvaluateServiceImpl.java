package com.cn.my.service.impl;

import java.io.OutputStream;
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

import com.cn.my.mapper.PreEvaluateMapper;
import com.cn.my.mapper.UserMapper;
import com.cn.my.model.EvaluateFileInfo;
import com.cn.my.model.PreKeyword;
import com.cn.my.model.PreMessage;
import com.cn.my.service.IPreEvaluateService;
import com.cn.my.util.Constant;
import com.cn.my.util.FileUtil;
import com.cn.my.util.JsonUtils;

@Service("evaluteService")
@Transactional
public class PreEvaluateServiceImpl implements IPreEvaluateService{
	@Autowired
	private PreEvaluateMapper preEvaluateMapper;
	
	@Autowired
	private UserMapper userMapper;
	
	private final static Log logger = LogFactory.getLog(PreEvaluateServiceImpl.class);

	@Override
	public boolean readFile2Database(List<PreMessage> messagelist, int userid,String filename){
		//先插入文件信息，返回文件id，再插入短信信息
		try {
			EvaluateFileInfo efi = new EvaluateFileInfo();
			efi.setCreatetime(new Date());
			efi.setFilename(filename);
			efi.setDatanumber(messagelist.size());
			efi.setUserid(userid);
			efi.setFiletype(Constant.FILE_TYPE_SMS);
			preEvaluateMapper.insertfileinfo(efi);
			int total = preEvaluateMapper.insertPreMessage(messagelist,efi.getFileid(),new Date());
			if(total==messagelist.size()){
				logger.info("上传数据成功！");
			}
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return false;
	}

	@Override
	public Map<String, Object> queryPreMessageFileInfo(Date from, Date to,
			String filename, Integer number, Integer size){
		try {
			Map<String,Object> map = new HashMap<>();
			int total = preEvaluateMapper.countMessageFileByCondition(from,to,filename,Constant.FILE_TYPE_SMS);
			map.put("total", total);
			List<EvaluateFileInfo> list = preEvaluateMapper.queryPreFileInfo(from,to,filename,(number-1)*size,size,Constant.FILE_TYPE_SMS);
			List<Map<String,Object>> array = new ArrayList<>();
			Map<Integer,String> cache = new HashMap<>();
			for(EvaluateFileInfo message:list){
				Map<String,Object> mapobj = JsonUtils.objectToMap(message);
				if(null==cache.get(mapobj.get("userid"))||"".equals(cache.get(mapobj.get("userid")))){
					cache.put((Integer) mapobj.get("userid"), userMapper.getUserInfoById((int) mapobj.get("userid")).getUser_name());
				}
				mapobj.put("operator", cache.get(mapobj.get("userid")));
				mapobj.remove("userid");
				array.add(mapobj);
			}
			map.put("data", array);
			return map;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public Map<String, Object> queryPreMessage(Integer fileid,Integer servicetype, 
			String content, Integer page, Integer size){
		if(fileid==null||fileid==0){
			return null;
		}
		try {
			Map<String, Object> map = new HashMap<>();
			int total = preEvaluateMapper.countPreMessageNumByCondition(fileid,servicetype,content);
			map.put("total", total);
			List<PreMessage> list = preEvaluateMapper.queryPreMessage(fileid,servicetype,content,(page-1)*size,size);
			List<Map<String,Object>> array = new ArrayList<>();
			for(PreMessage message:list){
				Map<String,Object> mapobj = JsonUtils.objectToMap(message);
				mapobj.put("servicetype_cn", KeyWordService.getKeywordServicetype(message.getServicetype()));
				mapobj.put("auditedresult_cn",message.getAuditedresult()==0?"正常":"垃圾");
				array.add(mapobj);
			}
			map.put("data", array);
			return map;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public void addPreEvaluateSms(PreMessage preMessage){
		try {
			preMessage.setCreatedate(new Date());
			preEvaluateMapper.addPreEvaluateSms(preMessage);
			preEvaluateMapper.recountDataNum(preMessage.getFileid());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public void editPreEvaluateSms(PreMessage preMessage){
		try {
			preEvaluateMapper.editPreEvaluateSms(preMessage);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public void delPreEvaluateSms(int id){
		try {
			PreMessage message = preEvaluateMapper.getMessageById(id);
			preEvaluateMapper.delPreEvaluateSms(id);
			preEvaluateMapper.recountDataNum(message.getFileid());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public void exportdata(Integer fileid, Integer servicetype, String content,
			OutputStream out){
		//一次最多导出十万条数据
		try {
			Map<String, Object> map= queryPreMessage(fileid,servicetype,content,0,10000);
			@SuppressWarnings("unchecked")
			List<Map<String,Object>> maplist = (List<Map<String,Object>>) map.get("data");
			String[] cn_title = {"短信内容","主叫号码","业务类型","入库时间","被叫号码","审核结果"};
			String[] en_property = {"content","callerid","servicetype_cn","createdate","calledid","auditedresult_cn"};
			FileUtil.writeJSONData2Out(cn_title,en_property,maplist,out);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}	
	}

	@Override
	public boolean readPreKeyword2DB(List<PreKeyword> keywordlist, int userid,String filename){
		//先插入文件信息，返回文件id，再插入短信信息
		try {
		EvaluateFileInfo efi = new EvaluateFileInfo();
		efi.setCreatetime(new Date());
		efi.setFilename(filename);
		efi.setDatanumber(keywordlist.size());
		efi.setUserid(userid);
		efi.setFiletype(Constant.FILE_TYPE_KEY);
		preEvaluateMapper.insertfileinfo(efi);
		int total = preEvaluateMapper.insertPreKeyword(keywordlist,efi.getFileid(),new Date());
		if(total==keywordlist.size()){
			logger.info("上传数据成功！");
		}
		return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return false;
	}

	@Override
	public Map<String, Object> queryPreKeywordFileInfo(Date from, Date to,
			String filename, Integer number, Integer size){
		try {
			Map<String,Object> map = new HashMap<>();
			int total = preEvaluateMapper.countMessageFileByCondition(from,to,filename,Constant.FILE_TYPE_KEY);
			map.put("total", total);
			List<EvaluateFileInfo> list = preEvaluateMapper.queryPreFileInfo(from,to,filename,(number-1)*size,size,Constant.FILE_TYPE_KEY);
			List<Map<String,Object>> array = new ArrayList<>();
			Map<Integer,String> cache = new HashMap<>();
			for(EvaluateFileInfo message:list){
				Map<String,Object> mapobj = JsonUtils.objectToMap(message);
				if(null==cache.get(mapobj.get("userid"))||"".equals(cache.get(mapobj.get("userid")))){
					cache.put((Integer) mapobj.get("userid"), userMapper.getUserInfoById((int) mapobj.get("userid")).getUser_name());
				}
				mapobj.put("operator", cache.get(mapobj.get("userid")));
				mapobj.remove("userid");
				array.add(mapobj);
			}
			map.put("data", array);
			return map;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public Map<String, Object> queryPreKeyword(Integer fileid,Integer servicetype, 
			Integer kwclass, String keyword, Integer page,Integer size){
		if(fileid==null||fileid==0){
			return null;
		}
		try {
			Map<String, Object> map = new HashMap<>();
			int total = preEvaluateMapper.countPreKeywordNumByCondition(fileid,servicetype,kwclass,keyword);
			map.put("total", total);
			List<PreKeyword> list = preEvaluateMapper.queryPreKeyword(fileid,servicetype,kwclass,keyword,(page-1)*size,size);
			List<Map<String,Object>> array = new ArrayList<>();
			for(PreKeyword keywordobj:list){
				Map<String,Object> mapobj = JsonUtils.objectToMap(keywordobj);
				mapobj.put("servicetype_cn", KeyWordService.getKeywordServicetype(keywordobj.getServicetype()));
				mapobj.put("kwclass_cn", KeyWordService.getKeywordKwclass(keywordobj.getKwclass()));
				array.add(mapobj);
			}
			map.put("data", array);
			return map;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public void addPreEvaluateKeyword(PreKeyword preKeyword){
		try {
			preKeyword.setCreatedtime(new Date());
			preEvaluateMapper.addPreEvaluateKeyword(preKeyword);
			preEvaluateMapper.recountDataNumPreKey(preKeyword.getFileid());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public void editPreEvaluateKeyword(PreKeyword preKeyword){
		try {
			preEvaluateMapper.editPreEvaluateKeyword(preKeyword);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public void delPreEvaluateKeyword(int id){
		try {
			PreKeyword preKeyword = preEvaluateMapper.getKeywordById(id);
			preEvaluateMapper.delPreEvaluateKeyword(id);
			preEvaluateMapper.recountDataNumPreKey(preKeyword.getFileid());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public void exportKeyword(Integer fileid, Integer servicetype,
			Integer kwclass, String keyword, OutputStream out){
		//一次最多导出十万条数据
		try {
			Map<String, Object> map= queryPreKeyword(fileid,servicetype,kwclass,keyword,0,10000);
			@SuppressWarnings("unchecked")
			List<Map<String,Object>> maplist = (List<Map<String,Object>>) map.get("data");
			String[] cn_title = {"策略内容","业务类型","关键词分类","入库时间"};
			String[] en_property = {"keyword","servicetype_cn","kwclass_cn","createdtime"};
			FileUtil.writeJSONData2Out(cn_title,en_property,maplist,out);
		} catch (Exception e) {
					logger.error(e.getMessage());
		}	
	}
	
}
