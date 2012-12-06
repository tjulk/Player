package com.baidu.browser.framework;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewStub;
import android.webkit.CookieSyncManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.browser.BPBrowser;
import com.baidu.browser.core.util.BdLog;
import com.baidu.browser.explore.BPExploreChromeClient;
import com.baidu.browser.explore.BPExploreView;
import com.baidu.browser.explore.BPExploreViewClient;
import com.baidu.browser.explore.BPExploreViewListener;
import com.baidu.browser.ui.BaseWebView;
import com.baidu.browser.ui.HomeView;
import com.baidu.browser.webpool.BPWebPoolView;
import com.baidu.player.R;
import com.baidu.webkit.sdk.BGeolocationPermissions.BCallback;
import com.baidu.webkit.sdk.BValueCallback;
import com.baidu.webkit.sdk.BWebSettings;
import com.baidu.webkit.sdk.BWebSettings.BPluginState;
import com.baidu.webkit.sdk.BWebView;
import com.baidu.webkit.sdk.BWebView.BHitTestResult;
import com.baidu.webkit.sdk.BWebView.BWebViewTransport;

/**
 * @ClassName: BPWindow 
 * @Description: �ര���е�ÿһ�ÿ������ά���Լ�����Ϣ
 * @author LEIKANG 
 * @date 2012-12-5 ����5:17:08
 */
public class BPWindow extends FrameLayout implements BPExploreViewListener{
	
    /** Log Tag */
    public static final String TAG = "BdWindow";
 
    /**��window position��ϳ�key����������current url��*/
    private static final String CURRENT_URL = "CURRENT_URL";
    
    /**��window position��ϳ�key���������洰�ڱ��⡣*/
    private static final String WINDOW_TITLE = "WINDOW_TITLE";
 
    /**��window position��ϳ�key����������exploreView��״̬��*/
    private static final String EXPLOREVIEW_STATE = "EXPLOREVIEW_STATE";
    
    /**��ҳ��*/
    private HomeView mHomeView;
    
	/** ��BdFrameView **/
	private BPFrameView mFrameView;
    
	/** ��ǰ�ڲ�webview **/
	private BPExploreView mExploreView;
	
	/** stub */
	private ViewStub stub;
	
	/** ��ǰ���ڽ��� **/
	private int mCurrentPageProgerss;
	
	/**
     * ��ǰ���ص�url�������ж��Ƿ�����ҳ��
     * ��ΪgetUrl()���ӳ٣���ʹ�����Ѽ�¼�ķ�ʽ��
     */
	private String mCurrentUrl;
	
    /**��Window���б��е�λ�á�*/
    private int mPos = -1;
    
	/** title **/
	private String mTitle;
	
	/** ExploreView�ϴα����״̬ */
	private Bundle mExploreViewSavedState;
    
    /**
     * @Title: setPosition 
     * @Description: ���ø�Window���б��е�λ�� 
     * @param  pos     
     * @return void     
     * @throws
     */
    public void setPosition(int pos) {
    	mPos = pos;
    }
    
    /**
     * @Title: getPostition 
     * @Description: ��ȡ��window���б��е�λ��
     * @param   
     * @return int     
     * @throws
     */
    public int getPostition() {
    	return mPos;
    }
    
    /**
     * @param context
     */
	public BPWindow(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public BPWindow(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	
	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public BPWindow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mHomeView = HomeView.getInstance(context);

		mExploreView = new BPExploreView(context);
		mExploreView.setEventListener(this);
		mExploreView.setWebViewClient(new BdWindowCustomViewClient());
		mExploreView.setWebChromeClient(new BdWindowCustomChromeClient());
		//TODO ���ص�ǰ�汾��ʱ����
		//mExploreView.setDownloadListener(new BDownloadCustomViewListener());
		stub = new ViewStub(context);
		stub.setLayoutResource(R.layout.browser_geolocation_permissions_prompt);
        FrameLayout.LayoutParams stubLayout = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
		initSettings();
		addView(mExploreView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
		addView(stub, stubLayout);
		LinearLayout.LayoutParams windowLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		windowLayout.weight = 1.0f;
		setLayoutParams(windowLayout);
	}
	
	/**
	 * @Title: saveStateToBundle 
	 * @Description: ����״̬
	 * @param  savedState     
	 * @return void     
	 * @throws
	 */
    public void saveStateToBundle(Bundle savedState) {
        if (savedState == null) {
            return;
        }
        savedState.putString(mPos + CURRENT_URL, mCurrentUrl);
        savedState.putString(mPos + WINDOW_TITLE, mTitle);
        Bundle exploreViewState = new Bundle();
        mExploreView.saveStateToBundle(exploreViewState);
        savedState.putBundle(mPos + EXPLOREVIEW_STATE, exploreViewState);
    }
    
    /**
     * @Title: restoreFromBundle 
     * @Description: �ָ�״̬ 
     * @param  savedState     
     * @return void     
     * @throws
     */
    public void restoreFromBundle(Bundle savedState) {
        if (savedState == null) {
            return;
        }
        mCurrentUrl = savedState.getString(mPos + CURRENT_URL);
        mTitle = savedState.getString(mPos + WINDOW_TITLE);
        mExploreViewSavedState = savedState.getBundle(mPos + EXPLOREVIEW_STATE);
    }
    
    /**
     * @Title: restoreExploreViewState 
     * @Description: �ָ�ExploreView��״̬����Window Resume����ִ�лָ� 
     * @param      
     * @return void     
     * @throws
     */
    private void restoreExploreViewState() {
        if (mExploreViewSavedState != null && mExploreView != null) {
            mExploreView.restoreFromBundle(mExploreViewSavedState);
            mExploreViewSavedState = null;
        }
    }
    
    /**
     * @Title: onPause 
     * @Description: ��ǰ������ͣʱ���ã���ͣ�����
     * @param      
     * @return void     
     * @throws
     */
	protected void onPause() {
		mExploreView.onPause();
	}

	/**
	 * @Title: onResume 
	 * @Description: ��ǰ����resume�������resume 
	 * @param      
	 * @return void     
	 * @throws
	 */
	protected void onResume() {
	    restoreExploreViewState();
	    if (mExploreView != null) {
	        mExploreView.onResume();
	    }
	}
	
	/**
	 * @Title: loadInitailHome 
	 * @Description: ���س�ʼ��ҳ�����Ҳ�ֱ�ӵ��´����л� Frameview createWindow ʱ����
	 * @param      
	 * @return void     
	 * @throws
	 */
	public void loadInitailHome() {
	    loadUrl(BPBrowser.HOME_PAGE);
	}
	
	/**
	 * @Title: loadUrl 
	 * @Description: ����URL������Ϊ�ⲿ����URL��webview �ڲ� js �����д���
	 * @param @param url     
	 * @return void     
	 * @throws
	 */
    public void loadUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            boolean handled = false;
            boolean isHomeUrl = TextUtils.equals(url, BPBrowser.HOME_PAGE);
            if (!isHomeUrl) {
                handled = mFrameView.getBrowser().handleUrl(mExploreView, url);
            }
            if (!handled) {
                mCurrentUrl = url;
                if (isHomeUrl) {
                    showHomeView();
                } else {
                    hideHomeView();
                }
                if (mExploreView != null) {
                    mExploreView.loadUrl(url);
                }
            }
        }
    }

    /**
     * @Title: goBack 
     * @Description: ��ʷ���� 
     * @param      
     * @return void     
     * @throws
     */
	public void goBack() {
		mExploreView.goBack();
		clearTitleBarIfNeed();
	}

	/**
	 * @Title: goForward 
	 * @Description: ��ʷǰ�� 
	 * @param      
	 * @return void     
	 * @throws
	 */
	public void goForward() {
		mExploreView.goForward();
		clearTitleBarIfNeed();
	}
	
	/**
	 * @Title: canGoForward 
	 * @Description: �ж��Ƿ���ǰ��
	 * @param @return     
	 * @return boolean     
	 * @throws
	 */
	public boolean canGoForward() {
		if (mExploreView == null) {
			return false;
		}
		return mExploreView.canGoForward();
	}
	
	/**
	 * @Title: canGoBack 
	 * @Description: �ж��Ƿ��ܺ��� 
	 * @param @return     
	 * @return boolean     
	 * @throws
	 */
	public boolean canGoBack() {
		if (mExploreView == null) {
			return false;
		}
		return mExploreView.canGoBack();
	}
	
	/**
	 * @Title: loadJavaScript 
	 * @Description: ����js����
	 * @param  js     
	 * @return void     
	 * @throws
	 */
    public void loadJavaScript(String js) {
        mExploreView.loadJavaScript(js);
    }
    
    /**
     * @Title: reload 
     * @Description: ����ˢ�½��� 
     * @param      
     * @return void     
     * @throws
     */
	public void reload() {
	    // ���ȡurlʧ�ܣ��ʹ���ʷ�б���ȡ��ǰ��url����������url
	    // ��ʷ�б��߼���webview�ڲ���ʷ�޹�
		if (mExploreView.getUrl() == null) {
		    loadUrl(mExploreView.getCurUrl());
		    return;
		}
		mExploreView.reload();
	}
	
	/**
	 * @Title: stopLoading 
	 * @Description: ֹͣ��ǰwebview���� 
	 * @param      
	 * @return void     
	 * @throws
	 */
	public void stopLoading() {
		mExploreView.stopLoading();
	}

	/**
	 * @Title: clearHistory 
	 * @Description: �����ʷ  
	 * @param      
	 * @return void     
	 * @throws
	 */
	public void clearHistory() {
		mExploreView.clearHistory();
	}
	
	/**
	 * @Title: freeMemory 
	 * @Description: �ͷ��ڴ� 
	 * @param      
	 * @return void     
	 * @throws
	 */
	public void freeMemory() {
		mExploreView.freeMemory();
	}
	
	/**
	 * @Title: getFrameView 
	 * @Description: ���ظ�BPFrameView
	 * @param    
	 * @return BPFrameView  
	 * @throws
	 */
	public BPFrameView getFrameView() {
		return mFrameView;
	}
	
	/**
	 * @Title: setFrameView 
	 * @Description: ���ø�BPFrameView
	 * @param aFrameView   
	 * @return void  
	 * @throws
	 */
	public void setFrameView(BPFrameView aFrameView) {
		this.mFrameView = aFrameView;
	}
	
	/**
	 * @Title: getCurrentPageProgerss 
	 * @Description: ��ȡ��ǰ����
	 * @return int
	 */
	public int getCurrentPageProgerss() {
		return mCurrentPageProgerss;
	}
	
	/**
	 * @Title: setCurrentPageProgerss 
	 * @Description: ���õ�ǰ����
	 * @param  aCurrentPageProgerss   
	 * @return void
	 */
	public void setCurrentPageProgerss(int aCurrentPageProgerss) {
		this.mCurrentPageProgerss = aCurrentPageProgerss;
	}
    
    /**
     * @Title: getUrl 
     * @Description: ��ȡ�����URL
     * @param   
     * @return String     
     * @throws
     */
	public String getUrl() {
		return mExploreView.getUrl();
	}
	
	/**
	 * @Title: getCurrentUrl 
	 * @Description:  ��ȡ��ǰURL 
	 *                =========
	 * @param    
	 * @return String
	 */
	public String getCurrentUrl() {
		return mCurrentUrl;
	}
	
	/**
	 * @Title: setUrl 
	 * @Description: ���õ�ǰURL
	 * @param  aUrl   
	 * @return void
	 */
	public void setCurrentUrl(String aUrl) {
		this.mCurrentUrl = aUrl;
	}
	
	/**
	 * @Title: getTitle 
	 * @Description: ��ȡ��ǰ��ַ����
	 * @param    
	 * @return String
	 */
	public String getTitle() {
	    if (TextUtils.equals(BPBrowser.HOME_PAGE, mCurrentUrl)) {
	        return "SHOUYE";
	    }
	    //�����к�̨��ɱ�����ٻָ�ʱWebView��title�ǿյģ���ʱӦ��ʹ��ɱ��ǰ�����mTitle��
	    String title = mExploreView.getTitle();
	    if (!TextUtils.isEmpty(title)) {
	        return title;
	    }
		return mTitle;
	}
	
	/**
	 * @Title: setTitle 
	 * @Description: ���ñ���
	 * @param  aTitle   
	 * @return void
	 */
	public void setTitle(String aTitle) {
		this.mTitle = aTitle;
	}
	
	/**
	 * @Title: webviewScrollBy 
	 * @Description: ����WebView���� 
	 * @param  x
	 * @param  y   
	 */
	public void webviewScrollBy(int x, int y) {
		mExploreView.scrollBy(x, y);
	}
	
	/**
	 * @Title: webviewScrollTo 
	 * @Description: ����WebView���� //TODO ����scrollBy ��scrollTo 
	 * @param x
	 * @param y   
	 */
	public void webviewScrollTo(int x, int y) {
		mExploreView.scrollTo(x, y);
	}
	
	/**
	 * @Title: getExploreView 
	 * @Description: �����ʾҳ���WebView 
	 * @return BdExploreView
	 */
	public BPExploreView getExploreView() {
	    return mExploreView;
	}
	
	/**
	 * @Title: getHomeView 
	 * @Description: �����ҳHomeView����
	 * @return HomeView
	 */
	public HomeView getHomeView() {
	    return mHomeView;
	}
	
	/**
	 * @Title: release 
	 * @Description: ������ͷ��ڴ�    
	 */
	public void release() {
		if (mExploreView != null) {
			mExploreView.clear();
			mExploreView = null;
		}
	}
    
	/**
	 * @Title: clearTitleBarIfNeed 
	 * @Description: 4.1��ǰ���������titlebar
	 * @param      
	 * @return void     
	 */
	private void clearTitleBarIfNeed() {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            setEmbeddedTitleBar(null);
        }
        mExploreView.requestLayout();
	}
	
	/**
	 * @Title: setEmbeddedTitleBar 
	 * @Description: ����webview headView
	 * @param  aView     
	 * @return void     
	 */
	public void setEmbeddedTitleBar(View aView) {
	    if (mExploreView != null) {
	        mExploreView.setEmbeddedTitleBar(aView);
	    }
	}
 

	/**
     * @Title: showHomeView 
     * @Description: ��ʾHomeView 
     * @param      
     * @return void     
     * @throws
     */
    public void showHomeView() {
        
        ViewParent parent = mHomeView.getParent();
        if (null == parent) {
            addView(mHomeView);
            mHomeView.onResume();
        } else {
            if (this != parent) {
                ((ViewGroup) parent).removeView(mHomeView);
                addView(mHomeView);
                mHomeView.onResume();
            }
        }
        //���¹�������������״̬
        mFrameView.updateState(BPWindow.this);
        requestLayout();
    }
    
    /**
     * @Title: hideHomeView 
     * @Description: �Ƴ�HomeView
     * @param      
     * @return void     
     * @throws
     */
    public void hideHomeView() {
        ViewParent parent = mHomeView.getParent();
        if (this == parent) {
            mHomeView.onPause();
            ((ViewGroup) parent).removeView(mHomeView);
            requestLayout();
        }
    }
	
	/**
	 * @Title: initSettings 
	 * @Description: ��ʼ��webview�ص�settings
	 */
	public void initSettings() {
		if (mExploreView != null) {
			BWebSettings settings = mExploreView.getSettings();
			settings.setLightTouchEnabled(false);
			settings.setNeedInitialFocus(false);
			settings.setJavaScriptEnabled(true);
			settings.setSupportZoom(true);
			settings.setBuiltInZoomControls(true);
			settings.setLoadsImagesAutomatically(true);
			settings.setLoadWithOverviewMode(true);
			settings.setUseWideViewPort(true);
			settings.setGeolocationEnabled(true);
			settings.setDatabaseEnabled(true);
			settings.setDomStorageEnabled(true);
			settings.setAppCacheEnabled(true);
			String databasePath = getContext().getDir(
					BaseWebView.APP_DATABASE_PATH, 0).getPath();
			String geolocationDatabasePath = getContext().getDir(
					BaseWebView.APP_GEO_PATH, 0).getPath();
			String appCachePath = getContext().getDir(
					BaseWebView.APP_CACHE_PATH, 0).getPath();
			settings.setGeolocationDatabasePath(geolocationDatabasePath);
			settings.setDatabasePath(databasePath);
			settings.setAppCachePath(appCachePath);
			// ����򿪶ര��
			settings.setSupportMultipleWindows(true);
			final int pageCacheCapacityHigh = 5;
			final int pageCacheCapacityLow = 1;
			final int defaultMemoryclass = 16;
			ActivityManager am = (ActivityManager) getContext()
					.getSystemService(Context.ACTIVITY_SERVICE);
			// ��Ӧ�õĿ����ڴ����16Mʱ���ö�ҳ������ƣ��ӿ�ǰ�����˵��ٶ�
			if (am.getMemoryClass() > defaultMemoryclass) {
				settings.setPageCacheCapacity(pageCacheCapacityHigh);
			} else {
				settings.setPageCacheCapacity(pageCacheCapacityLow);
			}
			try {
				settings.setPluginState(BPluginState.ON_DEMAND);
			} catch (NoClassDefFoundError e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @Title: onLongPress 
	 * @Description:����Ч����ʱ������
	 */
	@Override
	public void onLongPress(BHitTestResult result) {
		try {
			if (result == null) {
				return;
			}
			int type = result.getType();
			if (type == BHitTestResult.UNKNOWN_TYPE) {
 
			} else if (type == BHitTestResult.IMAGE_TYPE) { 
 
			} else if (type == BHitTestResult.SRC_ANCHOR_TYPE) {
 
			} else if (type == BHitTestResult.SRC_IMAGE_ANCHOR_TYPE) { 
 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onSelectionSearch(String aSelection) {
		mFrameView.onSelectionSearch(aSelection);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mExploreView.onKeyDown(keyCode, event)) {
				mFrameView.updateState(BPWindow.this);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @Title: closeSelectedMenu 
	 * @Description: �رջ����˵�    
	 */
	public void closeSelectedMenu() {
		mExploreView.doSelectionCancel();
	}
	
	/**
	 * @Title: requestFocusNodeHref 
	 * @Description: �������� 
	 * @param msg   
	 */
	public void requestFocusNodeHref(Message msg) {
		mExploreView.requestFocusNodeHref(msg);
	}
	
	/**
	 * @Title: handleUrl 
	 * @Description: ����URL
	 * @param view
	 * @param url
	 * @return boolean
	 */
	public boolean handleUrl(BPWebPoolView view, String url) {
	    // �ϲ�Ӧ���Ƚ��д���
	    boolean handled = mFrameView.getBrowser().handleUrl(view, url);
	    if (handled) {
	        return true;
	    } else {
	    	return false;
	    }
	}
	
	/**
	 * @ClassName: BdWindowCustomViewClient 
	 * @Description: �ڲ� client �ص��� 
	 * @author LEIKANG 
	 * @date 2012-12-5 ����5:58:11
	 */
	class BdWindowCustomViewClient extends BPExploreViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(BPWebPoolView view, String url) {
            mCurrentUrl = url;
            if (TextUtils.equals(url, BPBrowser.HOME_PAGE)) {
                mFrameView.switchBetweenHomeAndBrowser(url);
            }
			setEmbeddedTitleBar(null);
			return handleUrl(view, url);
		}

		@Override
		public void onPageStarted(BPWebPoolView view, String url, Bitmap favicon) {
            //��ҳ�����ҳ�л�
		    final boolean isHomeUrl = TextUtils.equals(url, BPBrowser.HOME_PAGE);
            if (isHomeUrl) {
                mFrameView.switchBetweenHomeAndBrowser(url);
            }
            mCurrentUrl = url;
		    setEmbeddedTitleBar(null);
            mCurrentPageProgerss = BPBrowser.PROGRESS_MIN;
            
			mFrameView.getBrowser().pageStateChanged(BPBrowser.STATE_PAGE_STARTED, url);
			mFrameView.updateState(BPWindow.this);
			
		}
		@Override
		public void onPageFinished(BPWebPoolView view, String url) {
            mCurrentPageProgerss = BPBrowser.PROGRESS_MAX;
			mFrameView.updateState(BPWindow.this);
		}
		@Override
		public void onWebViewChanged(BPWebPoolView view) {
			mExploreView.doSelectionCancel();
		}
	}
	
	/**
	 * @ClassName: BdWindowCustomChromeClient 
	 * @Description: �ڲ� client �ص���
	 * @author LEIKANG 
	 * @date 2012-12-6 ����11:02:08
	 */
	class BdWindowCustomChromeClient extends BPExploreChromeClient {
		@Override
		public void onProgressChanged(BPWebPoolView view, int newProgress) {
			if (newProgress == BPBrowser.PROGRESS_MAX) {
				// sync cookies and cache promptly here.
				CookieSyncManager.getInstance().sync();
				mCurrentPageProgerss = newProgress;
			} else {
				mCurrentPageProgerss = newProgress;
			}
			mFrameView.updateState(BPWindow.this);
		}
		@Override
		public void onReceivedTitle(BPWebPoolView view, String title) {
			if (title != null) {
				mTitle = title;
			}
		}
		@Override
		public boolean onCreateWindow(BPWebPoolView view, boolean dialog, boolean userGesture,
				Message resultMsg, BWebView.BWebViewTransport bpTransport) {
			BdLog.d(dialog + ", " + userGesture);
			// ���ҳ�棬���Ҳ��ǶԻ��������´��ڴ�
			if (!dialog && userGesture) {
				BPWindow newWin = mFrameView.onInnerCreateNewWindow(BPWindow.this);
				if (newWin != null) {
					newWin.setWebViewToTargetForNewWindow(resultMsg, bpTransport);
					return true;
				}
			} else if (dialog && userGesture) {
				// �����Ի���
			}
			return false;
		}
		// For Android 3.0+
		public void openFileChooser(BValueCallback<Uri> uploadMsg, String acceptType) {  
			mFrameView.openFileChooser(uploadMsg, acceptType);
		}
		// For Android < 3.0
		public void openFileChooser(BValueCallback<Uri> uploadMsg) { 
			mFrameView.openFileChooser(uploadMsg, mCurrentUrl);
		}
		@Override
        public void onGeolocationPermissionsHidePrompt() {
 
        }
		@Override
		public void onGeolocationPermissionsShowPrompt(String origin, BCallback callback) {
 
		}
	}

	/**
	 * @Title: isHomePage 
	 * @Description: �жϵ�ǰ�Ƿ���ҳ 
	 * @return   
	 * boolean
	 */
	public boolean isHomePage() {
        if (TextUtils.equals(BPBrowser.HOME_PAGE, mCurrentUrl)) {
            return true;
        }
        return false;
	}

    /**
     * �����¿����ڶ���ʱ����ֱ��loadUrl,�����ڶ������ʱ��load.���⶯�����١�
     * @param resultMsg resultMsg
     * @param bdTransport bdTransport
     */
	public void setWebViewToTargetForNewWindow(Message resultMsg,
			BWebViewTransport bdTransport) {
		//TODO
	}

	/**
	 * @Title: setUpSelect 
	 * @Description: ����ѡ��ģʽ   
	 */
	public void setUpSelect() {
		mExploreView.emulateShiftHeld();
		Toast.makeText(getContext(), R.string.text_selection_tip, 0).show();
	}

}
