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
import com.cn.my.model.User;
import com.cn.my.model.Variant_word;
import com.cn.my.service.Variant_wordService;
import com.cn.my.util.Constant;
import com.cn.my.util.ExcelExportUtil;
import com.cn.my.util.FileUtil;
import com.cn.my.util.TokenUtil;

@Controller
@RequestMapping("/Variant_word")
public class Variant_wordControler {
	
	@Autowired
	@Qualifier("Variant_wordService")
	private Variant_wordService Variant_wordService;
	
	// 日志管理
	private final static Log logger = LogFactory.getLog(Variant_wordControler.class);
	
	@RequestMapping(value="getVariant_words",method=RequestMethod.GET)
	@ResponseBody
	public Result<?> getVariant_words(@RequestParam("pageSize")int pageSize,@RequestParam("pageNumber")int pageNumber){
		Map<String, Object> variant_words = Variant_wordService.getVariant_words(pageSize, pageNumber);
		if(variant_words!=null){
			Result<Map<String,Object>> re = new Result<Map<String,Object>>(Constant.CODE_FOR_SUCCESSR, "获取数据成功！");
			re.setData(variant_words);
			return re;
		}else{
			Result<Map<String,Object>> re = new Result<Map<String,Object>>(Constant.CODE_FOR_FAILURER, "获取数据失败！");
			return re;
		}
	}
	
	@RequestMapping(value="getVariant_word",method=RequestMethod.GET)
	@ResponseBody
	public Result<?> getVariant_word(@RequestParam("id")int id){
		try {
			Result<Variant_word> rs=new Result<Variant_word>(Constant.CODE_FOR_SUCCESSR, "查询样本模板信息成功！");
			rs.setData(Variant_wordService.getvariant_word(id));
			return rs;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			return new Result<String>(Constant.CODE_FOR_FAILURER, e.getMessage());
		}
		
	}
	
	@RequestMapping(value="deleteVariant_word",method=RequestMethod.POST)
	@ResponseBody
	public Result<?> deleteVariant_word(@RequestParam("id") int id){
		try {
			Variant_wordService.deleteVariant_word(id);
			return new Result<Variant_word>(Constant.CODE_FOR_SUCCESSR, "删除样本模板信息成功！");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			return new Result<String>(Constant.CODE_FOR_FAILURER, "删除事物错误，请查看数据库是否正常！");
		}
		
	}
	
	@RequestMapping(value="updateVariant_word",method=RequestMethod.POST)
	@ResponseBody
	public Result<?> updateVariant_word(@RequestBody Variant_word variant_word){
		try {
			Variant_wordService.updateVariant_word(variant_word);
			return new Result<Variant_word>(Constant.CODE_FOR_SUCCESSR, "修改样本模板信息成功！");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			return new Result<String>(Constant.CODE_FOR_FAILURER, "修改数据失败，请联系开发人员查看问题！");
		}
	}
	
	@RequestMapping(value="insertVariant_word",method=RequestMethod.POST)
	@ResponseBody
	public Result<?> insertVariant_word(@RequestBody Variant_word variant_word,HttpServletRequest request){
		User user = TokenUtil.tokenMap.get(TokenUtil.getCookieByName(request, "token")).getUser();
		variant_word.setId(user.getId());
		boolean insertVariant_word = Variant_wordService.insertVariant_word(variant_word);
		if(insertVariant_word){
			return new Result<String>(Constant.CODE_FOR_SUCCESSR,"插入数据成功!");
		}else{
			return new Result<String>(Constant.CODE_FOR_FAILURER,"插入数据失败!");
		}
	}
	
	//下载模板
	@RequestMapping(value = "/downloadModel_va",method = RequestMethod.GET)
	public void dowloadModel(HttpServletResponse response){
			OutputStream out=null;
		try {
			String path = ParticipleControler.class.getClassLoader().getResource("variant_word.xls").getPath();
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");//返回类型为文件流，return子句中不再返回值
			response.setHeader("Content-disposition", "attachment; filename=variant_word.xls");
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
	
	//导入
	@RequestMapping(value="uploadWord",method = RequestMethod.POST)
	@ResponseBody
	public Result<?> uploadWord(@RequestParam("file")MultipartFile file,HttpServletRequest request){
		InputStream in = null;
		try {
			if(file!=null){
				in=file.getInputStream();
				List<Variant_word> list = FileUtil.stream2Entity(Variant_word.class, in);
				if(list.size()==0){
					logger.error("上传空文件！");
					return new Result<String>("400", "上传空文件，未读取到数据！");
				}
				int fromid=TokenUtil.tokenMap.get(TokenUtil.getCookieByName(request, "token")).getUser().getId();
				boolean uploadSp = Variant_wordService.uploadWord(list, fromid);
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
	@RequestMapping(value="exportVar",method=RequestMethod.GET)
	public void exportVar(HttpServletResponse response,HttpServletRequest request){
		String []head={"srcword:正常词","varword:变异词","fromid:来源","createdtime:创建时间"};
		List<Variant_word> all = Variant_wordService.getAll();
		ExcelExportUtil.excelExport(head, all, response);
	}
}
