package com.cn.my.controler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.cn.my.model.PreKeyword;
import com.cn.my.model.PreMessage;
import com.cn.my.model.Result;
import com.cn.my.service.IPreEvaluateService;
import com.cn.my.util.FileUtil;
import com.cn.my.util.TokenUtil;
/**
 * @ClassName: PreEvaluateControl
 * @Description: 预评估文件处理入口
 * @author zengyejun
 * @date 2018年1月21日
 */
@Controller
@RequestMapping("/evalute")
public class PreEvaluateControl {
	@Autowired
	@Qualifier("evaluteService")
	private IPreEvaluateService evaluteService;
	
	// 开启日志
	private final static Log logger = LogFactory.getLog(PreEvaluateControl.class);
	
	/**
	 * @Title: downloadPreMessageModel
	 * @Description:下载预评估短信模板
	 */
	@RequestMapping(value = "/downloadPreMessageModel",method = RequestMethod.GET)
	public void downloadPreMessageModel(HttpServletResponse response){
		OutputStream out = null;
		try {
			String filepath = PreEvaluateControl.class.getClassLoader().getResource("PreMessageModel.xls").getPath();
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");//返回类型为文件流，return子句中不再返回值
			response.setHeader("Content-disposition", "attachment; filename=PreMessageModel.xls");
			out = response.getOutputStream();
			out.write(FileUtil.File2byte(filepath));
			out.flush();
		} catch (IOException e) {
			logger.error(e.getMessage());
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
	
	@RequestMapping(value = "/uploadPreSms",method = RequestMethod.POST)
	public @ResponseBody Result<?> uploadPreSms(@RequestParam("file")MultipartFile file,HttpServletRequest request){
		InputStream in = null;
		try {
			if(!file.isEmpty()){
				in = file.getInputStream();
				List<PreMessage> messagelist = FileUtil.stream2Entity(PreMessage.class, in);
				if(messagelist.size()==0){
					logger.error("上传空文件！");
					return new Result<String>("400", "上传空文件，未读取到数据！");
				}
				int userid = TokenUtil.tokenMap.get(TokenUtil.getCookieByName(request, "token")).getUser().getId();
				String filename = file.getOriginalFilename();
				boolean flag = evaluteService.readFile2Database(messagelist,userid,filename);
				if(flag){
					return new Result<String>("200", "上传数据成功！");
				}else{
					return new Result<String>("400", "上传数据失败！");
				}
			}else{
				logger.error("上传空文件！");
				return new Result<String>("400", "上传空文件，未读取到数据！");
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			return new Result<String>("400", "文件IO错误！");
		} catch (Exception e) {
			logger.error("数据入库失败！"+e.getMessage());
			return new Result<String>("300", "数据入库失败！");
		}finally {
			try {
				if(in!=null){
					in.close();
				}
			} catch (IOException e) {
				logger.error("输入流关闭失败!"+e.getMessage());	
			}
		}
	}
	
	@RequestMapping(value = "/queryPreMessageFileInfo",method = RequestMethod.GET)
	@ResponseBody
	public Result<?> queryPreMessageFileInfo(@RequestParam("from")long fromL,@RequestParam("to")long toL,
			@RequestParam("filename")String filename,@RequestParam("number")Integer number,@RequestParam("size")Integer size){
		try {
			Date from = new Date(fromL);
			Date to = new Date(toL);
			Map<String,Object> map = evaluteService.queryPreMessageFileInfo(from,to,filename,number,size);
			if(map!=null){
				return new Result<Map<String,Object>>("200", "查询预评估短信相关信息成功！",map);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return new Result<String>("400", "查询预评估短信相关信息失败！");
	}
	
	@RequestMapping(value = "/queryPreMessage",method = RequestMethod.GET)
	@ResponseBody
	public Result<?> queryPreMessage(@RequestParam("fileid")Integer fileid,@RequestParam("servicetype")Integer servicetype,@RequestParam("content")String content,
			@RequestParam("page")Integer page,@RequestParam("size")Integer size){
		try {
			if(!"".equals(content)){
				content = URLDecoder.decode(content , "UTF-8");
			}
			Map<String,Object> map = evaluteService.queryPreMessage(fileid,servicetype,content,page,size);
			if(map!=null){
				return new Result<Map<String,Object>>("200", "查询预评估短信相关信息成功！",map);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return new Result<String>("400", "查询预评估短信相关信息失败！");
	}
	
	@RequestMapping(value = "/addPreEvaluateSms",method = RequestMethod.POST)
	@ResponseBody
	public Result<?> addPreEvaluateSms(@RequestBody PreMessage preMessage){
		try {
			evaluteService.addPreEvaluateSms(preMessage);
			return new Result<String>("200", "增加预评估短信成功！");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return new Result<String>("400", "增加预评估短信失败！");
	}
	
	@RequestMapping(value = "/editPreEvaluateSms",method = RequestMethod.POST)
	@ResponseBody
	public Result<?> editPreEvaluateSms(@RequestBody PreMessage preMessage){
		try {
			evaluteService.editPreEvaluateSms(preMessage);
			return new Result<String>("200", "修改预评估短信成功！");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return new Result<String>("400", "修改预评估短信失败！");
	}
	
	@RequestMapping(value = "/delPreEvaluateSms/{id}",method = RequestMethod.GET)
	@ResponseBody
	public Result<?> delPreEvaluateSms(@PathVariable("id") int id){
		try {
			evaluteService.delPreEvaluateSms(id);
			return new Result<String>("200", "删除预评估短信成功！");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return new Result<String>("400", "删除预评估短信失败！");
	}
	
	@RequestMapping(value = "/exportdata",method = RequestMethod.GET)
	public void exportdata(@RequestParam("fileid")Integer fileid,@RequestParam("servicetype")Integer servicetype,
			@RequestParam("content")String content,HttpServletResponse response){
		OutputStream out = null;
		try {
			if(!"".equals(content)){
				content = URLDecoder.decode(content , "UTF-8");
			}
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");//返回类型为文件流，return子句中不再返回值
			response.setHeader("Content-disposition", "attachment; filename=PreMessage.xls");
			out = response.getOutputStream();
			evaluteService.exportdata(fileid,servicetype,content,out);
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
	
	/**
	 * @Title: downloadPreMessageModel
	 * @Description:下载预评估策略模板
	 */
	@RequestMapping(value = "/downloadPreStrategyModel",method = RequestMethod.GET)
	public void downloadPreStrategyModel(HttpServletResponse response){
		OutputStream out = null;
		try {
			String filepath = PreEvaluateControl.class.getClassLoader().getResource("PreStrategyModel.xls").getPath();
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");//返回类型为文件流，return子句中不再返回值
			response.setHeader("Content-disposition", "attachment; filename=PreStrategyModel.xls");
			out = response.getOutputStream();
			out.write(FileUtil.File2byte(filepath));
			out.flush();
		} catch (IOException e) {
			logger.error(e.getMessage());
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
	
	@RequestMapping(value = "/uploadPrePolicy",method = RequestMethod.POST)
	public @ResponseBody Result<?> uploadPrePolicy(@RequestParam("file")MultipartFile file,HttpServletRequest request){
		InputStream in = null;
		try {
			if(!file.isEmpty()){
				in = file.getInputStream();
				List<PreKeyword> keywordlist = FileUtil.stream2Entity(PreKeyword.class, in);
				if(keywordlist.size()==0){
					logger.error("上传空文件！");
					return new Result<String>("400", "上传空文件，未读取到数据！");
				}
				int userid = TokenUtil.tokenMap.get(TokenUtil.getCookieByName(request, "token")).getUser().getId();
				String filename = file.getOriginalFilename();
				boolean flag = evaluteService.readPreKeyword2DB(keywordlist,userid,filename);
				if(flag){
					return new Result<String>("200", "上传数据成功！");
				}else{
					return new Result<String>("400", "上传数据失败！");
				}
			}else{
				logger.error("上传空文件！");
				return new Result<String>("400", "上传空文件，未读取到数据！");
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			return new Result<String>("400", "文件IO错误！");
		} catch (Exception e) {
			logger.error("数据入库失败！"+e.getMessage());
			return new Result<String>("300", "数据入库失败！");
		}finally {
			try {
				if(in!=null){
					in.close();
				}
			} catch (IOException e) {
				logger.error("输入流关闭失败!"+e.getMessage());	
			}
		}
	}
	
	@RequestMapping(value = "/queryPreKeywordFileInfo",method = RequestMethod.GET)
	@ResponseBody
	public Result<?> queryPreKeywordFileInfo(@RequestParam("from")long fromL,@RequestParam("to")long toL,
			@RequestParam("filename")String filename,@RequestParam("number")Integer number,@RequestParam("size")Integer size){
		try {
			Date from = new Date(fromL);
			Date to = new Date(toL);
			Map<String,Object> map = evaluteService.queryPreKeywordFileInfo(from,to,filename,number,size);
			if(map!=null){
				return new Result<Map<String,Object>>("200", "查询预评估策略相关信息成功！",map);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return new Result<String>("400", "查询预评估策略相关信息失败！");
	}
	
	@RequestMapping(value = "/queryPreKeyword",method = RequestMethod.GET)
	@ResponseBody
	public Result<?> queryPreKeyword(@RequestParam("fileid")Integer fileid,@RequestParam("servicetype")Integer servicetype,@RequestParam("kwclass")Integer kwclass,
			@RequestParam("keyword")String keyword,@RequestParam("page")Integer page,@RequestParam("size")Integer size){
		try {
			if(!"".equals(keyword)){
				keyword = URLDecoder.decode(keyword , "UTF-8");
			}
			Map<String,Object> map = evaluteService.queryPreKeyword(fileid,servicetype,kwclass,keyword,page,size);
			if(map!=null){
				return new Result<Map<String,Object>>("200", "查询预评估策略相关信息成功！",map);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return new Result<String>("400", "查询预评估策略相关信息失败！");
	}
	
	@RequestMapping(value = "/addPreEvaluateKeyword",method = RequestMethod.POST)
	@ResponseBody
	public Result<?> addPreEvaluateKeyword(@RequestBody PreKeyword preKeyword){
		try {
			evaluteService.addPreEvaluateKeyword(preKeyword);
			return new Result<String>("200", "增加预评估策略成功！");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return new Result<String>("400", "增加预评估策略失败！");
	}
	
	@RequestMapping(value = "/editPreEvaluateKeyword",method = RequestMethod.POST)
	@ResponseBody
	public Result<?> editPreEvaluateKeyword(@RequestBody PreKeyword preKeyword){
		try {
			evaluteService.editPreEvaluateKeyword(preKeyword);
			return new Result<String>("200", "修改预评估策略成功！");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return new Result<String>("400", "修改预评估策略失败！");
	}
	
	@RequestMapping(value = "/delPreEvaluateKeyword/{id}",method = RequestMethod.GET)
	@ResponseBody
	public Result<?> delPreEvaluateKeyword(@PathVariable("id") int id){
		try {
			evaluteService.delPreEvaluateKeyword(id);
			return new Result<String>("200", "删除预评估策略成功！");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return new Result<String>("400", "删除预评估策略失败！");
	}
	
	@RequestMapping(value = "/exportKeyword",method = RequestMethod.GET)
	public void exportKeyword(@RequestParam("fileid")Integer fileid,@RequestParam("servicetype")Integer servicetype,
			@RequestParam("kwclass")Integer kwclass,@RequestParam("keyword")String keyword,HttpServletResponse response){
		OutputStream out = null;
		try {
			if(!"".equals(keyword)){
				keyword = URLDecoder.decode(keyword , "UTF-8");
			}
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");//返回类型为文件流，return子句中不再返回值
			response.setHeader("Content-disposition", "attachment; filename=PreKeyword.xls");
			out = response.getOutputStream();
			evaluteService.exportKeyword(fileid,servicetype,kwclass,keyword,out);
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
