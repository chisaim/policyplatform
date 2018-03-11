package com.cn.my.service.impl;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.cn.my.mapper.MessageAuditedMapper;
import com.cn.my.mapper.MessageJunkMapper;
import com.cn.my.mapper.MessageRepeatedMapper;
import com.cn.my.mapper.UserMapper;
import com.cn.my.model.EvaluateWorker;
import com.cn.my.model.MapModel;
import com.cn.my.model.MessageAudited;
import com.cn.my.model.MessageJunk;
import com.cn.my.service.IMessageService;
import com.cn.my.util.FileUtil;
import com.cn.my.util.JsonUtils;

@Service("messageService")
@Transactional
public class MessageServiceImpl implements IMessageService {

	@Autowired
	private MessageJunkMapper messageJunkMapper;

	@Autowired
	private MessageAuditedMapper messageAuditedMapper;

//	@Autowired
//	private MessageAutoauditedMapper messageAutoauditedMapper;

	@Autowired
	private MessageRepeatedMapper messageRepeatedMapper;
	
	@Autowired
	private UserMapper userMapper;

	private final static Log logger = LogFactory.getLog(MessageServiceImpl.class);

	@Override
	public List<MessageJunk> getMessageJunk(Date datefrom, Date dateto,
			Integer inspect_level, Integer number) {
		try {
			// 业务逻辑：请求数据，并改变数据状态
			List<MessageJunk> list = messageJunkMapper.getMessageJunk(datefrom,
					dateto, inspect_level, number);
			//计算repeatcount的值
			if (list.size() == 0) {
				return null;
			}
			List<Integer> junk_idList = readIdList(list);
			List<Map<String, Integer>> mapList = messageRepeatedMapper.countRepeat(junk_idList);
			Map<Integer, Integer> cache = countList2Map(mapList);
			List<MessageJunk> listRe = new ArrayList<>();
			for (MessageJunk message:list) {
				Integer i = cache.get(message.getJunk_id());
				message.setRepeatcount(i==null?0:i);
				listRe.add(message);
			}
			messageJunkMapper.updateStatus(junk_idList);
			return listRe;
		} catch (Exception e) {
			logger.error("数据库执行查询或批量更新数据出错！");
			logger.error("!-_-!"+e.getMessage()+e.getStackTrace());
			return null;
		}
	}

	private List<Integer> readIdList(List<MessageJunk> messageList) {
		List<Integer> list = new ArrayList<>();
		for(MessageJunk message:messageList){
			list.add(message.getJunk_id());
		}
		return list;
	}

	private Map<Integer, Integer> countList2Map(List<Map<String, Integer>> maplist){
		Map<Integer, Integer> cache = new HashMap<>();
		//解析mybatis所返回的hashmap
		for(Map<String, Integer> map:maplist){
			//map返回的是Long类型
			cache.put(map.get("keyFiled"), ((Number)map.get("valueFiled")).intValue());
		}
		return cache;
	}
	
	//将mybatis返回的hashMap的list结果
	@SuppressWarnings("unchecked")
	private <T> Map<String,T> maplist2Map(List<Map<String,Object>> maplist,Class<T> clazz,String key){
		try {
			Map<String,T> mapRe = new HashMap<>();
			@SuppressWarnings("rawtypes")
			Iterator it = maplist.iterator();
			while(it.hasNext()){
				T obj = (T) it.next();
				//设置对象的访问权限，保证对private的属性的访问  
				Field field = obj.getClass().getDeclaredField(key);  
				field.setAccessible(true);
				mapRe.put((String)field.get(obj),obj);
			}
			return mapRe;
		} catch (Exception e) {
				logger.error(e.getMessage());
				return null;
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean submitMessageAudited(int user_id, JSONArray array){
		/**
		 * 业务逻辑说明：根据msg_id先获取短信息，再将短信信息填充到已审核的表中，再将待审核表中对应的数据删除，最后是更新countrepeat数据
		 */
		try {
			List<MessageAudited> list = new ArrayList<>();
			List<MessageAudited> auditedList = JSONArray.parseArray(JSONArray.toJSONString(array),MessageAudited.class);
			List<Map<String,Object>> maplist = messageJunkMapper.queryMessageJunkByAuditedMsgId(auditedList);
			Map<String,MessageJunk> map = maplist2Map(maplist,MessageJunk.class,"msg_id");
			for(MessageAudited message:auditedList){
				MessageJunk messagejunk = map.get(message.getMsg_id());
				message.setAdvertiser(messagejunk.getAdvertiser());
				message.setCalledid(messagejunk.getCalledid());
				message.setCallerid(messagejunk.getCallerid());
				message.setContent(messagejunk.getContent());
				message.setContentmd5(messagejunk.getContentmd5());
				message.setContentOrigin(messagejunk.getContent_origin());
				message.setContentsimhash(messagejunk.getContentsimhash());
				message.setCreatedtime(messagejunk.getCreatedtime());
				message.setInspect_info(messagejunk.getInspect_info());
				message.setInspect_level(messagejunk.getInspect_level());
				message.setMonitoredtime(messagejunk.getMonitoredtime());
				message.setServicetype(messagejunk.getServicetype());
				message.setUserid(user_id);
				message.setVerifytime(new Date());
				list.add(message);
			}
			messageAuditedMapper.insertRecords(list);
			//删除待审核表中的对应数据
			messageJunkMapper.batchDeleteData(auditedList);
			//计算并更新countrepeat
			List<Integer> junk_idList = new ArrayList<>();
			for(Object obj:array){
				junk_idList.add((Integer) ((HashMap) obj).get("junk_id"));
			}
			Map<Integer, Integer> cache = countList2Map(messageRepeatedMapper.countRepeat(junk_idList));
			List<MessageAudited> listUpdateCount = new ArrayList<>();
			for(MessageAudited message:list){
				message.setRepeatcount(cache.get(((MessageJunk)map.get(message.getMsg_id())).getJunk_id()));
				listUpdateCount.add(message);
			}
			messageAuditedMapper.updateRepeatCountByMsgId(listUpdateCount);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return false;
	}

	/**
	 * 查询当天个人已审核的数据量及当天团队总共已审核的数据量
	 */
	@Override
	public Map<String, Integer> queryAuditedNum(int user_id){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Calendar now = Calendar.getInstance();
			String dateStr = now.get(Calendar.YEAR)+"-"+(now.get(Calendar.MONTH) + 1)+"-"+now.get(Calendar.DAY_OF_MONTH)+" 00:00:00";
			Date date = sdf.parse(dateStr);
			int personal = messageAuditedMapper.countPersonlAuditedNumToday(user_id,date);
			int team = messageAuditedMapper.countTeamAuditedNumToday(date);
			Map<String, Integer> map = new HashMap<>();
			map.put("person", personal);
			map.put("team", team);
			return map;
		} catch (ParseException e) {
			logger.error("日期格式转换出错！");
		} catch (Exception e) {
			logger.error("数据库查询数据出错");
		}
		return null;
	}
	
	@Override
	public int queryJUNKNum(){
		try {
			int num = messageJunkMapper.countNeedAuditNum();
			return num;
		} catch (Exception e) {
			logger.error("数据库查询数据出错");
		}
		return -1;
	}

	@Override
	public Map<String, Object> queryMessageAudited(Date from, Date to,
			Integer level, Integer auditedresult, int size, int number){
		try {
			Map<String,Object> map = new HashMap<>();
			int total = messageAuditedMapper.countAllMessageAudited(from,to,level,auditedresult);
			List<MessageAudited> list = messageAuditedMapper.queryMessageAuditedByCondition(from,to,level,auditedresult,(number-1)*size,size);
			map.put("total", total);
			List<Map<String,Object>> maplist = new ArrayList<>();
			Map<Integer,String> cache = new HashMap<>();
			for(MessageAudited message:list){
				Map<String,Object> mapobj = JsonUtils.objectToMap(message);
				mapobj.remove("monitoredtime");
				mapobj.remove("inspect_info");
				mapobj.remove("contentmd5");
				mapobj.remove("contentsimhash");
				mapobj.remove("createdtime");
				mapobj.put("auditedresult", "0".equals(String.valueOf(mapobj.get("auditedresult")))?"正常":"垃圾");
				mapobj.put("servicetype",KeyWordService.getKeywordServicetype((Integer) mapobj.get("servicetype")));
				int inspect_level = (int) mapobj.get("inspect_level");
				mapobj.put("inspect_level",inspect_level==1?"监控":(inspect_level==2?"拦截":"加黑"));
				if(null==cache.get(mapobj.get("userid"))||"".equals(cache.get(mapobj.get("userid")))){
					cache.put((Integer) mapobj.get("userid"), userMapper.getUserInfoById((int) mapobj.get("userid")).getUser_name());
				}
				if(mapobj.get("repeatcount")==null)
					mapobj.put("repeatcount",0);
				mapobj.put("operator",cache.get(mapobj.get("userid")));
				mapobj.remove("userid");
				maplist.add(mapobj);
			}
			map.put("data", maplist);
			return map;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	//主要是将查询到的结果写入到输出流中
	@Override
	public void exportdata(Date from, Date to, Integer level,Integer auditedresult, OutputStream out){
		//一次最多导出十万条数据
		try {
			Map<String, Object> map= queryMessageAudited(from, to, level, auditedresult, 100000,1);
			@SuppressWarnings("unchecked")
			List<Map<String,Object>> maplist = (List<Map<String,Object>>) map.get("data");
			String[] cn_title = {"短信id","短信内容","广告主","被叫号码","主叫号码","违规级别","业务类型","审核人","重复次数","审核时间","审核结果","短信原始内容"};
			String[] en_property = {"msg_id","content","advertiser","calledid","callerid","inspect_level","servicetype","operator","repeatcount","verifytime","auditedresult","content_origin"};
			FileUtil.writeJSONData2Out(cn_title,en_property,maplist,out);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public Map<String, Object> queryMessageJunk(Date from, Date to,
			Integer level, int size, int number) throws Exception {
		try {
			Map<String,Object> map = new HashMap<>();
			int total = messageJunkMapper.countAllMessageJunk(from,to,level);
			List<MessageJunk> list = messageJunkMapper.queryMessageJunkByCondition(from,to,level,(number-1)*size,size);
			map.put("total", total);
			List<Map<String,Object>> maplist = new ArrayList<>();
			for(MessageJunk message:list){
				Map<String,Object> mapobj = JsonUtils.objectToMap(message);
				mapobj.remove("monitoredtime");
				mapobj.remove("inspect_info");
				mapobj.remove("contentmd5");
				mapobj.remove("contentsimhash");
				mapobj.remove("status");
				mapobj.remove("repeatcount");
				mapobj.put("servicetype",KeyWordService.getKeywordServicetype((Integer) mapobj.get("servicetype")));
				int inspect_level = (int) mapobj.get("inspect_level");
				mapobj.put("inspect_level",inspect_level==1?"监控":(inspect_level==2?"拦截":"加黑"));
				maplist.add(mapobj);
			}
			map.put("data", maplist);
			return map;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public void exportJunkdata(Date from, Date to, Integer level,OutputStream out){
		//一次最多导出十万条数据
		try {
			Map<String, Object> map= queryMessageJunk(from, to, level, 100000,1);
			@SuppressWarnings("unchecked")
			List<Map<String,Object>> maplist = (List<Map<String,Object>>) map.get("data");
			String[] cn_title = {"短信id","短信内容","广告主","被叫号码","主叫号码","违规级别","业务类型","短信原始内容","入库时间"};
			String[] en_property = {"msg_id","content","advertiser","calledid","callerid","inspect_level","servicetype","content_origin","createdtime"};
			FileUtil.writeJSONData2Out(cn_title,en_property,maplist,out);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	//定时任务统计每天审核工作量
	public void calculateWorkResultPerDay(String start, String end) {
		try {
			SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			SimpleDateFormat formaterdate = new SimpleDateFormat("yyyy-MM-dd");
			Date from = formater.parse(start);
			Date to = formater.parse(end);
			Date date = formaterdate.parse(start);
			List<MapModel> list = messageAuditedMapper.calculateWorkResultPerDay(from,to);
			messageAuditedMapper.insertWorkResultPerDay(list,date,new Date());
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(e.getClass()+e.getCause().toString());
		}
	}

	@Override
	public List<Map<String,Object>> getExamUserInfo(Date from, Date to){
		try {
			List<Integer> list = messageAuditedMapper.getExamUserInfo(from,to);
			List<Map<String,Object>> maplist = new ArrayList<>();
			Map<Integer,String> cache = new HashMap<>();
			for(Integer i:list){
				Map<String,Object> map = new HashMap<>();
				if(!cache.containsKey(i)){
					cache.put(i, userMapper.getUserInfoById(i).getUser_name());
				}
				map.put("id", i);
				map.put("username", cache.get(i));
				maplist.add(map);
			}
			return maplist;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public Map<String, Object> queryExamerData(Date from, Date to,Integer userid, int size, int page){
		try {
			int total = messageAuditedMapper.countExamerData(from,to,userid);
			List<EvaluateWorker> list = messageAuditedMapper.queryExamerData(from,to,userid,size,(page-1)*size);
			Map<String,Object> map = new HashMap<>();
			map.put("total", total);
			List<Map<String,Object>> maplist = new ArrayList<>();
			String username = userMapper.getUserInfoById(userid).getUser_name();
			for(EvaluateWorker ew:list){
				Map<String,Object> mapobj = JsonUtils.objectToMap(ew);
				mapobj.put("username", username);
				maplist.add(mapobj);
			}
			map.put("data", maplist);
			return map;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public void exportExamerReport(Date from, Date to, Integer userid,OutputStream out){
		//一次最多导出十万条数据
		try {
			Map<String, Object> map= queryExamerData(from, to, userid, 100000,1);
			@SuppressWarnings("unchecked")
			List<Map<String,Object>> maplist = (List<Map<String,Object>>) map.get("data");
			String[] cn_title = {"用户名","统计日期","统计数量","入库时间"};
			String[] en_property = {"username","date","number","createtime"};
			FileUtil.writeJSONData2Out(cn_title,en_property,maplist,out);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public void resetJunkSms(String fromstr, String tostr) {
		try {
			SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date from = formater.parse(fromstr);
			Date to = formater.parse(tostr);
			messageJunkMapper.resetJunkSms(from,to);
			logger.info("成功重置待审核数据状态！");
		} catch (ParseException e) {
			logger.error("重置待审核数据状态失败！");
			logger.error(e.toString());
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}
	
}
