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

import com.cn.my.model.Participle;
import com.cn.my.model.Result;
import com.cn.my.model.User;
import com.cn.my.service.ParticipleService;
import com.cn.my.util.Constant;
import com.cn.my.util.ExcelExportUtil;
import com.cn.my.util.FileUtil;
import com.cn.my.util.TokenUtil;

@Controller
@RequestMapping("/Participle")
public class ParticipleControler {
	
	@Autowired
	@Qualifier("ParticipleService")
	private ParticipleService ParticipleService;
	
	// 日志管理
	private final static Log logger = LogFactory.getLog(ParticipleControler.class);
	
	@RequestMapping(value="/getParticiple", method = RequestMethod.GET)
	public @ResponseBody Result<?> getParticiple(@RequestParam("id") int id){
		try {
			Result<Participle> rs=new Result<Participle>(Constant.CODE_FOR_SUCCESSR, "查询样本模板信息成功！");
			rs.setData(ParticipleService.getParticiple(id));
			return rs;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			return new Result<String>(Constant.CODE_FOR_FAILURER, e.getMessage());
		}		
	}
	
	@RequestMapping(value="/addParticiple", method = RequestMethod.POST)
	@ResponseBody 
	public Result<?> insertParticiple(@RequestBody Participle participle,HttpServletRequest request){
		User user = TokenUtil.tokenMap.get(TokenUtil.getCookieByName(request, "token")).getUser();
		participle.setOperator(user.getUser_name());
		Boolean bool=ParticipleService.insertParticiple(participle);
		if(bool){
			return new Result<String>("200","插入数据成功!");
		}
		return new Result<String>("400","插入数据失败!");
	}
	
	@RequestMapping(value="/editParticiple", method = RequestMethod.POST)
	@ResponseBody
	public Result<?> updateParticiple(@RequestBody Participle participle,HttpServletRequest request){
		User user = TokenUtil.tokenMap.get(TokenUtil.getCookieByName(request, "token")).getUser();
		participle.setOperator(user.getUser_name());
		 Boolean bool=ParticipleService.updateParticiple(participle);
		 if(bool){
			 return new Result<String>("200","修改数据成功!");
		 }
		return new Result<String>("400","修改数据失败!");
	}
	
	@RequestMapping(value="deleteParticiple", method = RequestMethod.POST)
	@ResponseBody
	public Result<?> deleteParticiple(int id){
		Boolean bool=ParticipleService.deleteParticiple(id);
		if(bool){
			return new Result<String>("200","删除数据成功!");
		}
		return new Result<String>("400","删除数据失败!");
	}
	
	@RequestMapping(value="pageInfo",method = RequestMethod.GET)
	@ResponseBody
	public Result<?> pageInfo(@RequestParam("pageSize")int pageSize,@RequestParam("pageNumber")int pageNumber){
		Map<String, Object> map=ParticipleService.pageInfo(pageSize, pageNumber);
		if(map!=null){
			Result<Map<String,Object>> re = new Result<Map<String,Object>>("200", "获取数据成功！");
			re.setData(map);
			return re;
		}else{
			Result<Map<String,Object>> re = new Result<Map<String,Object>>("400", "获取数据失败！");
			return re;
		}
	}
	
	//下载excel模板
	@RequestMapping(value = "/downloadModel",method = RequestMethod.GET)
	public void dowloadModel(HttpServletResponse response){
			OutputStream out=null;
		try {
			String path = ParticipleControler.class.getClassLoader().getResource("participleModel.xls").getPath();
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");//返回类型为文件流，return子句中不再返回值
			response.setHeader("Content-disposition", "attachment; filename=participleModel.xls");
			out=response.getOutputStream();
			out.write(FileUtil.File2byte(path));
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}finally{
			if(out!=null){
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error("关闭失败");
				}
			}
		}
	}
	
	@RequestMapping(value="uploadParticiple",method = RequestMethod.POST)
	@ResponseBody
	public Result<?> uploadParticiple(@RequestParam("file")MultipartFile file,HttpServletRequest request){
		InputStream in = null;
		try {
			if(file!=null){
				in=file.getInputStream();
				List<Participle> stream2Entity = FileUtil.stream2Entity(Participle.class, in);
				if(stream2Entity.size()==0){
					logger.error("上传空文件！");
					return new Result<String>("400", "上传空文件，未读取到数据！");
				}
				String operator=TokenUtil.tokenMap.get(TokenUtil.getCookieByName(request, "token")).getUser().getName();
				boolean uploadData = ParticipleService.uploadData(stream2Entity, operator);
				if(uploadData){
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
	
	//导出
	@RequestMapping(value="exportData")
	public void exportData(HttpServletResponse response,HttpServletRequest request){
		List<Participle> all = ParticipleService.getAll();
		String[] head={"name:分词名","participle_type:分词类型","Pcreate_time:创建时间","operator:来源","remark:备注"};
		ExcelExportUtil.excelExport(head, all, response);
	}
}

