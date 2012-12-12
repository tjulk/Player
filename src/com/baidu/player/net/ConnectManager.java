package com.baidu.player.net;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

/**
 * @ClassName: ConnectManager 
 * @Description: �����жϵ�ǰ�����������ͣ�����㣬�����������Ϣ�� 
 * @author LEIKANG 
 * @date 2012-12-12 ����1:51:31
 */
public class ConnectManager {
    
    /** log tag. */
    private static final String TAG = ConnectManager.class.getSimpleName();
    
    /** log debug on/off .*/
    private static final boolean DEBUG = true;
    
    /** current apn. */
    private String mApn;
    
    /** apn proxy. */
    private String mProxy;
    
    /** proxy port. */
    private String mPort;
    
    /** ���������ַ���.*/
    private String mNetType; 
    
    /** ��ǰ�����Ƿ�ʹ��wap��
     *  wifi ���� cmnet��Ϊ��ʹ�� wap.
     */
    private boolean mUseWap;
    
    /** prefered apn url. */
    public static final Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");
    
    /**
     * constructor.
     * @param context  context
     */
    public ConnectManager(Context context) {
        checkNetworkType(context);
    }
    
    /**
     * ��鵱ǰϵͳapn ���� ״̬.
     * @param context context
     */
    private void checkApn(Context context) {
        Cursor cursor = context.getContentResolver().query(
                PREFERRED_APN_URI,
                new String[] { "_id", "apn", "proxy", "port" }, null, null,
                null);
        
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int anpIndex = cursor.getColumnIndex("apn");
                int proxyIndex = cursor.getColumnIndex("proxy");
                int portIndex = cursor.getColumnIndex("port");
                
                mApn = cursor.getString(anpIndex);
                mProxy = cursor.getString(proxyIndex);
                mPort = cursor.getString(portIndex);
                
                mNetType = mApn;
                
                if (DEBUG) {
                    Log.d(TAG, "apn: " + " " + mApn + " " + mProxy + " " + mPort);
                }
                
                if (mProxy != null && mProxy.length() > 0) {
                    //��������˴���
                    
                    if ("10.0.0.172".equals(mProxy.trim())) {
                        // ��ǰ������������Ϊcmwap || uniwap
                        mUseWap = true;
                        mPort = "80";
                    } else if ("10.0.0.200".equals(mProxy.trim())) {
                        mUseWap = true;
                        mPort = "80";
                    } else {
                        // ����Ϊnet
                        mUseWap = false;
                    }

                } else if (mApn != null) {
                    // ����û�ֻ������apn��û�����ô��������Զ�����
                    
                    String strApn = mApn.toUpperCase();
                    if (strApn.equals("CMWAP") || strApn.equals("UNIWAP")
                            || strApn.equals("3GWAP")) {
                        mUseWap = true;
                        mProxy = "10.0.0.172";
                        mPort = "80";
                    } else if (strApn.equals("CTWAP")) {
                        mUseWap = true;
                        mProxy = "10.0.0.200";
                        mPort = "80";
                    }

                } else {
                    // �������綼������net
                    mUseWap = false;
                }
                if (DEBUG) {
                    Log.d(TAG, "adjust apn: " + " " + mApn + " " + mProxy + " " + mPort);
                }
            }
            
            cursor.close();
        }        
    }
    
    /**
     * ��鵱ǰ�������͡�
     * @param context context
     */
    private void checkNetworkType(Context context) {
        
        ConnectivityManager connectivityManager = 
            (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null) {
            
            if (DEBUG) {
                Log.d(TAG, "network type : " + activeNetInfo.getTypeName().toLowerCase());
            }
            
            if ("wifi".equals(activeNetInfo.getTypeName().toLowerCase())) {
            	mNetType = "wifi";
                mUseWap = false;
            } else {
                checkApn(context);
            }
        }
    }
    
    /**
     * ��ǰ���������Ƿ���wap���硣
     * @return  cmwap 3gwap ctwap ����true
     */
    public boolean isWapNetwork() {
        return mUseWap;
    }
    
    /**
     * ��ȡ��ǰ��������apn.
     * @return apn
     */
    public String getApn() {
        return mApn;
    }
    
    /**
     * ��õ�ǰ��������
     * @return wifi, cmnet, uninet, ctnet, cmwap ����
     */
    public String getNetType() {
    	return mNetType;
    }
    
    /**
     * ��ȡ��ǰ�������ӵĴ����������ַ������ cmwap ���������10.0.0.172.
     * @return ��ȡ��ǰ�������ӵĴ����������ַ������ cmwap ���������10.0.0.172
     */
    public String getProxy() {
        return mProxy;
    }
    
    /**
     * ��ȡ��ǰ�������ӵĴ���������˿ڣ����� cmwap ����������˿� 80.
     * @return ��ȡ��ǰ�������ӵĴ���������˿ڣ����� cmwap ����������˿� 80
     */
    public String getProxyPort() {
        return mPort;
    }
    
	/**
	 * �����Ƿ���á�
	 * @param context Context
	 * @return ���Ӳ����÷��� true
	 */
	public static boolean isNetworkConnected(Context context) {
		NetworkInfo networkInfo = getActiveNetworkInfo(context);
		return networkInfo != null && networkInfo.isConnected();
	}

	/**
	 * ��ȡ������ӡ�
	 * @param context Context
	 * @return ��ǰ����
	 */
	private static NetworkInfo getActiveNetworkInfo(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return null;
		}
		return connectivity.getActiveNetworkInfo();
	}
}
