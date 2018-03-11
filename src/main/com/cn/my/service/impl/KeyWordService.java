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

import com.cn.my.mapper.KeyWordMapper;
import com.cn.my.mapper.UserMapper;
import com.cn.my.model.Keyword;
import com.cn.my.model.User;
import com.cn.my.service.IKeyWordService;
import com.cn.my.util.CharacterConvertUtil;
import com.cn.my.util.JsonUtils;
@Service("keywordService")
@Transactional
public class KeyWordService implements IKeyWordService{
	
	@Autowired
	private KeyWordMapper keywordMapper;
	@Autowired
	private UserMapper userMapper;
	private final static Log logger = LogFactory.getLog(KeyWordService.class);
	
	public boolean readFile2Database(List<Keyword> wordlist, int userid){
		try {
			Date date = new Date();
			//策略关键字不能为空
			List<Keyword> list = keywordRidNull(wordlist);
			keywordMapper.readFile2Database(list,userid,date);
			return true;
		} catch (Exception e) {
			logger.error("批量插入数据失败！"+e.getMessage());
			return false;
		}
	}
	//排除空策略
	private List<Keyword> keywordRidNull(List<Keyword> wordlist) {
		List<Keyword> list = new ArrayList<>();
		for(Keyword keyword:wordlist){
			if((keyword.getKeyword()!=null)&&(!"".equals(keyword.getKeyword()))){
				list.add(keyword);
			}else{
				logger.error(keyword.toString());
			}
		}
		return list;
	}
	
	@Override
	public Map<String, Object> queryKeyword(int page, int size){
		Map<String, Object> map = new HashMap<>();
		int offset = (page-1)*size;
		int limit = size;
		try {
			int total = keywordMapper.countKeyword();
			map.put("total", total);
			if(0==total){
				return map;
			}
			List<Keyword> list = keywordMapper.queryKeywordByPage(offset,limit);
			//组装业务数据：'id','keyword','servicetype','createdtime','kwclass','optype','operator',
			//'monthreshold','filterthreshold','abthreshold','unittime',
			List<Object> objList = new ArrayList<>();
			Map<Integer,String> nameCache = new HashMap<>();
			for(Keyword keyword:list){
				Map<String, Object> mapObj = new HashMap<String, Object>();
				mapObj.put("id",keyword.getId());
				mapObj.put("keyword", keyword.getKeyword());
				mapObj.put("servicetype", getKeywordServicetype(keyword.getServicetype()));
				mapObj.put("createdtime", keyword.getCreatedtime());
				mapObj.put("kwclass", getKeywordKwclass(keyword.getKwclass()));
				mapObj.put("optype", getKeywordOptype(keyword.getOptype()));
				if(!nameCache.containsKey(keyword.getUserid())){
					User user = userMapper.getUserInfoById(keyword.getUserid());
					String username = user==null?"":user.getUser_name();
					nameCache.put(keyword.getUserid(), username);
				}
				mapObj.put("operator", nameCache.get(keyword.getUserid()));
				mapObj.put("monthreshold", keyword.getMonthreshold());
				mapObj.put("filterthreshold", keyword.getMonthreshold());
				mapObj.put("abthreshold", keyword.getMonthreshold());
				String unit = getKeywordUnit(keyword.getUnit());
				mapObj.put("unittime", keyword.getTimespan()+" "+unit);
				objList.add(JsonUtils.map2obj(mapObj, Object.class));
			}
			map.put("data", objList);
			return map;
		} catch (Exception e) {
			logger.error("查询数据出错！");
			return null;
		}
	}
	//**************************数据与policy_keyword表中字段严格对应*************************//
	private String getKeywordUnit(Integer unit) {
		//关键字处置粒度：1：秒，2：分，3：小时，4：天，5：月
		switch (unit) {
			case 1 :
				return "second";
			case 2 :
				return "minute";
			case 3 :
				return "hour";
			case 4 :
				return "day";
			default :
				return "month";
		}
	}
	public static String getKeywordServicetype(Integer servicetype) {
		//业务类型：1：“点到点”2：“网间”3：“端口类自有业务短信”4：“端口类集团客户/行业应用短信”5：“端口类梦网SP短信”6：“其它”
		switch (servicetype) {
			case 1 :
				return "点到点";
			case 2 :
				return "网间";
			case 3 :
				return "端口类自有业务短信";
			case 4 :
				return "端口类集团客户/行业应用短信";
			case 5 :
				return "端口类梦网SP短信";
			default :
				return "其它";
		}
	}
	public static String getKeywordKwclass(Integer kwclass) {
		//关键词分类，1：涉黄、2：政治类、3：商业广告、4：违法诈骗、5：其他		
		switch (kwclass) {
			case 1 :
				return "涉黄";
			case 2 :
				return "政治类";
			case 3 :
				return "商业广告";
			case 4 :
				return "违法诈骗";
			default :
				return "其它";
		}
	}
	private String getKeywordOptype(Integer optype){
		//操作类型:1：制作0：解除
		switch (optype) {
			case 1 :
				return "制作";
			default :
				return "解除";
		}
	}
	//**************************数据与policy_keyword表中字段严格对应*************************//
	
	// 按id删除策略关键字
	@Override
	public boolean delKeyword(int keywordid) throws Exception {
		try {
			keywordMapper.deleteByPrimaryKey(keywordid);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	// 单独增加新的策略关键字
	@Override
	public boolean addNewKeyword(Keyword keyword){
		try {
			//关键字全半角中英文符号转换
			String string = keyword.getKeyword();
			string = CharacterConvertUtil.qj2bj(string);
			string = CharacterConvertUtil.ZHsymbol2ENsymbol(string);
			keyword.setKeyword(string);
			keywordMapper.insert(keyword);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
	}
	
	@Override
	public Keyword querySingleKeyword(int keywordid){
		try {
			return keywordMapper.selectByPrimaryKey(keywordid);
		} catch (Exception e) {
			logger.error("未查询到数据"+e.getMessage());
			return null;
		}
	}
	@Override
	public boolean editKeyword(Keyword keyword){
		try {
			keywordMapper.updateByPrimaryKey(keyword);
			return true;
		} catch (Exception e) {
			logger.error("数据库更新数据出错："+e.getMessage());
			return false;
		}
	}
	
}
