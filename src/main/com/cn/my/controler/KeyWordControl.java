package com.cn.my.controler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.cn.my.model.Keyword;
import com.cn.my.model.Result;
import com.cn.my.service.IKeyWordService;
import com.cn.my.util.Constant;
import com.cn.my.util.FileUtil;
import com.cn.my.util.TokenUtil;

/**
 * @ClassName: KeyWordControl
 * @Description: 策略管理类
 * @author zengyejun
 * @date 2018年1月9日
 */
@Controller
@RequestMapping("/keyword")
public class KeyWordControl {
	@Autowired
	@Qualifier("keywordService")
	private IKeyWordService keywordService;
	
	// 开启日志
	private final static Log logger = LogFactory.getLog(KeyWordControl.class);
	
	@RequestMapping(value = "/addNewKeyword", method = RequestMethod.POST)
	@ResponseBody
	public Result<?> addNewKeyword(@RequestBody Keyword keyword,HttpServletRequest request){
		try {
			keyword.setUserid(TokenUtil.tokenMap.get(TokenUtil.getCookieByName(request, "token")).getUser().getId());
			keyword.setCreatedtime(new Date());
			boolean flag = keywordService.addNewKeyword(keyword);
			if(flag){
				return new Result<String>("200", "插入策略关键字信息成功！");
			}
			return new Result<String>("400", "插入策略关键字信息失败！");
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new Result<String>("400", "插入策略关键字信息失败！");
		}
	}
	
	@RequestMapping(value = "/editKeyword", method = RequestMethod.POST)
	@ResponseBody
	public Result<?> editKeyword(@RequestBody Keyword keyword,HttpServletRequest request){//现在只修改关键词、处罚粒度、阈值、分类、处置
		//level不更新，时间及用户都更新，其他数据更新
		try {
			int userid = TokenUtil.tokenMap.get(TokenUtil.getCookieByName(request, "token")).getUser().getId();
			keyword.setUserid(userid);
			keyword.setCreatedtime(new Date());
			boolean flag = keywordService.editKeyword(keyword);
			if(!flag){
				return new Result<String>("400","更新数据失败！");
			}
			return new Result<String>("200","更新数据成功！");
		} catch (Exception e) {
			logger.error("更新数据失败！"+e.getMessage());
			return new Result<String>("400","更新数据失败！");
		}
	}
	
	@RequestMapping(value = "/delKeyword", method = RequestMethod.GET)
	@ResponseBody
	public Result<?> delKeyword(@RequestParam("keywordid") int keywordid){
		try {
			boolean flag = keywordService.delKeyword(keywordid);
			if(flag){
				return new Result<String>("200","删除数据成功！");
			}
			return new Result<String>("400","删除数据出错！");
		} catch (Exception e) {
			logger.error("函数keywordService.delKeyword错误"+e.getMessage());
			return new Result<String>("400","删除数据出错！");
		}
	}
	
	@RequestMapping(value = "/querySingleKeyword", method = RequestMethod.GET)
	@ResponseBody
	public Result<?> querySingleKeyword(@RequestParam("keywordid") int keywordid){
		try {
			Keyword keyword = keywordService.querySingleKeyword(keywordid);
			if(keyword!=null){
				return new Result<Keyword>("200","查询数据成功！",keyword);
			}
			return new Result<String>("400","未查询到数据！");
		} catch (Exception e) {
			logger.error("函数keywordService.querySingleKeyword错误"+e.getMessage());
			return new Result<String>("400","查询数据出错！");
		}
	}
	
	//查询此时的关键词策略不涉及多个版本，故不使用时间参数，其它参数如分类后期按需求更改
	@RequestMapping(value = "/queryKeyword", method = RequestMethod.GET)
	@ResponseBody
	public Result<?> queryKeyword(@RequestParam("page_number")int page,@RequestParam("page_size")int size){
		try {
			Map<String,Object> data = keywordService.queryKeyword(page,size);
			if(null==data){
				return new Result<String>("400","未查询到数据！");
			}else{
				Result<Map<String,Object>> re = new Result<Map<String,Object>>("200","查询数据成功！");
				re.setData(data);
				return re;
			}
		} catch (Exception e) {
			logger.error("函数出错：keywordService.queryKeyword,"+e.getMessage());
			return new Result<String>("400","查询数据出错！");
		}
	}
	
	/**
	 *策略关键字模板及说明文件下载keywordModel.xls下载
	 */
	@RequestMapping(value = "/downloadModel",method = RequestMethod.GET)
	public void downloadKeywordModel(HttpServletResponse response){
		OutputStream out = null;
		try {
			String filepath = KeyWordControl.class.getClassLoader().getResource("keywordModel.xls").getPath();
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");//返回类型为文件流，return子句中不再返回值
			response.setHeader("Content-disposition", "attachment; filename=keywordModel.xls");
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
	
	/**
	 * 一般在异步获取数据时使用，在使用@RequestMapping后，返回值通常解析为跳转路径，
	 * 加上@responsebody后返回结果不会被解析为跳转路径，而是直接写入HTTP response body中
	 * 不加@responsebody返回数据则会解析为路径跳转成/keyword/keyword/uploaddata,导致400错误
	 */
	@RequestMapping(value = "/uploaddata",method = RequestMethod.POST)
	@CrossOrigin(origins = Constant.URL_PATH)
	public @ResponseBody Result<?> uploaddata(@RequestParam("file")MultipartFile file,HttpServletRequest request){
		InputStream in = null;
		try {
			if(!file.isEmpty()){
				in = file.getInputStream();
				List<Keyword> wordlist = FileUtil.stream2Entity(Keyword.class, in);
				if(wordlist.size()==0){
					logger.error("上传空文件！");
					return new Result<String>("400", "上传空文件，未读取到数据！");
				}
				int userid = TokenUtil.tokenMap.get(TokenUtil.getCookieByName(request, "token")).getUser().getId();
				boolean flag = keywordService.readFile2Database(wordlist,userid);
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
}
