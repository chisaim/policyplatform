package com.cn.my.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil{
	public static String getCurrentTime() {
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date=new Date();
		return dateFormater.format(date);
	}
	public static void main(String[] args) {
		System.out.println(Long.valueOf(Timestamp.valueOf(String.valueOf(TimeUtil.getCurrentTime())).getTime()));
		
	}
	
}
