package com.spurs.framework.core.Page;

import java.io.Serializable;

public class Page implements Serializable {

	/**
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	 */
	private static final long serialVersionUID = -3217049657169644257L;

	private static ThreadLocal<Page> page = new ThreadLocal<Page>(); // 分页常量
	private int pageSize; // 每页显示条数
	private int currPage; // 当前页
	private long total; // 总条数

	public static Page getDefaultPage() {
		Page p = new Page();
		p.setPageSize(30);
		p.setCurrPage(0);
		return p;
	}

	public static Page get() {
		return page.get();
	}

	public static void set(Page p) {
		page.set(p);
	}

	public static void remove() {
		page.remove();
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCurrPage() {
		return currPage;
	}

	public void setCurrPage(int currPage) {
		this.currPage = currPage;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
