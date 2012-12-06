/** 
 * Filename:    BdLog.java 
 * Description:  
 * Copyright:   Baidu MIC Copyright(c)2011 
 * @author:     gong.haiping 
 * @version:    1.1 
 * Create at:   2011-4-19 下午12:16:22 
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2011-4-19   gong.haiping      1.0         1.0 Version 
 * 2011-7-30   CoCoMo            1.1         Add  printStackTrace
 * 2011-8-1    CoCoMo            1.2         Modify Tag Name
 * 2011-11-18    gong.haiping            1.3         Add printStackTrace API
 */
package com.baidu.browser.core.util;

import android.util.Log;

/**
 * Log输出类
 */
public final class BdLog {

	/** 全局LOG开关 */
	public static final boolean DEBUG_LOG = false;
	/** DEBUG模式开关 */
	public static final boolean DEBUG_DEBUG = true;
	/** ERROR模式开关 */
	public static final boolean DEBUG_ERROR = true;
	/** INFO模式开关 */
	public static final boolean DEBUG_INFO = true;
	/** VERBOSE模式开关 */
	public static final boolean DEBUG_VERBOSE = true;
	/** WARN模式开关 */
	public static final boolean DEBUG_WARN = true;

	/**
	 * Log级别
	 */
	public enum LogLevel {
		/** 调试级别 */
		DEBUG,
		/** 错误级别 */
		ERROR,
		/** 信息级别 */
		INFO,
		/** VERBOSE级别 */
		VERBOSE,
		/** 警告级别 */
		WARN,
	}

	/**
	 * TAG过滤掉FILE_TYPE
	 */
	private static final String FILE_TYPE = ".java";

	/**
	 * Constructor
	 */
	private BdLog() {
	}

	/**
	 * @param aMessage
	 *            调试信息
	 */
	public static void d(String aMessage) {
		if (DEBUG_LOG && DEBUG_DEBUG) {
			myLog(LogLevel.DEBUG, aMessage, 2, true, null);
		}
	}

	/**
	 * @param aMessage
	 *            调试信息
	 * @param aThrow
	 *            异常
	 */
	public static void d(String aMessage, Throwable aThrow) {
		if (DEBUG_LOG && DEBUG_DEBUG) {
			myLog(LogLevel.DEBUG, aMessage, 2, true, aThrow);
		}
	}

	/**
	 * @param aMessage
	 *            错误信息
	 */
	public static void e(String aMessage) {
		if (DEBUG_LOG && DEBUG_ERROR) {
			myLog(LogLevel.ERROR, aMessage, 2, true, null);
		}
	}

	/**
	 * @param aMessage
	 *            错误信息
	 * @param aThrow
	 *            异常
	 */
	public static void e(String aMessage, Throwable aThrow) {
		if (DEBUG_LOG && DEBUG_ERROR) {
			myLog(LogLevel.ERROR, aMessage, 2, true, aThrow);
		}
	}

	/**
	 * @param aMessage
	 *            INFO信息
	 */
	public static void i(String aMessage) {
		if (DEBUG_LOG && DEBUG_INFO) {
			myLog(LogLevel.INFO, aMessage, 2, true, null);
		}
	}

	/**
	 * @param aMessage
	 *            INFO信息
	 * @param aThrow
	 *            异常
	 */
	public static void i(String aMessage, Throwable aThrow) {
		if (DEBUG_LOG && DEBUG_INFO) {
			myLog(LogLevel.INFO, aMessage, 2, true, aThrow);
		}
	}

	/**
	 * @param aMessage
	 *            VERBOSE信息
	 */
	public static void v(String aMessage) {
		if (DEBUG_LOG && DEBUG_VERBOSE) {
			myLog(LogLevel.VERBOSE, aMessage, 2, true, null);
		}
	}

	/**
	 * @param aMessage
	 *            VERBOSE信息
	 * @param aThrow
	 *            异常
	 */
	public static void v(String aMessage, Throwable aThrow) {
		if (DEBUG_LOG && DEBUG_VERBOSE) {
			myLog(LogLevel.VERBOSE, aMessage, 2, true, aThrow);
		}
	}

	/**
	 * @param aMessage
	 *            警告信息
	 */
	public static void w(String aMessage) {
		if (DEBUG_LOG && DEBUG_WARN) {
			myLog(LogLevel.WARN, aMessage, 2, true, null);
		}
	}

	/**
	 * @param aMessage
	 *            警告信息
	 * @param aThrow
	 *            异常
	 */
	public static void w(String aMessage, Throwable aThrow) {
		if (DEBUG_LOG && DEBUG_WARN) {
			myLog(LogLevel.WARN, aMessage, 2, true, aThrow);
		}
	}

	/**
	 * @param aLevel
	 *            Log级别
	 * @param aMessage
	 *            要输出的Log信息
	 * @param aStackTraceLevel
	 *            函数调用栈层级
	 * @param aShowMethod
	 *            是否输出调用log的类方法
	 * @param aThrow
	 *            输出异常栈信息
	 */
	private static void myLog(LogLevel aLevel, String aMessage, int aStackTraceLevel, boolean aShowMethod,
			Throwable aThrow) {
		final String tag = "SearchBrowser";
		StackTraceElement stackTrace = (new Throwable()).getStackTrace()[aStackTraceLevel];
		String filename = stackTrace.getFileName();
		String methodname = stackTrace.getMethodName();
		int linenumber = stackTrace.getLineNumber();
		//当心！proguard混淆以后getFileName会是一个null值！
		if (filename != null && filename.contains(FILE_TYPE)) {
			filename = filename.replace(FILE_TYPE, "");
		}

		String output = "";
		if (aShowMethod) {
			output = String.format("[%s: %s: %d]%s", filename, methodname, linenumber, aMessage);
		} else {
			output = String.format("[%s: %d]%s", filename, linenumber, aMessage);
		}

		switch (aLevel) {
			case DEBUG:
				if (aThrow == null) {
					Log.d(tag, output);
				} else {
					Log.d(tag, output, aThrow);
				}
				break;
			case ERROR:
				if (aThrow == null) {
					Log.e(tag, output);
				} else {
					Log.e(tag, output, aThrow);
				}
				break;
			case INFO:
				if (aThrow == null) {
					Log.i(tag, output);
				} else {
					Log.i(tag, output, aThrow);
				}
				break;
			case VERBOSE:
				if (aThrow == null) {
					Log.v(tag, output);
				} else {
					Log.v(tag, output, aThrow);
				}
				break;
			case WARN:
				if (aThrow == null) {
					Log.w(tag, output);
				} else {
					Log.w(tag, output, aThrow);
				}
				break;
			default:
				break;
		}
	}

}
