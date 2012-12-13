package com.baidu.player.util;

import android.content.Context;

/**
 * @ClassName: Configurations 
 * @Description: �������Ҫ���ڴ��һЩ���ڵ�ǰ���л�����һЩ������Ϣ���Լ����������������Ϣ.
 * @author LEIKANG 
 * @date 2012-12-13 ����12:54:27
 */
public final class Configurations {

	/**
	 * ��ֻ֤��һ��������
	 */
	private Configurations() {

	}
	/**
	 * 
	 */
	private static float sdIPSCAL = 2.0f;

	/**
	 * ��ȡ��ǰ��Ļ��dip����ڱ�׼ֵ�ı�ֵ.
	 * 
	 * @return dip
	 */
	public static double getDIPScal() {
		return sdIPSCAL;
	}

	/**
	 * ������Activity���õ�ǰ��Ļ��dip����ڱ�׼ֵ�ı�ֵ.
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
	 * ��ȡһ����Ե�ǰ��Ļdip��intֵ.�ڲ�ͬ��Ļ��dip��ͬ��������Ҫת����
	 * 
	 * @param value
	 *            ����ֵ
	 * @return ���ֵ
	 */
	public static int getScaledIntValue(int value) {
		return (int) (value * sdIPSCAL);
	}

	/**
	 * ��ȡһ����Ե�ǰ��Ļdip��floatֵ.�ڲ�ͬ��Ļ��dip��ͬ��������Ҫת����
	 * 
	 * @param value
	 *            ����ֵ
	 * @return ���ֵ
	 */
	public static float getScaledFloatValue(float value) {
		return value * sdIPSCAL;
	}
}
