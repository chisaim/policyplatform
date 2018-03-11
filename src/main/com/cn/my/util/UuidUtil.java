package com.cn.my.util;

import java.util.UUID;

public class UuidUtil {
	public static String generateToken(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	public static void main(String[] args) {
		String s = generateToken();
		System.out.println(s);
		System.out.println(MD5Util.MD5Encode(s, "utf-8", false));
		System.out.println(MD5Util.MD5Encode(s, "utf-8", true));
	}
}
