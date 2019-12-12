package com.spurs.framework.core.jdbc.sqlExecutor;

import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

public interface SqlExecutorInterface {

	public <E> List<E> listData(String sql, Class<E> clz);

	/**
	 * 集合条件查询,带分页
	 * 
	 * @param sql
	 * @param clz    待装载Bean Class
	 * @param objArr 参数以数组形式传入 按？顺序 排列
	 * @return
	 * @author GC
	 * @date 2017-3-24 下午10:46:39
	 */
	public <E> List<E> listData(String sql, Class<E> clz, Object[] objArr);

	/**
	 * 
	 * @param sql
	 * @param clz
	 * @param objArr
	 * @param page	是否分页
	 * @return
	 */
	public <E> List<E> listData(String sql, Class<E> clz, Object[] objArr, boolean page);

	/**
	 * 单条记录查询
	 * 
	 * @param sql
	 * @param clz 待装载Bean Class
	 * @return
	 * @author GC
	 * @date 2017-3-24 下午10:46:39
	 */
	public <E> E getData(String sql, Class<E> clz);

	/**
	 * 单条记录查询
	 * 
	 * @param        <E>
	 * @param sql
	 * @param clz    待装载Bean Class
	 * @param objArr 参数以数组形式传入 按？顺序 排列
	 * @return
	 * @author GC
	 * @date 2017-3-24 下午10:46:39
	 */
	public <E> E getData(String sql, Class<E> clz, Object[] objArr);

	/**
	 * 增删改 操作 无参
	 * 
	 * @param sql
	 * @return
	 * @author GC 2017年3月22日 上午10:15:09
	 */
	public int execute(String sql);

	/**
	 * 增删改操作 有参数
	 * 
	 * @param sql
	 * @param objArr 参数以数组形式传入 按？顺序 排列
	 * @return
	 * @author GC
	 * @date 2017-3-24 下午10:45:22
	 */
	public int execute(String sql, Object[] objArr);

	/**
	 * 增删改 批量操作
	 * 
	 * @param sql
	 * @param objArr 参数以数组形式传入 按？顺序 排列
	 * @return
	 * @author GC
	 * @date 2017-3-24 下午10:45:22
	 */
	public int[] executeBatch(String sql, BatchPreparedStatementSetter bs);

}
