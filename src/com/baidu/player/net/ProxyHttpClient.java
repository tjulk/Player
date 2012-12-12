package com.baidu.player.net;

import org.apache.http.HttpHost;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

/**
 * @ClassName: ProxyHttpClient 
 * @Description: 
 * �ṩһ��anroidƽ̨�Զ����apn proxy�� HttpClient.
 * �Զ���� proxy ���ã�����Ҫʹ�����ٴι���wap net�����
 * @author LEIKANG 
 * @date 2012-12-12 ����1:52:54
 */
public class ProxyHttpClient extends DefaultHttpClient {
    
    /** log tag. */
    private static final String TAG = ProxyHttpClient.class.getSimpleName();
    
    /** log debug on/off .*/
    private static final boolean DEBUG = true;
    
    /** apn proxy. */
    private String mProxy;
    
    /** proxy port. */
    private String mPort;
    
    /** ��ǰ�����Ƿ�ʹ��wap��
     *  wifi ���� cmnet��Ϊ��ʹ�� wap.
     */
    private boolean mUseWap;
    
    /** http ��ʱ�� */
    private static final int HTTP_TIMEOUT_MS = 30000;

    /** ���ڼ���Ƿ���û���ͷ�(close)�����. */
    private RuntimeException mLeakedException = new IllegalStateException(
                                                        "ProxyHttpClient created and never closed");
    
    /**
     * constructor.
     * @param context  context
     */
    public ProxyHttpClient(Context context) {
        this(context, null, null);
    }
    
    /**
     * ���캯��.
     * @param context application context
     * @param userAgent  useragent you want to set 
     */
    public ProxyHttpClient(Context context, String userAgent) {
        this(context, userAgent, null);
    }
    
    /**
     * ���캯��.
     * @param context application context
     * @param connectManager ConnectManager
     */
    public ProxyHttpClient(Context context, ConnectManager connectManager) {
        this(context, null, connectManager);
    }
    
    /**
     * ���캯��.
     * @param context application context
     * @param userAgent  useragent you want to set 
     * @param connectManager ConnectManager
     */
    public ProxyHttpClient(Context context, String userAgent, ConnectManager connectManager) {
        
        // ����������� wifi /mobile & apn proxy
        ConnectManager cm = connectManager;
        if (cm == null) {
            cm = new ConnectManager(context);
        }
        
        mUseWap = cm.isWapNetwork();
        mProxy = cm.getProxy();
        mPort = cm.getProxyPort();
        
        if (DEBUG) {
            Log.d(TAG, "wap :" + mUseWap + " " + mProxy + " " + mPort);
        }
        
        if (mUseWap) {
            //���ʹ��wap ���磬��Ҫ���ô���û�еĻ�ֱ��.
            HttpHost proxy = new HttpHost(mProxy, Integer.valueOf(mPort));
            getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
        }

        // Default connection and socket timeout of 30 seconds. 
        HttpConnectionParams.setConnectionTimeout(getParams(), HTTP_TIMEOUT_MS);
        HttpConnectionParams.setSoTimeout(getParams(), HTTP_TIMEOUT_MS);
        HttpConnectionParams.setSocketBufferSize(getParams(), 8192); // SUPPRESS CHECKSTYLE
        
        if (!TextUtils.isEmpty(userAgent)) {
            HttpProtocolParams.setUserAgent(getParams(), userAgent);
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (mLeakedException != null) {
            Log.e(TAG, "Leak found", mLeakedException);
        }
    }
    
    /**
     * Release resources associated with this client.  You must call this,
     * or significant resources (sockets and memory) may be leaked.
     */
    public void close() {
        if (mLeakedException != null) {
            getConnectionManager().shutdown();
            mLeakedException = null;
        }
    }
    
    @Override
    protected HttpParams createHttpParams() {
        HttpParams params = super.createHttpParams();
        // ���� Expect: 100-continue����ͨ uniwap���ز�֧�֣�post���ݷ�������
        HttpProtocolParams.setUseExpectContinue(params, false);
        return params;
    }
}
