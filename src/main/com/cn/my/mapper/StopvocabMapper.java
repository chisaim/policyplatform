package com.cn.my.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.my.model.Stopvocab;
/**
 * @ClassName: Stopvocab
 * @Description: 停用词库管理接口
 * @author caixiaoyu
 * @date 2017年11月15日
 */

public interface StopvocabMapper {

	List<Stopvocab> getStopvocab(@Param("offset")int pageSize,@Param("limit") int pageNumber);

	int addstopvocab(Stopvocab stopvocab) throws Exception;

	int removestopvocab(int stopvocab);

	int editstopvocab(Stopvocab stopvocab)  throws Exception;

    // 按名称进行查询数据
	List<Stopvocab> foundStopvocab(@Param("stopword") String stopword);
	//获取停用词总条数
	int stopTotal();
	//上传停用词
	 void readFile2Database(@Param("list")List<Stopvocab> wordlist,@Param("fromid")int userid,@Param("date")Date date)throws Exception;

	List<Stopvocab> getAll();
		
}
