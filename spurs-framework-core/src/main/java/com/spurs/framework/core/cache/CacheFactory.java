package com.spurs.framework.core.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.spurs.framework.core.cache.redis.RedisUtil;
import com.spurs.framework.utils.SpringContextUtil;

/**
 * 多种缓存时生产各缓存的对象
 * 
 * @ClassName: CacheFactory
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author GC
 * @date 2018年10月15日 下午2:24:46
 *
 */
@Component
public class CacheFactory {
	private static Logger logger = LoggerFactory.getLogger(CacheFactory.class);

	private static Cache cacheUtil;

	public static Cache get() {
		if (cacheUtil == null) {
			cacheUtil = (Cache) SpringContextUtil.getBean(RedisUtil.class);
			logger.info("初始化缓存bean结束，当前缓存类型：redis");
		}
		return cacheUtil;
	}

}
