package com.spurs.framework.core.jdbc.sqlExecutor;

import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

public interface JdbcUtilsInterface3 {
//    /**
//     * 集合查询 无查询条件 带分页
//     * 
//     * @param sql
//     * @param clz
//     *            待装载Bean Class
//     * @return
//     * @author GC
//     * @date 2017-3-24 下午10:46:39
//     */
//    public <E> List<E> listData(String sql, Class<E> clz,Page page);

	/**
	 * 集合查询 无查询条件,不分页
	 * 
	 * @param sql
	 * @param clz 待装载Bean Class
	 * @return
	 * @author GC
	 * @date 2017-3-24 下午10:46:39
	 */
	public <E> List<E> listData(String sql, Class<E> clz, boolean page);

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
	 * 大数据量下的集合条件查询 分页
	 * 
	 * @param sql      如果此sql为了性能内嵌了子查询，子查询里面用到了对应的limit，则只写limit即可，datahome会自动动态替换limit
	 * @param clz      待装载Bean Class
	 * @param objArr   参数以数组形式传入 按？顺序 排列
	 * @param countSql 单独查询前端分页所需要的总数，
	 * @return List<E>
	 * @author GC
	 * @date 2017年3月15日 下午3:31:00
	 */
	public <E> List<E> listData(String sql, Class<E> clz, Object[] objArr, String countSql);

	/**
	 * 集合查询，不带分页
	 * 
	 * @param        <E>
	 * @param sql
	 * @param clz    待装载Bean Class
	 * @param objArr 参数以数组形式传入 按？顺序 排列
	 * @return
	 * @author GC
	 * @date 2017-3-24 下午10:46:39
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
	 * 如果不传入Class ，则返回值类型为Map<String,String>
	 * 
	 * @param sql
	 * @param objArr
	 * @return E
	 * @author GC
	 * @date 2017-3-24 下午10:46:39
	 */
	public <E> E getData(String sql, Object[] objArr);

	/**
	 * 如果不传入Class ，则返回值类型为Map<String,String>
	 * 
	 * @param sql
	 * @return E
	 * @author GC
	 * @date 2017-3-24 下午10:46:28
	 */
	public <E> E getData(String sql);

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

	/**
	 * 增删改 操作 无参
	 * 
	 * @param sql
	 * @return
	 * @author GC 2017年3月22日 上午10:15:09
	 */
	public int execute(String sql);

	/**
	 * 存储过程 有参数、返回值
	 * 
	 * @param sql
	 * @param argArr
	 * @return List<E>
	 * @author GC 2017年3月22日 上午10:15:09
	 */
	public <E> List<E> executeProcedure(String sql, String argArr[]);

	/**
	 * 存储过程 无参无返回值
	 * 
	 * @param sql
	 * @author GC 2017年3月22日 上午10:15:09
	 */
	public void executeProcedure(String sql);

	/**
	 * 原生sql查询 不做任何封装
	 * 
	 * @param sql
	 * @return List<E>
	 * @author GC
	 * @date 2017-3-24 下午10:45:22
	 */
	public <E> List<E> getMapData(String sql);

	/**
	 * 原生sql查询 不做任何封装
	 * 
	 * @param sql
	 * @param objArr
	 * @param tableName 要查询的表名称
	 * @return 参数 List<E> 返回类型
	 * @author GC
	 * @date 2017-3-24 下午10:45:22
	 */
	public <E> List<E> getMapData(String sql, Object[] objArr, String[] tableName);

	public <E> List<E> listData(String sql, Class<E> clz);
}
