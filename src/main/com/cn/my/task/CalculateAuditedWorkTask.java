package com.cn.my.task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cn.my.model.Token;
import com.cn.my.service.impl.MessageServiceImpl;
import com.cn.my.util.SpringTool;
import com.cn.my.util.TokenUtil;

@Component
public class CalculateAuditedWorkTask {
	
	private final static Log logger = LogFactory.getLog(CalculateAuditedWorkTask.class);
	
	/**
	 *注意事项：
	 *1、spring的@Scheduled注解  需要写在实现方法上
	 *2、 定时器的任务方法不能有返回值（如果有返回值，spring初始化的时候会告诉你有个错误、需要设定一个proxytargetclass的某个值为true）
	 *3、实现类上要有组件的注解@Component
	 *通配符说明:

	 * 表示所有值. 例如:在分的字段上设置 "*",表示每一分钟都会触发。
	? 表示不指定值。使用的场景为不需要关心当前设置这个字段的值。
	例如:要在每月的10号触发一个操作，但不关心是周几，所以需要周位置的那个字段设置为"?" 具体设置为 0 0 0 10 * ?
	- 表示区间。例如 在小时上设置 "10-12",表示 10,11,12点都会触发。
	, 表示指定多个值，例如在周字段上设置 "MON,WED,FRI" 表示周一，周三和周五触发
	/ 用于递增触发。如在秒上面设置"5/15" 表示从5秒开始，每增15秒触发(5,20,35,50)。 在月字段上设置'1/3'所示每月1号开始，每隔三天触发一次。
	L 表示最后的意思。在日字段设置上，表示当月的最后一天(依据当前月份，如果是二月还会依据是否是润年[leap]), 在周字段上表示星期六，相当于"7"或"SAT"。如果在"L"前加上数字，则表示该数据的最后一个。例如在周字段上设置"6L"这样的格式,则表示“本月最后一个星期五"
	W 表示离指定日期的最近那个工作日(周一至周五). 例如在日字段上设置"15W"，表示离每月15号最近的那个工作日触发。如果15号正好是周六，则找最近的周五(14号)触发, 如果15号是周未，则找最近的下周一(16号)触发.如果15号正好在工作日(周一至周五)，则就在该天触发。如果指定格式为 "1W",它则表示每月1号往后最近的工作日触发。如果1号正是周六，则将在3号下周一触发。(注，"W"前只能设置具体的数字,不允许区间"-").
	# 序号(表示每月的第几个周几)，例如在周字段上设置"6#3"表示在每月的第三个周六.注意如果指定"#5",正好第五周没有周六，则不会触发该配置(用在母亲节和父亲节再合适不过了) ；
	小提示：
	'L'和 'W'可以组合使用。如果在日字段上设置"LW",则表示在本月的最后一个工作日触发；
	周字段的设置，若使用英文字母是不区分大小写的，即MON 与mon相同；
	 */
	
	//扩展：定时时间的设置
	//如：“0/5 * * * * ?”
	//CronTrigger配置完整格式为： [秒] [分] [小时] [日] [月] [周] [年]
	//序号	说明	是否必填	允许填写的值	允许的通配符
	//1 	秒 	是 	0-59 	, - * /
	//2 	分 	是 	0-59 	, - * /
	//3 	小时 	是 	0-23 	, - * /
	//4 	日 	是 	1-31 	, - * ? / L W
	//5 	月 	是 	1-12或JAN-DEC 	, - * /
	//6 	周 	是 	1-7或SUN-SAT 	, - * ? / L W
	//7 	年 	否 	empty 或1970-2099 	, - * /
	
	@Scheduled(cron = "0 0 1 * * ? ") // 每天凌晨1点执行
	public void calculateWorkResultPerDay(){
		try {
			//定时(每天凌晨1点)每天统计审核人员的审核工作量
			Date today = new Date();
			Date yesterday = new Date(today.getTime() - 86400000L);//86400000L，它的意思是说1天的时间=24小时 x 60分钟 x 60秒 x 1000毫秒 单位是L。
			SimpleDateFormat startFormater = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
			SimpleDateFormat endFormater = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
			SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
			//统计任务开始
			logger.info(formater.format(yesterday)+"统计任务开始...");
			MessageServiceImpl message = (MessageServiceImpl) SpringTool.getBean("messageService");
			message.calculateWorkResultPerDay(startFormater.format(yesterday),endFormater.format(yesterday));
			//统计任务结束
			logger.info(formater.format(yesterday)+"统计任务结束!");
		} catch (Exception e) {
			Date today = new Date();
			Date yesterday = new Date(today.getTime() - 86400000L);
			SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
			System.err.println(formater.format(yesterday)+"的审核人员工作量统计任务执行失败"+e.getMessage());
		}
	}
	
	//每小时定时下线长时间未调用服务的用户
	@Scheduled(cron = "0 0 0/1 * * ?") // 每个整点检查一次，下线长时间(1小时)未使用系统的用户;
//	@Scheduled(cron = "0/20 * * * * ?")
	public void longTimeNoSeeUser(){
		HashMap<String,Token> map = TokenUtil.tokenMap;
		if(map.size()==0){
			logger.info("当前没有用户在线");
		}else{
			for(Iterator<String> iterator = map.keySet().iterator();iterator.hasNext();) {
				String key = iterator.next();
				Token token = map.get(key);
				long time = (new Date()).getTime()-token.getLatestAction().getTime();
				int hour = (int)(time/1000/60/60);
//			int hour = (int)(time/1000/20);
				if(hour!=0){
					System.err.println("下线用户："+TokenUtil.tokenMap.get(key).getUser().getUser_name());
					TokenUtil.tokenMap.remove(key);
				}
			}
		}
	}
	
	//每晚11点将未提交审核结果的且已被请求的待审核数据状态置为待审核
	@Scheduled(cron = "0 0 23 * * ? ")
	public void resetJunkSms(){
		//定时(每天凌晨1点)每天统计审核人员的审核工作量
		Date today = new Date();
		SimpleDateFormat startFormater = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		SimpleDateFormat endFormater = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
		MessageServiceImpl messageService = (MessageServiceImpl) SpringTool.getBean("messageService");
		messageService.resetJunkSms(startFormater.format(today),endFormater.format(today));
	}
}
