package com.spurs.framework.core.exception.handler;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spurs.framework.core.exception.define.PageInfoNotFountException;

@ControllerAdvice
public class ExceptionHandler {

	@org.springframework.web.bind.annotation.ExceptionHandler({ PageInfoNotFountException.class })
	@ResponseBody
	public String requestNotReadable(PageInfoNotFountException e) {
		e.printStackTrace();
		return "";
	}
}
