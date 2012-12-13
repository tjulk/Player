package com.baidu.player.util;

import android.content.Context;

/**
 * @ClassName: Configurations 
 * @Description: 这个类主要用于存放一些关于当前运行环境的一些配置信息，以及管理软件的配置信息.
 * @author LEIKANG 
 * @date 2012-12-13 下午12:54:27
 */
public final class Configurations {

	/**
	 * 保证只有一个事例。
	 */
	private Configurations() {

	}
	/**
	 * 
	 */
	private static float sdIPSCAL = 2.0f;

	/**
	 * 获取当前屏幕的dip相对于标准值的比值.
	 * 
	 * @return dip
	 */
	public static double getDIPScal() {
		return sdIPSCAL;
	}

	/**
	 * 从启动Activity设置当前屏幕的dip相对于标准值的比值.
	 * 
	 * @param ctx
	 *            context
	 */
	public static void setDIPScal(Context ctx) {
		if (ctx != null) {
		    sdIPSCAL = ctx.getResources().getDisplayMetrics().density;
		}
	}

	/**
	 * 获取一个针对当前屏幕dip的int值.在不同屏幕中dip不同，所以需要转换。
	 * 
	 * @param value
	 *            参数值
	 * @return 结果值
	 */
	public static int getScaledIntValue(int value) {
		return (int) (value * sdIPSCAL);
	}

	/**
	 * 获取一个针对当前屏幕dip的float值.在不同屏幕中dip不同，所以需要转换。
	 * 
	 * @param value
	 *            参数值
	 * @return 结果值
	 */
	public static float getScaledFloatValue(float value) {
		return value * sdIPSCAL;
	}
}
