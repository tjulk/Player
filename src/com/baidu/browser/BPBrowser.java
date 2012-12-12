package com.baidu.browser;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.browser.framework.BPFrameView;
import com.baidu.browser.framework.BPWindow;
import com.baidu.browser.webpool.BPWebPoolView;
import com.baidu.player.R;
import com.baidu.webkit.sdk.BValueCallback;

/**
 * @ClassName: BPBrowser 
 * @Description: 浏览模块 该类负责桥接 MainActivity 与 BPFrameview ,UI线程需从该函数实现里调用 BPFrameview  
 * @author LEIKANG 
 * @date 2012-12-5 下午3:10:14
 */
public class BPBrowser extends Fragment{
	
	/**Fragment tag.*/
    public static final String FRAGMENT_TAG = "BPBrowser";
    
    /** 首页URL.*/
    public static final String HOME_PAGE = "http://m.iqiyi.com";
    
	/** 进度条最小值。 */
	public static final int PROGRESS_MIN = 10;

	/** 进度条最大值。 */
	public static final int PROGRESS_MAX = 100;
	
	/** 页面加载开始 **/
	public static final int STATE_PAGE_STARTED = 0x01;

	/** 页面加载完成 **/
	public static final int STATE_PAGE_FINISHED = 0x02;

	/** 页面加载进度中 **/
	public static final int STATE_PROGRESS_CHANGED = 0x03;
	
	/** 根视图 **/
	private BPFrameView mFrameView;
	
	/** 监听类 **/
	private BrowserListener mListener;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initInflate();
		mFrameView.restoreFromBundle(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    mFrameView.saveStateToBundle(outState);
	    super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        
		initInflate();
		if (mFrameView != null) {
			mFrameView.onResume();
		}
	}
	
	@Override
	public void onPause() {
		if (mFrameView != null) {
			mFrameView.closeSelectedMenu();
			mFrameView.onPause();
		}
        super.onPause();
	}
	
	public void onDestroy() {
		freeMemory();
		if (mFrameView != null) {
			mFrameView.release();
			mFrameView = null;
			//TODO browser 销毁时事件处理
		}
		super.onDestroy();
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		if (mFrameView != null) {
			mFrameView.freeMemory();
		}
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    View view = getView();
	    if (view == null) {
	        view = getRootView();
	        ViewGroup parent = (ViewGroup) view.getParent();
	        if (parent != null) {
	            parent.removeView(view);
	        }
	    }
	    return view;
	}

	/**
	 * @Title: scrollBy 
	 * @Description: 控制页面滚动
	 * @param x
	 * @param y   
	 */
	public void scrollBy(int x, int y) {
		initInflate();
		mFrameView.webviewScrollBy(x, y);
	}
	
	/**
	 * @Title: scrollTo 
	 * @Description: 控制页面滚动 
	 * @param x
	 * @param y   
	 */
	public void scrollTo(int x, int y) {
		initInflate();
		mFrameView.webviewScrollTo(x, y);
	}
	
	/**
	 * @Title: addWebViewTitle 
	 * @Description: 加入webview headView 
	 * @param aView   
	 */
	public void addWebViewTitle(View aView) {
		initInflate();
		mFrameView.addWebViewTitle(aView);
	}
	
	/**
	 * @Title: setmListener 
	 * @Description: 注册当前监听
	 * @param aListener   
	 */
	public void setmListener(BrowserListener aListener) {
		this.mListener = aListener;
	}
	
	/**
	 * @Title: initInflate 
	 * @Description: 布局解码 
	 * @param     设定文件 
	 * @return void    返回类型 
	 * @throws
	 */
	public void initInflate() {
		if (mFrameView == null) {
		    Activity activity = getActivity();
		    mFrameView = (BPFrameView) activity.findViewById(R.id.bdframeview_id);
		    if (mFrameView == null) {
		        mFrameView = new BPFrameView(activity);
		        mFrameView.setId(R.id.bdframeview_id);
		        mFrameView.setBrowser(this);
		    }
		}
	}
	
	/**
	 * @Title: freeMemory 
	 * @Description: 释放内存  
	 * @param     设定文件 
	 * @return void    返回类型 
	 * @throws
	 */
	public void freeMemory() {
		if (mFrameView != null) {
			mFrameView.freeMemory();
		}
	}
	
	/**
	 * 
	 * @Title: onKeyDown 
	 * @Description: 拦截Activity的onKeyDown.
	 * @param  keyCode
	 * @param  event
	 * @param  设定文件 
	 * @return boolean   如果需要继续交给Activity处理，return false，否则return true.
	 * @throws
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mFrameView != null) {
			if (mFrameView.onKeyDown(keyCode, event)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @Title: onKeyUp 
	 * @Description: 拦截Activity的onKeyUp.
	 * @param  keyCode
	 * @param  event
	 * @param  设定文件 
	 * @return boolean 如果需要继续交给Activity处理，return false，否则return true.
	 * @throws
	 */
	public boolean onKeyUp(int keyCode, KeyEvent event) {
	    if (mFrameView != null) {
            if (mFrameView.onKeyUp(keyCode, event)) {
                return true;
            }
        }
	    return false;
	}
 
	/**
	 * @Title: goBack 
	 * @Description: 历史后退 
	 * @param     设定文件 
	 * @return void    返回类型 
	 * @throws
	 */
	public void goBack() {
		initInflate();
		mFrameView.goBack();
	}
    
	/**
	 * @Title: goForward 
	 * @Description: 历史前进 
	 * @param     设定文件 
	 * @return void    返回类型 
	 * @throws
	 */
	public void goForward() {
		initInflate();
		mFrameView.goForward();
	}
	
	/**
	 * @Title: canGoForward 
	 * @Description: 判断是否可以前进 
	 * @param  设定文件 
	 * @return boolean    返回类型 
	 * @throws
	 */
	public boolean canGoForward() {
		initInflate();
		return mFrameView.canGoForward();
	}
	
	/**
	 * @Title: canGoBack 
	 * @Description: 判断是否可以后退 
	 * @param  设定文件 
	 * @return boolean    返回类型 
	 * @throws
	 */
	public boolean canGoBack() {
		initInflate();
		return mFrameView.canGoBack();
	}
	
	/**
	 * @Title: loadUrl 
	 * @Description: 加载网址，浏览器所有外部浏览行为必须使用该函数调用
	 *               ======================================== 
	 * @param  url  设定文件 
	 * @return void 返回类型 
	 * @throws
	 */
	public void loadUrl(String url) {
		initInflate();
		mFrameView.loadUrl(url);
	}
	
	/**
	 * @Title: loadUrlFromHome 
	 * @Description:从首页打开一个URL
	 * @param  url
	 * @param  isOpenBackground 是否后台打开
	 * @return void    返回类型 
	 * @throws
	 */
	public void loadUrlFromHome(String url, boolean isOpenBackground) {
		initInflate();
		
		BPWindow current = mFrameView.getCurrentWindow();
        if (!isOpenBackground) {
            if (current != null) {
                current.loadUrl(url);
            }
        } else {
            mFrameView.createNewWindowOpenUrl(url, current, false, null);
        }
	}
	
	/**
	 * @Title: reload 
	 * @Description: 重新加载页面  
	 * @param     设定文件 
	 * @return void    返回类型 
	 * @throws
	 */
	public void reload() {
		initInflate();
		mFrameView.reload();
	}
	
	/**
	 * @Title: stopLoading 
	 * @Description: 停止当前webview加载 
	 * @param     设定文件 
	 * @return void    返回类型 
	 * @throws
	 */
	public void stopLoading() {
		initInflate();
		mFrameView.stopLoading();
	}
	
	/**
	 * @Title: clearHistory 
	 * @Description: 清除历史
	 * @param     设定文件 
	 * @return void    返回类型 
	 * @throws
	 */
	public void clearHistory() {
		if (mFrameView != null) {
			mFrameView.clearHistory();
		}
	}
	
	/**
	 * @Title: getWindowList 
	 * @Description: 获得窗口列表
	 * @return List<BdWindow>
	 */
	public List<BPWindow> getWindowList() {
	    if (mFrameView != null) {
	        return mFrameView.getWindowList();
	    }
	    return null;
	}
	
	/**
	 * @Title: getRootView 
	 * @Description: 获取browser显示视图
	 * @return View
	 */
	public View getRootView() {
		initInflate();
		return mFrameView;
	}
	
	/**
	 * @Title: getCurrentWindow 
	 * @Description: 获得当前窗口 
	 * @return BPWindow
	 */
    public BPWindow getCurrentWindow() {
        initInflate();
        return mFrameView.getCurrentWindow();
    }
	
	/**
	 * @Title: setUpSelect 
	 * @Description: 进入选字模式/划词搜索 
	 * @param     设定文件 
	 * @return void    返回类型 
	 * @throws
	 */
	public void setUpSelect() {
		mFrameView.setUpSelect();
		//TODO 当前版本暂不实现该功能
	}

	public boolean handleUrl(BPWebPoolView view, String url) {
		return false;
	}
 
	/**
	 * @Title: pageStateChanged 
	 * @Description: 通知外部页面状态发生改变
	 * @param statePageStarted 状态掩码
	 * @param url 传递值
	 */
	public void pageStateChanged(int statePageStarted, String url) {
		if (mListener != null) {
			mListener.onBrowserStateChanged(statePageStarted, url);
		}		
	}
	
	/**
	 * @Title: onGoHome 
	 * @Description: 返回首页    
	 */
	public void onGoHome() {
		if (mListener != null) {
			mListener.onGoHome();
		}
	}
	
	/**
	 * @Title: onAddAsBookmark 
	 * @Description: 添加书签
	 * @param title
	 * @param url   
	 */
    public void onAddAsBookmark(String title, String url) {
        if (mListener != null) {
            mListener.onAddAsBookmark(title, url);
        }
    }
    
    /**
     * @Title: onDownloadStart 
     * @Description: 通知下载
     * @param url
     * @param userAgent
     * @param contentDisposition
     * @param mimetype
     * @param contentLength   
     */
	public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
			long contentLength) {
		if (mListener != null) {
			mListener.onDownloadStart(url, userAgent, contentDisposition, mimetype, contentLength);
		}
	}
	
	/**
	 * @Title: onDownloadStartNoStream 
	 * @Description: 下载
	 * @param url
	 * @param userAgent
	 * @param contentDisposition
	 * @param mimetype
	 * @param contentLength   
	 */
	public void onDownloadStartNoStream(String url, String userAgent, String contentDisposition,
			String mimetype, long contentLength) {
		if (mListener != null) {
			mListener.onDownloadStartNoStream(url, userAgent, contentDisposition, mimetype, contentLength);
		}
	}
	
	/**
	 * @Title: onSelectionSearch 
	 * @Description: 划词回调搜索 ** 以备以后版本搜索电影功能
	 * @param aSelection   
	 */
	public void onSelectionSearch(String aSelection) {
		if (mListener != null) {
			mListener.onSelectionSearch(aSelection);
		}
	}

	public void openFileChooser(BValueCallback<Uri> uploadMsg, String acceptType) {
		if (mListener != null) {
			mListener.openFileChooser(uploadMsg, acceptType);
		}
	}
	
	public void openFileChooser(BValueCallback<Uri> uploadMsg) { // SUPPRESS CHECKSTYLE
		if (mListener != null) {
			mListener.openFileChooser(uploadMsg);
		}
	}
	
	/**
	 * @ClassName: BrowserListener 
	 * @Description: 监听回调接口,用于向外发送事件
	 * @author LEIKANG 
	 * @date 2012-12-6 下午5:53:14
	 */
	public interface BrowserListener {

		/** 通知返回首页 */
		void onGoHome();

		/** 添加当前的浏览页为书签 */
		void onAddAsBookmark(String title, String url);

		/** 通知进入多窗口切换 */
		void onSwitchToMultiWinodow();

		/** 通知分享 */
		void onOpenFromBrowser(String aTitle, String aUrl);

		/** 浏览器状态改变 */
		void onBrowserStateChanged(int stateMask, Object newValue);

		/** 启动语音搜索 */
		void onClickVoiceSearch();

		/** 通知下载 */
		void onDownloadStart(String url, String userAgent,
				String contentDisposition, String mimetype, long contentLength);

		/** 通知下载 */
		void onDownloadStartNoStream(String url, String userAgent,
				String contentDisposition, String mimetype, long contentLength);

		/** 划词搜索 */
		void onSelectionSearch(String aSelection);

		/** 当弹出menu的书签选项被选中 */
		void onSelectBookmarkPopMenu(String title, String url);

		/** 搜索协议 */
		void onProtocolSearch(String aSelection);

		/** 获得当前搜索框属性 */
		Bundle getSearchboxBundle(boolean withKeyword);

		/** 通知tab变动结束 */
		Bundle onTabChangeFinished(Bundle aBundle);

		// For Android 3.0+
		void openFileChooser(BValueCallback<Uri> uploadMsg, String acceptType); // SUPPRESS

		// For Android < 3.0
		void openFileChooser(BValueCallback<Uri> uploadMsg); // SUPPRESS

		Message onRequestCopyHref();  

		void onDismissPopMenu();
	}

	public void initFromIntent(Intent intent) {
		// TODO Auto-generated method stub
		
	}
    
}
