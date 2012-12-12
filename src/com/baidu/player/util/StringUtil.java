package com.baidu.player.util;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

public class StringUtil {
	public static String bytes2String(byte[] value) {
		return (value == null) ? "" : new String(value);
	}

	public static boolean isEmpty(String paramString) {
		if ((paramString == null) || ("".equals(paramString))) {
			return true;
		}
		return false;
	}

	public static boolean isEmptyArray(Object[] obj) {
		return isEmptyArray(obj, 1);
	}

	public static boolean isEmptyArray(Object[] array, int paramInt) {
		if ((array == null) || (array.length < paramInt)) {
			return true;
		}
		return false;
	}

	public static boolean isNumeric(String str) {
		final String number = "0123456789";
		for (int i = 0; i < str.length(); i++) {
			if (number.indexOf(str.charAt(i)) == -1) {
				return false;
			}
		}
		return true;
	}

	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0XFF);
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toUpperCase();
	}
	
	public static String reverse(String value) {
		StringBuilder sb = new StringBuilder();
		for(int i = value.length() - 1; i >= 0; --i) {
			sb.append(value.charAt(i));
		}
		return sb.toString();
	}

	public static String createUUID() {
		return UUID.randomUUID().toString();
	}

	public static String formatSpeed(int value) {
		return formatSize(value) + "/s";
	}

	public static String formatSize(long value) {

		double k = (double) value / 1024;
		if (k == 0) {
			return String.format("%dB", value);
		}

		double m = k / 1024;
		if (m < 1) {
			return String.format("%.1fK", k);
		}

		double g = m / 1024;
		if (g < 1) {
			return String.format("%.1fM", m);
		}

		return String.format("%.1fG", g);
	}

	public static String formatTime(int second) {

		int hh = second / 3600;
		int mm = second % 3600 / 60;
		int ss = second % 60;

		if (0 != hh) {
			return String.format("%02d:%02d:%02d", hh, mm, ss);
		} else {
			return String.format("%02d:%02d", mm, ss);
		}
	}
	
	public static String findMaxSub(List<String> strings) {  
        String s = strings.get(0);  
        int len = s.length();  
        int i = 0;  
        String maxSub = "";  
        while (i < len - maxSub.length()) {  
            for (int j = len; j >= i + maxSub.length(); j--) {  
                String sub = s.substring(i, j);  
                int p = 1;  
                while (p < strings.size() && strings.get(p).contains(sub)) p++;  
                if (p == strings.size()) {  
                    maxSub = sub;  
                    break;  
                }  
            }  
            i++;  
        }  
        return maxSub;  
    }
	
	public static String getBaseUrl(String pageUrl) {
		try{
			if(!pageUrl.contains("://")){
				pageUrl="http://"+pageUrl;
			}
			int end = pageUrl.indexOf("/", pageUrl.indexOf("://") + 4);
			return pageUrl.substring(0, end);
		}catch (Exception e) {
		}
		return null;
	}
	
	public static String getUrlCode(String pageUrl){
		try{
			String temp=getBaseUrl(pageUrl);
			temp=temp.substring(temp.indexOf("://")+3).replace(".", "").replace("/", "");
			return temp;
		}catch (Exception e) {
		}
		return null;
	}
	
	public static String encode(String url) {
		return URLEncoder.encode(url);
	}
	
	public static String decode(String url) {
		return URLDecoder.decode(url);
	}
}
