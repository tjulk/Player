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
 * @Description: 多窗口中的每一项，每个窗口维护自己的信息
 * @author LEIKANG 
 * @date 2012-12-5 下午5:17:08
 */
public class BPWindow extends FrameLayout implements BPExploreViewListener{
	
    /** Log Tag */
    public static final String TAG = "BdWindow";
 
    /**与window position组合成key，用来保存current url。*/
    private static final String CURRENT_URL = "CURRENT_URL";
    
    /**与window position组合成key，用来保存窗口标题。*/
    private static final String WINDOW_TITLE = "WINDOW_TITLE";
 
    /**与window position组合成key，用来保存exploreView的状态。*/
    private static final String EXPLOREVIEW_STATE = "EXPLOREVIEW_STATE";
    
    /**首页。*/
    private HomeView mHomeView;
    
	/** 父BdFrameView **/
	private BPFrameView mFrameView;
    
	/** 当前内部webview **/
	private BPExploreView mExploreView;
	
	/** stub */
	private ViewStub stub;
	
	/** 当前窗口进度 **/
	private int mCurrentPageProgerss;
	
	/**
     * 当前加载的url，用于判断是否在首页。
     * 因为getUrl()有延迟，故使用自已记录的方式。
     */
	private String mCurrentUrl;
	
    /**该Window在列表中的位置。*/
    private int mPos = -1;
    
	/** title **/
	private String mTitle;
	
	/** ExploreView上次保存的状态 */
	private Bundle mExploreViewSavedState;
    
    /**
     * @Title: setPosition 
     * @Description: 设置该Window在列表中的位置 
     * @param  pos     
     * @return void     
     * @throws
     */
    public void setPosition(int pos) {
    	mPos = pos;
    }
    
    /**
     * @Title: getPostition 
     * @Description: 获取该window在列表中的位置
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
		//TODO 下载当前版本暂时不做
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
	 * @Description: 保存状态
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
     * @Description: 恢复状态 
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
     * @Description: 恢复ExploreView的状态。在Window Resume后再执行恢复 
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
     * @Description: 当前窗口暂停时调用，暂停浏览器
     * @param      
     * @return void     
     * @throws
     */
	protected void onPause() {
		mExploreView.onPause();
	}

	/**
	 * @Title: onResume 
	 * @Description: 当前窗口resume，浏览器resume 
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
	 * @Description: 加载初始主页，并且不直接导致窗口切换 Frameview createWindow 时调用
	 * @param      
	 * @return void     
	 * @throws
	 */
	public void loadInitailHome() {
	    loadUrl(BPBrowser.HOME_PAGE);
	}
	
	/**
	 * @Title: loadUrl 
	 * @Description: 加载URL，这里为外部加载URL，webview 内部 js 不进行处理
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
     * @Description: 历史后退 
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
	 * @Description: 历史前期 
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
	 * @Description: 判断是否能前进
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
	 * @Description: 判断是否能后退 
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
	 * @Description: 加载js代码
	 * @param  js     
	 * @return void     
	 * @throws
	 */
    public void loadJavaScript(String js) {
        mExploreView.loadJavaScript(js);
    }
    
    /**
     * @Title: reload 
     * @Description: 重新刷新界面 
     * @param      
     * @return void     
     * @throws
     */
	public void reload() {
	    // 如果取url失败，就从历史列表中取当前的url，重新载入url
	    // 历史列表逻辑与webview内部历史无关
		if (mExploreView.getUrl() == null) {
		    loadUrl(mExploreView.getCurUrl());
		    return;
		}
		mExploreView.reload();
	}
	
	/**
	 * @Title: stopLoading 
	 * @Description: 停止当前webview加载 
	 * @param      
	 * @return void     
	 * @throws
	 */
	public void stopLoading() {
		mExploreView.stopLoading();
	}

	/**
	 * @Title: clearHistory 
	 * @Description: 清除历史  
	 * @param      
	 * @return void     
	 * @throws
	 */
	public void clearHistory() {
		mExploreView.clearHistory();
	}
	
	/**
	 * @Title: freeMemory 
	 * @Description: 释放内存 
	 * @param      
	 * @return void     
	 * @throws
	 */
	public void freeMemory() {
		mExploreView.freeMemory();
	}
	
	/**
	 * @Title: getFrameView 
	 * @Description: 返回父BPFrameView
	 * @param    
	 * @return BPFrameView  
	 * @throws
	 */
	public BPFrameView getFrameView() {
		return mFrameView;
	}
	
	/**
	 * @Title: setFrameView 
	 * @Description: 设置父BPFrameView
	 * @param aFrameView   
	 * @return void  
	 * @throws
	 */
	public void setFrameView(BPFrameView aFrameView) {
		this.mFrameView = aFrameView;
	}
	
	/**
	 * @Title: getCurrentPageProgerss 
	 * @Description: 获取当前进度
	 * @return int
	 */
	public int getCurrentPageProgerss() {
		return mCurrentPageProgerss;
	}
	
	/**
	 * @Title: setCurrentPageProgerss 
	 * @Description: 设置当前进度
	 * @param  aCurrentPageProgerss   
	 * @return void
	 */
	public void setCurrentPageProgerss(int aCurrentPageProgerss) {
		this.mCurrentPageProgerss = aCurrentPageProgerss;
	}
    
    /**
     * @Title: getUrl 
     * @Description: 获取浏览器URL
     * @param   
     * @return String     
     * @throws
     */
	public String getUrl() {
		return mExploreView.getUrl();
	}
	
	/**
	 * @Title: getCurrentUrl 
	 * @Description:  获取当前URL 
	 *                =========
	 * @param    
	 * @return String
	 */
	public String getCurrentUrl() {
		return mCurrentUrl;
	}
	
	/**
	 * @Title: setUrl 
	 * @Description: 设置当前URL
	 * @param  aUrl   
	 * @return void
	 */
	public void setCurrentUrl(String aUrl) {
		this.mCurrentUrl = aUrl;
	}
	
	/**
	 * @Title: getTitle 
	 * @Description: 获取当前网址标题
	 * @param    
	 * @return String
	 */
	public String getTitle() {
	    if (TextUtils.equals(BPBrowser.HOME_PAGE, mCurrentUrl)) {
	        return "SHOUYE";
	    }
	    //进程切后台被杀死，再恢复时WebView的title是空的，此时应该使用杀死前保存的mTitle。
	    String title = mExploreView.getTitle();
	    if (!TextUtils.isEmpty(title)) {
	        return title;
	    }
		return mTitle;
	}
	
	/**
	 * @Title: setTitle 
	 * @Description: 设置标题
	 * @param  aTitle   
	 * @return void
	 */
	public void setTitle(String aTitle) {
		this.mTitle = aTitle;
	}
	
	/**
	 * @Title: webviewScrollBy 
	 * @Description: 控制WebView滚动 
	 * @param  x
	 * @param  y   
	 */
	public void webviewScrollBy(int x, int y) {
		mExploreView.scrollBy(x, y);
	}
	
	/**
	 * @Title: webviewScrollTo 
	 * @Description: 控制WebView滚动 //TODO 区分scrollBy 和scrollTo 
	 * @param x
	 * @param y   
	 */
	public void webviewScrollTo(int x, int y) {
		mExploreView.scrollTo(x, y);
	}
	
	/**
	 * @Title: getExploreView 
	 * @Description: 获得显示页面的WebView 
	 * @return BdExploreView
	 */
	public BPExploreView getExploreView() {
	    return mExploreView;
	}
	
	/**
	 * @Title: getHomeView 
	 * @Description: 获得主页HomeView引用
	 * @return HomeView
	 */
	public HomeView getHomeView() {
	    return mHomeView;
	}
	
	/**
	 * @Title: release 
	 * @Description: 浏览器释放内存    
	 */
	public void release() {
		if (mExploreView != null) {
			mExploreView.clear();
			mExploreView = null;
		}
	}
    
	/**
	 * @Title: clearTitleBarIfNeed 
	 * @Description: 4.1，前进后退清除titlebar
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
	 * @Description: 加入webview headView
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
     * @Description: 显示HomeView 
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
        //更新工具栏及进度条状态
        mFrameView.updateState(BPWindow.this);
        requestLayout();
    }
    
    /**
     * @Title: hideHomeView 
     * @Description: 移除HomeView
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
	 * @Description: 初始化webview池的settings
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
			// 允许打开多窗口
			settings.setSupportMultipleWindows(true);
			final int pageCacheCapacityHigh = 5;
			final int pageCacheCapacityLow = 1;
			final int defaultMemoryclass = 16;
			ActivityManager am = (ActivityManager) getContext()
					.getSystemService(Context.ACTIVITY_SERVICE);
			// 当应用的可用内存大于16M时启用多页缓存机制，加快前进后退的速度
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
	 * @Description:长按效果暂时不处理
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
	 * @Description: 关闭划屏菜单    
	 */
	public void closeSelectedMenu() {
		mExploreView.doSelectionCancel();
	}
	
	/**
	 * @Title: requestFocusNodeHref 
	 * @Description: 请求链接 
	 * @param msg   
	 */
	public void requestFocusNodeHref(Message msg) {
		mExploreView.requestFocusNodeHref(msg);
	}
	
	/**
	 * @Title: handleUrl 
	 * @Description: 处理URL
	 * @param view
	 * @param url
	 * @return boolean
	 */
	public boolean handleUrl(BPWebPoolView view, String url) {
	    // 上层应用先进行处理。
	    boolean handled = mFrameView.getBrowser().handleUrl(view, url);
	    if (handled) {
	        return true;
	    } else {
	    	return false;
	    }
	}
	
	/**
	 * @ClassName: BdWindowCustomViewClient 
	 * @Description: 内部 client 回调类 
	 * @author LEIKANG 
	 * @date 2012-12-5 下午5:58:11
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
            //首页与浏览页切换
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
	 * @Description: 内部 client 回调类
	 * @author LEIKANG 
	 * @date 2012-12-6 上午11:02:08
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
			// 点击页面，并且不是对话框，则用新窗口打开
			if (!dialog && userGesture) {
				BPWindow newWin = mFrameView.onInnerCreateNewWindow(BPWindow.this);
				if (newWin != null) {
					newWin.setWebViewToTargetForNewWindow(resultMsg, bpTransport);
					return true;
				}
			} else if (dialog && userGesture) {
				// 创建对话框
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
	 * @Description: 判断当前是否首页 
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
     * 在有新开窗口动画时，不直接loadUrl,而是在动画完成时才load.以免动画卡顿。
     * @param resultMsg resultMsg
     * @param bdTransport bdTransport
     */
	public void setWebViewToTargetForNewWindow(Message resultMsg,
			BWebViewTransport bdTransport) {
		//TODO
	}

	/**
	 * @Title: setUpSelect 
	 * @Description: 进入选词模式   
	 */
	public void setUpSelect() {
		mExploreView.emulateShiftHeld();
		Toast.makeText(getContext(), R.string.text_selection_tip, 0).show();
	}

}
