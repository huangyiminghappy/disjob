package com.huangyiming.disjob.quence;

import org.apache.log4j.Logger;

public class Log {

	private static final String thisClassName = Log.class.getName();
	private static final String msgSep = "\r\n";

	private static Logger logger = null;

	static {
		logger = Logger.getLogger("");
	}

	private static Object getStackMsg(Object msg) {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		if (ste == null) {
			return "";
		} 

		boolean srcFlag = false;
		for (int i = 0; i < ste.length; i++) {
			StackTraceElement s = ste[i];
			// 如果上一行堆栈代码是本类的堆栈，则该行代码则为源代码的最原始堆栈。
			if (srcFlag) {
				return s == null ? "" : s.toString() + msgSep + msg.toString();
			}
			// 定位本类的堆栈
			if (thisClassName.equals(s.getClassName())) {
				srcFlag = true;
				i++;
			}
		}
		return "";
	}

	public static void debug(Object msg) {
		Object message = getStackMsg(msg);
		logger.debug(message);
	}

	public static void debug(Object msg, Throwable t) {
		Object message = getStackMsg(msg);
		logger.debug(message, t);
	}

	public static void info(Object msg) {
		Object message = getStackMsg(msg);
		logger.info(message);
	}

	public static void info(Object msg, Throwable t) {
		Object message = getStackMsg(msg);
		logger.info(message, t);
	}
	public static void warn(Object msg) {
		Object message = getStackMsg(msg);
		logger.warn(message);
	}

	public static void warn(Object msg, Throwable t) {
		Object message = getStackMsg(msg);
		logger.warn(message, t);
	}

	public static void error(Object msg) {
		Object message = getStackMsg(msg);
		logger.error(message);
	}

	public static void error(Object msg, Throwable t) {
		Object message = getStackMsg(msg);
		if (logger != null) {
			logger.error(message, t);
		}
	}

	public static void fatal(Object msg) {
		Object message = getStackMsg(msg);
		logger.fatal(message);
	}

	public static void fatal(Object msg, Throwable t) {
		Object message = getStackMsg(msg);
		logger.fatal(message, t);
	}
}

