package com.spurs.framework.core.cache.redis;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisUtil implements com.spurs.framework.core.cache.Cache {

//	@Resource(name = "redisCacheManager")
//	private RedisCacheManager cacheManager;
	@Autowired
    private StringRedisTemplate redisTemplate;

	public StringRedisTemplate getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(StringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
	
//	@Override
//	public Object get(String cacheName, String key) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public boolean put(String cacheName, String key, Object value, int... cacheSeconds) {
//		// TODO Auto-generated method stub
////		redisTemplate.opsForValue().set("123", "sadad");
//		return false;
//	}
//
//	@Override
//	public boolean remove(String cacheName, String cacheKey) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public List<Map<String, Object>> getAllDatas() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public boolean clear(String name) {
//		// TODO Auto-generated method stub
//		return false;
//	}

}
