package com.cn.my.service.impl;

import java.io.InputStream;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.my.mapper.EvaluateResultMapper;
import com.cn.my.mapper.KeyWordMapper;
import com.cn.my.mapper.MessageAuditedMapper;
import com.cn.my.mapper.PreEvaluateMapper;
import com.cn.my.mapper.UserMapper;
import com.cn.my.model.EvaluateFileInfo;
import com.cn.my.model.EvaluateResult;
import com.cn.my.model.MessageAudited;
import com.cn.my.model.PolicyPreEvaluation;
import com.cn.my.model.PreEvaluateResult;
import com.cn.my.model.PreMessage;
import com.cn.my.service.IEvaluateService;
import com.cn.my.util.Constant;
import com.cn.my.util.HttpUtil;
import com.cn.my.util.JsonUtils;
import com.cn.my.util.Shell;
@Service("evaluateService")
@Transactional
public class EvaluateServiceImpl implements IEvaluateService{
	
	@Autowired
	private EvaluateResultMapper evaluateMapper;
	
	@Autowired
	private KeyWordMapper keywordMapper;
	
	@Autowired
	private MessageAuditedMapper auditedMapper;
	
	@Autowired
	private PreEvaluateMapper preEvaluateMapper;
	
	@Autowired
	private UserMapper userMapper;
	
	private final static Log logger = LogFactory.getLog(EvaluateServiceImpl.class);

	@Override
	public Map<String, Object> queryEvaluateResult(int page, int size){
		try {
			Map<String,Object> map = new HashMap<>();
			int total = evaluateMapper.countEvaluateResult();
			map.put("total", total);
			List<EvaluateResult> list = evaluateMapper.queryEvaluateResult(size,(page-1)*size);
			List<Map<String,Object>> maplist = new ArrayList<>();
			
			for(EvaluateResult er:list){
				Map<String,Object> mapObj = JsonUtils.objectToMap(er);
				mapObj.put("keyword",keywordMapper.selectByPrimaryKey(er.getKeyword_id()).getKeyword());
				maplist.add(mapObj);
			}
			map.put("data", maplist);
			return map;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> queryTargetMessage(String audited_ids){
		try {
			String[] arrayStr = audited_ids.split(",");
			List<Integer> list = new ArrayList<>();
			for(String str:arrayStr){
				list.add(Integer.valueOf(str));
			}
			if(list.size()==0){
				return null;
			}
			List<MessageAudited> messagelist = auditedMapper.queryTargetMessage(list);
			List<Map<String, Object>> maplist = new ArrayList<>();
			for(MessageAudited ma:messagelist){
				Map<String, Object> map = JsonUtils.objectToMap(ma);
				map.put("auditedresult", ((int)map.get("auditedresult"))==0?"正常":"垃圾");
				maplist.add(map);
			}
			return maplist;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public void executePreEvaluate(int preMessageFileid, int preKeywordFileid,int userid){
		InputStream inStream = null;
		PreparedStatement pst = null;
		ResultSet rs= null;
		PreEvaluateResult per = new PreEvaluateResult();
		try {
			//插入任务信息
			per.setPremessage_fileid(preMessageFileid);
			per.setPrepolicy_fileid(preKeywordFileid);
			EvaluateFileInfo preMefi = preEvaluateMapper.getFileinfoByfileid(preMessageFileid);
			per.setPremessage_number(preMefi.getDatanumber());
			EvaluateFileInfo preKefi = preEvaluateMapper.getFileinfoByfileid(preKeywordFileid);
			per.setPrepolicy_number(preKefi.getDatanumber());
			per.setUserid(userid);
			per.setTask_starttime(new Date());
			per.setTask_status(Constant.TASK_STATUS_EXECUATING);
			evaluateMapper.insertPreEvaluateTaskInfo(per);
			//调用远程服务，执行预评估过程
			inStream = EvaluateServiceImpl.class.getClassLoader().getResourceAsStream("db.properties");
			Properties prop = new Properties();  
			prop.load(inStream);  
			String rpcurl = prop.getProperty("rpcservice.url");
			String rpcusername = prop.getProperty("rpcservice.username");
			String rpcpassword = prop.getProperty("rpcservice.password");
			Shell shell = new Shell(rpcurl, rpcusername, rpcpassword);
			//关键词id 短信文件id taskid	shell.execute("/usr/local/shells/strate-pre_eval.sh 1 1");
			String rpcexecutepath = prop.getProperty("rpcservice.executepath");
			shell.execute(rpcexecutepath+" "+preKeywordFileid+" "+preMessageFileid+" "+per.getTaskid());
			//查询执行结果,此处用框架会取不出结果来
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection(prop.getProperty("jdbc.url"),prop.getProperty("jdbc.username"),prop.getProperty("jdbc.password"));
			String sql = "select APP_ID from SPARK_TASK_STATUS where TASK_ID = ?";
			pst = connection.prepareStatement(sql);
			pst.setInt(1, per.getTaskid());
			rs = pst.executeQuery();
			String appId = null;
			while(rs.next()){
				appId = rs.getString(1);
			}
			String rpcqueryinterface = prop.getProperty("rpcservice.queryinterface");
			rpcqueryinterface = rpcqueryinterface.replace("&&&&", appId);
			logger.info(rpcqueryinterface);
			String executeResult = HttpUtil.httpURLConectionGET(rpcqueryinterface);
			if(executeResult==null){
				logger.error(rpcqueryinterface+"接口请求数据失败");
				per.setTask_endtime(new Date());
				per.setTask_status(2);
				preEvaluateMapper.updatePreEvaluateResult(per);
			}else{
				executeResult = executeResult.substring(1, executeResult.length()-1);
				Map<String,Object> map = JsonUtils.json2map(executeResult);
				String status = (String) map.get("status");
				int spark_status = 0;
				if("SUCCEEDED".equals(status)){
					spark_status = Constant.SPARK_EXECUATE_STATUS_SUCCEES;
					calculatePreEvaluateResult(per);
				}else{
					per.setTask_endtime(new Date());
					per.setTask_status(2);
					preEvaluateMapper.updatePreEvaluateResult(per);
				}
				preEvaluateMapper.insertSparkExcuatedStatus(spark_status,per.getTaskid());
				//将计算结果插入数据库,计算查全率、查准率等
			}
			logger.info("执行预评估成功！");
		}catch(SocketException e){
			logger.error("远程连接失败！ "+e.toString());
			per.setTask_endtime(new Date());
			per.setTask_status(2);
			try {
				preEvaluateMapper.updatePreEvaluateResult(per);
			} catch (Exception e1) {
				logger.error(e1.toString());
			}
		}
		catch (Exception e) {
			logger.error(e.toString());
			per.setTask_endtime(new Date());
			per.setTask_status(2);
			try {
				preEvaluateMapper.updatePreEvaluateResult(per);
			} catch (Exception e1) {
				logger.error(e1.toString());
			}
		}finally {
			try {
				if(inStream!=null){
					inStream.close();
				}
				if(pst!=null){
					pst.close();
				}
				if(rs!=null){
					rs.close();
				}
			} catch (Exception e2) {
				logger.error(e2.getMessage());
			}
		}
	}
	
	private void calculatePreEvaluateResult(PreEvaluateResult per) {
		//先获取本次任务所有的结果，统计并计算命中短信策略数、被命中短信数、查全率、查准率
		try {
			List<PolicyPreEvaluation> list = preEvaluateMapper.queryDataByTaskid(per.getTaskid(), 0, 100000);
			int target_policy = 0,target_message = 0;
			double recall_ratio=0,precision_ratio=0;
			HashSet<Integer> messageset = new HashSet<>();
			for(PolicyPreEvaluation ppe:list){
				if(ppe.getPre_message_ids()!=null){
					target_policy++;
					String[] array = ppe.getPre_message_ids().split(",");
					for(String str:array){
						messageset.add(Integer.valueOf(str));
					}
				}
			}
			target_message = messageset.size();
			recall_ratio = (double)target_message/per.getPremessage_number();
			List<Integer> premessages = new ArrayList<>(messageset);
			if(premessages.size()!=0){
				int messageLaji = preEvaluateMapper.countPreMessageLaji(premessages);
				precision_ratio = (double)messageLaji/target_message;
			}
			per.setTarget_policy(target_policy);
			per.setTarget_message(target_message);
			per.setRecall_ratio(recall_ratio);
			per.setPrecision_ratio(precision_ratio);
			per.setTask_endtime(new Date());
			per.setTask_status(1);
			preEvaluateMapper.updatePreEvaluateResult(per);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public Map<String, Object> queryPreResult(int page, int size){
		try {
			Map<String,Object> map = new HashMap<>();
			int total = preEvaluateMapper.countPreEvaluateResult();
			if(total==0){
				return null;
			}
			map.put("total", total);
			List<PreEvaluateResult> list = preEvaluateMapper.queryPreResult((page-1)*size,size);
			List<Map<String,Object>> maplist = new ArrayList<>();
			for(PreEvaluateResult per:list){
				Map<String,Object> mapObj = JsonUtils.objectToMap(per);
				mapObj.put("prepolicy_filename", preEvaluateMapper.getFileinfoByfileid(per.getPrepolicy_fileid()).getFilename());
				mapObj.put("premessage_filename", preEvaluateMapper.getFileinfoByfileid(per.getPremessage_fileid()).getFilename());
				mapObj.put("task_status", per.getTask_status()==0?"执行中":(per.getTask_status()==1?"执行完":"执行失败"));
				mapObj.put("username", userMapper.getUserInfoById(per.getUserid()).getUser_name());
				if(per.getTask_endtime()==null){
					mapObj.put("task_endtime", new Date());
				}
				maplist.add(mapObj);
			}
			map.put("data", maplist);
			return map;
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return null;
	}

	@Override
	public Map<String, Object> queryDataByTaskid(int taskid, int page, int size){
		try {
			Map<String,Object> map = new HashMap<>();
			int total = preEvaluateMapper.countPreKeyWordBytaskId(taskid);
			if(total==0){
				return null;
			}
			map.put("total", total);
			List<PolicyPreEvaluation> list = preEvaluateMapper.queryDataByTaskid(taskid,(page-1)*size,size);
			List<Map<String,Object>> maplist = new ArrayList<>();
			for(PolicyPreEvaluation ppe:list){
				Map<String,Object> mapObj = JsonUtils.objectToMap(ppe);
				mapObj.put("keyword", preEvaluateMapper.getKeywordById(ppe.getKeyword_id()).getKeyword());
				maplist.add(mapObj);
			}
			map.put("data", maplist);
			return map;
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return null;
	}

	@Override
	public List<PreMessage> queryPreMessages(String messageids){
		try {
			String[] array = messageids.split(",");
			List<Integer> list = new ArrayList<>();
			for(String str:array){
				list.add(Integer.valueOf(str));
			}
			List<PreMessage> listPreMessage = preEvaluateMapper.queryPreMessagesByID(list);
			return listPreMessage;
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return null;
	}
	
	public static void main(String[] args) {
		Shell shell = new Shell("192.168.102.161", "root", "bjjh123");
		shell.execute("/usr/local/shells/strate-pre_eval.sh 27 26 11");
	}
}
 