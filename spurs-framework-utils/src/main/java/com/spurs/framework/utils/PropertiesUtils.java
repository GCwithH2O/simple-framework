package com.spurs.framework.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * Properties文件载入工具类.
 * 可载入多个properties文件,相同的属性在最后载入的文件中的值将会覆盖之前的值，但以System的Property优先.
 * 
 * @author shaosuone
 */
public class PropertiesUtils implements Serializable {

	private static final long serialVersionUID = -5636962843941151568L;

	private static Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);

	private static ResourceLoader resourceLoader = new DefaultResourceLoader();

	private final Properties properties;

	public PropertiesUtils(String... resourcesPaths) {
		properties = loadProperties(resourcesPaths);
	}

	public PropertiesUtils(File... file) {
		properties = loadPropertiesByFile(file);
	}

	public Properties getProperties() {
		return properties;
	}

	/**
	 * 取出Property
	 * 
	 * @param key
	 * @return
	 */
	private String getValue(String key) {
		String systemProperty = System.getProperty(key);
		if (systemProperty != null) {
			return systemProperty;
		}
		return properties.getProperty(key);
	}

	/**
	 * 取出String类型的Property,如果都为Null则抛出异常.
	 * 
	 * @param key
	 * @return
	 */
	public String getProperty(String key) {
		String value = getValue(key);
		if (value == null) {
			throw new NoSuchElementException();
		}
		return value;
	}

	/**
	 * 取出String类型的Property.如果都为Null則返回Default值.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getProperty(String key, String defaultValue) {
		String value = getValue(key);
		return value != null ? value : defaultValue;
	}

	/**
	 * 取出Integer类型的Property.如果都为Null或内容错误则抛出异常.
	 * 
	 * @param key
	 * @return
	 */
	public Integer getInteger(String key) {
		String value = getValue(key);
		if (value == null) {
			throw new NoSuchElementException();
		}
		return Integer.valueOf(value);
	}

	/**
	 * 取出Integer类型的Property.如果都为Null则返回Default值，如果内容错误则抛出异常
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public Integer getInteger(String key, Integer defaultValue) {
		String value = getValue(key);
		return value != null ? Integer.valueOf(value) : defaultValue;
	}

	/**
	 * 取出Double类型的Property.如果都为Null或内容错误则抛出异常.
	 * 
	 * @param key
	 * @return
	 */
	public Double getDouble(String key) {
		String value = getValue(key);
		if (value == null) {
			throw new NoSuchElementException();
		}
		return Double.valueOf(value);
	}

	/**
	 * 取出Double类型的Property.如果都为Null則返回Default值，如果内容错误则抛出异常
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public Double getDouble(String key, Integer defaultValue) {
		String value = getValue(key);
		return value != null ? Double.valueOf(value) : defaultValue;
	}

	/**
	 * 取出Boolean类型的Property.如果都为Null抛出异常,如果内容不是true/false则返回false.
	 * 
	 * @param key
	 * @return
	 */
	public Boolean getBoolean(String key) {
		String value = getValue(key);
		if (value == null) {
			throw new NoSuchElementException();
		}
		return Boolean.valueOf(value);
	}

	/**
	 * 取出Boolean类型的Propert.如果都为Null则返回Default值,如果内容不为true/false则返回false.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public Boolean getBoolean(String key, boolean defaultValue) {
		String value = getValue(key);
		return value != null ? Boolean.valueOf(value) : defaultValue;
	}

	/**
	 * 载入多个文件, 文件路径使用Spring Resource格式.
	 * 
	 * @param resourcesPaths
	 * @return
	 */
	private Properties loadProperties(String... resourcesPaths) {
		Properties props = new Properties();

		for (String location : resourcesPaths) {

			logger.debug("加载 properties 文件 从 路径:{}", location);

			InputStream is = null;
			try {
				Resource resource = resourceLoader.getResource(location);
				is = resource.getInputStream();
				props.load(new InputStreamReader(is, "utf-8"));
				// props.load(is);
			} catch (IOException ex) {
				logger.error("不能加载 properties 文件从路径:{}, {} ", location, ex.getMessage());
			} finally {
				IOUtils.closeQuietly(is);
			}
		}
		return props;
	}

	/**
	 * 从File中读取配置文件
	 * 
	 * @param files
	 * @return
	 */
	private Properties loadPropertiesByFile(File... files) {
		Properties props = new Properties();

		for (File file : files) {
			logger.debug("加载 properties 文件 从 路径:{}", file.getPath());
			FileInputStream is = null;
			try {
				is = new FileInputStream(file);
				props.load(new InputStreamReader(is, "utf-8"));
				// props.load(is);
			} catch (IOException ex) {
				logger.error("不能加载 properties 文件从路径:{}, {} ", file.getPath(), ex.getMessage());
			} finally {
				IOUtils.closeQuietly(is);
			}
		}
		return props;
	}
}
