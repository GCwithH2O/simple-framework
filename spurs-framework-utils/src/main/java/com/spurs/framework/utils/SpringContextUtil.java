package com.spurs.framework.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextUtil implements ApplicationContextAware{
    private Logger logger = LoggerFactory.getLogger(SpringContextUtil.class);
    
    private static ApplicationContext applicationContext;

    /**
     * 获取上下文
     * 
     * @Title: getApplicationContext
     * 
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * 
     * @param @return 参数
     * 
     * @return ApplicationContext 返回类型
     * 
     * @throws
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 设置上下文
     * 
     * @Title: setApplicationContext
     * 
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * 
     * @param @param applicationContext 参数
     * 
     * @return void 返回类型
     * 
     * @throws
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextUtil.applicationContext = applicationContext;
    }

    /**
     * 通过名字获取上下文中的bean
     * 
     * @Title: getBean
     * 
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * 
     * @param @param name
     * 
     * @param @return 参数
     * 
     * @return Object 返回类型
     * 
     * @throws
     */
    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    /**
     * 通过类型获取上下文中的bean
     * 
     * @Title: getBean
     * 
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * 
     * @param @param requiredType
     * 
     * @param @return 参数
     * 
     * @return Object 返回类型
     * 
     * @throws
     */
    public static Object getBean(Class<?> requiredType) {
        return applicationContext.getBean(requiredType);
    }

}
