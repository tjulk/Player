/** 
 * Filename:    BdUtil.java
 * Description:  
 * Copyright:   Baidu MIC Copyright(c)2011 
 * @author:     CoCoMo 
 * @version:    1.0
 * Create at:   2012-4-23 ����09:11:42
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2012-4-23    CoCoMo      1.0         1.0 Version 
 */
package com.baidu.browser.core.util;

import android.content.Context;

import com.baidu.browser.core.BdCore;

/**
 * BdUtil
 */
public final class BdUtil {

	/**
	 * Constructor
	 */
	private BdUtil() {
	}

	/**
	 * ��ȡdip��Ӧ��pixֵ
	 * 
	 * @param aContext
	 *            Context
	 * @param aValue
	 *            dipֵ
	 * @return dip��Ӧ��pixֵ
	 */
	public static int dip2pix(Context aContext, float aValue) {
		return Math.round(aValue * BdCore.getScreenDensity(aContext));
	}
	
	/**
	 * ��һ��url�л�ȡһ��������
	 * 
	 * @param key ��Ҫ��õĲ�����key��
	 * @param url ����ѯ��url 
	 * @return ����ԭʼ������û���ҵ�����null.
	 */
    public static String getQueryParameter(String url, String key) {

        final String encodedKey = key;
        final int length = url.length();
        
        int start = 0;
        start = url.indexOf("?") + 1;
        
        do {
            int nextAmpersand = url.indexOf('&', start);
            int end = nextAmpersand != -1 ? nextAmpersand : length;

            int separator = url.indexOf('=', start);
            if (separator > end || separator == -1) {
                separator = end;
            }

            if (separator - start == encodedKey.length()
                    && url.regionMatches(start, encodedKey, 0, encodedKey.length())) {
                if (separator == end) {
                  return "";
                } else {
                  return (url.substring(separator + 1, end));
                }
            }

            // Move start to end of name.
            if (nextAmpersand != -1) {
                start = nextAmpersand + 1;
            } else {
                break;
            }
        } while (true);
        
        return null;
    }
}
