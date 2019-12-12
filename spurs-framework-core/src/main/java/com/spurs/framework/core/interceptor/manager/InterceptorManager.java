package com.spurs.framework.core.interceptor.manager;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.spurs.framework.core.interceptor.define.LoginInterceptor;
import com.spurs.framework.core.interceptor.define.PageInterceptor;

/**
 * 拦截器管理
 * 
 * @ClassName: InterceptorManager
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author GC
 * @date 2018年9月21日 下午3:57:13
 *
 */
@Configuration
public class InterceptorManager implements WebMvcConfigurer {

	private static Logger logger = LoggerFactory.getLogger(InterceptorManager.class);

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// TODO Auto-generated method stub、
		// 登录拦截器
		List<String> addPathArr_login = new ArrayList<String>();
		addPathArr_login.add("/**");
		List<String> excludePathArr_login = new ArrayList<String>();
		excludePathArr_login.add("/error");
		registry.addInterceptor(new LoginInterceptor()).addPathPatterns(addPathArr_login).excludePathPatterns(excludePathArr_login);
		// 分页拦截器
		/*
		 * 
		 * commented out by GC at 2019-07-05 22:06:00
		 * 
		 * 登录拦截器放到filter中
		 *
		 * List<String> addPathArr_page = new ArrayList<String>();
		 * addPathArr_page.add("/**"); List<String> excludePathArr_page = new
		 * ArrayList<String>(); excludePathArr_page.add("/error");
		 * registry.addInterceptor(new
		 * PageInterceptor()).addPathPatterns(addPathArr_page).excludePathPatterns(
		 * excludePathArr_page);
		 */
		WebMvcConfigurer.super.addInterceptors(registry);
	}

}
