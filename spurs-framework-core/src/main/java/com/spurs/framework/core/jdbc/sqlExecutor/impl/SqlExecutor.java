package com.spurs.framework.core.jdbc.sqlExecutor.impl;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Component;

import com.spurs.framework.core.JdbcTemplate.JdbcTemplateManager;
import com.spurs.framework.core.Page.Page;
import com.spurs.framework.core.jdbc.sqlExecutor.SqlExecutorInterface;

@Component
public class SqlExecutor extends JdbcTemplateManager implements SqlExecutorInterface {

	private static Logger logger = LoggerFactory.getLogger(SqlExecutor.class);

	@Override
	public <E> List<E> listData(String sql, Class<E> clz) {
		// TODO Auto-generated method stub
		return listData(sql, clz, new Object[] {});
	}

	@Override
	public <E> List<E> listData(String sql, Class<E> clz, Object[] objArr) {
		// TODO Auto-generated method stub
		return listData(sql, clz, objArr, true);
	}

	@Override
	public <E> List<E> listData(String sql, Class<E> clz, Object[] objArr, boolean page) {
		// TODO Auto-generated method stub
		Page p = Page.get() != null ? Page.get() : Page.getDefaultPage();
		if (page) {
			// 获取结果总数
			String sql_total = "select count(*) as count from (" + sql + ")t";
			long total = this.getData(sql_total, Long.class);
			p.setTotal(total);
			int pageSize = p.getPageSize();
			int currPage = p.getCurrPage();
			sql = "select * from (" + sql + ")t limit " + currPage * pageSize + ", " + pageSize;
		}
		List<E> resultList = getJdbcTemplate().query(sql, objArr, rs -> {
			return this.resultSetToBeanList(rs, clz);
		});
		if (!page) {
			p.setTotal(resultList.size());
		}
		Page.set(p);
		return resultList;
	}

	private <E> List<E> resultSetToBeanList(ResultSet rs, Class<E> clz) throws SQLException {
		// TODO Auto-generated method stub
		List<E> dataList = new ArrayList<E>();
		ResultSetMetaData md = rs.getMetaData();
		int colcount = md.getColumnCount();
		rs.beforeFirst();
		while (rs.next()) {
			Map<String, String> map = new HashMap<String, String>();
			for (int i = 0; i < colcount; i++) {
				map.put(md.getColumnName(i + 1), rs.getString(i + 1));
			}
			dataList.add(wrapDataBean(map, clz));
		}
		return dataList;
	}

	@SuppressWarnings("unchecked")
	private <E> E wrapDataBean(Map<String, String> map, Class<E> clz) {
		// TODO Auto-generated method stub
		String clzName = clz.getName();
		if ("java.lang.String".equals(clzName)) {
			for (Map.Entry<String, String> e : map.entrySet()) {
				return (E) String.valueOf(e.getValue());
			}
		} else if ("java.lang.Integer".equals(clzName) || "int".equals(clzName)) {
			for (Map.Entry<String, String> e : map.entrySet()) {
				return (E) Integer.valueOf(e.getValue());
			}
		} else if ("java.lang.Long".equals(clzName) || "long".equals(clzName)) {
			for (Map.Entry<String, String> e : map.entrySet()) {
				return (E) Long.valueOf(e.getValue());
			}
		} else if ("java.lang.Double".equals(clzName) || "double".equals(clzName)) {
			for (Map.Entry<String, String> e : map.entrySet()) {
				return (E) Double.valueOf(e.getValue());
			}
		} else if ("java.lang.Short".equals(clzName) || "short".equals(clzName)) {
			for (Map.Entry<String, String> e : map.entrySet()) {
				return (E) Short.valueOf(e.getValue());
			}
		} else if ("java.lang.Float".equals(clzName) || "float".equals(clzName)) {
			for (Map.Entry<String, String> e : map.entrySet()) {
				return (E) Float.valueOf(e.getValue());
			}
		} else if ("java.lang.Boolean".equals(clzName) || "boolean".equals(clzName)) {
			for (Map.Entry<String, String> e : map.entrySet()) {
				return (E) Boolean.valueOf(e.getValue());
			}
		} else if ("java.lang.Byte".equals(clzName) || "byte".equals(clzName)) {
			for (Map.Entry<String, String> e : map.entrySet()) {
				return (E) Byte.valueOf(e.getValue());
			}
		}
		E obj = null;
		try {
			obj = clz.newInstance();
			BeanUtils.populate(obj, map);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}

	@Override
	public <E> E getData(String sql, Class<E> clz) {
		// TODO Auto-generated method stub
		return getData(sql, clz, new Object[] {});
	}

	@Override
	public <E> E getData(String sql, Class<E> clz, Object[] objArr) {
		// TODO Auto-generated method stub
		return getJdbcTemplate().query(sql, objArr, rs -> {
			Map<String, String> map = new HashMap<String, String>();
			ResultSetMetaData md = rs.getMetaData();
			int colcount = md.getColumnCount();
			rs.beforeFirst();
			while (rs.next()) {
				for (int i = 0; i < colcount; i++) {
					map.put(md.getColumnName(i + 1), rs.getString(i + 1));
				}
			}
			return wrapDataBean(map, clz);
		});
	}

	@Override
	public int execute(String sql) {
		// TODO Auto-generated method stub
		return execute(sql, new Object[] {});
	}

	@Override
	public int execute(String sql, Object[] objArr) {
		// TODO Auto-generated method stub
		return getJdbcTemplate().update(sql, objArr);
	}

	@Override
	public int[] executeBatch(String sql, BatchPreparedStatementSetter bs) {
		// TODO Auto-generated method stub
		return getJdbcTemplate().batchUpdate(sql, bs);
	}

}
