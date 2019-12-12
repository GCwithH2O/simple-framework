package com.spurs.framework.utils;

public class UUID {

	/**
	 * 获得一个uuid，长度默认是20位
	 * @Title: get 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param @param size
	 * @param @return    参数
	 * @return String    返回类型 
	 * @throws
	 */
	public static String get() {
		return java.util.UUID.randomUUID().toString().replaceAll("-", "");
	}

}
