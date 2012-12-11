package com.baidu.browser.core.util;

import android.content.Context;

import com.baidu.browser.core.BdCore;

/**
 * @ClassName: BdUtil 
 * @Description: BdUtil
 * @author LEIKANG 
 * @date 2012-12-11 ����5:20:45
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
