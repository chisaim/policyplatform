package com.cn.my.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.my.mapper.StopvocabMapper;
import com.cn.my.model.Stopvocab;
import com.cn.my.service.IStopvocabService;
/**
 * @ClassName: User
 * @Description: 停用词实现层
 * @author caixiaoyu
 * @date 2017年11月15日
 */
@Service("stopvocabservice")
@Transactional
public class StopvocabServiceImpl  implements IStopvocabService{
	@Autowired
	private StopvocabMapper stopvocabMapper;
	// 开启日志
	private final static Log logger = LogFactory.getLog(StopvocabServiceImpl.class);
    //返回数据的页码和数据条数
	public Map<String, Object>  getStopvocab(int pageSize, int pageNumber) {
		Map<String, Object> map=new HashMap<String, Object>();
		int count=stopvocabMapper.stopTotal();
		map.put("total", count);
		
		List<Stopvocab> plist=stopvocabMapper.getStopvocab(pageSize, (pageNumber-1)*pageSize);
				
		map.put("data", plist);//括号中是key,value的形式
		return map;//map中封装了total和data的值
	}
	//停用词的添加
	public boolean addstopvocab(Stopvocab stopvocab) {
		try {
			if(stopvocab.getWordtype()!=0){
				stopvocab.setWordtype(0);
			}
			stopvocab.setCreatedtime(new Timestamp(new Date().getTime()));
			int num = stopvocabMapper.addstopvocab(stopvocab);
			if(num!=1){
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
	}

	//停用词的删除
		public boolean removestopvocab(int stopvocab) {
			try {
				
				int num = stopvocabMapper.removestopvocab(stopvocab);
				if(num!=1){
					return false;
				}
				return true;
			} catch (Exception e) {
				logger.error(e.getMessage());
				return false;
			}
		}
		//停用词的修改

		public boolean editstopvocab(Stopvocab stopvocab) {
			try {
//				if(stopvocab.getWordtype()!=0){
//					stopvocab.setWordtype(0);
//				}
				//stopvocab.setCreatedtime(new Timestamp(new Date().getTime()));
				int num = stopvocabMapper.editstopvocab(stopvocab);
				if(num!=1){
					return false;
				}
				return true;
			} catch (Exception e) {
				logger.error(e.getMessage());
				return false;
			}
		}
		//按名称查找
		public boolean foundstopvocab(String stopvocab_stopword) {
			
			return false;
		}

		public List<Stopvocab> foundStopvocab(String stopword) {
			
			return stopvocabMapper.foundStopvocab(stopword);
		}

		public Map<String, Object> initstopvocabSearch(int pageSize, int pageNumber) {
			
			return null;
		}

		public List<Stopvocab> getStopvocab() {
			
			return null;
		}
		//文件上传
		public boolean readFile2Database(List<Stopvocab> wordlist, int fromid){
			try {
				Date date = new Date();
				//策略关键字不能为空
				List<Stopvocab> list = StopvocabRidNull(wordlist);
				stopvocabMapper.readFile2Database(list,fromid,date);
				return true;
			} catch (Exception e) {
				logger.error("批量插入数据失败！"+e.getMessage());
				return false;
			}
		}
		//排除空策略
		private List<Stopvocab> StopvocabRidNull(List<Stopvocab> wordlist) {
			List<Stopvocab> list = new ArrayList<>();
			for(Stopvocab st:wordlist){
				if((st.getStopword()!=null)&&(!"".equals(st.getStopword()))){
					list.add(st);
				}else{
					logger.error(st.toString());
				}
			}
			return list;
		}
		@Override
		public List<Stopvocab> getAll() {
			
			return stopvocabMapper.getAll();
		}
}
