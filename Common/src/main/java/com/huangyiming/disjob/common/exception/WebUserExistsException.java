package com.huangyiming.disjob.common.exception;

/**
 * <pre>
 * 
 *  File: UserExistsException.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  web用户存在异常
 * 
 *  Revision History
 *
 *  Date：		2016年5月19日
 *  Author：		Disjob
 *
 * </pre>
 */  
public class WebUserExistsException extends DisJobAbstractCheckException {
	private static final long serialVersionUID = 5938178916897833968L;

	public WebUserExistsException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public WebUserExistsException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public WebUserExistsException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public WebUserExistsException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
}
