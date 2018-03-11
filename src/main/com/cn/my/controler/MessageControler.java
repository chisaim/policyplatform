package com.cn.my.controler;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.cn.my.model.MessageJunk;
import com.cn.my.model.Result;
import com.cn.my.service.IMessageService;
import com.cn.my.util.TokenUtil;

/**
 * @ClassName: MessageControler
 * @Description: 用于短信审核相关接口的接入层
 * @author zengyejun
 * @date 2018年1月16日
 */
@Controller
@RequestMapping("/message")
public class MessageControler {
	@Autowired
	@Qualifier("messageService")
	private IMessageService messageService;
	
	// 开启日志
	private final static Log logger = LogFactory.getLog(MessageControler.class);
	
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	@ResponseBody
	public Result<?> test(){
//			try {
//				InputStream inStream = MessageControler.class.getClassLoader().getResourceAsStream("db.properties");
//				Properties prop = new Properties();  
//				prop.load(inStream);
//				String key = prop.getProperty("rpcservice.url");
//				System.err.println(prop.getProperty("rpcservice.queryinterface"));
//				System.err.println(key);
//			} catch (IOException e) {
//				logger.error(e.toString());
//			}  
		return new Result<>("100", "只是因为。。。!-_-!。。。");
	}
	
	/**
	 * @Title: getMessageJunk
	 * @Description: 根据数据入库时间来请求待审核的数据，数据被请求后其状态改变，防止重复请求，数据按入库时间排序
	 */
	@RequestMapping(value = "/getMessageJunk", method = RequestMethod.GET)
	@ResponseBody
	public Result<?> getMessageJunk(@RequestParam("from")String from,@RequestParam("to")String to,
			@RequestParam("inspect_level")Integer inspect_level,@RequestParam("number")Integer number){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date datefrom = sdf.parse(from);
			Date dateto = sdf.parse(to);
			List<MessageJunk> list = messageService.getMessageJunk(datefrom,dateto,inspect_level,number);
			if(list==null){
				return new Result<>("400", "未查询到数据！");
			}
			return new Result<List<MessageJunk>>("200", "获取审核数据成功！",list);
		} catch (ParseException e) {
			logger.error("输入的日期格式有误！"+e.getMessage());
			return new Result<>("400","输入的日期格式有误,导致查询操作无法进行！");
		} catch (Exception e) {
			logger.error("查询数据出错！"+e.getMessage());
			return new Result<>("400","数据库访问数据时出错，请重新请求！");
		}
	}
	
	/**
	 * @Title: submitMessageAudited
	 * @Description: 提交已经审核后的数据
	 */
	@RequestMapping(value = "/submitMessageAudited", method = RequestMethod.POST)
	@ResponseBody
	public Result<?> submitMessageAudited(@RequestBody JSONArray array,HttpServletRequest request){
		//业务逻辑：将数据插入已审核表，并且将数据从待审核表中移除
		try {
			if(array.size()==0){
				return new Result<>("400","提交审核结果失败!");
			}
			int user_id = TokenUtil.tokenMap.get(TokenUtil.getCookieByName(request, "token")).getUser().getId();
			boolean status = messageService.submitMessageAudited(user_id,array);
			if(status){
				return new Result<>("200","提交审核结果成功!");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return new Result<>("400","提交审核结果失败!");
	}
	
	/**
	 * @Title: queryAuditedNum
	 * @Description: 查询当天个人已审核的数据量及总共已审核的数据量
	 */
	@RequestMapping(value = "/queryAuditedNum", method = RequestMethod.GET)
	@ResponseBody
	public Result<?> queryAuditedNum(HttpServletRequest request){
		try {
			int user_id = TokenUtil.tokenMap.get(TokenUtil.getCookieByName(request, "token")).getUser().getId();
			Map<String,Integer> map = messageService.queryAuditedNum(user_id);
			if(map!=null){
				String str = "截止目前，当天你已审核"+map.get("person")+"条数据,团队共审核"+map.get("team")+"条数据！ Y(^_^)Y";
				return new Result<>("200",str);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return new Result<>("400","未查询到数据,可能是数据库出错,稍后再试试!^_^!");
	}
	
	/**
	 * @Title: queryJUNKNum
	 * @Description: 查询待审核的数据量
	 */
	@RequestMapping(value = "/queryJUNKNum", method = RequestMethod.GET)
	@ResponseBody
	public Result<?> queryJUNKNum(){
		try {
			int num = messageService.queryJUNKNum();
			if(num!=-1){
				String str = "截止目前，还有"+num+"条数据待审核！"+(num>5000?"加油~^o^~":"*@_@*");
				return new Result<>("200",str);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return new Result<>("400","未查询到数据,可能是数据库出错,稍后再试试!^_^!");
	}
	
	/**
	 * @Title: queryMessageAudited
	 * @Description: 按要求查询所有的已审核数据
	 */
	@RequestMapping(value = "/queryMessageAudited", method = RequestMethod.GET)
	@ResponseBody
	public Result<?> queryMessageAudited(@RequestParam("from")long fromL,@RequestParam("to")long toL,
			@RequestParam("inspect_level")Integer level,@RequestParam("auditedresult")Integer auditedresult,
			@RequestParam("size")int size,@RequestParam("number")int number){
		try {
			Date from = new Date(fromL);
			Date to = new Date(toL);
			Map<String,Object> map = messageService.queryMessageAudited(from,to,level,auditedresult,size,number);
			return new Result<Map<String,Object>>("200", "查询成功！", map);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return new Result<>("400", "查询失败！");
	}
	
	@RequestMapping(value = "/exportdata",method = RequestMethod.GET)
	public void exportdata(@RequestParam("from")long fromL,@RequestParam("to")long toL,
			@RequestParam("inspect_level")Integer level,@RequestParam("auditedresult")Integer auditedresult,HttpServletResponse response){
		OutputStream out = null;
		try {
			Date from = new Date(fromL);
			Date to = new Date(toL);
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");//返回类型为文件流，return子句中不再返回值
			response.setHeader("Content-disposition", "attachment; filename=MessageAudited.xls");
			out = response.getOutputStream();
			messageService.exportdata(from,to,level,auditedresult,out);
			out.flush();
		} catch (IOException e) {
			logger.error(e.getMessage());
		} catch (Exception e) {
			logger.error("数据写入输出流失败！");
		}finally {
				try {
					if(null!=out){
						out.close();
					}
				} catch (IOException e) {
					logger.error("关闭输出流失败！");
				}
		}
	}
	
	@RequestMapping(value = "/queryMessageJunk", method = RequestMethod.GET)
	@ResponseBody
	public Result<?> queryMessageJunk(@RequestParam("from")long fromL,@RequestParam("to")long toL,
			@RequestParam("inspect_level")Integer level,@RequestParam("size")int size,@RequestParam("number")int number){
		try {
			Date from = new Date(fromL);
			Date to = new Date(toL);
			Map<String,Object> map = messageService.queryMessageJunk(from,to,level,size,number);
			return new Result<Map<String,Object>>("200", "查询成功！", map);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return new Result<>("400", "查询失败！");
	}
	
	@RequestMapping(value = "/exportJunkdata", method = RequestMethod.GET)
	@ResponseBody
	public void exportJunkdata(@RequestParam("from")long fromL,@RequestParam("to")long toL,
			@RequestParam("inspect_level")Integer level,HttpServletResponse response){
		OutputStream out = null;
		try {
			Date from = new Date(fromL);
			Date to = new Date(toL);
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");//返回类型为文件流，return子句中不再返回值
			response.setHeader("Content-disposition", "attachment; filename=MessageJunk.xls");
			out = response.getOutputStream();
			messageService.exportJunkdata(from,to,level,out);
			out.flush();
		} catch (IOException e) {
			logger.error(e.getMessage());
		} catch (Exception e) {
			logger.error("数据写入输出流失败！");
		}finally {
				try {
					if(null!=out){
						out.close();
					}
				} catch (IOException e) {
					logger.error("关闭输出流失败！");
				}
		}
	}
	
	@RequestMapping(value = "/getExamUserInfo", method = RequestMethod.GET)
	@ResponseBody
	public Result<?> getExamUserInfo(@RequestParam("from")long fromL,@RequestParam("to")long toL){
		try {
			Date from = new Date(fromL);
			Date to = new Date(toL);
			List<Map<String,Object>> map = messageService.getExamUserInfo(from,to);
			if(map==null){
				return new Result<>("200", "未查询到结果！");
			}
			return new Result<List<Map<String,Object>>>("200", "查询到结果！", map);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return new Result<>("400", "查询失败！");
	}
	
	@RequestMapping(value = "/queryExamerData", method = RequestMethod.GET)
	@ResponseBody
	public Result<?> queryExamerData(@RequestParam("from")long fromL,@RequestParam("to")long toL,
			@RequestParam("userid")String userid,@RequestParam("size")int size,@RequestParam("page")int page){
		try {
			Date from = new Date(fromL);
			Date to = new Date(toL);
			if(userid.equals("")||userid==null){
				return new Result<>("400", "入参错误！");
			}
			Map<String,Object> map = messageService.queryExamerData(from,to,Integer.valueOf(userid),size,page);
			if(map!=null){
				new Result<Map<String,Object>>("200", "未查询到数据！", map);
			}
			return new Result<Map<String,Object>>("200", "查询成功！", map);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return new Result<>("400", "查询失败！");
	}
	
	@RequestMapping(value = "/exportExamerReport",method = RequestMethod.GET)
	public void exportExamerReport(@RequestParam("from")long fromL,@RequestParam("to")long toL,
			@RequestParam("userid")String userid,HttpServletResponse response){
		OutputStream out = null;
		try {
			Date from = new Date(fromL);
			Date to = new Date(toL);
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");//返回类型为文件流，return子句中不再返回值
			response.setHeader("Content-disposition", "attachment; filename=ExamerReport.xls");
			out = response.getOutputStream();
			messageService.exportExamerReport(from,to,Integer.valueOf(userid),out);
			out.flush();
		} catch (IOException e) {
			logger.error(e.getMessage());
		} catch (Exception e) {
			logger.error("数据写入输出流失败！");
		}finally {
				try {
					if(null!=out){
						out.close();
					}
				} catch (IOException e) {
					logger.error("关闭输出流失败！");
				}
		}
	}
}
