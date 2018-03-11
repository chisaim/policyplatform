package com.cn.my.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.cn.my.model.MeanFirst;
import com.cn.my.model.MeanSecond;
import com.cn.my.model.Token;
import com.cn.my.service.impl.UserServiceImpl;

public class TokenUtil {
	
	public static HashMap<String,Token> tokenMap;
	
	static{
		if(null==tokenMap){
			tokenMap = new HashMap<String, Token>();
		}
	}
	
	//验证token是否在有效期内
	public static boolean isTokenLegal(String token){
		return tokenMap.containsKey(token);
	}
	
	//根据cookie名获取cookie值
	public static String getCookieByName(HttpServletRequest request,
			String name) {
		Cookie[] cookies = request.getCookies();
		if (null != cookies) {
			for (Cookie cookie : cookies) {
				if (name.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	public static boolean isRoleLegal(List<MeanFirst> list, String cookieByName) {
		//要创建新角色的用户的权限分栏id
		List<Integer> action_column_id_self = java.util.Arrays.asList(Token.getActionIdsFromActions(tokenMap.get(cookieByName).getActions()));
		List<String> mean_secondcode = getSecondMean(list);
		UserServiceImpl userService = (UserServiceImpl) SpringTool.getBean("userService");
		List<Integer> action_column_id_new = userService.getActionColIdBySecondCode(mean_secondcode);
		return action_column_id_self.containsAll(action_column_id_new);
	}

	public static List<String> getSecondMean(List<MeanFirst> list) {
		List<String> mean_secondcode = new ArrayList<String>();
		for(MeanFirst mf:list){
			for(MeanSecond ms:mf.getMenus()){
				mean_secondcode.add(ms.getMenuid());
			}
		}
		return mean_secondcode;
	}

}
