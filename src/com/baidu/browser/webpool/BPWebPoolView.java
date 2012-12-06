package com.baidu.browser.webpool;

import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.baidu.browser.core.util.BdLog;
import com.baidu.browser.ui.BaseWebView;
import com.baidu.browser.ui.BaseWebView.ActivityNotStartedException;
import com.baidu.browser.webpool.BPErrorView.BPErrorViewListener;
import com.baidu.browser.webpool.BPWebPoolCustomView.LoadMode;
import com.baidu.player.R;
import com.baidu.webkit.sdk.BDownloadListener;
import com.baidu.webkit.sdk.BGeolocationPermissions;
import com.baidu.webkit.sdk.BSslError;
import com.baidu.webkit.sdk.BSslErrorHandler;
import com.baidu.webkit.sdk.BValueCallback;
import com.baidu.webkit.sdk.BWebBackForwardList;
import com.baidu.webkit.sdk.BWebChromeClient;
import com.baidu.webkit.sdk.BWebChromeClient.BCustomViewCallback;
import com.baidu.webkit.sdk.BWebHistoryItem;
import com.baidu.webkit.sdk.BWebSettings;
import com.baidu.webkit.sdk.BWebView;
import com.baidu.webkit.sdk.BWebView.BHitTestResult;
import com.baidu.webkit.sdk.BWebViewClient;
/**
 * @ClassName: BPWebPoolView 
 * @Description: BdWebPoolView 
 * @author LEIKANG 
 * @date 2012-12-6 ����2:26:09
 */
public class BPWebPoolView extends FrameLayout implements BPErrorViewListener, OnLongClickListener {

	/** DEBUG mode */
	public static final boolean DEBUG = false;

	/** Ĭ�ϳص����size */
	public static final int DEFAULT_MAX_POOL_SIZE = 4;

	/** ��ʶWebView���ڵ�������� */
	public static final String BUNDLE_CLINK_MODE = "CLINK_MODE";

	/** ��ʾ��ҳ���ڵ������ */
	private static final byte CLICK_FROM_PAGE = 1;

	/** ��ʶ��ҳ���������� */
	private static final byte CLICK_FROM_OUTSIDE_PAGE = 2;

	/** ��wap1.0ҳ������ */
	public static final byte PAGE_TYPE_NORMAL = 1;

	/** wap1.0ҳ������ */
	public static final byte PAGE_TYPE_WAP10 = 2;

	/** ҳ�����ͣ�Ŀǰ����wap1.0���������� */
	public static final String BUNDLE_PAGE_TYPE = "PAGE_TYPE";

	/** ׼���������ҳ�� */
	public static final int STATE_PAGE_STARTED = 0x01;
	/** �������ҳ�Ľ��ȡ� */
	public static final int STATE_PROGRESS_CHANGED = 0x02;
	/** ���ҳ������ϡ� */
	public static final int STATE_PAGE_FINISHED = 0x04;
	/** ���ҳ��ʼ���ա� */
	public static final int STATE_PAGE_RECEIVED = 0x08;
	/** ҳ�濪ʼ��ʾ�� */
	public static final int STATE_START_SHOW = 0x10;
	/** �յ����� */
	public static final int STATE_RECEIVE_ERROR = 0x20;

	/** ��δ��ʾ�� */
	public static final int NOT_SHOW = 0;
	/** ��ʼ��ʾ�� */
	public static final int START_SHOW = 1;
	/** �����ʾ�� */
	public static final int HAS_SHOWN = 2;

	/** ��������Сֵ�� */
	public static final int PROGRESS_MIN = 10;
	/** ���������ֵ�� */
	public static final int PROGRESS_MAX = 100;
	/** ��ʱ�� */
	public static final int DELAYED_TIME = 200;

	/** ������ʶҳ�����ʾ״̬�� */
	private int mShowState;

	/** BdWebPoolCustomView List */
	private List<BPWebPoolCustomView> mWebViewList;
	/** BdWebPoolViewClient */
	private BPWebPoolViewClient mWebPoolViewClient;
	/** BdWebPoolChromeClient */
	private BPWebPoolChromeClient mWebPoolChromeClient;

	/** ��ǰ��BdWebPoolCustomView */
	private BPWebPoolCustomView mCurWebView;

	/** �ص����size */
	private int mMaxPoolSize = DEFAULT_MAX_POOL_SIZE;
	
	/** ��View������View */
	private BPWebPoolMaskView mMaskView;

	/** WebView���� */
	private BWebSettings mSettings;

	/** Attached Javascript interfaces */
	private Map<String, Object> mJsItems;

	/** �ص�֪ͨ�ϲ�ĵײ�BdWebPoolCustomView�Ƿ��ǵ�ǰ��BdWebPoolCustomView */
	private boolean mCurWebViewNotify;

	/** ����ҳ���ձ� */
	private HashMap<Integer, Integer> mErrorCodeList = new HashMap<Integer, Integer>();

	/** ��ʾ�Ĵ���ҳ�� */
	private BPErrorView mErrorView;

	/** ���ؼ����� */
	private BDownloadListener mDownloadListener;

	/**���ʵ�վ�㡣ֻ��һ��ʵ�����Է���ֻ�Բ������ݣ�����title�����¡�*/
    //private VisitedSite mVisitedSite = new VisitedSite();
    
    /** webview custom view.  */
    private View mCustomView;
    
    /** custom view ��Ӧ��ȫ�� container. */
    protected FrameLayout mFullscreenContainer;
    
    /** ȫ��֮ǰactivity�� orientation. */
    private int mOriginalOrientation;
    
    /** customview callback. */
    private BCustomViewCallback mCustomViewCallback;
    
    /** custom view layout param. */
    protected static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS =
        new FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT);

    
	/**
	 * Constructor
	 * 
	 * @param aContext
	 *            Context
	 */
	public BPWebPoolView(Context aContext) {
		this(aContext, null);
	}

	/**
	 * Constructor
	 * 
	 * @param aContext
	 *            Context
	 * @param aAttrs
	 *            AttributeSet
	 */
	public BPWebPoolView(Context aContext, AttributeSet aAttrs) {
		this(aContext, aAttrs, 0);
	}

	/**
	 * Constructor
	 * 
	 * @param aContext
	 *            Context
	 * @param aAttrs
	 *            AttributeSet
	 * @param aDefStyle
	 *            DefaultStyle
	 */
	public BPWebPoolView(Context aContext, AttributeSet aAttrs, int aDefStyle) {
		this(aContext, null, 0, DEFAULT_MAX_POOL_SIZE);
	}

	/**
	 * @param aMaxPoolSize
	 *            �ص����size
	 * @param aContext
	 *            Context
	 * @param aAttrs
	 *            AttributeSet
	 * @param aDefStyle
	 *            DefaultStyle
	 */
	public BPWebPoolView(Context aContext, AttributeSet aAttrs, int aDefStyle, int aMaxPoolSize) {
		super(aContext, aAttrs, aDefStyle);

		setBackgroundColor(Color.WHITE);

		mMaxPoolSize = aMaxPoolSize;
		mWebViewList = new ArrayList<BPWebPoolCustomView>(DEFAULT_MAX_POOL_SIZE);
		
		// ��ʼ��BdWebPoolView��BdWebSettings
		BWebView initWebView = getFreeWebView();
		BWebSettings settings = initWebView.getSettings();
		mSettings = settings;

		// ���ǰ�����˵�MaskView
		mMaskView = new BPWebPoolMaskView(aContext);
		mMaskView.setBackgroundColor(Color.WHITE);
		mMaskView.setVisibility(GONE);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT);
		addView(mMaskView, params);
	}

	/**
	 * Set the WebViewClient that will receive various notifications and
	 * requests. This will replace the current handler.
	 * 
	 * @param aClient
	 *            An implementation of BdWebPoolViewClient.
	 */
	public void setWebViewClient(BPWebPoolViewClient aClient) {
		mWebPoolViewClient = aClient;
	}

	/**
	 * Set the chrome handler. This is an implementation of WebChromeClient for
	 * use in handling Javascript dialogs, favicons, titles, and the progress.
	 * This will replace the current handler.
	 * 
	 * @param aClient
	 *            An implementation of BdWebPoolChromeClient.
	 */
	public void setWebChromeClient(BPWebPoolChromeClient aClient) {
		mWebPoolChromeClient = aClient;
	}

	/**
	 * Use this function to bind an object to Javascript so that the methods can
	 * be accessed from Javascript.
	 * <p>
	 * <strong>IMPORTANT:</strong>
	 * <ul>
	 * <li>Using addJavascriptInterface() allows JavaScript to control your
	 * application. This can be a very useful feature or a dangerous security
	 * issue. When the HTML in the WebView is untrustworthy (for example, part
	 * or all of the HTML is provided by some person or process), then an
	 * attacker could inject HTML that will execute your code and possibly any
	 * code of the attacker's choosing.<br>
	 * Do not use addJavascriptInterface() unless all of the HTML in this
	 * WebView was written by you.</li>
	 * <li>The Java object that is bound runs in another thread and not in the
	 * thread that it was constructed in.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param obj
	 *            The class instance to bind to Javascript
	 * @param interfaceName
	 *            The name to used to expose the class in Javascript
	 */
	public void addJavascriptInterface(Object obj, String interfaceName) {
		if (mJsItems == null) {
			mJsItems = new HashMap<String, Object>();
		}
		if (mJsItems.containsKey(interfaceName)) {
			mJsItems.remove(interfaceName);
		}
		mJsItems.put(interfaceName, obj);

		for (BWebView webView : mWebViewList) {
			webView.addJavascriptInterface(obj, interfaceName);
		}
	}

	/**
	 * Return the WebSettings object used to control the settings for this
	 * WebView.
	 * 
	 * @return A WebSettings object that can be used to control this WebView's
	 *         settings.
	 */
	public BWebSettings getSettings() {
		return mSettings;
	}

	/**
	 * Load the given url.
	 * 
	 * @param aUrl
	 *            The url of the resource to load.
	 */
	public void loadUrl(String aUrl) {
		Bundle extra = new Bundle();
		extra.putByte(BUNDLE_CLINK_MODE, CLICK_FROM_OUTSIDE_PAGE);
		extra.putByte(BPWebPoolView.BUNDLE_PAGE_TYPE, BPWebPoolView.PAGE_TYPE_NORMAL);
		loadUrl(aUrl, extra);
	}

	/**
	 * ��ȡ��ǰWebView��Url
	 * 
	 * @return ��ȡ��ǰҳ���Url
	 */
	public String getUrl() {
		if (mCurWebView != null) {
			return mCurWebView.getUrl();
		} else {
			return "";
		}
	}
	
	/**
     * Gets a new picture that captures the current display of this WebView.
     * This is a copy of the display, and will be unaffected if this WebView
     * later loads a different URL.
     *
     * @return a picture containing the current contents of this WebView. Note
     *         this picture is of the entire document, and is not restricted to
     *         the bounds of the view.
     */
    public android.graphics.Picture capturePicture() {
        if (mCurWebView != null) {
            return mCurWebView.capturePicture();
        } else {
            return null;
        }
    }
    
    /**
     * Return the scrolled left position of this view. This is the left edge of
     * the displayed part of your view. You do not need to draw any pixels
     * farther left, since those are outside of the frame of your view on
     * screen.
     *
     * @return The left edge of the displayed part of your view, in pixels.
     */
    public int getWebViewScrollX() {
        if (mCurWebView != null) {
            return mCurWebView.getScrollX();
        } else {
            return 0;
        }
    }
    
    /**
     * �õ�ʵ��ʹ�õ�WebView
     * @return webview
     */
    public View getWebView() {
        if (mCurWebView != null) {
            return mCurWebView;
        } else {
            return null;
        }
    }
    
    /**
     * Return the scrolled top position of this view. This is the top edge of
     * the displayed part of your view. You do not need to draw any pixels above
     * it, since those are outside of the frame of your view on screen.
     *
     * @return The top edge of the displayed part of your view, in pixels.
     */
    public int getWebViewScrollY() {
        if (mCurWebView != null) {
            return mCurWebView.getScrollY();
        } else {
            return 0;
        }
    }
	/**
	 * Reload the current url.
	 */
	public void reload() {
		if (mCurWebView != null) {
			mCurWebView.setLoadMode(LoadMode.LOAD_RELOAD);
			mCurWebView.reload();
		}
		if (mWebPoolViewClient != null) {
			mWebPoolViewClient.onReload(this);
		}
	}

	/**
	 * Stop the current load.
	 */
	public void stopLoading() {
		if (mCurWebView != null) {
			mCurWebView.stopLoading();
		}
	}

	/**
	 * Tell the WebView to clear its internal back/forward list.
	 */
	public void clearHistory() {
		for (BPWebPoolCustomView webview : mWebViewList) {
			webview.setHistoryCount(0);
			webview.clearHistory();
		}
 
	}

	/**
	 * Call this to inform the view that memory is low so that it can free any
	 * available memory.
	 */
	public void freeMemory() {
		BdLog.w("");
		for (BPWebPoolCustomView webview : mWebViewList) {
			webview.freeMemory();
		}
	}

	/**
	 * Use this method to put the WebView into text selection mode. Do not rely
	 * on this functionality; it will be deprecated in the future.
	 */
	public void emulateShiftHeld() {
		if (mCurWebView != null) {
			mCurWebView.emulateShiftHeld();
		}
	}

	/**
	 * ���WebPoolView����
	 */
	public void clear() {
		while (mWebViewList.size() > 0) {
			BWebView webview = mWebViewList.get(0);
			webview.stopLoading();
			webview.clearFocus();
			webview.clearView();
			webview.clearHistory();
			webview.destroy();
			mWebViewList.remove(0);
		}
	}

	/**
	 * clearView
	 */
	public void clearView() {
		if (mCurWebView != null) {
			mCurWebView.clearView();
		}
	}

	/**
	 * �Ƿ��ܹ�ǰ������ָ������
	 * 
	 * @param aSteps
	 *            The negative or positive number of steps to move the history.
	 * @return �Ƿ��ܹ�ǰ�����ߺ���ָ���Ĳ���
	 */
	public boolean canGoBackOrForward(int aSteps) {
        //ʹ�õ�WebView�������Լ�����ǰ��������ʷ�б�
	    if (mCurWebView != null) {
	        return mCurWebView.canGoBackOrForward(aSteps);
	    }
	    return false;
	}

	/**
	 * �Ƿ���Ժ���
	 * 
	 * @return �ܹ����˷���true�����򷵻�false
	 */
	public boolean canGoBack() {
		return canGoBackOrForward(-1);
	}

	/**
	 * �Ƿ��ܹ�ǰ��
	 * 
	 * @return �ܹ�ǰ������true�����򷵻�false
	 */
	public boolean canGoForward() {
		return canGoBackOrForward(1);
	}

	/**
	 * ����
	 */
	public void goBack() {
        //ʹ�õ�WebView�������Լ�����ǰ��������ʷ�б�
        if (mCurWebView != null) {
            mCurWebView.goBack();
        }
	}

	/**
	 * ǰ��
	 */
	public void goForward() {
        //ʹ�õ�WebView�������Լ�����ǰ��������ʷ�б�
	    if (mCurWebView != null) {
	        mCurWebView.goForward();
	    }
	}

	/**
	 * ��ȡ��ǰ�б�λ��
	 * 
	 * @return ���ص�ǰ�б�λ��
	 */
	public int getCurIndex() {
        //ʹ�õ�WebView�������Լ�����ǰ��������ʷ�б�
	    if (mCurWebView != null) {
	        mCurWebView.copyBackForwardList().getCurrentIndex();
	    }
	    return -1;
	}

	/**
	 * ��ȡ��ǰ�б�Url
	 * 
	 * @return ���ص�ǰ�б�Url
	 */
	public String getCurUrl() {
		String curUrl = null;
        //ʹ�õ�WebView�������Լ�����ǰ��������ʷ�б�
        BWebHistoryItem curItem = null;
        try {
            curItem = mCurWebView.copyBackForwardList().getCurrentItem();
        } catch (Exception e) {
            BdLog.w(e.getMessage());
        }
        
		if (curItem != null) {
			curUrl = curItem.getUrl();
		}
		return curUrl;
	}

	/**
	 * Add or remove a title bar to be embedded into the WebView, and scroll
	 * along with it vertically, while remaining in view horizontally. Pass null
	 * to remove the title bar from the WebView, and return to drawing the
	 * WebView normally without translating to account for the title bar.
	 * 
	 * @param aView
	 *            ������View
	 */
	public void setEmbeddedTitleBar(View aView) {
		if (mCurWebView != null) {
		    mCurWebView.setEmbeddedTitleBar(aView);
		}
	}
	
	/**
	 * �������ַ�մ򿪸���ҳ������򿪰ٶ�����ҳ��
	 * 
	 * @param aUrl
	 *            Ҫ���ʵ�url��Ҫ�����Ĺؼ���
	 * @return �µ�url
	 */
	public String composeUrl(String aUrl) {
		String searchUrl = "http://m.baidu.com/ssid=0/from=0/bd_page_type=1/uid=wiaui_1298960413_1175/s?tn=iphone&st=11104i&tj=i_sbtn0&pu=sz%401320_480&word=";// SUPPRESS
																																								// CHECKSTYLE
		String newUrl = aUrl;
		if (aUrl != null && aUrl.length() > 0) {
			if ((!aUrl.startsWith("http://")) && (!aUrl.startsWith("https://"))
					&& (!aUrl.startsWith("ftp://")) && (!aUrl.startsWith("rtsp://"))
					&& (!aUrl.startsWith("mms://"))) {
				newUrl = "http://" + aUrl;
			}
			// ����url��������
			if (!checkStrIsUrl(newUrl)) {
				try {
					newUrl = searchUrl + URLEncoder.encode(aUrl, "gbk");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return newUrl;
	}

	/**
	 * ���ƽӿ�
	 * 
	 * @return true��ʾ���Ƴɹ�
	 */
	public boolean commitCopy() {
		if (mCurWebView != null) {
			//TODO
			return true;//mCurWebView.commitCopy();
		} else {
			return false;
		}
	}

	/**
	 * nativeGetSelection����
	 * 
	 * @return ѡ�е��ַ���
	 */
	public String getSelection() {
		if (mCurWebView != null) {
			return "";//mCurWebView.getSelection();
		} else {
			return "";
		}
	}

	/**
	 * ��ȡWebView��mExtendSelectionֵ
	 * 
	 * @return mExtendSelectionֵ
	 */
	public boolean getExtendSelection() {
		if (mCurWebView != null) {
			return mCurWebView.getExtendSelection();
		} else {
			return false;
		}
	}

	/**
	 * ����WebView��mExtendSelectionֵ
	 * 
	 * @param aExtendSelection
	 *            �µ�mExtendSelection����ֵ
	 * @return true��ʾ���óɹ�����֮��Ȼ
	 */
	public boolean setExtendSelection(boolean aExtendSelection) {
		if (mCurWebView != null) {
			return mCurWebView.setExtendSelection(aExtendSelection);
		} else {
			return false;
		}
	}

	/**
	 * ��ȡWebView��mSelectingTextֵ
	 * 
	 * @return mSelectingTextֵ
	 */
	public boolean getSelectingText() {
		if (mCurWebView != null) {
			return mCurWebView.getSelectingText();
		} else {
			return false;
		}
	}

	/**
	 * ����WebView��mSelectingTextֵ
	 * 
	 * @param aSelectingText
	 *            �µ�mSelectingText����ֵ
	 * @return true��ʾ���óɹ�����֮��Ȼ
	 */
	public boolean setSelectingText(boolean aSelectingText) {
		if (mCurWebView != null) {
			return mCurWebView.setSelectingText(aSelectingText);
		} else {
			return false;
		}
	}

	/**
	 * ��ȡWebView��mDrawSelectionPointerֵ
	 * 
	 * @return mDrawSelectionPointerֵ
	 */
	public boolean getDrawSelectionPointer() {
		if (mCurWebView != null) {
			return mCurWebView.getDrawSelectionPointer();
		} else {
			return false;
		}
	}

	/**
	 * ����WebView��aDrawSelectionPointerֵ
	 * 
	 * @param aDrawSelectionPointer
	 *            �µ�aDrawSelectionPointer����ֵ
	 * @return true��ʾ���óɹ�����֮��Ȼ
	 */
	public boolean setDrawSelectionPointer(boolean aDrawSelectionPointer) {
		if (mCurWebView != null) {
			return mCurWebView.setDrawSelectionPointer(aDrawSelectionPointer);
		} else {
			return false;
		}
	}

	/**
	 * ��ȡWebView��mShiftIsPressedֵ
	 * 
	 * @return mExtendSelectionֵ
	 */
	public boolean getShiftIsPressed() {
		if (mCurWebView != null) {
			return mCurWebView.getShiftIsPressed();
		} else {
			return false;
		}
	}

	/**
	 * ����WebView��mShiftIsPressedֵ
	 * 
	 * @param aShiftIsPressed
	 *            �µ�mShiftIsPressed����ֵ
	 * @return true��ʾ���óɹ�����֮��Ȼ
	 */
	public boolean setShiftIsPressed(boolean aShiftIsPressed) {
		if (mCurWebView != null) {
			return mCurWebView.setShiftIsPressed(aShiftIsPressed);
		} else {
			return false;
		}
	}

	/**
	 * ��ȡWebView��mTouchSelectionֵ
	 * 
	 * @return mTouchSelectionֵ
	 */
	public boolean getTouchSelection() {
		if (mCurWebView != null) {
			return false;//mCurWebView.getTouchSelection();
		} else {
			return false;
		}
	}

	/**
	 * @return the mCurWebViewNotify
	 */
	public boolean isCurWebViewNotify() {
		return mCurWebViewNotify;
	}

	/**
	 * ����WebView��mTouchSelectionֵ
	 * 
	 * @param aTouchSelection
	 *            �µ�mTouchSelection����ֵ
	 * @return true��ʾ���óɹ�����֮��Ȼ
	 */
	public boolean setTouchSelection(boolean aTouchSelection) {
		if (mCurWebView != null) {
			return false;//mCurWebView.setTouchSelection(aTouchSelection);
		} else {
			return false;
		}
	}

	/**
	 * ��ȡBdHitTestResult
	 * 
	 * @return BdHitTestResultʵ��
	 */
	public BHitTestResult getHitTestResult() {
		if (mCurWebView != null) {
			return mCurWebView.getHitTestResult();
		} else {
			return null;
		}
	}

	/**
	 * Call this view's OnLongClickListener, if it is defined. Invokes the
	 * context menu if the OnLongClickListener did not consume the event.
	 * 
	 * @return True if one of the above receivers consumed the event, false
	 *         otherwise.
	 */
	public boolean performLongClick() {
		return superPerformLongClick();
	}

	/**
	 * Super performLongClick
	 * 
	 * @return True if one of the above receivers consumed the event, false
	 *         otherwise.
	 */
	public boolean superPerformLongClick() {
		if (mCurWebView != null) {
			return mCurWebView.performLongClick();
		} else {
			return false;
		}
	}

	/**
	 * Draw one child of this View Group. This method is responsible for getting
	 * the canvas in the right state. This includes clipping, translating so
	 * that the child's scrolled origin is at 0, 0, and applying any animation
	 * transformations.
	 * 
	 * @param canvas
	 *            The canvas on which to draw the child
	 * @param child
	 *            Who to draw
	 * @param drawingTime
	 *            The time at which draw is occuring
	 * @return True if an invalidate() was issued
	 */
	public boolean drawChild(Canvas canvas, View child, long drawingTime) {
		if (child.getClass().getName().equals("com.adobe.flashplayer.FlashPaintSurface")) {
			SurfaceView flashSurface = (SurfaceView) child;
			flashSurface.setZOrderOnTop(false);
		}
		return superDrawChild(canvas, child, drawingTime);
	}

	/**
	 * Super drawChild
	 * 
	 * @param canvas
	 *            The canvas on which to draw the child
	 * @param child
	 *            Who to draw
	 * @param drawingTime
	 *            The time at which draw is occuring
	 * @return True if an invalidate() was issued
	 */
	public boolean superDrawChild(Canvas canvas, View child, long drawingTime) {
		if (mCurWebView != null) {
			return mCurWebView.drawChild(canvas, child, drawingTime);
		} else {
			return false;
		}
	}

	/**
	 * Implement this method to handle touch screen motion events.
	 * 
	 * @param event
	 *            The motion event.
	 * @return True if the event was handled, false otherwise.
	 */
	public boolean onTouchEvent(MotionEvent event) {
		return superOnTouchEvent(event);
	}
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mCustomView != null) {
                hideCustomView();
                return true;
            }
        }
        
        return super.onKeyDown(keyCode, event);
    }



	/**
	 * Super onTouchEvent
	 * 
	 * @param event
	 *            The motion event.
	 * @return True if the event was handled, false otherwise.
	 */
	public boolean superOnTouchEvent(MotionEvent event) {
		if (mCurWebView != null) {
			return mCurWebView.onTouchEvent(event);
		} else {
			return false;
		}
	}

	/**
	 * Return the scrolled left position of this view. This is the left edge of
	 * the displayed part of your view. You do not need to draw any pixels
	 * farther left, since those are outside of the frame of your view on
	 * screen.
	 * 
	 * @return The left edge of the displayed part of your view, in pixels.
	 */
	public int getCurScrollX() {
		if (mCurWebView != null) {
			View view = mCurWebView;
			return view.getScrollX();
		} else {
			return -1;
		}
	}

	/**
	 * Return the scrolled top position of this view. This is the top edge of
	 * the displayed part of your view. You do not need to draw any pixels above
	 * it, since those are outside of the frame of your view on screen.
	 * 
	 * @return The top edge of the displayed part of your view, in pixels.
	 */
	public final int getCurScrollY() {
		if (mCurWebView != null) {
			View view = mCurWebView;
			return view.getScrollY();
		} else {
			return -1;
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (BWebView webView : mWebViewList) {
			sb.append(getWebViewDebugInfo(webView));
		}
		return sb.toString();
	}

	/**
	 * ��ȡ������
	 * 
	 * @return ������
	 */
	public int getErrorCode() {
		Integer errorCode = null;
		if (mCurWebView != null) {
			BWebBackForwardList list = mCurWebView.copyBackForwardList();
			errorCode = mErrorCodeList.get(list.getCurrentIndex());
		}
		return errorCode == null ? 0 : errorCode;
	}

	/**
	 * ���ô�����
	 * 
	 * @param errorCode
	 *            ������
	 */
	public void setErrorCode(int errorCode) {
		if (mCurWebView != null) {
			BWebBackForwardList list = mCurWebView.copyBackForwardList();
			mErrorCodeList.put(list.getCurrentIndex(), errorCode);
		}
	}

	/**
	 * ����WebContent�Ĵ���ҳ��
	 */
	public void hideErrorPage() {
		View errorView = mErrorView;
		if (errorView != null) {
			ViewGroup parent = (ViewGroup) errorView.getParent();
			if (parent != null) {
				parent.removeView(errorView);
			}
		}
	}

	/**
	 * ��ʾWebContent�Ĵ���ҳ��
	 */
	public void showErrorPage() {
		int errorCode = getErrorCode();
		if (errorCode == 0) {
			hideErrorPage();
			return;
		}

		BPErrorView errorView = mErrorView;

		if (errorView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			errorView = (BPErrorView) inflater.inflate(R.layout.browser_webview_error, null);
			mErrorView = errorView;
			errorView.setErrorCode(errorCode);
			errorView.setAttachedFixedWebView(this);
			errorView.setEventListener(this);
		}

		ViewGroup oldParent = (ViewGroup) errorView.getParent();
		if (oldParent != null) {
			oldParent.removeView(errorView);
		}
		ViewGroup newParent = (ViewGroup) getParent();
		ViewGroup.LayoutParams params = getLayoutParams();
		newParent.addView(errorView, params);
	}

	/**
	 * ��ȡҳ����ʾ״̬����NOT_SHOW, START_SHOW��HAS_SHOWN����״̬��
	 * 
	 * @return ��ʾ״̬��
	 */
	public int getShowState() {
		return mShowState;
	}

	/**
	 * ����ҳ����ʾ״̬����NOT_SHOW, START_SHOW��HAS_SHOWN����״̬��
	 * 
	 * @param showState
	 *            ��ʾ״̬��
	 */
	public void setShowState(int showState) {
		mShowState = showState;
	}

	/**
	 * Set the scrolled position of your view. This will cause a call to
	 * {@link #onScrollChanged(int, int, int, int)} and the view will be
	 * invalidated.
	 * 
	 * @param x
	 *            the x position to scroll to
	 * @param y
	 *            the y position to scroll to
	 */
	public void scrollTo(int x, int y) {
		if (mCurWebView != null) {
			mCurWebView.scrollTo(x, y);
		}
	}

	/**
	 * Move the scrolled position of your view. This will cause a call to
	 * {@link #onScrollChanged(int, int, int, int)} and the view will be
	 * invalidated.
	 * 
	 * @param x
	 *            the amount of pixels to scroll by horizontally
	 * @param y
	 *            the amount of pixels to scroll by vertically
	 */
	public void scrollBy(int x, int y) {
		if (mCurWebView != null) {
			mCurWebView.scrollBy(x, y);
		}
	}

	/**
	 * Register the interface to be used when content can not be handled by the
	 * rendering engine, and should be downloaded instead. This will replace the
	 * current handler.
	 * 
	 * @param listener
	 *            An implementation of DownloadListener.
	 */
	public void setDownloadListener(BDownloadListener listener) {
		for (BPWebPoolCustomView webview : mWebViewList) {
			webview.setDownloadListener(listener);
		}
		mDownloadListener = listener;
	}

	/**
	 * Get the title for the current page. This is the title of the current page
	 * until WebViewClient.onReceivedTitle is called.
	 * 
	 * @return The title for the current page.
	 */
	public String getTitle() {
		if (mCurWebView != null) {
			return mCurWebView.getTitle();
		} else {
			return "";
		}
	}

	@Override
	public void onErrorPageGoBack() {
		goBack();
	}

	@Override
	public void onErrorPageRefresh() {
		reload();
	}

	/**
     * ����״̬��
     * @param savedState Bundle.
     */
    public void saveStateToBundle(Bundle savedState) {
        if (savedState == null || mCurWebView == null) {
            return;
        }
        
        mCurWebView.saveState(savedState);
    }
    
    /**
     * �ָ�״̬��
     * @param savedState Bundle.
     */
    public void restoreFromBundle(Bundle savedState) {
        if (savedState == null || mCurWebView == null) {
            return;
        }
        
        mCurWebView.restoreState(savedState);
    }
    
	/**
	 * ��ȡһ�����õ�BdWebview
	 * 
	 * @param aClickMode
	 *            ���ģʽ
	 * @return BdWebviewʵ��
	 */
	private BPWebPoolCustomView getAvailableWebView(byte aClickMode) {
		// 1. ��һ�μ��أ�ʹ�ó�ʼ����WebView
		if (mCurWebView == null) {
			return getWebViewByPos(0);
		} else {
			// 2.1. ҳ���ڼ���
			// �жϵ�ǰwebview����ʷ��¼�Ƿ�������������У���ʹ����һ��webview���أ�����˵�����ض�������ģ�ʹ�õ�ǰwebview����
			if (aClickMode == CLICK_FROM_PAGE) {
				// 2.1.1 ʹ�õ�ǰwebview
				if (isRedirectLoad()) {
					BdLog.v("the request is redirect, use current webview to load it.");
					return mCurWebView;
				} else {
					// 2.1.2 �¿�webview
					return getFreeWebView();
				}
			} else {
				// 2.2. ҳ������أ��¿�webview
				return getFreeWebView();
			}
		}
	}

	/**
	 * ��ȡһ���µ�BdWebview
	 * 
	 * @return BdWebviewʵ��
	 */
	private BPWebPoolCustomView getFreeWebView() {
 
	    BPWebPoolCustomView webview = mCurWebView;
	    if (webview == null) {
			Context context = getContext();
			if (!(context instanceof Activity)) {
				BdLog.w("context is not activity, can not create webview.");
				return webview;
			}
			Activity act = (Activity) context;
			webview = new BPWebPoolCustomView(this, act.getParent() == null ? act : act.getParent());
			webview.setWebViewClient(new BPWebPoolCustomViewClient());
			webview.setWebChromeClient(new BPWebPoolCustomChromeClient());
			webview.setOnLongClickListener(this);

			// ��ʼ��JavascriptInterface
			initJavascriptClients(webview);
			// �������ؼ�����
			if (mDownloadListener != null) {
				webview.setDownloadListener(mDownloadListener);
			}
			
			webview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
			
			mWebViewList.add(webview);
			//��ԭ��switchWebView�е��߼��Ƶ��˴�����֤��WebView����ʹ��
			addWebView(webview);
			webview.onResume();
            View view = webview;
            if (view != null) {
                view.requestFocus();
            }
            mCurWebView = webview;
		}
		return webview;
	}

	/**
	 * ����������ȡָ����BdWebView
	 * 
	 * @param aPos
	 *            BdWebView������
	 * @return BdWebViewʵ��
	 */
	private BPWebPoolCustomView getWebViewByPos(int aPos) {
		if (aPos < 0 || aPos >= mWebViewList.size()) {
			return null;
		}
		return mWebViewList.get(aPos);
	}
	
	/**
	 * ��ȡ���õ�BdWebviewǰ���жϵ�ǰBdWebView�Ƿ��ڼ����ض���Url
	 * 
	 * @return true��ʾ���ض���false��ʾ�����ض���
	 */
	private boolean isRedirectLoad() {
		return isRedirectLoad(mCurWebView);
	}

	/**
	 * ��ȡ���õ�BdWebviewǰ���ж�ָ��BdWebView�Ƿ��ڼ����ض���Url
	 * 
	 * @param aWebView
	 *            BdWebViewʵ��
	 * @return true��ʾ���ض���false��ʾ�����ض���
	 */
	private boolean isRedirectLoad(BWebView aWebView) {
		boolean isRedirectLoad = false;
		int index = getWebViewIndex(aWebView);
		BPWebPoolCustomView webview = getWebViewByPos(index);
		if (webview != null) {
			int count_1 = aWebView.copyBackForwardList().getSize();
			int count_2 = webview.getHistoryCount();
			if (count_1 <= count_2) {
				isRedirectLoad = true;
			}
		}
		// �п�������Ϊ�ض��������ȡ���û���������ӣ�����Ϊ�����ض���
		if (isRedirectLoad) {
			String clinkLink = getClickLink(aWebView);
			BdLog.d("clinkLink: " + clinkLink);
			if (clinkLink != null && clinkLink != "undefined" && !clinkLink.startsWith("#")
					&& !clinkLink.startsWith("javascript")) {
				isRedirectLoad = false;
				BdLog.i("because get clink link, we think this load is not redirect.");
			}
		}
		return isRedirectLoad;
	}

	/**
	 * ��ȡָ��BdWebView������
	 * 
	 * @param aWebView
	 *            BdWebViewʵ��
	 * @return ָ��BdWebView������
	 */
	private int getWebViewIndex(BWebView aWebView) {
		for (int i = 0; i < mWebViewList.size(); i++) {
			BWebView webView = mWebViewList.get(i);
			if (webView.equals(aWebView)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * ���BdWebViewʵ����UI��
	 * 
	 * @param aWebView
	 *            BdWebViewʵ��
	 */
	private void addWebView(BWebView aWebView) {
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT);
		if (aWebView != null) {
			View aView = aWebView;
			if (aView != null && aView.getParent() == null) {
				addView(aView, 0, params);
			}
		}
	}

	/**
	 * ��ʾ���ֲ�
	 * 
	 */
	protected void showMaskView() {
		BdLog.d("");
		mMaskView.setVisibility(VISIBLE);
	}

	/**
	 * �������ֲ�
	 */
	protected void hideMaskView() {
		BdLog.d("");
		if (mMaskView.getVisibility() == VISIBLE) {
			mMaskView.setVisibility(GONE);
		}
	}
	
	/**
	 * ����WebView��һ�εĵ�ǰλ��
	 * 
	 * @param aWebView
	 *            BdWebViewʵ��
	 * @param aCurrentIndex
	 *            WebView��һ�εĵ�ǰλ��
	 */
	private void setLastCurrentIndex(BWebView aWebView, int aCurrentIndex) {
		int currentIndex = aWebView.copyBackForwardList().getCurrentIndex();
		int webview_index = getWebViewIndex(aWebView);
		BPWebPoolCustomView webview = getWebViewByPos(webview_index);
		if (webview != null) {
			webview.setLastIndex(currentIndex);
		}
	}

	/**
	 * ��ȡWebView��һ�εĵ�ǰλ��
	 * 
	 * @param aWebView
	 *            BdWebViewʵ��
	 * @return WebView��һ�εĵ�ǰλ��
	 */
	private int getLastCurrentIndex(BWebView aWebView) {
		int webview_index = getWebViewIndex(aWebView);
		BPWebPoolCustomView webview = getWebViewByPos(webview_index);
		if (webview != null) {
			return webview.getLastIndex();
		} else {
			return -1;
		}
	}

	/**
	 * ��ȡBdWebView���������
	 * 
	 * @param aWebView
	 *            BdWebViewʵ��
	 * @return ���������
	 */
	private String getClickLink(BWebView aWebView) {
		int webview_index = getWebViewIndex(aWebView);
		BPWebPoolCustomView webview = getWebViewByPos(webview_index);
		if (webview != null) {
			return webview.getLoadUrl();
		} else {
			return "";
		}
	}

	/**
	 * ����BdWebView���������
	 * 
	 * @param aWebView
	 *            BdWebViewʵ��
	 * @param aClickLink
	 *            ���������
	 */
	private void setClickLink(BWebView aWebView, String aClickLink) {
		int webview_index = getWebViewIndex(aWebView);
		BPWebPoolCustomView webview = getWebViewByPos(webview_index);
		if (webview != null) {
			webview.setLoadUrl(aClickLink);
		}
	}

	/**
	 * ������ӵ�ַ��#�ſ�ͷ����javascript��ͷ��������Ч�������ӣ��õ�View�򿪣������ö�View��
	 * 
	 * @param aClickLink
	 *            ��ȡ���ĵ������
	 * @return true��ʾ��Ч�ĵ�����ӣ��ö�View�򿪣���֮��Ȼ
	 */
	private boolean isValidClickLink(String aClickLink) {
	    return false;
	}

	/**
	 * ����ָ����url
	 * 
	 * @param aUrl
	 *            Ҫ���ص�url
	 * @param aExtra
	 *            Extra����
	 */
	protected void loadUrl(String aUrl, Bundle aExtra) {
		byte clickMode = aExtra.getByte(BUNDLE_CLINK_MODE, CLICK_FROM_OUTSIDE_PAGE);
		byte pageType = aExtra.getByte(BUNDLE_PAGE_TYPE, PAGE_TYPE_NORMAL);
		BdLog.i(clickMode + ", " + pageType);
		// 1. �����ǰ�������ڼ���wap1.0ҳ�棬�����Ƿ���wap1.0ҳ��
		if (pageType != PAGE_TYPE_WAP10) {
 
		} else {
			// ����wap1.0ҳ�棬���ģʽ��Ϊҳ���ڼ���
			// aExtra.putByte(BUNDLE_CLINK_MODE, CLICK_FROM_PAGE);
			clickMode = CLICK_FROM_PAGE;
		}
		if (mCurWebView != null) {
			mCurWebView.setLoadMode(LoadMode.LOAD_NORMAL);
			mCurWebView.loadUrl(aUrl);
		}

		hideErrorPage();
	}
	
	/**
     * ����js����
     * 
     * @param js js code
     */
	public void loadJavaScript(String js) {
	    if (mCurWebView != null) {
            mCurWebView.setLoadMode(LoadMode.LOAD_NORMAL);
            initJavascriptClients(mCurWebView);
            mCurWebView.loadUrl(js);
        }
	}

	/**
	 * ��View���ƣ����ڳ�ʼ��ʱֻ����һ��BdWebView�����洴��BdWebView��JavascriptClientҪ��һ�γ�ʼ������
	 * 
	 * @param aWebView
	 *            BdWebViewʵ��
	 */
	private void initJavascriptClients(BWebView aWebView) {
		if (mJsItems != null) {
			Iterator<String> iter = mJsItems.keySet().iterator();
			while (iter.hasNext()) {
				String interfaceName = iter.next();
				Object obj = mJsItems.get(interfaceName);
				aWebView.addJavascriptInterface(obj, interfaceName);
			}
		}
	}

	/**
	 * ��ӡ��BdWebView��Ϣ��������
	 * 
	 * @param aWebView
	 *            BdWebViewʵ��
	 * @return BdWebView������Ϣ
	 */
	private String getWebViewDebugInfo(BWebView aWebView) {
		StringBuffer sb = new StringBuffer();
		int size = aWebView.copyBackForwardList().getSize();
		sb.append(aWebView + ", " + size);
		if (aWebView instanceof BPWebPoolCustomView) {
			BPWebPoolCustomView customView = (BPWebPoolCustomView) aWebView;
			sb.append(", " + customView.getLoadMode());
		}
		sb.append("\n");
		for (int i = 0; i < size; i++) {
			sb.append(i + ": ");
			BWebHistoryItem item = aWebView.copyBackForwardList().getItemAtIndex(i);
			if (item != null) {
				sb.append(item.getUrl());
				sb.append("\n");
			}
		}
		sb.append("***************************");
		sb.append("\n");
		return sb.toString();
	}

	/**
	 * �ж��ַ����Ƿ�Ϊ��ַ
	 * 
	 * @param input
	 *            ������ַ���
	 * @return ���Ϊ��վ�򷵻�trur����֮��Ȼ
	 */
	private boolean checkStrIsUrl(String input) {
		Pattern pattern = Pattern.compile("^((https|http|ftp|rtsp|mms)?://)"
				+ "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?"
				+ "(([0-9]{1,3}\\.){3}[0-9]{1,3}" + "|" + "([0-9a-z_!~*'()-]+\\.)*"
				+ "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." + "[a-z]{2,6})" + "(:[0-9]{1,4})?" + "((/?)|"
				+ "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(input);
		return matcher.find();
	}

	/**
	 * ���ڶ�View�л������ܻᵼ�µ�ǰWebView���е���̨�ˣ������ڼ���
	 * 
	 * @param aCallBackWebView
	 *            BdWebViewʵ��
	 */
	private void setCurWebViewNotify(BWebView aCallBackWebView) {
		if (aCallBackWebView == null) {
			mCurWebViewNotify = false;
		} else {
			mCurWebViewNotify = aCallBackWebView.equals(mCurWebView);
		}
	}
	
	/**
	 * ���ݴ�����ı�״̬
	 * 
	 * @param view
	 *            webview
	 * @param changeStateMask
	 *            �ɵ�״̬
	 * @return ��״̬
	 */
	private int changeStateMaskByErrorCode(View view, int changeStateMask) {
		Object errorTag = view.getTag(R.id.webcontent_error_code);
		int errorTagCode = errorTag == null ? 0 : (Integer) errorTag;
		if (errorTagCode != 0) {
			changeStateMask |= STATE_RECEIVE_ERROR;
		}
		return changeStateMask;
	}

	/**
	 * �������״̬�����ı�ʱ�������˷�����
	 * 
	 * @param view
	 *            �����ı����View
	 * @param changeStateMask
	 *            ״̬�ı��־λ
	 * @param newValue
	 *            ��ֵ
	 * */
	public void onStateChanged(int changeStateMask, Object newValue) {
		// ҳ�濪ʼ��ʾ(�ƶϳ����ģ��Ѿ���ʾ����)
		BdLog.d(changeStateMask + ", " + getShowState());
		if ((changeStateMask & BPWebPoolView.STATE_START_SHOW) != 0) {
			// ҳ�濪ʼ��ʾʱ�����ش���ҳ
			if (getShowState() > BPWebPoolView.NOT_SHOW) {
				hideErrorPage();
			}

			if ((changeStateMask & BPWebPoolView.STATE_RECEIVE_ERROR) != 0) {
				Object errorTag = getTag(R.id.webcontent_error_code);
				int errorTagCode = errorTag == null ? 0 : (Integer) errorTag;
				setErrorCode(errorTagCode);
				showErrorPage();
			} else if (getErrorCode() != 0) {
				showErrorPage();
			} else {
				hideErrorPage();
			}
		}

		// ҳ����ؽ��ȸı䡣
		if ((changeStateMask & BPWebPoolView.STATE_PROGRESS_CHANGED) != 0) {
			int progress = (Integer) newValue;
			if (progress < BPWebPoolView.PROGRESS_MIN) {
				progress = BPWebPoolView.PROGRESS_MIN;
			}
		}
	}

	@Override
	public boolean onLongClick(View v) {
		return false;
	}

	/**
	 * Call this to balanace a previous call to onPause()
	 */
	public void onResume() {
		if (mCurWebView != null) {
			mCurWebView.onResume();
		}
	}

	/**
	 * Call this to pause any extra processing associated with this view and its
	 * associated DOM/plugins/javascript/etc. For example, if the view is taken
	 * offscreen, this could be called to reduce unnecessary CPU and/or network
	 * traffic. When the view is again "active", call onResume().
	 * 
	 * Note that this differs from pauseTimers(), which affects all views/DOMs
	 */
	public void onPause() {
		if (mCurWebView != null) {
			mCurWebView.onPause();
		}
	}

	/**
	 * @param resultMsg
	 *            Message
	 * @param bdTransport
	 *            BdWebViewTransport
	 * @return �Ƿ�ִ�гɹ�
	 */
	public boolean setWebViewToTarget(Message resultMsg, BWebView.BWebViewTransport bdTransport) {
		BPWebPoolCustomView webPoolCustomView = getAvailableWebView(CLICK_FROM_PAGE);
		if (webPoolCustomView != null) {
			bdTransport.setWebView(webPoolCustomView);
			resultMsg.sendToTarget();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Request the href of an anchor element due to getFocusNodePath returning
	 * "href." If hrefMsg is null, this method returns immediately and does not
	 * dispatch hrefMsg to its target.
	 * 
	 * @param hrefMsg
	 *            This message will be dispatched with the result of the request
	 *            as the data member with "url" as key. The result can be null.
	 */
	public void requestFocusNodeHref(Message hrefMsg) {
		if (mCurWebView != null) {
			mCurWebView.requestFocusNodeHref(hrefMsg);
		}
	}
    
    /**
     * ���� activity�Ƿ�ȫ��.
     * @param activity Activity
     * @param enabled �Ƿ�ȫ����
     */
    public void setFullscreen(Activity activity, boolean enabled) {
        Window win = activity.getWindow();
        
        int flag = !enabled ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
        win.setFlags(flag, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    
    /**
     * ���� custom view��
     */
    private void hideCustomView() {
        if (mCustomView == null) {
            return;
        }
        
        Context context = getContext();
        Activity activity = null;
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
        
        if (activity != null) {
            setFullscreen(activity, false);
            FrameLayout decor = (FrameLayout) activity.getWindow().getDecorView();
            decor.removeView(mFullscreenContainer);
            mFullscreenContainer = null;
            mCustomView = null;
            mCustomViewCallback.onCustomViewHidden();
            // Show the content view.
            activity.setRequestedOrientation(mOriginalOrientation);
        }
    }
    
    /**
     * ��ʾ customview.
     * �ѻص��� custom view ��ӵ� layout�С���ʾ������
     * 
     * @param view Ҫ��ʾ�� customview��
     * @param requestedOrientation  customview ����� orientation 
     * @param callback callback.
     */
    private void showCustomView(View view, int requestedOrientation, BCustomViewCallback callback) {
        Context context = getContext();
        Activity activity = null;
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
        
        if (activity != null) {
            // if a view already exists then immediately terminate the new one
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }

            mOriginalOrientation = activity.getRequestedOrientation();
            FrameLayout decor = (FrameLayout) activity.getWindow().getDecorView();
            mFullscreenContainer = new FullscreenHolder(activity);
            mFullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
            decor.addView(mFullscreenContainer, COVER_SCREEN_PARAMS);
            mCustomView = view;
            setFullscreen(activity, true);
            mCustomViewCallback = callback;
            activity.setRequestedOrientation(requestedOrientation);
        }

    }
    
    /**
     * webview custom view full screen holder. as a container.
     */
    static class FullscreenHolder extends FrameLayout {

        /**
         * constructor.
         * @param ctx Context
         */
        public FullscreenHolder(Context ctx) {
            super(ctx);
            setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
        }

        @Override
        public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }

    }
    

	/**
	 * BdWebPoolCustomViewClient
	 */
	class BPWebPoolCustomViewClient extends BWebViewClient {

		/** BdWebView Wrapper */
		private SoftReference<BWebView> mWebViewWrapper;

		/**
		 * obtain WebView Wrapper
		 * 
		 * @param aWebView
		 *            WebView
		 * @return WebView Wrapper
		 */
		public BWebView obtainWebViewWrapper(BWebView aWebView) {
			BWebView result = null;
			if (mWebViewWrapper != null) {
				result = mWebViewWrapper.get();
			}

			if (result == null || !result.equals(aWebView)) {
				result = new BPWebPoolCustomView(BPWebPoolView.this, aWebView.getContext());
				mWebViewWrapper = new SoftReference<BWebView>(result);
			}
			return result;
		}

		@Override
		public boolean shouldOverrideUrlLoading(BWebView aView, String aUrl) {
			BdLog.d(aUrl);
			
			if (aUrl.startsWith("about:")) {
			    return false;
			}

			// ��������Э�鴦���ŵ�BaseWebViewBaseWebView.handleSpecialScheme()
			try {
			    if (BaseWebView.handleSpecialScheme(getContext(), aUrl)) {
			        return true;
			    }
			    
			} catch (ActivityNotStartedException e) {
			    Toast.makeText(getContext(), R.string.activity_not_found, Toast.LENGTH_SHORT).show();
			    return true;
			}
			
			if (mWebPoolViewClient != null) {
				if (mWebPoolViewClient.shouldOverrideUrlLoading(BPWebPoolView.this, aUrl)) {
					return true;
				}
			}


			// �������������
			String clinkLink = getClickLink(aView);
			BdLog.d("clinkLink: " + clinkLink);
			boolean result;
			if (isValidClickLink(clinkLink)) {
				// �����ӣ��ö�View��
				Bundle extra = new Bundle();
				extra.putByte(BUNDLE_CLINK_MODE, CLICK_FROM_PAGE);
				extra.putByte(BPWebPoolView.BUNDLE_PAGE_TYPE, BPWebPoolView.PAGE_TYPE_NORMAL);
				loadUrl(aUrl, extra);
				result = true;
			} else {
				// �������ӣ��õ�View��
				result = false;
			}
			setClickLink(aView, null);

			return result;
		}

		@Override
		public void onPageStarted(BWebView aView, String aUrl, Bitmap aFavicon) {
			BdLog.d(aUrl);
			
			// ��onPageStartedʱ����ʾ����һ����ҳ�棬������Ҫ������ΪNOT_SHOW��
			setShowState(NOT_SHOW);

			if (mWebPoolViewClient != null) {
				mWebPoolViewClient.onPageStarted(BPWebPoolView.this, aUrl, aFavicon);
				int changeStateMask = changeStateMaskByErrorCode(BPWebPoolView.this, STATE_PAGE_STARTED);
				onStateChanged(changeStateMask, null);
			}
		}

		@Override
		public void onPageFinished(BWebView aView, String aUrl) {
			BdLog.d(aUrl);
			// 3. ����ǰ�����˵�����ͼ
			hideMaskView();

			setCurWebViewNotify(aView);
			if (mWebPoolViewClient != null) {
				mWebPoolViewClient.onPageFinished(BPWebPoolView.this, aUrl);
				// ���ִ�еĲ�����ҳ���ǰ������ˣ�����ӵ��ҳ�滺�棬��ֱ��ִ��onPageStarted��onPageFinished��
				// ��������doUpdateVisitedHistory��onProgressChanged��
				// �����ڴ˴���STATE_START_SHOW���ϣ���ʾҳ���Ѿ���ʼ��ʾ��
				int changeStateMask = STATE_PAGE_FINISHED;
				if (mShowState == NOT_SHOW) {
					changeStateMask |= STATE_START_SHOW;
				}
				setShowState(HAS_SHOWN);
				changeStateMask = changeStateMaskByErrorCode(BPWebPoolView.this, changeStateMask);

				onStateChanged(changeStateMask, null);

				// webcontent_error_code��tag������Ǹ�ҳ���Ƿ���һ������ҳ��
				// ��tagӦ������onPageFinished���������������onPageStarted���޳���
				// ��Ϊ���tag������һ��ҳ���onReceivedError����ģ�֮��ͻᾭ��onPageStarted����Ĭ�ϴ���ҳ��
				setTag(R.id.webcontent_error_code, 0);
			}
		}

		@Override
		public void onLoadResource(BWebView aView, String aUrl) {
 
		}

		@Override
		public void onReceivedError(BWebView view, int errorCode, String description, String failingUrl) {
			BdLog.d(errorCode + ", " + description);
			if (mWebPoolViewClient != null) {
				mWebPoolViewClient.onReceivedError(BPWebPoolView.this, errorCode, description, failingUrl);
			}

			// ����webcontent_error_code��tag����ʾ��һ�μ��ص�ҳ�潫���Ǵ���ҳ��
			setTag(R.id.webcontent_error_code, errorCode);
		}

		@Override
		public void onReceivedSslError(BWebView view, BSslErrorHandler handler, BSslError error) {
			BdLog.d("");
			handler.proceed();
			hideErrorPage();
		}

		@Override
		public void doUpdateVisitedHistory(BWebView view, String url, boolean isReload) {
			if (url != null) { // url ==null��ʾ�Ǵ�onReceivedError�����ģ���Ӧ����START_SHOW��
				// ��doUpdateVisitedHistory���������һ��onProgressChangedʱ��ʼ��ʾҳ�档
				// ���Դ˴���START_SHOW���ϣ��Ա���onProgressChangedʱ֪ͨҳ���ѿ�ʼ��ʾ��
				setShowState(START_SHOW);
			}
			super.doUpdateVisitedHistory(view, url, isReload);
		}
	}

	/**
	 * BdWebPoolCustomChromeClient
	 */
	class BPWebPoolCustomChromeClient extends BWebChromeClient {

		/** BdWebView Wrapper */
		private SoftReference<BWebView> mWebViewWrapper;
		
		/**
		 * obtain WebView Wrapper
		 * 
		 * @param aWebView
		 *            WebView
		 * @return WebView Wrapper
		 */
		public BWebView obtainWebViewWrapper(WebView aWebView) {
			BWebView result = null;
			if (mWebViewWrapper != null) {
				result = mWebViewWrapper.get();
			}
			if (result == null) {
				result = new BPWebPoolCustomView(BPWebPoolView.this, aWebView.getContext());
				mWebViewWrapper = new SoftReference<BWebView>(result);
			}
			return result;
		}

		@Override
		public void onProgressChanged(BWebView aView, int aNewProgress) {
			setCurWebViewNotify(aView);
			if (mWebPoolChromeClient != null) {
				mWebPoolChromeClient.onProgressChanged(BPWebPoolView.this, aNewProgress);
				// �����ʾ״̬ΪSTART_SHOW����ʾdoUpdateVisitedHistory�Ѵ�����
				// ���ε�onProgressChanged֮��ͻῪʼ��ʾҳ�档
				int changeStateMask = STATE_PROGRESS_CHANGED;
				if (mShowState == START_SHOW) {
					changeStateMask |= STATE_START_SHOW;
					setShowState(HAS_SHOWN);
					// ��������ʱ����֪����Ƿ���ʾ����ҳ�����������أ��ٺ�����Ҫʱ����ʾ����
					hideErrorPage();
				}
				// ��������˾����������
				if (aNewProgress <= PROGRESS_MIN) {
					setErrorCode(0);
				}

				if (mWebPoolViewClient != null) {
					onStateChanged(changeStateMask, aNewProgress);
				}

				if (aNewProgress == 100) {
					hideMaskView();
				}
			}
		}

		@Override
		public void onReceivedTitle(BWebView aView, String aTitle) {
			setCurWebViewNotify(aView);
			if (mWebPoolChromeClient != null) {
				mWebPoolChromeClient.onReceivedTitle(BPWebPoolView.this, aTitle);
				int changeStateMask = changeStateMaskByErrorCode(BPWebPoolView.this, STATE_PAGE_RECEIVED);
				if (mWebPoolViewClient != null) {
					onStateChanged(changeStateMask, null);
				}
			}
			//TODO LEIKANG
		}

		@Override
		public void onReceivedIcon(BWebView view, Bitmap icon) {
		    super.onReceivedIcon(view, icon);
		    //TODO LEIKANG 
		}
		
		@Override
		public Bitmap getDefaultVideoPoster() {
			return null;
		}

		@Override
		public View getVideoLoadingProgressView() {
			return null;
		}

		@Override
		public boolean onCreateWindow(BWebView view, boolean dialog, boolean userGesture, Message resultMsg) {
			if (mWebPoolChromeClient != null) {
				//BWebView.BWebViewTransport transport = (BWebView.BWebViewTransport) resultMsg.obj;
				BWebView.BWebViewTransport bdTransport = view.new BWebViewTransport();
				return mWebPoolChromeClient.onCreateWindow(BPWebPoolView.this, dialog, userGesture,
						resultMsg, bdTransport);
			} else {
				return false;
			}
		}

		@Override
		public void onCloseWindow(BWebView window) {
		}

        @Override
        public void onHideCustomView() {
            hideCustomView();
        }

        @Override
        public void onShowCustomView(View view, BCustomViewCallback callback) {
            Context context = getContext();
            Activity activity = null;
            if (context instanceof Activity) {
                activity = (Activity) context;
            }
            
            if (activity != null) {
                onShowCustomView(view, activity.getRequestedOrientation(), callback);
            }
        }
        
        @Override
        public void onShowCustomView(View view, int requestedOrientation, BCustomViewCallback callback) {
            showCustomView(view, requestedOrientation, callback);
        }

		// For Android 3.0+
		@Override
		public void openFileChooser(BValueCallback<Uri> uploadMsg, String acceptType) { // SUPPRESS CHECKSTYLE
			if (mWebPoolChromeClient != null) {
				mWebPoolChromeClient.openFileChooser(uploadMsg, acceptType);
			}
		}

		// For Android < 3.0
		@Override
		public void openFileChooser(BValueCallback<Uri> uploadMsg) { // SUPPRESS CHECKSTYLE
			if (mWebPoolChromeClient != null) {
				mWebPoolChromeClient.openFileChooser(uploadMsg);
			}
		}

		/**
		 * Instructs the client to show a prompt to ask the user to set the
		 * Geolocation permission state for the specified origin.
		 */
		public void onGeolocationPermissionsShowPrompt(String origin,
				BGeolocationPermissions.BCallback callback) {
			if (mWebPoolChromeClient != null) {
				mWebPoolChromeClient.onGeolocationPermissionsShowPrompt(origin, callback);
			}
		}

		/**
		 * Instructs the client to hide the Geolocation permissions prompt.
		 */
		public void onGeolocationPermissionsHidePrompt() {
			if (mWebPoolChromeClient != null) {
				mWebPoolChromeClient.onGeolocationPermissionsHidePrompt();
			}
		}
	}

}