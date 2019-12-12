package com.spurs.framework.core.jdbc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Component;

import com.spurs.framework.core.Page.Page;
import com.spurs.framework.core.jdbc.sqlExecutor.SqlExecutorInterface;
import com.spurs.framework.core.jdbc.sqlExecutor.impl.SqlExecutor;

@Component
public class BaseDao implements SqlExecutorInterface {

	@Autowired
	private SqlExecutor sqlExecutor;

	@Override
	public <E> List<E> listData(String sql, Class<E> clz) {
		// TODO Auto-generated method stub
		return sqlExecutor.listData(sql, clz);
	}

	@Override
	public <E> List<E> listData(String sql, Class<E> clz, Object[] objArr) {
		// TODO Auto-generated method stub
		return sqlExecutor.listData(sql, clz, objArr);
	}

	@Override
	public <E> List<E> listData(String sql, Class<E> clz, Object[] objArr, boolean page) {
		// TODO Auto-generated method stub
		return sqlExecutor.listData(sql, clz, objArr, page);
	}

	@Override
	public <E> E getData(String sql, Class<E> clz) {
		// TODO Auto-generated method stub
		return sqlExecutor.getData(sql, clz);
	}

	@Override
	public <E> E getData(String sql, Class<E> clz, Object[] objArr) {
		// TODO Auto-generated method stub
		return sqlExecutor.getData(sql, clz, objArr);
	}

	@Override
	public int execute(String sql) {
		// TODO Auto-generated method stub
		return sqlExecutor.execute(sql);
	}

	@Override
	public int execute(String sql, Object[] objArr) {
		// TODO Auto-generated method stub
		return sqlExecutor.execute(sql, objArr);
	}

	@Override
	public int[] executeBatch(String sql, BatchPreparedStatementSetter bs) {
		// TODO Auto-generated method stub
		return sqlExecutor.executeBatch(sql, bs);
	}

}
