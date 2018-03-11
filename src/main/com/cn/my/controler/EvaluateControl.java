package com.cn.my.controler;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.my.model.PreMessage;
import com.cn.my.model.Result;
import com.cn.my.service.IEvaluateService;
import com.cn.my.util.TokenUtil;

@Controller
@RequestMapping("/evaluateResult")
public class EvaluateControl {
	@Autowired
	@Qualifier("evaluateService")
	private IEvaluateService evaluateService;
	
	// 开启日志
	private final static Log logger = LogFactory.getLog(EvaluateControl.class);
	
	@RequestMapping(value = "/queryEvaluateResult", method = RequestMethod.GET)
	@ResponseBody
	public Result<?> queryEvaluateResult(@RequestParam("page")int page,@RequestParam("size")int size){
		try {
			Map<String,Object> map = evaluateService.queryEvaluateResult(page,size);
			if(map==null){
				return new Result<>("200", "未查询到数据！");
			}
			return new Result<>("200", "查询到数据！",map);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return new Result<>("400", "未查询到数据！");
	}
	
	@RequestMapping(value = "/queryTargetMessage", method = RequestMethod.GET)
	@ResponseBody
	public Result<?> queryTargetMessage(@RequestParam("audited_ids")String audited_ids){
		try {
			List<Map<String,Object>> map = evaluateService.queryTargetMessage(audited_ids);
			if(map==null){
				return new Result<>("200", "未查询到数据！");
			}
			return new Result<>("200", "查询到数据！",map);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return new Result<>("400", "未查询到数据！");
	}
	
	@RequestMapping(value = "/executePreEvaluate",method = RequestMethod.GET)
	public void executePreEvaluate(@RequestParam("preMFid")int preMessageFileid,@RequestParam("preKFid")int preKeywordFileid,HttpServletRequest request){
		try {
			int userid = TokenUtil.tokenMap.get(TokenUtil.getCookieByName(request, "token")).getUser().getId();
			evaluateService.executePreEvaluate(preMessageFileid,preKeywordFileid,userid);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	@RequestMapping(value = "/queryPreResult",method = RequestMethod.GET)
	@ResponseBody
	public Result<?> queryPreResult(@RequestParam("page")int page,@RequestParam("size")int size){
		try {
			Map<String,Object> map = evaluateService.queryPreResult(page,size);
			if(map==null){
				return new Result<>("200", "未查询到数据！");
			}
			return new Result<>("200", "查询到数据！",map);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return new Result<>("200", "未查询到数据！");
	}
	
	@RequestMapping(value = "/queryDataByTaskid",method = RequestMethod.GET)
	@ResponseBody
	public Result<?> queryDataByTaskid(@RequestParam("taskid")int taskid,@RequestParam("page")int page,@RequestParam("size")int size){
		try {
			Map<String,Object> map = evaluateService.queryDataByTaskid(taskid,page,size);
			if(map==null){
				return new Result<>("200", "未查询到数据！");
			}
			return new Result<>("200", "查询到数据！",map);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return new Result<>("400", "未查询到数据！");
	}
	
	@RequestMapping(value = "/queryPreMessages",method = RequestMethod.GET)
	@ResponseBody
	public Result<?> queryPreMessages(@RequestParam("message_ids")String messageids){
		try {
			if(!"".equals(messageids)){
				List<PreMessage> list = evaluateService.queryPreMessages(messageids);
				if(list!=null){
					return new Result<>("200", "查询到数据！",list);
				}
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return new Result<>("400", "未查询到数据！");
	}
}
