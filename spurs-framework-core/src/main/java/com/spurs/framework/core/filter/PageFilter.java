package com.spurs.framework.core.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.spurs.framework.core.Page.Page;

@Component
@WebFilter(urlPatterns = "/*", filterName = "pageFilter")
public class PageFilter implements Filter {

	private Logger logger = LoggerFactory.getLogger(PageFilter.class);
	private static final String FLAG_PAGE_INFO = "app.page";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		Object o = request.getAttribute(FLAG_PAGE_INFO);
		if (o == null) {
			o = request.getParameter(FLAG_PAGE_INFO);
		}
		if (o != null) {
			try {
				Page page = JSON.parseObject(o.toString(), Page.class);
				Page.set(page);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("分页信息格式有误，解析失败");
				e.printStackTrace();
			}
		}
		chain.doFilter(request, response);
		if (Page.get() != null) {
			request.setAttribute(FLAG_PAGE_INFO, Page.get());
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
