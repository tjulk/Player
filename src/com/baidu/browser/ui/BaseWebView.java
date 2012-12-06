package com.baidu.browser.ui;

import java.net.URISyntaxException;
import java.net.URLDecoder;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import com.baidu.webkit.sdk.BSslError;
import com.baidu.webkit.sdk.BSslErrorHandler;
import com.baidu.webkit.sdk.BWebSettings;
import com.baidu.webkit.sdk.BWebView;
import com.baidu.webkit.sdk.BWebViewClient;

/**
 * һ�����������е� webview ����Ҫ�̳д��࣬���ߵ��� setSupportHtml5() �����������õ���webview����Ҫ֧��html5.
 * ��Ϊ����в���û��֧��html5�Ļ�������� localstorageʧ�ܵ����⡣
 * 
 * ����Ϊ��������̱�ɱ������������֮ǰ�洢��locaostorage��ȡʧ�ܡ�
 * 
 * ��Ҫ��������в�Ҫ��Ӻ�ҵ���߼���صĶ�����
 * 
 * @author LEIKANG
 * @since 2012-12-04
 */
public class BaseWebView extends BWebView {
    
    /** ����Ŀ¼�� */
    public static final String APP_CACHE_PATH = "appcache";
    /** ���ݿ�Ŀ¼�� */
    public static final String APP_DATABASE_PATH = "databases";
    /** ����λ�ö�λ��ϢĿ¼�� */
    public static final String APP_GEO_PATH = "geolocation";

    /**
     * constructor.
     * @param context Context
     * @param attrs AttributeSet
     * @param defStyle int
     */
    public BaseWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        init(context);
    }

    /**
     * constructor.
     * 
     * @param context Context
     * @param attrs AttributeSet
     */
    public BaseWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        init(context);
    }

    /**
     * constructor. 
     * @param context Context
     */
    public BaseWebView(Context context) {
        super(context);
        
        init(context);
    }
    
    /**
     * init 
     * @param context Context
     */
    private void init(Context context) {
        setDefaultWebSettings(context, this);
        setSupportHtml5(context, this);
        removeInitialFocus(context, this);
        setWebViewClient(new BaseWebViewClient());
    }
    
    /**
     * ȥ����ʼ�Ľ��㣬��������Ҵܵ�����
     * @param context Context
     * @param webView WebView
     */
    public static void removeInitialFocus(Context context, BWebView webView) {
        BWebSettings s = webView.getSettings();
        s.setNeedInitialFocus(false);
    }

    /**
     * ����֧�� html 5
     * 
     * һ�����������е� webview������Ҫ���ô˺��������߼��ɴ��ࡣ�����õ���webview����Ҫ֧��html5.
     * ��Ϊ����в���û��֧��html5�Ļ�������� localstorageʧ�ܵ����⡣
     * 
     * @param context Context
     * @param webview WebView
     */
    public static void setSupportHtml5(Context context, BWebView webview) {
        BWebSettings s = webview.getSettings();
        
        s.setAppCacheEnabled(true);
        s.setDatabaseEnabled(true);
        s.setDomStorageEnabled(true);
        s.setGeolocationEnabled(true);
        
        String databasePath = context.getDir(APP_DATABASE_PATH, 0).getPath();
        String geolocationDatabasePath = context.getDir(APP_GEO_PATH, 0).getPath();
        String appCachePath = context.getDir(APP_CACHE_PATH, 0).getPath();
        s.setGeolocationDatabasePath(geolocationDatabasePath);
        s.setDatabasePath(databasePath);
        // settings.setAppCacheMaxSize(appCacheMaxSize);
        s.setAppCachePath(appCachePath);
    }
    
    /**
     * ����Ĭ�ϵ�WebSettings֧��
     * 
     * һ���Ե�WebView�����ã�����js֧�֣�localstorage֧�ֵ�
     * 
     * @param context Context
     * @param webview WebView
     */
    public static void setDefaultWebSettings(Context context, BWebView webview) {
        BWebSettings s = webview.getSettings();
        // ����js����
        s.setJavaScriptEnabled(true);
        // ����js localstorage ����
        s.setDomStorageEnabled(true);
    }
    
    /**
     * ��������Э�飬���粦�绰�������ŵ�
     * @param context context
     * @param url 
     * @return �Ƿ���
     * @throws ActivityNotStartedException ����Activity����
     */
    public static boolean handleSpecialScheme(Context context, String url) throws ActivityNotStartedException {
        if (url.startsWith("wtai://")) {
            // ����wap�绰����
            int start = "wtai://wp/wc;".length();
            if (url.length() > start) {
                String tel = url.substring(start);
                startDialer(context, tel);
                
                return true;
            }
        } else if (url.startsWith("sms:") || url.startsWith("smsto:")) {
            // ���ֻ��͵Ķ��Ŵ��������⣬�Լ�����
            sendSms(context, url);
            return true;
            
        } else if (startActivityFromUrl(context, url)) {
            // bugfix#SEARHBOX-907
            return true;
        }
        
        return false;
    }
    
    /**
     * ����Dialer
     * 
     * @param phoneNumber
     *            �绰����
     * @throws ActivityNotStartedException ����Activity����
     */
    private static void startDialer(Context context, String phoneNumber) throws ActivityNotStartedException {
        phoneNumber = "tel:" + phoneNumber;
        Intent dialerIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumber));
        dialerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(context, dialerIntent);
    }
    
    /**
     * ������Ϣ��
     * @param activity Activity
     * @param url ��ʽΪ sms:xxxxx?body= ��URL
     * @throws ActivityNotStartedException ����Activity�쳣
     * */
    private static void sendSms(Context context, String url) throws ActivityNotStartedException {
        try {
            String tel = null, body = null;
            if (url.startsWith("sms:")) {
                int index = url.indexOf('?');
                if (index < "sms:?".length()) {
                    tel = url.substring("sms:".length());
                    body = "";
                    
                } else {
                    tel = url.substring("sms:".length(), index);
                    index = url.indexOf("body=");
                    if (index > -1) {
                        body = url.substring(index + "body=".length());
                        if (!TextUtils.isEmpty(body)) {
                            body = URLDecoder.decode(body, "UTF-8");
                        }
                    }
                }
                
            } else if (url.startsWith("smsto:")) {
                int index = url.indexOf('?');
                if (index < "smsto:?".length()) {
                    tel = url.substring("smsto:".length());
                    body = "";
                    
                } else {
                    tel = url.substring("smsto:".length(), index);
                    index = url.indexOf("body=");
                    if (index > -1) {
                        body = url.substring(index + "body=".length());
                        if (!TextUtils.isEmpty(body)) {
                            body = URLDecoder.decode(body, "UTF-8");
                        }
                    }
                }
            }
            
            Intent it = new Intent(Intent.ACTION_VIEW);
            it.putExtra("address", tel);
            it.putExtra("sms_body", body);
            it.setType("vnd.android-dir/mms-sms");
            startActivity(context, it);
            
        } catch (ActivityNotStartedException e) {
            throw e;
            
        } catch (Throwable t) {
            t.printStackTrace();
        }   
    }
    
    /**
     * ��ȫ����Ӧ�ó��򣬽ػ�Exception
     * 
     * @param activity
     *            Context
     * @param intent
     *            Intent
     * @throws ActivityNotStartedException ����Activity����
     * */
    private static void startActivity(Context context, Intent intent) throws ActivityNotStartedException {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            ActivityNotStartedException ae = new ActivityNotStartedException();
            ae.initCause(e);
            throw ae;
        } catch (SecurityException e) {
            ActivityNotStartedException ae = new ActivityNotStartedException();
            ae.initCause(e);
            throw ae;
        }
    }
    
    /**
     * ͨ��url��Ӧ�ó��򣬴������� gel:xx, ���� intent: xxx ��url. tel:, sms:, mailto
     * 
     * @param url
     *            Ҫ�򿪵�url
     * @return �Ƿ��ܹ�����
     * @throws ActivityNotStartedException 
     */
    private static boolean startActivityFromUrl(Context context, String url) throws ActivityNotStartedException {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        
        // http https�Լ����������ñ��˴�������Ҳ�и��׶ˣ�����ע�������intent���޷��õ�����
        if (url.startsWith("http:") || url.startsWith("https:")) {
            return false;
        }
        
        Intent intent;
        // perform generic parsing of the URI to turn it into an Intent.
        try {
            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
        } catch (URISyntaxException ex) {
            Log.w("Browser", "Bad URI " + url + ": " + ex.getMessage());
            return false;
        }
        
        // whether we can download it from the Market.
        ResolveInfo ri = context.getPackageManager().resolveActivity(intent, 0);
        if (ri == null) {
            String packagename = intent.getPackage();
            if (packagename != null) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pname:"
                        + packagename));
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                // modify begin by caohaitao 20120727 , ��� ����û��market������crash.
                try {
                    context.startActivity(intent);
                    return true;
                } catch (ActivityNotFoundException ex) {
                    ActivityNotStartedException ae = new ActivityNotStartedException();
                    ae.initCause(ex);
                    throw ae;
                }
                // modify end by caohaitao
                
            } else {
                return false;
            }
        }
        
        // sanitize the Intent, ensuring web pages can not bypass browser
        // security (only access to BROWSABLE activities).
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setComponent(null);
        try {
            if (context instanceof Activity) {
                Activity act = (Activity) context;
                if (act.startActivityIfNeeded(intent, -1)) {
                    return true;
                }
            }
        } catch (ActivityNotFoundException ex) {
            ActivityNotStartedException ae = new ActivityNotStartedException();
            ae.initCause(ex);
            throw ae;
            // ignore the error. If no application can handle the URL,
            // eg about:blank, assume the browser can handle it.
        }
        
        return false;
    }
    
    /**
     * @param client �贫��{@link BaseWebViewClient}�Ķ���
     */
    @Override
    public void setWebViewClient(BWebViewClient client) {
        
        // check
        if (client == null || !(client instanceof BaseWebViewClient)) {
            throw new RuntimeException("WebViewClient must be extended from BaseWebViewClient!!");
        }
        
        super.setWebViewClient(client);
    }
    
    /**
     * Ĭ�ϵ�WebViewClientʵ�ֻ��࣬Ĭ�϶�https����ķ�����SSL֤���ȡ���εĴ���
     */
    public static class BaseWebViewClient extends BWebViewClient {
        
        /**
         * Ĭ������ssl֤��
         */
        @Override
        public void onReceivedSslError(BWebView view, BSslErrorHandler handler, BSslError error) {
            handler.proceed();
        }
        
        /**
         * Ĭ����ӶԲ��绰�������ŵ�����Э���֧��
         */
        @Override
        public boolean shouldOverrideUrlLoading(BWebView view, String url) {
            try {
                return BaseWebView.handleSpecialScheme(view.getContext(), url);
            } catch (ActivityNotStartedException e) {
                // ����webviewĬ�ϴ���
                return false;
            }
        }
        
    }
    
    /**
     * ����Activity�쳣
     */
    public static class ActivityNotStartedException extends Exception {
        /** serialVersionUID */
        private static final long serialVersionUID = 1L;
        /**
         * ����Activity�쳣
         */
        public ActivityNotStartedException() {
            super();
        }
    }
}
