package com.spurs.framework.core.cache;

import java.util.List;
import java.util.Map;

public interface Cache {

	public static final String CACHE_TYPE_EHCACHE = "ehcache";
	public static final String CACHE_TYPE_REDIS = "redis";
    /**
     * 可超时的缓存
     */
    public static final String CACHE_NAME = "paladinCache";
    /**
     * 默认缓存名
     */
    public static final String CACHE_NAME_ETERNAL = "defaultCacheName";
    /**
     * 登录令牌缓存
     */
    public static final String CACHE_NAME_LOGIN_TOKEN = "loginTokenCacheName";
    /**
     * 短信验证码缓存
     */
    public static final String CACHE_NAME_SMS_CAPTCHA = "captchaCacheName";

    /**
     * 获取cacheName中key的值
     * 
     * @param cacheName
     * @param key
     * @return
     */
    public Object get(String cacheName, String key);

    /**
     * 存储缓存
     * 
     * @param cacheName
     * @param key
     * @param value
     * @return
     */
    public boolean put(String cacheName, String key, Object value, int... cacheSeconds);

    /**
     * 清除指定key缓存
     * 
     * @param cacheName
     * @param cacheKey
     * @return
     */
    public boolean remove(String cacheName, String cacheKey);

    /**
     * 获取所有keys
     * 
     * @return
     */
    public List<Map<String, Object>> getAllDatas();

    public boolean clear(String name);
}
