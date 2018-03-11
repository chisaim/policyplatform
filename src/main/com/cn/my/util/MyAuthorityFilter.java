package com.cn.my.util;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
//自定义的权限过滤器，先用自定义的，后期考虑把shiro框架融合进来
//主要是用于前端访问时不同角色对于不同资源的请求
public class MyAuthorityFilter implements Filter{

	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		//主要是根据请求的url来拦截，不同的服务url是不一样的，对于静态请求(即请求前端资源都进行放行)，否则查询该用户是否具有该权限
		HttpServletRequest req = (HttpServletRequest)request;
		// 获得用户请求的URI
		String path = req.getRequestURI();
		response.setCharacterEncoding("utf-8");//防止本过滤器直接返回的数据乱码
		if(path.endsWith(".html")||path.endsWith(".js")||path.endsWith(".png")||
				path.endsWith(".gif")||path.endsWith(".css")||path.endsWith(".jpg")){
			//防止对于html等静态文件的拦截
			//do nothing
		}else if(path.equals("/policyplatform/user/userLogin")){
			//用户登录，直接放行，去调用用户登录服务
			//do nothing
		}else{
			//这部分主要是防止没有权限的人用http请求来模拟，从而批量请求
			//进行权限验证，看是否有该权限
			String token = TokenUtil.getCookieByName(req, "token");
			if(null==token){
				//将数据写入body里面
				JSONObject object = new JSONObject();
				object.put("code", "300");
				object.put("message", "user was unrecognized!");
				response.setCharacterEncoding("UTF-8");    
				response.setContentType("application/json; charset=utf-8"); 
				response.getWriter().print(object);
				return;
			}
			boolean iflegal = TokenUtil.isTokenLegal(token);
			if(!iflegal){
				JSONObject object = new JSONObject();
				object.put("code", "400");
				object.put("message", "user's token was within the period of validity!");
				response.setCharacterEncoding("UTF-8");    
				response.setContentType("application/json; charset=utf-8"); 
				response.getWriter().print(object);
				return;
			}
			//更新LatestAction time
			TokenUtil.tokenMap.get(token).setLatestAction(new Timestamp((new Date()).getTime()));
		}
		chain.doFilter(request, response);
	}

	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}

}
