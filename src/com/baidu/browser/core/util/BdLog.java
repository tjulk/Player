package com.baidu.browser.core.util;

import android.util.Log;

/**
 * @ClassName: BdLog 
 * @Description: ����� Log����� 
 * @author LEIKANG 
 * @date 2012-12-11 ����5:20:22
 */
public final class BdLog {

	/** ȫ��LOG���� */
	public static final boolean DEBUG_LOG = false;
	/** DEBUGģʽ���� */
	public static final boolean DEBUG_DEBUG = true;
	/** ERRORģʽ���� */
	public static final boolean DEBUG_ERROR = true;
	/** INFOģʽ���� */
	public static final boolean DEBUG_INFO = true;
	/** VERBOSEģʽ���� */
	public static final boolean DEBUG_VERBOSE = true;
	/** WARNģʽ���� */
	public static final boolean DEBUG_WARN = true;

	/**
	 * Log����
	 */
	public enum LogLevel {
		/** ���Լ��� */
		DEBUG,
		/** ���󼶱� */
		ERROR,
		/** ��Ϣ���� */
		INFO,
		/** VERBOSE���� */
		VERBOSE,
		/** ���漶�� */
		WARN,
	}

	/**
	 * TAG���˵�FILE_TYPE
	 */
	private static final String FILE_TYPE = ".java";

	/**
	 * Constructor
	 */
	private BdLog() {
	}

	/**
	 * @param aMessage
	 *            ������Ϣ
	 */
	public static void d(String aMessage) {
		if (DEBUG_LOG && DEBUG_DEBUG) {
			myLog(LogLevel.DEBUG, aMessage, 2, true, null);
		}
	}

	/**
	 * @param aMessage
	 *            ������Ϣ
	 * @param aThrow
	 *            �쳣
	 */
	public static void d(String aMessage, Throwable aThrow) {
		if (DEBUG_LOG && DEBUG_DEBUG) {
			myLog(LogLevel.DEBUG, aMessage, 2, true, aThrow);
		}
	}

	/**
	 * @param aMessage
	 *            ������Ϣ
	 */
	public static void e(String aMessage) {
		if (DEBUG_LOG && DEBUG_ERROR) {
			myLog(LogLevel.ERROR, aMessage, 2, true, null);
		}
	}

	/**
	 * @param aMessage
	 *            ������Ϣ
	 * @param aThrow
	 *            �쳣
	 */
	public static void e(String aMessage, Throwable aThrow) {
		if (DEBUG_LOG && DEBUG_ERROR) {
			myLog(LogLevel.ERROR, aMessage, 2, true, aThrow);
		}
	}

	/**
	 * @param aMessage
	 *            INFO��Ϣ
	 */
	public static void i(String aMessage) {
		if (DEBUG_LOG && DEBUG_INFO) {
			myLog(LogLevel.INFO, aMessage, 2, true, null);
		}
	}

	/**
	 * @param aMessage
	 *            INFO��Ϣ
	 * @param aThrow
	 *            �쳣
	 */
	public static void i(String aMessage, Throwable aThrow) {
		if (DEBUG_LOG && DEBUG_INFO) {
			myLog(LogLevel.INFO, aMessage, 2, true, aThrow);
		}
	}

	/**
	 * @param aMessage
	 *            VERBOSE��Ϣ
	 */
	public static void v(String aMessage) {
		if (DEBUG_LOG && DEBUG_VERBOSE) {
			myLog(LogLevel.VERBOSE, aMessage, 2, true, null);
		}
	}

	/**
	 * @param aMessage
	 *            VERBOSE��Ϣ
	 * @param aThrow
	 *            �쳣
	 */
	public static void v(String aMessage, Throwable aThrow) {
		if (DEBUG_LOG && DEBUG_VERBOSE) {
			myLog(LogLevel.VERBOSE, aMessage, 2, true, aThrow);
		}
	}

	/**
	 * @param aMessage
	 *            ������Ϣ
	 */
	public static void w(String aMessage) {
		if (DEBUG_LOG && DEBUG_WARN) {
			myLog(LogLevel.WARN, aMessage, 2, true, null);
		}
	}

	/**
	 * @param aMessage
	 *            ������Ϣ
	 * @param aThrow
	 *            �쳣
	 */
	public static void w(String aMessage, Throwable aThrow) {
		if (DEBUG_LOG && DEBUG_WARN) {
			myLog(LogLevel.WARN, aMessage, 2, true, aThrow);
		}
	}

	/**
	 * @param aLevel
	 *            Log����
	 * @param aMessage
	 *            Ҫ�����Log��Ϣ
	 * @param aStackTraceLevel
	 *            ��������ջ�㼶
	 * @param aShowMethod
	 *            �Ƿ��������log���෽��
	 * @param aThrow
	 *            ����쳣ջ��Ϣ
	 */
	private static void myLog(LogLevel aLevel, String aMessage, int aStackTraceLevel, boolean aShowMethod,
			Throwable aThrow) {
		final String tag = "SearchBrowser";
		StackTraceElement stackTrace = (new Throwable()).getStackTrace()[aStackTraceLevel];
		String filename = stackTrace.getFileName();
		String methodname = stackTrace.getMethodName();
		int linenumber = stackTrace.getLineNumber();
		//���ģ�proguard�����Ժ�getFileName����һ��nullֵ��
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
