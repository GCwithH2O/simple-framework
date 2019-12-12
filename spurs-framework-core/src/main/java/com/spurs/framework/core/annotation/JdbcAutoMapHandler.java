package com.spurs.framework.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 
 * JDBC级联对象反射注解类
 * jdbc操作中，Bean中需要级联反射的对象属性如果为自定义对象，如：（MyBean） 需要在此属性上加入此注解
 * @author GC
 * @date 2015-6-3 上午9:22:06 
 *  
 */
@Deprecated
@Documented
@Target({ElementType.TYPE,ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JdbcAutoMapHandler {
}
