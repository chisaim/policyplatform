package com.cn.my.controler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import org.springframework.web.multipart.MultipartFile;

import com.cn.my.model.Result;
import com.cn.my.model.Special_char;
import com.cn.my.model.User;
import com.cn.my.service.Special_charService;
import com.cn.my.util.Constant;
import com.cn.my.util.ExcelExportUtil;
import com.cn.my.util.FileUtil;
import com.cn.my.util.TokenUtil;

@Controller
@RequestMapping("/Special_char")
public class Special_charControler {
	
	@Autowired
	@Qualifier("Special_charService")
	private Special_charService Special_charService;
	
	// 日志管理
	private final static Log logger = LogFactory.getLog(Special_charControler.class);
	
	@RequestMapping(value="getSpecial_chars",method = RequestMethod.GET)
	@ResponseBody
	public Result<?> getSpecial_chars(@RequestParam("pageSize")int pageSize,@RequestParam("pageNumber")int pageNumber){
		Map<String, Object> map=Special_charService.getSpecial_chars(pageSize, pageNumber);
		if(map!=null){
			Result<Map<String,Object>> re = new Result<Map<String,Object>>(Constant.CODE_FOR_SUCCESSR, "获取数据成功！");
			re.setData(map);
			return re;
		}else{
			Result<Map<String,Object>> re = new Result<Map<String,Object>>(Constant.CODE_FOR_FAILURER, "获取数据失败！");
			return re;
		}
	}
	
	@RequestMapping(value="getSpecial_char",method = RequestMethod.GET)
	@ResponseBody
	public Result<?> getSpecial_char(@RequestParam("id")int id){
		try {
			Result<Special_char> rs=new Result<Special_char>(Constant.CODE_FOR_SUCCESSR, "查询样本模板信息成功！");
			rs.setData(Special_charService.getSpecial_char(id));
			return rs;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			return new Result<String>(Constant.CODE_FOR_FAILURER, e.getMessage());
		}
	}
	
	@RequestMapping(value="deleteSpecial_char",method = RequestMethod.POST)
	@ResponseBody
	public Result<?> deleteSpecial_char(@RequestParam("id")int id){
		Boolean bool=Special_charService.deleteSpecial_char(id);
		if(bool){
			return new Result<String>(Constant.CODE_FOR_SUCCESSR,"删除数据成功!");
		}
		return new Result<String>(Constant.CODE_FOR_FAILURER,"删除数据失败!");
	}
	
	@RequestMapping(value="updateSpecial_char",method = RequestMethod.POST)
	@ResponseBody
	public Result<?> updateSpecial_char(@RequestBody Special_char special_char) throws Exception{
		boolean bool=Special_charService.updateSpecial_char(special_char);
		 if(bool){
			 return new Result<String>(Constant.CODE_FOR_SUCCESSR,"修改数据成功!");
		 }
		return new Result<String>(Constant.CODE_FOR_FAILURER,"修改数据失败!");	
	}
	
	@RequestMapping(value="insertSpecial_char",method = RequestMethod.POST)
	@ResponseBody
	public Result<?> insertSpecial_char(@RequestBody Special_char special_char,HttpServletRequest request) throws Exception{
		User user = TokenUtil.tokenMap.get(TokenUtil.getCookieByName(request, "token")).getUser();
		special_char.setId(user.getId());
		boolean bool=Special_charService.insertSpecial_char(special_char);
		if(bool){
			return new Result<String>(Constant.CODE_FOR_SUCCESSR,"插入数据成功!");
		}
		return new Result<String>(Constant.CODE_FOR_FAILURER,"插入数据失败!");	
	}
	
	//下载模板
	@RequestMapping(value = "/downloadModel_sp", method = RequestMethod.GET)
	public void dowloadModel(HttpServletResponse response) {
		OutputStream out = null;
		try {
			String path = Special_charControler.class.getClassLoader().getResource("special_char.xls").getPath();
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");// 返回类型为文件流，return子句中不再返回值
			response.setHeader("Content-disposition", "attachment; filename=special_char.xls");
			out = response.getOutputStream();
			out.write(FileUtil.File2byte(path));
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error("关闭失败");
				}
			}
		}
	}
	
	//导入
	@RequestMapping(value="uploadSp",method = RequestMethod.POST)
	@ResponseBody
	public Result<?> uploadSp(@RequestParam("file")MultipartFile file,HttpServletRequest request){
		InputStream in = null;
		try {
			if(file!=null){
				in=file.getInputStream();
				List<Special_char> list = FileUtil.stream2Entity(Special_char.class, in);
				if(list.size()==0){
					logger.error("上传空文件！");
					return new Result<String>("400", "上传空文件，未读取到数据！");
				}
				int userid=TokenUtil.tokenMap.get(TokenUtil.getCookieByName(request, "token")).getUser().getId();
				boolean uploadSp = Special_charService.uploadSp(list, userid);
				if(uploadSp){
					return new Result<String>("200", "上传数据成功！");
				}else{
					return new Result<String>("400", "上传数据失败！");
				}
			}else{
				logger.error("上传空文件！");
				return new Result<String>("400", "上传空文件，未读取到数据！");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			return new Result<String>("400", "文件IO错误！");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("数据入库失败！"+e.getMessage());
			return new Result<String>("300", "数据入库失败！");
		}finally{
			try {
				if(in!=null){
					in.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("输入流关闭失败!"+e.getMessage());	
			}
		}
	}
	
	//导出excel
	@RequestMapping(value="exportSp",method=RequestMethod.GET)
	public void export(HttpServletResponse response,HttpServletRequest request){
		String[] head={"spchar:特殊字符","fromid:来源","createdtime:创建时间"};
		List<Special_char> all = Special_charService.getAll();
		ExcelExportUtil.excelExport(head, all, response);
	}
}
