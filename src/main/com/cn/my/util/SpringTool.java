package com.cn.my.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

//解决工具类不能使用注解服务
public final class SpringTool implements ApplicationContextAware{
	
	private static ApplicationContext applicationContext = null;  
	  
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {  
        if (SpringTool.applicationContext == null) {  
            SpringTool.applicationContext = applicationContext;  
            System.out.println("========ApplicationContext配置成功,在普通类可以通过调用ToolSpring.getAppContext()获取applicationContext对象,applicationContext="  
                            + applicationContext + "========");  
        }  
    }  
  
    public static ApplicationContext getApplicationContext() {  
        return applicationContext;  
    }  
  
    public static Object getBean(String name) {  
        return getApplicationContext().getBean(name);  
    }  
}
