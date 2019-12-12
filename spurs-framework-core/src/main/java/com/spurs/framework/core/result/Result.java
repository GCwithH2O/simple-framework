package com.spurs.framework.core.result;

import java.io.Serializable;

public class Result implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1165910562645821914L;

	private String message;
	private String code;
	private Object result;
	private boolean status;

	@SuppressWarnings("unused")
	private Result() {
	}

	/**
	 * 构造方法
	 * @param status	状态，true：正常返回，false：异常返回
	 * @param code		返回码：参照/core/responseCode/responseCode.properties
	 * @param message	返回信息
	 * @param result	携带的返回对象
	 */
	public Result(boolean status, String code, String message, Object result) {
		this.status = status;
		this.code = code;
		this.message = message;
		this.result = result;
	}

	@SuppressWarnings("unchecked")
	public <E> E getResult(Class<E> clz) {
		return (E) this.result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

}
