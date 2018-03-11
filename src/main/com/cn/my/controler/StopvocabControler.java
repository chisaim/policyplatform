package com.cn.my.controler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
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

import com.cn.my.model.Result;
import com.cn.my.model.Stopvocab;
import com.cn.my.service.IStopvocabService;
import com.cn.my.util.Constant;
import com.cn.my.util.ExcelExportUtil;
import com.cn.my.util.FileUtil;
import com.cn.my.util.TokenUtil;
/**
 * @ClassName: Stopvocab
 * @Description: 停用词库管理Controller
 * @author caixiaoyu
 * @date 2017年11月15日
 */
@Controller
@RequestMapping("/stopvocab")
public class StopvocabControler {
	//查看停用词的所有数据
	/**
	 * 自动注入stopvocabservic
	 */
	@Autowired
	@Qualifier("stopvocabservice")
	private IStopvocabService stopvocabservice;
	// 开启日志
	private final static Log logger = LogFactory.getLog(StopvocabControler.class);
	
	//增加停用词
	@RequestMapping(value="/addStopvocab",method=RequestMethod.POST)
	@ResponseBody
	public Result<?> addStopvocab(@RequestBody Stopvocab stopvocab){
		boolean flag = stopvocabservice.addstopvocab(stopvocab);
		 Result<?> re = new Result<Object>(); 
		if (flag==true){
			re.setCode("200");
			re.setMessage("添加信息成功");
		}
		else{
			re.setCode("400");
			re.setMessage("添加信息不成功");
		}
		return re;
	}
	//删除停用词
	@RequestMapping(value="/removeStopvocab",method=RequestMethod.GET)
	@ResponseBody
	public Result<?> removeStopvocab(@RequestParam("stopvocab_id") int stopvocab){
		boolean flag = stopvocabservice.removestopvocab(stopvocab);
		 Result<?> re = new Result<Object>(); 
		if (flag==true){
			re.setCode("200");
			re.setMessage("删除信息成功");
		}
		else{
			re.setCode("400");
			re.setMessage("删除信息不成功");
		}
		return re;
	}
	//修改停用词
	@RequestMapping(value="/editStopvocab",method=RequestMethod.POST)
	@ResponseBody
	public Result<?> editStopvocab(@RequestBody Stopvocab stopvocab){
		boolean flag = stopvocabservice.editstopvocab(stopvocab);
		 Result<?> r = new Result<Object>(); 
		if (flag==true){
			r.setCode("200");
			r.setMessage("修改信息成功");
		}
		else{
			r.setCode("400");
			r.setMessage("修改信息不成功");
		}
		return r;
	}

	//按停用词名称查询词库
	@SuppressWarnings("deprecation")
	@RequestMapping(value="/foundStopvocab",method=RequestMethod.GET)
    @ResponseBody
    public Result<?> foundStopvocab(@RequestParam("stopvocab_stopword") String stopvocab_stopword){ //@RequestParam 注解后是"stopvocab_wordname"，而不是Name
		//boolean flag = stopvocabservice.foundstopvocab(stopvocab_wordname); 
		stopvocab_stopword = URLDecoder.decode(stopvocab_stopword);
			List<Stopvocab> list = stopvocabservice.foundStopvocab(stopvocab_stopword);
			Result<List<Stopvocab>> re = new Result<List<Stopvocab>>();
			re.setCode(Constant.CODE_FOR_SUCCESSR);
			re.setMessage("查询前台数据成功");
			re.setData(list);
			return re;
	}
	//分页查询
	@RequestMapping(value="getStopvocab",method = RequestMethod.GET)
	@ResponseBody
	public Result<?> pageInfo(@RequestParam("pageSize")int pageSize,@RequestParam("pageNumber")int pageNumber){
		Map<String, Object> map=stopvocabservice.getStopvocab(pageSize, pageNumber);
		if(map!=null){
			Result<Map<String,Object>> re = new Result<Map<String,Object>>("200", "获取数据成功！");
			re.setData(map);
			return re;
		}else{
			Result<Map<String,Object>> re = new Result<Map<String,Object>>("400", "获取数据失败！");
			return re;
		}
	}
	//下载停用词模板  downloadModel
	@RequestMapping(value = "/downloadModel",method = RequestMethod.GET)
	public void downloadStopwordModel(HttpServletResponse response){
		OutputStream out = null;
		try {
			String filepath =  StopvocabControler.class.getClassLoader().getResource("stopvocabModel.xls").getPath();
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");//返回类型为文件流，return子句中不再返回值
			response.setHeader("Content-disposition", "attachment; filename=stopvocabModel.xls");
			out = response.getOutputStream();
			out.write(FileUtil.File2byte(filepath));//流转化成字节，对应util中的一个方法
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
	//上传停用词文件
	@RequestMapping(value = "/upload",method = RequestMethod.POST)
	@CrossOrigin(origins = Constant.URL_PATH)
	public @ResponseBody Result<?> upload(@RequestParam("file")MultipartFile file,HttpServletRequest request){
		InputStream in = null;
		try {
			if(!file.isEmpty()){
				in = file.getInputStream();
				List<Stopvocab> wordlist = FileUtil.stream2Entity(Stopvocab.class, in);
				if(wordlist.size()==0){
					logger.error("上传空文件！");
					return new Result<String>("400", "上传空文件，未读取到数据！");
				}
				int fromid = TokenUtil.tokenMap.get(TokenUtil.getCookieByName(request, "token")).getUser().getId();
				boolean flag = stopvocabservice.readFile2Database(wordlist,fromid);
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
	//导出数据
		@RequestMapping(value="exportdata")
		public void exportData(HttpServletResponse response,HttpServletRequest request){
			List<Stopvocab> all = stopvocabservice.getAll();
			String[] head={"stopword:停用词名称","wordtype:停用词类型","createdtime:创建时间","fromid:来源ID","status:状态"};
			ExcelExportUtil.excelExport(head, all, response);
		}
}
