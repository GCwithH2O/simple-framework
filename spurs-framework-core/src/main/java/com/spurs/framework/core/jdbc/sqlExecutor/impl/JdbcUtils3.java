package com.spurs.framework.core.jdbc.sqlExecutor.impl;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowCountCallbackHandler;
import org.springframework.stereotype.Component;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.spurs.framework.core.JdbcTemplate.JdbcTemplateManager;
import com.spurs.framework.core.Page.Page;
import com.spurs.framework.core.annotation.JdbcAutoMapHandler;
import com.spurs.framework.core.jdbc.sqlExecutor.JdbcUtilsInterface3;

@Deprecated
@Component(value = "JdbcUtils")
public abstract class JdbcUtils3 extends JdbcTemplateManager implements JdbcUtilsInterface3 {

	private static Logger logger = LoggerFactory.getLogger(JdbcUtils3.class);
	private static Map<String, String[]> sqlMap2 = Maps.newHashMap();

	@Override
	public <E> List<E> listData(String sql, Class<E> clz, boolean page) {
		return listData(sql, clz, null, false);
	}

	@Override
	public <E> List<E> listData(String sql, Class<E> clz) {
		// TODO Auto-generated method stub
		return listData(sql, clz, null);
	}

	/**
	 * 集合条件查询,不带分页
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <E> List<E> listData(String sql, Class<E> clz, Object[] objArr, boolean page) {
		String[] sqlAlias = sqlMap2.get(sql);

		// 如果找不到则截取sql串
		if (sqlAlias == null) {
			sqlAlias = this.getAliasMap(sql);
			sqlMap2.put(sql, sqlAlias);
		}

		final List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		final String[] sqlAliasMap = sqlAlias;
		// 获取结果Map放入List
		logger.debug(sql);
//        sql = sql.replaceAll("`", "");
		getJdbcTemplate().query(sql, objArr, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				Map<String, String> map = new HashMap<String, String>();
				for (String key : sqlAliasMap) {
					map.put(key, rs.getString(key));
				}
				list.add(map);
			}
		});

		if (clz == null) {// 如果clz为空，直接返回类型为Map的List对象
			return (List<E>) list;
		}

		Method[] methodArr = clz.getDeclaredMethods();

		List objList = new ArrayList();

		// 循环结果集 放入集合对象中
		for (Map<String, String> map : list) {
			Object obj = this.setPropertyBean(methodArr, map, clz); // set值到Bean里的属性
			if (obj == null) {
				continue;
			}
			objList.add(obj);
		}
		return objList;
	}

	@Override
	public <E> List<E> listData(String sql, Class<E> clz, Object[] objArr) {
		return listData(sql, clz, objArr, null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
	@Override
	public <E> List<E> listData(String sql, Class<E> clz, Object[] objArr, String cSql) {
		sql = joinAnnoSql(sql, clz);
		String[] sqlAlias = sqlMap2.get(sql);
		// 如果找不到则截取sql串
		if (sqlAlias == null) {
			sqlAlias = this.getAliasMap(sql);
			sqlMap2.put(sql, sqlAlias);
		}
		final List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		final String[] sqlAliasMap = sqlAlias;
		StringBuffer kendoSql = kendoSql(sql);
		if (Page.get() != null) {
			int pageSize = Page.get().getPageSize();
			int curPage = Page.get().getCurrPage();
			int startnumber = pageSize * curPage;
			int endnumber = startnumber + pageSize;
			kendoSql = kendoSql.append(" limit ").append(startnumber).append(",").append(endnumber);
			String countSql = "select count(0) from (" + kendoSql.toString() + ") tmp_count"; // 记录统计Sql
			if (!Strings.isNullOrEmpty(cSql)) {
				countSql = cSql;
			}
			logger.debug("paladin_sql:统计条数 | {}", countSql);

			int count = 0;
//            countSql = countSql.replaceAll("`", "");
			count = getJdbcTemplate().queryForObject(countSql, objArr, Integer.class);
			Page page = Page.get();
			if (page == null) {

			}
			page.setTotal(count); // 设置总记录数
//            kendoSql = FldTypeAdapter.getSqlByPage(getJdbcTemplate(), kendoSql, cSql);// 拼凑Sql分页Limit,可做未来其它数据库分页扩展
			logger.debug("paladin_sql:分页： | {}", kendoSql);
		}
//        kendoSql = kendoSql.replaceAll("`", "");
		// 获取结果Map放入List
		getJdbcTemplate().query(kendoSql.toString(), objArr, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				Map<String, String> map = new HashMap<String, String>();
				for (String key : sqlAliasMap) {
					map.put(key, rs.getString(key));
				}
				list.add(map);
			}
		});

		if (clz == null) {// 如果clz为空，直接返回类型为Map的List对象
			// logger.warn("class为空，直接返回类型为Map的List");
			return (List<E>) list;
		}

		Method[] methodArr = clz.getDeclaredMethods();

		List objList = new ArrayList();

		// 循环结果集 放入集合对象中
		for (Map<String, String> map : list) {
			Object obj = this.setPropertyBean(methodArr, map, clz); // set值到Bean里的属性
			if (obj == null) {
				continue;
			}
			objList.add(obj);
		}
		return objList;
	}

	/**
	 * 单条记录查询
	 */
	@Override
	public <E> E getData(String sql, Class<E> clz) {
		return getData(sql, clz, null);
	}

	/**
	 * 如果不传入Class ，则返回值类型为Map<String,String>
	 */
	@Override
	public <E> E getData(String sql) {
		return getData(sql, null, null);
	}

	/**
	 * 如果不传入Class ，则返回值类型为Map<String,String>
	 */

	@Override
	public <E> E getData(String sql, Object[] objArr) {
		return getData(sql, null, objArr);
	}

	/**
	 * 单条记录查询 含参
	 * 
	 * @param <E>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <E> E getData(String sql, Class<E> clz, Object[] objArr) {
		String[] sqlAlias = sqlMap2.get(sql);

		// 如果找不到则截取sql串
		if (sqlAlias == null) {
			sqlAlias = this.getAliasMap(sql);
			sqlMap2.put(sql, sqlAlias);
		}
		final String[] sqlAliasMap = sqlAlias;
		final List<Map<String, String>> list = new ArrayList<Map<String, String>>();
//        DBType dbType = DataSourcesManager.getCurrentConnectionDBType();
//        if (dbType.equals(DBType.Oracle)) {
//            sql = sql.replaceAll("(?i)group_concat", Function.getGROUP_CONCAT(dbType));
//        }
		logger.debug(sql);
//        sql = sql.replaceAll("`", "");
		if (clz != null && (clz == Integer.class || (clz == String.class))) {
			List<E> l = getJdbcTemplate().queryForList(sql, objArr, clz);
			if (l == null || l.size() == 0) {
				return null;
			}
			return l.get(0);
		}

		getJdbcTemplate().query(sql, objArr, new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				Map<String, String> map = new HashMap<String, String>();
				for (String key : sqlAliasMap) {
					map.put(key, rs.getString(key));
				}
				list.add(map);
			}
		});

		if (list == null || list.size() == 0) {
			return null;
		}

		if (clz == null) { // 如果clz为空，直接返回类型为Map的List对象
			if (list.size() > 1) {
				return (E) list;
			}
			return (E) list.get(0);
		}

		Method[] methodArr = clz.getDeclaredMethods();

		// 获取list内第一条，设置对象内的属性
		Map<String, String> map2 = list.get(0);
		Object obj = this.setPropertyBean(methodArr, map2, clz); // set值到Bean里的属性
		return (E) obj;
	}

	@Override
	public int execute(String sql, Object[] objArr) {
		logger.debug(sql);
//        sql = sql.replaceAll("`", "");
		int i = getJdbcTemplate().update(sql, objArr);
		return i;
	}

	@Override
	public int execute(String sql) {
		logger.debug("paladin--修改一条数据。sql:" + sql);
//        sql = sql.replaceAll("`", "");
		getJdbcTemplate().execute(sql);
		return 1;
	}

	@Override
	public int[] executeBatch(String sql, BatchPreparedStatementSetter bs) {
		logger.debug("paladin--批量操作。sql:" + sql);
//        sql = sql.replaceAll("`", "");
		return getJdbcTemplate().batchUpdate(sql, bs);
	}

	/**
	 * 存储过程调用 有返回值的
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <E> List<E> executeProcedure(final String sql, String argArr[]) {
		getJdbcTemplate().execute(new CallableStatementCreator() {
			@Override
			public CallableStatement createCallableStatement(Connection con) throws SQLException {
				CallableStatement cs = con.prepareCall(sql);
				/** 如果有参数 在此设置参数 如：{call testpro(?,?)} **/
				return cs;
			}
		}, new CallableStatementCallback() {
			@Override
			public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
				List<Map<String, String>> list = new ArrayList<Map<String, String>>();
				cs.execute();
				ResultSet rs = (ResultSet) cs.getObject(2);// 获取游标一行的值
				while (rs.next()) { // 转换每行的返回值到Map中
					Map row = new HashMap();
					row.put("id", rs.getString("id"));
					row.put("name", rs.getString("name"));
					list.add(row);
				}
				rs.close();
				return list;
			}
		});
		return null;
	}

	/**
	 * 存储过程 无参无返回值
	 */
	@Override
	public void executeProcedure(String sql) {
//        sql = sql.replaceAll("`", "");
		getJdbcTemplate().execute(sql);
	}

	/**
	 * 取sql语句select 和 from之间的字符串
	 * 
	 * @param sql
	 * @return
	 */
	private String getKey(String sql) {
		if (StringUtils.isEmpty(sql)) {
			logger.error("SQL语句为空");
			throw new RuntimeException("查询sql语句为空。");
		}

		String key = null;
		String temp = sql.toLowerCase().trim();

		if (temp.startsWith("select") && temp.indexOf(" from ") != -1) {
			// 截取关键字和from之间的字符串
			int select = temp.indexOf("select");
			int from = temp.indexOf("from ");
			key = sql.substring(select + 6, from);
		}
		if (StringUtils.isEmpty(key)) {
			logger.error("SQL语句格式错误，请检查拼写。");
			throw new RuntimeException("SQL语句格式错误，请检查拼写。");
		}
		return key.trim();

	}

	private StringBuffer kendoSql(String sql) {
		String key = null;
		String temp = sql.toLowerCase().trim();
		StringBuffer sb = new StringBuffer();
		if (temp.startsWith("select") && temp.indexOf(" from ") != -1) {
			// 截取关键字和from之间的字符串
			int select = temp.indexOf("select");
			int from = temp.indexOf("from ");
			key = sql.substring(select + 6, from);
		}
		if (StringUtils.isEmpty(key)) {
			logger.error("SQL语句格式错误，请检查拼写。");
			throw new RuntimeException("SQL语句格式错误，请检查拼写。");
		}
		sb.append("select ");
		for (String str : key.split(",")) {
			sb.append("paladin_vvv." + str.substring(str.indexOf(" as ") + 4).trim());
			sb.append(str.substring(str.indexOf(" as ")) + ",");
		}
		sb.deleteCharAt(sb.length() - 1);
		// 在oracle中表别名不能用as 使用sql不用temp 数据库对大小写敏感时结果不正确
		// 在oracle中一些表的字段是关键字，需要用""引上字段名才能调用，并且""中的字段名必须是大写的 huxx 2016-03-21
//        DBType dbType =DataSourcesManager.getCurrentConnectionDBType();
//        if (dbType.equals(DBType.Oracle)) {
//            sql = sql.replaceAll("(?i)group_concat", Function.getGROUP_CONCAT(dbType));
//        }
		sb.append(" from (" + sql + ")   paladin_vvv");
		return sb;
	}

	/**
	 * 获取字段别名 以映射bean属性
	 * 
	 * @param sql
	 * @return
	 */
	@SuppressWarnings("unused")
	private String[] getAlias(String sql) {
		String key = getKey(sql);
		String alias[] = null;
		String str = "";
		if (key.equals("*")) {
			// 获取beanClass每个属性拼接字符串
		}
		alias = key.split(",");
		for (int i = 0; i < alias.length; i++) {
			String temp = alias[i];
			if (temp.lastIndexOf(" ") == -1 || temp.substring(temp.lastIndexOf(" ")).indexOf("'") > -1) {
				alias[i] = null;
			} else {
				alias[i] = temp.substring(temp.lastIndexOf(" ")).trim();
			}
		}

		for (int i = 0; i < alias.length; i++) {
			if (alias[i] != null) {
				str += alias[i];
				str += ",";
			}
		}
		return str.split(",");
	}

	/**
	 * 获取 别名和字段名的map key:别名，value：字段名
	 * 
	 * @param sql
	 * @return
	 */
	private String[] getAliasMap(String sql) {
		String key = getKey(sql);
		List<String> list = Lists.newArrayList();

		if (key.toLowerCase().indexOf(" as ") == -1) {
			Map<String, String> map = Maps.newHashMap();
			for (String str : Splitter.on(",").trimResults().omitEmptyStrings().split(key)) {
				map.put(str, str.substring(str.lastIndexOf(".") + 1, str.length()));
				list.add(str.substring(str.lastIndexOf(".") + 1, str.length()));
			}
		} else {

			Map<String, String> map = Splitter.on(",").trimResults().omitEmptyStrings().withKeyValueSeparator(" as ")
					.split(key.replace(" AS ", " as ").replace(" As ", " as ").replace(" aS ", " as "));
			for (String str : map.values()) {
				list.add(str);
			}
		}
		String[] arr = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			arr[i] = list.get(i);
		}
		return arr;
	}

	/**
	 * 匹配setter方法
	 * 
	 * @param key
	 * @param methodName
	 * @return
	 */
	private boolean eqSetMethodName(String key, String methodName) {
		String str = "set" + captureName(key);
		if (str.endsWith(methodName)) {
			return true;
		}
		return false;
	}

	/**
	 * 由成员变量的名称获取对应的setter方法名称
	 * 
	 * @param key
	 * @return
	 */
	private String getMethodNameForProperty(String key) {
		String str = "set" + captureName(key);
		return str;
	}

	/**
	 * 由setter方法中获取对应的成员变量名称
	 * 
	 * @param methodName
	 * @return
	 */
	@SuppressWarnings("unused")
	private String getPropertyforSetMethod(String methodName) {
		if (!methodName.startsWith("set")) {
			return null;
		}
		return lowerCaseName(methodName.substring(3));
	}

	/**
	 * 设置Bean里的属性
	 * 
	 * @param methodArr
	 * @param map
	 * @param obj
	 * @param clz
	 */
	private Object setPropertyBean(Method[] methodArr, Map<String, String> map, Class<?> clz) {
		// logger.debug("组装对象反射数据到Bean。");
		try {
			String clzName = clz.getName();
			if ("java.lang.String".equals(clzName)) {
				for (Map.Entry<String, String> e : map.entrySet()) {
					return String.valueOf(e.getValue());
				}
			} else if ("java.lang.Integer".equals(clzName) || "int".equals(clzName)) {
				for (Map.Entry<String, String> e : map.entrySet()) {
					return Integer.valueOf(e.getValue());
				}
			} else if ("java.lang.Long".equals(clzName) || "long".equals(clzName)) {
				for (Map.Entry<String, String> e : map.entrySet()) {
					return Long.valueOf(e.getValue());
				}
			} else if ("java.lang.Double".equals(clzName) || "double".equals(clzName)) {
				for (Map.Entry<String, String> e : map.entrySet()) {
					return Double.valueOf(e.getValue());
				}
			} else if ("java.lang.Short".equals(clzName) || "short".equals(clzName)) {
				for (Map.Entry<String, String> e : map.entrySet()) {
					return Short.valueOf(e.getValue());
				}
			} else if ("java.lang.Float".equals(clzName) || "float".equals(clzName)) {
				for (Map.Entry<String, String> e : map.entrySet()) {
					return Float.valueOf(e.getValue());
				}
			} else if ("java.lang.Boolean".equals(clzName) || "boolean".equals(clzName)) {
				for (Map.Entry<String, String> e : map.entrySet()) {
					return Boolean.valueOf(e.getValue());
				}
			} else if ("java.lang.Byte".equals(clzName) || "byte".equals(clzName)) {
				for (Map.Entry<String, String> e : map.entrySet()) {
					return Byte.valueOf(e.getValue());
				}
			}
			Object obj = clz.newInstance();

			for (Map.Entry<String, String> e : map.entrySet()) {
				for (Method m : methodArr) {
					if (this.eqSetMethodName(e.getKey(), m.getName())) { // 如果匹配到setter方法
																			// 则执行此方法把map的value作为参数传入
						Class<?> tArr[] = m.getParameterTypes();
						if (tArr != null && tArr.length == 1) {
							m.invoke(obj, wrapClass(tArr[0], e.getValue()));
							continue;
						}

					}
				}
			}
			obj = setPropertyBean(map, obj, clz);

			return obj;
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 递归设置Bean属性
	 * 
	 * @param map
	 * @param obj
	 * @param clz
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 */
	private Object setPropertyBean(Map<String, String> map, Object obj, Class<?> clz)
			throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException, InstantiationException {
		for (Field f : clz.getDeclaredFields()) {
			JdbcAutoMapHandler ann = f.getAnnotation(JdbcAutoMapHandler.class);
			if (ann != null) {
				Class<?> c = getFieldClass(f);
				Object o = c.newInstance();

				for (Map.Entry<String, String> e : map.entrySet()) {
					boolean isFind = false;
					// 获取对应属性的setter方法
					String methodName = this.getMethodNameForProperty(e.getKey());
					for (Method m : c.getDeclaredMethods()) {
						if (methodName.equals(m.getName())) {
							Class<?> tArr[] = m.getParameterTypes();
							if (tArr != null && tArr.length == 1) {
								m.invoke(o, wrapClass(tArr[0], e.getValue()));
								isFind = true;
								break;
							}
						}
					}
					if (!isFind) {
						for (Field f2 : c.getDeclaredFields()) { // 获取类的成员变量
							if (f2.getAnnotation(JdbcAutoMapHandler.class) != null) { // 如果此成员变量有JDBCHandler注解
								// 则解析注解
								/** 解析注解 暂时搁置 **/
								Class<?> c2 = getFieldClass(f2);
								Object o2 = setPropertyBean(c2.getDeclaredMethods(), map, c2);

								String methodName2 = this.getMethodNameForProperty(f2.getName());
								Method m2 = c.getDeclaredMethod(methodName2, c2);
								m2.invoke(o, o2);
							}

						}
					}

				}
				if (o != null) {
					String methodName = this.getMethodNameForProperty(f.getName());
					Method m = clz.getDeclaredMethod(methodName, c);
					m.invoke(obj, o);
				}
			}
		}

		return obj;
	}

	private static Object wrapClass(Class<?> c, String str) {
		String tArrName = c.getName();
		Object tArrObj = null;
		if (c.isInstance(str)) {
			tArrObj = str;
		} else if ("java.lang.Integer".equals(tArrName) || "int".equals(tArrName)) {
			if (str != null) {
				tArrObj = Integer.valueOf(str);
			}
		} else if ("java.lang.Double".equals(tArrName) || "double".equals(tArrName)) {
			if (str != null) {
				tArrObj = Double.valueOf(str);
			}
		} else if ("java.lang.Long".equals(tArrName) || "long".equals(tArrName)) {
			if (str != null) {
				tArrObj = Long.valueOf(str);
			}
		} else if ("java.lang.Byte".equals(tArrName) || "byte".equals(tArrName)) {
			if (str != null) {
				tArrObj = Byte.valueOf(str);
			}
		} else if ("java.lang.Short".equals(tArrName) || "short".equals(tArrName)) {
			if (str != null) {
				tArrObj = Short.valueOf(str);
			}
		} else if ("java.lang.Float".equals(tArrName) || "float".equals(tArrName)) {
			if (str != null) {
				tArrObj = Float.valueOf(str);
			}
		} else if ("java.lang.Boolean".equals(tArrName) || "boolean".equals(tArrName)) {
			if (str != null) {
				tArrObj = Boolean.valueOf(str);
			}
		} else if ("java.util.Date".equals(tArrName)) {
			if (str != null) {
				tArrObj = str2Date(str);
			}
		}
		return tArrObj;
	}

	// 首字母大写
	private static String captureName(String name) {
		char[] cs = name.toCharArray();
		if (cs[0] >= 'a' && cs[0] <= 'z') {
			cs[0] -= 32;
		}
		return String.valueOf(cs);
	}

	// 首字母小写
	public static String lowerCaseName(String name) {
		char[] cs = name.toCharArray();
		cs[0] += 32;
		return String.valueOf(cs);
	}

	private static Date str2Date(String date) {
		if (StringUtils.isNotEmpty(date)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				return sdf.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return new Date();
		} else {
			return null;
		}
	}

	/**
	 * 获取Field的class
	 * 
	 * @param f
	 * @return
	 * @throws ClassNotFoundException
	 */
	private Class<?> getFieldClass(Field f) throws ClassNotFoundException {
		Type t = f.getGenericType();
		String path = t.toString();
		path = path.substring(path.indexOf(" ") + 1);
		Class<?> c = Class.forName(path);
		return c;
	}

	/**
	 * 
	 * @Title: joinAnnoSql @Description:
	 *         注解方式自动拼接sql以便查询多数表需要关联到的user表及其userInfo和dept表信息
	 *         使用方式:@JDBCHandler(userId="adduser")
	 *         加入要join的对应的userId的字段,如果类定义是addUser,请全部小写成adduser
	 *         此方法暂且只支持kendo页面分页式集合查询 @param sql @param clz @return 参数 String
	 *         返回类型 @throws
	 */
	private String joinAnnoSql(String sql, Class<?> clz) {
		// 获取类字段
		if (clz == null) {
			return sql;
		}
		Field[] fields = clz.getDeclaredFields();
		for (Field f : fields) {
			if (f.isAnnotationPresent(JdbcAutoMapHandler.class)) {
//                if (f.getType().equals(User.class)) {
//                    String column = f.getAnnotation(JdbcAutoMapHandler.class).userId();
//                    if (Strings.isNullOrEmpty(column)) {
//                        return sql;
//                    }
//                    String key = null;
//                    String temp = sql.toLowerCase().trim();
//                    StringBuffer sb = new StringBuffer();
//                    if (temp.startsWith("select") && temp.indexOf(" from ") != -1) {
//                        // 截取关键字和from之间的字符串
//                        int select = temp.indexOf("select");
//                        int from = temp.indexOf("from");
//                        key = sql.substring(select + 6, from);
//                    }
//                    if (StringUtils.isEmpty(key)) {
//                        logger.error("SQL语句格式错误，请检查拼写。");
//                        throw new RuntimeException("SQL语句格式错误，请检查拼写。");
//                    }
//                    sb.append(
//                            "select paladin_user_view22.userId as userId, paladin_user_view22.userCode as userCode, paladin_user_view22.`password` as password, paladin_user_view22.userName as userName, paladin_user_view22.userInfoEmail as userInfoEmail, paladin_user_view22.userInfoAddress as userInfoAddress, paladin_user_view22.userInfoMobile as userInfoMobile, paladin_user_view22.sex as sex, paladin_user_view22.userInfoId as userInfoId, paladin_user_view22.deptId as deptId, paladin_user_view22.deptName as deptName, paladin_user_view22.deptParentId as deptParentId, paladin_user_view22.deptAddress as deptAddress, paladin_user_view22.deptType as deptType, paladin_user_view22.deptDistrict as deptDistrict,");
//                    for (String str : key.split(",")) {
//                        sb.append("paladin_qwer." + str.substring(str.indexOf(" as ") + 4).trim());
//                        sb.append(str.substring(str.indexOf(" as ")) + ",");
//                    }
//                    sb.deleteCharAt(sb.length() - 1);
//                    sb.append(" from (" + temp
//                            + ") as paladin_qwer left join (select * from user_view) paladin_user_view22 on (paladin_qwer."
//                            + column + "=paladin_user_view22.userId)");
//                    return sb.toString();
//                }
			}
		}
		return sql;
	}

	/**
	 * 
	 * @Title: getMapData @Description: 原生sql查询 @param sql @param objArr @param
	 *         tableName @return 参数 List<E> 返回类型 @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <E> List<E> getMapData(String sql, Object[] objArr, String[] tableName) {
		Map<String, String[]> sqlAlia = Maps.newHashMap();
		// 如果表名称数组不为空，则获取表字段名称数组依据表名称为key放入map中
		if (tableName != null) {
			for (String name : tableName) {
				sqlAlia.put("paladin_" + name, getTableColumns(name));
			}
		}
//        DBType dbType = DataSourcesManager.getCurrentConnectionDBType();
//        if (dbType.equals(DBType.Oracle)) {
//            sql = sql.replaceAll("(?i)group_concat", Function.getGROUP_CONCAT(dbType));
//        }
//        sql = sql.replaceAll("`", "");

		return (List<E>) getJdbcTemplate().queryForList(sql);
	}

	@Override
	public <E> List<E> getMapData(String sql) {
		return getMapData(sql, null, null);
	}

	// 动态获取表字段名称
	private String[] getTableColumns(String tableName) {
		String sql = "select * from " + tableName + " limit 0";
		RowCountCallbackHandler rcch = new RowCountCallbackHandler();
//        sql = sql.replaceAll("`", "");
		getJdbcTemplate().query(sql, rcch);
		String[] coloumnName = rcch.getColumnNames();
		return coloumnName;
	}

	/**
	 * insert blob
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public int insertObject(String sql, final List<Map<String, Object>> params) {
		return getJdbcTemplate().execute(sql, new PreparedStatementCallback<Integer>() {
			@Override
			public Integer doInPreparedStatement(PreparedStatement preparedStatement)
					throws SQLException, DataAccessException {
				setPreparedStatement(params, preparedStatement);
				return preparedStatement.executeUpdate();
			}
		});
	}

	/**
	 * 设置参数
	 * 
	 * @param params
	 * @param preparedStatement
	 * @throws SQLException
	 */
	private void setPreparedStatement(final List<Map<String, Object>> params, PreparedStatement preparedStatement)
			throws SQLException {
		int parameterIndex = 1;
		for (Iterator<Map<String, Object>> iterator = params.iterator(); iterator.hasNext();) {
			Map<String, Object> map = iterator.next();
			int type = (int) map.get("type");
			switch (type) {
			case Types.BIT:
				preparedStatement.setBoolean(parameterIndex, Boolean.valueOf(map.get("value").toString()));
				break;
			case Types.BOOLEAN:
				preparedStatement.setBoolean(parameterIndex, Boolean.valueOf(map.get("value").toString()));
				break;
			case Types.TINYINT:
				preparedStatement.setByte(parameterIndex, Byte.valueOf((byte) map.get("value")));
				break;
			case Types.SMALLINT:
				preparedStatement.setShort(parameterIndex, Short.valueOf(map.get("value").toString()));
				break;
			case Types.INTEGER:
				preparedStatement.setInt(parameterIndex, Integer.valueOf(map.get("value").toString()));
				break;
			case Types.BIGINT:
				preparedStatement.setLong(parameterIndex, Long.valueOf(map.get("value").toString()));
				break;
			case Types.VARBINARY:
				preparedStatement.setBytes(parameterIndex, (byte[]) map.get("value"));
				break;
			case Types.FLOAT:
				preparedStatement.setFloat(parameterIndex, Float.valueOf(map.get("value").toString()));
				break;
			case Types.REAL:
				preparedStatement.setFloat(parameterIndex, Float.valueOf(map.get("value").toString()));
				break;
			case Types.DOUBLE:
				preparedStatement.setDouble(parameterIndex, Double.valueOf(map.get("value").toString()));
				break;
			case Types.NUMERIC:
				preparedStatement.setBigDecimal(parameterIndex,
						BigDecimal.valueOf(Integer.valueOf(map.get("value").toString())));
				break;
			case Types.DATE:
				preparedStatement.setDate(parameterIndex, (java.sql.Date) map.get("value"));
				break;
			case Types.TIME:
				preparedStatement.setTime(parameterIndex, (java.sql.Time) map.get("value"));
				break;
			case Types.TIMESTAMP:
				preparedStatement.setTimestamp(parameterIndex, (java.sql.Timestamp) map.get("value"));
				break;
			case Types.LONGVARBINARY:
				preparedStatement.setBytes(parameterIndex, (byte[]) map.get("value"));
				break;
			case Types.JAVA_OBJECT:
				preparedStatement.setObject(parameterIndex, map.get("value"));
				break;
			case Types.ROWID:
				preparedStatement.setRowId(parameterIndex, (RowId) map.get("value"));
				break;
			case Types.CLOB:
				preparedStatement.setClob(parameterIndex, (Reader) map.get("value"));
				break;
			case Types.BLOB:
				preparedStatement.setBlob(parameterIndex, (InputStream) map.get("value"));
				break;
			case Types.VARCHAR:
				preparedStatement.setString(parameterIndex, map.get("value").toString());
				break;
			case Types.LONGVARCHAR:
				preparedStatement.setString(parameterIndex, map.get("value").toString());
				break;
			case Types.ARRAY:
				preparedStatement.setArray(parameterIndex, (Array) map.get("value"));
				break;
			case Types.NULL:
				preparedStatement.setNull(parameterIndex, Types.NULL);
				break;
			case Types.DATALINK:
				preparedStatement.setURL(parameterIndex, (URL) map.get("value"));
				break;
			case Types.NCHAR:
				preparedStatement.setNString(parameterIndex, map.get("value").toString());
				break;
			case Types.NVARCHAR:
				preparedStatement.setNString(parameterIndex, map.get("value").toString());
				break;
			case Types.LONGNVARCHAR:
				preparedStatement.setNString(parameterIndex, map.get("value").toString());
				break;
			case Types.NCLOB:
				preparedStatement.setNClob(parameterIndex, (NClob) map.get("value"));
				break;
			case Types.SQLXML:
				preparedStatement.setSQLXML(parameterIndex, (SQLXML) map.get("value"));
				break;
			case Types.DECIMAL:
				preparedStatement.setObject(parameterIndex, map.get("value"));
				break;
			case Types.STRUCT:
				preparedStatement.setObject(parameterIndex, map.get("value"));
				break;
			case Types.REF:
				preparedStatement.setObject(parameterIndex, map.get("value"));
				break;
			case Types.BINARY:
			case Types.OTHER:
			case Types.DISTINCT:
			default:
				preparedStatement.setString(parameterIndex, map.get("value").toString());
				break;
			}
			parameterIndex++;
		}
	}

	/**
	 * 获取blob
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> getList(String sql, final List<Map<String, Object>> params) {
		return getJdbcTemplate().execute(sql, new PreparedStatementCallback<List<Map<String, Object>>>() {
			@Override
			public List<Map<String, Object>> doInPreparedStatement(PreparedStatement ps)
					throws SQLException, DataAccessException {
				List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
				if (params != null) {
					setPreparedStatement(params, ps);
				}
				ResultSet rs = ps.executeQuery();
				ResultSetMetaData metaData = rs.getMetaData();
				while (rs.next()) {
					Map<String, Object> map = new HashMap<String, Object>();
					for (int i = 1; i <= metaData.getColumnCount(); i++) {
						String columnName = metaData.getColumnName(i);
						switch (metaData.getColumnType(i)) {
						case Types.BIT:
							map.put(columnName, rs.getBoolean(i));
							break;
						case Types.BOOLEAN:
							map.put(columnName, rs.getBoolean(i));
							break;
						case Types.TINYINT:
							map.put(columnName, rs.getByte(i));
							break;
						case Types.SMALLINT:
							map.put(columnName, rs.getShort(i));
							break;
						case Types.INTEGER:
							map.put(columnName, rs.getInt(i));
							break;
						case Types.BIGINT:
							map.put(columnName, rs.getLong(i));
							break;
						case Types.VARBINARY:
							map.put(columnName, rs.getBytes(i));
							break;
						case Types.FLOAT:
							map.put(columnName, rs.getFloat(i));
							break;
						case Types.REAL:
							map.put(columnName, rs.getFloat(i));
							break;
						case Types.DOUBLE:
							map.put(columnName, rs.getDouble(i));
							break;
						case Types.NUMERIC:
							map.put(columnName, rs.getBigDecimal(i));
							break;
						case Types.DATE:
							map.put(columnName, rs.getDate(i));
							break;
						case Types.TIME:
							map.put(columnName, rs.getTime(i));
							break;
						case Types.TIMESTAMP:
							map.put(columnName, rs.getTimestamp(i));
							break;
						case Types.LONGVARBINARY:
							map.put(columnName, rs.getBinaryStream(i));
							break;
						case Types.JAVA_OBJECT:
							map.put(columnName, rs.getObject(i));
							break;
						case Types.ROWID:
							map.put(columnName, rs.getRowId(i));
							break;
						case Types.CLOB:
							map.put(columnName, rs.getClob(i));
							break;
						case Types.BLOB:
							map.put(columnName, rs.getBinaryStream(i));
							break;
						case Types.VARCHAR:
							map.put(columnName, rs.getString(i));
							break;
						case Types.LONGVARCHAR:
							map.put(columnName, rs.getString(i));
							break;
						case Types.ARRAY:
							map.put(columnName, rs.getArray(i));
							break;
						case Types.NULL:
							map.put(columnName, null);
							break;
						case Types.DATALINK:
							map.put(columnName, rs.getURL(i));
							break;
						case Types.NCHAR:
							map.put(columnName, rs.getString(i));
							break;
						case Types.NVARCHAR:
							map.put(columnName, rs.getString(i));
							break;
						case Types.LONGNVARCHAR:
							map.put(columnName, rs.getString(i));
							break;
						case Types.NCLOB:
							map.put(columnName, rs.getNClob(i));
							break;
						case Types.SQLXML:
							map.put(columnName, rs.getSQLXML(i));
							break;
						case Types.DECIMAL:
							map.put(columnName, rs.getObject(i));
							break;
						case Types.STRUCT:
							map.put(columnName, rs.getObject(i));
							break;
						case Types.REF:
							map.put(columnName, rs.getObject(i));
							break;
						case Types.BINARY:
						case Types.OTHER:
						case Types.DISTINCT:
						default:
							map.put(columnName, rs.getString(i));
							break;
						}

					}
					result.add(map);
				}
				return result;
			}
		});
	}
}
