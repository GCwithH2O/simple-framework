package com.spurs.framework.core.cache.ehchche;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * EhCache缓存工具类
 * 
 * 配置见cache目录下applicationContext-ehcache.xml与ehcache.xml
 * 
 * @author shaosuone 2016.11.3
 */
@Component
public class EhcacheUtil implements com.spurs.framework.core.cache.Cache {
	private Logger logger = LoggerFactory.getLogger(EhcacheUtil.class);

	@Resource(name = "ehCacheCacheManager")
	private EhCacheCacheManager cacheManager;

	/**
	 * @param <T> 获取cacheName中key的值 @Title: get @Description:
	 *        TODO(这里用一句话描述这个方法的作用) @param key @return 参数 Object 返回类型 @throws
	 */
	@Override
	public Object get(String cacheName, String key) {
		if (cacheManager == null) {
			return null;
		}
		Cache cache = cacheManager.getCacheManager().getCache(cacheName);

		if (cache == null) {
			return null;
		}

		Element element = cache.get(key);

		if (element == null) {
			return null;
		}
		return element.getObjectValue();
	}

	/**
	 * 将数据放入cacheName
	 */
	@Override
	public boolean put(String cacheName, String key, Object value, int... cacheSeconds) {
		if (cacheName == null) {
			cacheName = EhcacheUtil.CACHE_NAME_ETERNAL;
		}
		if (cacheManager == null) {
			return false;
		}
		Cache cache = cacheManager.getCacheManager().getCache(cacheName);

		if (cache == null) {
			return false;
		}
		Element element = new Element(key, value);
		cache.put(element);
		return true;
	}

	/**
	 * 清除缓存 @Title: remove @Description: TODO(这里用一句话描述这个方法的作用) @param
	 * cacheName @param cacheKey @return 参数 boolean 返回类型 @throws
	 */
	@Override
	public boolean remove(String cacheName, String cacheKey) {
		if (cacheManager == null) {
			return false;
		}
		Cache cache = cacheManager.getCacheManager().getCache(cacheName);
		if (cache == null) {
			return false;
		}

		if (Strings.isNullOrEmpty(cacheKey)) {
			return false;
		}

		cache.remove(cacheKey);
		return true;
	}

	@Override
	public List<Map<String, Object>> getAllDatas() {
		if (cacheManager == null) {
			return null;
		}
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		CacheManager cManager = cacheManager.getCacheManager();
		String[] names = cManager.getCacheNames();
		for (String name : names) {
			Cache cache = cManager.getCache(name);
			List<String> keys = cache.getKeys();
			for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
				Map<String, Object> data = new HashMap<String, Object>();
				String key = iterator.next();
				Element element = cache.get(key);
				if (element != null) {
					Object value = element.getObjectValue();
					data.put("key", key);
					data.put("value", value);
					datas.add(data);
				}
			}
		}
		return datas;
	}

	@Override
	public boolean clear(String name) {
		// TODO Auto-generated method stub
		return false;
	}

}
