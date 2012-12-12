package com.baidu.browser.db;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @ClassName: HistoryConfig 
 * @Description: ��ʷ��¼���ó������� 
 * @author LEIKANG 
 * @date 2012-12-12 ����1:10:38
 */
public final class HistoryConfig {
	
	public static final String PREFS_NAME = "settings";
	
	/** ��˽ģʽ��Ӧ��preferebce key�� */
	public static final String PREFS_KEY_PRIVATE_MODE = "nohistory";
    
    /**һ��ĺ�������*/
    public static final long DAY_MILLIS = 86400000L;
    
    /**��ʷ��¼���ڵ�ʱ�����ޣ����룩��*/
    public static final long MAX_STAT_AGE_MILLIS = 30 * DAY_MILLIS;
    
    /**ÿ��ĺ�������*/
    public static final long PER_SECOND = 1000;
    
    /**��ʷ��¼���ڵ�ʱ�����ޣ��룩��*/
    public static final long MAX_STAT_AGE_SECOND = MAX_STAT_AGE_MILLIS / PER_SECOND;
    
    /**�����¼����͵ȼ��������������*/
    public static final int MIN_CLICKS_FOR_SOURCE_RANKING = 1;
    
    /**������Դ���͵ĳ�������ѯʱʹ�ã���*/
    public static final String SOURCE_ALL = "all";
    
    /**���б�����Դ���͵ĳ�������ѯʱʹ�ã���*/
    public static final String SOURCE_LOCAL = "local";
    
    /**Ӧ����Դ���͵ĳ�����*/
    public static final String SOURCE_APP = "app";
    
    /**Web��Դ���͵ĳ�����*/
    public static final String SOURCE_WEB = "web";
    /** ��¼�����Ƿ���¡� */
    private static boolean settingsUpdated = true;
    /** �Ƿ�Ϊ��˽ģʽ. */
    private static boolean isprivate = false;
    /** ���ع��췽������ֹ��ȡʵ�� */
    private HistoryConfig() {
        
    }

    /**
     * ��ȡ��ǰ�Ƿ�Ϊ��˽ģʽ������ǣ�������ʾ��ʷ��¼�����Ҳ�������µ���ʷ��¼��
     * @param ctx context
     * @return true ����˽ģʽ��false ������˽ģʽ��
     */
    public static boolean isPrivateMode(Context ctx) {
        if (settingsUpdated) {
            final SharedPreferences settings = ctx.getSharedPreferences(
            		PREFS_NAME, 0);
            isprivate = settings.getBoolean(PREFS_KEY_PRIVATE_MODE, false);
            settingsUpdated = false;
        }
        return isprivate;
    }

    /**
     * �����Ƿ�Ϊ��˽ģʽ��
     * @param ctx context
     * @param privateMode true ����Ϊ��˽ģʽ��false ����Ϊ����˽ģʽ��
     */
    public static void setPrivateMode(Context ctx, boolean privateMode) {
        final SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(PREFS_KEY_PRIVATE_MODE, privateMode);
        editor.commit();
        settingsUpdated = true;
    }
}
